package de.yadrone.android.videodeprecated;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import android.os.AsyncTask;
import android.util.Log;

import de.yadrone.base.command.CommandManager;
import de.yadrone.base.utils.ARDroneUtils;

/**
 * Note !!!
 * This class was part of an experiment to display the video stream on Android.
 * It has been used to as a proxy, which cut the proprietary PaVE header out of the video stream. 
 * It did not work :-(
 */
public class StreamProxy implements Runnable
{

	private static final String TAG = "YADrone";

	private static final int SERVER_PORT = 8888;

	private byte[] buffer = new byte[1000];
	private long bufferLength = 0;
	
	private Thread thread;
	private Socket droneSocket;
	private boolean isRunning;
	private ServerSocket socket;
	private int port;

	public StreamProxy(CommandManager manager)
	{
		// connect to drone
		
		try
		{
			droneSocket = new Socket("192.168.1.1", ARDroneUtils.VIDEO_PORT);
			droneSocket.setSoTimeout(3000);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		// toDo: API changed with version 0.3, see VideoTutorial on Homepage
//		ticklePort(ARDroneUtils.VIDEO_PORT);
//		manager.enableVideoData();
//		ticklePort(ARDroneUtils.VIDEO_PORT);
//		manager.disableAutomaticVideoBitrate();
		
		// Create server socket
		try
		{
			socket = new ServerSocket(SERVER_PORT, 0, InetAddress.getByAddress(new byte[] { 127, 0, 0, 1 }));
			socket.setSoTimeout(5000);
			port = socket.getLocalPort();
		}
		catch (UnknownHostException e)
		{ // impossible
		}
		catch (IOException e)
		{
			Log.e(TAG, "IOException initializing server", e);
		}

	}

	protected void ticklePort(int port)
	{
		byte[] buf = { 0x01, 0x00, 0x00, 0x00 };
		try
		{
			OutputStream os = droneSocket.getOutputStream();
			os.write(buf);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void start()
	{
		thread = new Thread(this);
		thread.start();
	}

	public void stop()
	{
		isRunning = false;
		thread.interrupt();
		try
		{
			thread.join(5000);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void run()
	{
		isRunning = true;
		while (isRunning)
		{
			try
			{
				Socket client = socket.accept();
				if (client == null)
				{
					continue;
				}
				Log.d(TAG, "client connected");

				StreamToMediaPlayerTask task = new StreamToMediaPlayerTask(client);
				if (task.processRequest())
				{
					task.execute();
				}

			}
			catch (SocketTimeoutException e)
			{
				// Do nothing
			}
			catch (IOException e)
			{
				Log.e(TAG, "Error connecting to client", e);
			}
		}
		Log.d(TAG, "Proxy interrupted. Shutting down.");
	}

	private class StreamToMediaPlayerTask extends AsyncTask<String, Void, Integer>
	{

		String localPath;
		Socket client;
		int cbSkip;

		public StreamToMediaPlayerTask(Socket client)
		{
			this.client = client;
		}

		public boolean processRequest()
		{
			// Read HTTP headers
			String headers = "";
			try
			{
				headers = readTextStreamAvailable(client.getInputStream());
			}
			catch (IOException e)
			{
				Log.e(TAG, "Error reading HTTP request header from stream:", e);
				return false;
			}

			// Get the important bits from the headers
			String[] headerLines = headers.split("\n");
			String urlLine = headerLines[0];
			if (!urlLine.startsWith("GET "))
			{
				Log.e(TAG, "Only GET is supported");
				return false;
			}
			urlLine = urlLine.substring(4);
			int charPos = urlLine.indexOf(' ');
			if (charPos != -1)
			{
				urlLine = urlLine.substring(1, charPos);
			}
			localPath = urlLine;

			// See if there's a "Range:" header
			for (int i = 0; i < headerLines.length; i++)
			{
				String headerLine = headerLines[i];
				if (headerLine.startsWith("Range: bytes="))
				{
					headerLine = headerLine.substring(13);
					charPos = headerLine.indexOf('-');
					if (charPos > 0)
					{
						headerLine = headerLine.substring(0, charPos);
					}
					cbSkip = Integer.parseInt(headerLine);
				}
			}
			return true;
		}

		@Override
		protected Integer doInBackground(String... params)
		{

			long fileSize = 10000;

			// Create HTTP header
//			String headers = "HTTP/1.0 200 OK\r\n";
//			headers += "Content-Type: video/H264\r\n";
//			headers += "Content-Length: " + Integer.MAX_VALUE + "\r\n";
//			headers += "Connection: close\r\n";
//			headers += "\r\n";

			StringBuilder sb = new StringBuilder();
			sb.append( "HTTP/1.1 200 OK\r\n");
			sb.append( "Content-Type: audio/mpeg\r\n");
			sb.append( "Connection: close\r\n" );
			sb.append( "Accept-Ranges: bytes\r\n" );
			sb.append( "Content-Length: " + Integer.MAX_VALUE + "\r\n" );
			
			String headers = sb.toString();
			
			// Begin with HTTP header
			int fc = 0;
			long cbToSend = fileSize - cbSkip;
			OutputStream output = null;
			byte[] buff = new byte[64 * 1024];
			try
			{
				output = new BufferedOutputStream(client.getOutputStream(), 32 * 1024);
				output.write(headers.getBytes());

				// Loop as long as there's stuff to send
				while (isRunning && cbToSend > 0 && !client.isClosed())
				{
					try
					{
						System.out.println("Start reading");
						
						InputStream is = droneSocket.getInputStream();
						int paveIndex = -1;
						int skipBytes = 0;
						int i;
						while ((i = is.read()) != -1)
						{
							if (skipBytes-- > 0)
							{
								if (skipBytes == 0) 
									paveIndex = -1;
								continue;
							}
							
							byte b = (byte)i;
							if ((paveIndex == -1) && (b == (byte)'P'))
								paveIndex = 0;
							else if ((paveIndex == 0) && (b == (byte)'a'))
								paveIndex = 1;
							else if ((paveIndex == 0) && (b != (byte)'a'))
							{
								output.write((byte)'P');
								paveIndex = -1;
							}
							else if ((paveIndex == 1) && (b == (byte)'V'))
								paveIndex = 2;
							else if ((paveIndex == 1) && (b != (byte)'V'))
							{
								output.write((byte)'P');
								output.write((byte)'a');
								paveIndex = -1;
							}
							else if ((paveIndex == 2) && (b == (byte)'E'))
							{
								paveIndex = 3;
								skipBytes = 61;
							}
							else if ((paveIndex == 2) && (b != (byte)'E'))
							{
								output.write((byte)'P');
								output.write((byte)'a');
								output.write((byte)'V');
								paveIndex = -1;
							}
							
							if (paveIndex == -1)
								output.write(b);
							else if ((paveIndex == 3) && (skipBytes == 0))
							{
								paveIndex = -1;
								System.out.println("PaVE skipped");
							}
						}
							
//						byte[] buffer = new byte[1024]; // Adjust if you want
//						int bytesRead;
//						while ((bytesRead = droneSocket.getInputStream().read(buffer)) != -1)
//						{
//							int startWriteIndex = 0;
//							if ((buffer[0] == (byte)'P') && (buffer[1] == (byte)'a') && (buffer[2] == (byte)'V') && (buffer[3] == (byte)'E'))
//							{
//								// it's pave
//								startWriteIndex = 64;
//							}
////							System.out.println("Read " + new String(buffer));
////							System.out.println("Write byte from " + startWriteIndex + " to " + (bytesRead - startWriteIndex));
//						    output.write(buffer, startWriteIndex, bytesRead - startWriteIndex);
//						}
					}
					catch(Exception exc)
					{
						exc.printStackTrace();
						isRunning = false;
					}
					
				}
			}
			catch (SocketException socketException)
			{
				Log.e(TAG, "SocketException() thrown, proxy client has probably closed. This can exit harmlessly");
			}
			catch (Exception e)
			{
				Log.e(TAG, "Exception thrown from streaming task:");
				Log.e(TAG, e.getClass().getName() + " : " + e.getLocalizedMessage());
				e.printStackTrace();
			}

			// Cleanup
			try
			{
				if (output != null)
				{
					output.close();
				}
				client.close();
			}
			catch (IOException e)
			{
				Log.e(TAG, "IOException while cleaning up streaming task:");
				Log.e(TAG, e.getClass().getName() + " : " + e.getLocalizedMessage());
				e.printStackTrace();
			}

			return 1;
		}

		private void discardPave(byte[] buffer)
		{
			if ((buffer[0] == (byte)'P') && (buffer[1] == (byte)'a') && (buffer[2] == (byte)'V') && (buffer[3] == (byte)'E'))
			{
				int version = bytesToInt(buffer[4]);
				int codec = bytesToInt(buffer[5]);
				int headerSize = bytesToInt(buffer[6], buffer[7]);
				int payloadSize = bytesToInt(buffer[8], buffer[9], buffer[10], buffer[11]);
				
				System.out.println("Pave decoding, header-size=" + headerSize);
			}
			else
				System.out.println("No Pave present");
		}
		
		public int bytesToInt(byte b1, byte b2, byte b3, byte b4)
		{
			int value = (0xFF & b1) << 24;
			value |= (0xFF & b2) << 16;
			value |= (0xFF & b3) << 8;
			value |= (0xFF & b4);

			return value;
		}
		
		private int bytesToInt(byte b1, byte b2)
		{
			int value = (0xFF & b1) << 8;
			value |= (0xFF & b2);

			return value;
		}
		
		private int bytesToInt(byte b1)
		{
			int value = (0xFF & b1) << 8;
			
			return value;
		}
		
		private String readTextStreamAvailable(InputStream inputStream) throws IOException
		{
			byte[] buffer = new byte[4096];
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream(4096);

			// Do the first byte via a blocking read
			outputStream.write(inputStream.read());

			// Slurp the rest
			int available = inputStream.available();
			while (available > 0)
			{
				int cbToRead = Math.min(buffer.length, available);
				int cbRead = inputStream.read(buffer, 0, cbToRead);
				if (cbRead <= 0)
				{
					throw new IOException("Unexpected end of stream");
				}
				outputStream.write(buffer, 0, cbRead);
				available -= cbRead;
			}
			return new String(outputStream.toByteArray());
		}
	}
}