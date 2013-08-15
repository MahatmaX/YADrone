/*
 *
  Copyright (c) <2011>, <Shigeo Yoshida>
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
The names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, 
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package de.yadrone.base.manager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

// TODO: I believe IOExceptions should be thrown all the way up,
// but it means all apps should handle them
// also need to think about effect if not connected (currently return null or ignore)
public abstract class AbstractTCPManager implements Runnable {

	protected InetAddress inetaddr = null;
	protected Socket socket = null;
	protected boolean connected = false;
	protected Thread thread;

	public AbstractTCPManager(InetAddress inetaddr) {
		this.inetaddr = inetaddr;
	}

	public boolean connect(int port) {
		try {
			socket = new Socket(inetaddr, port);
			socket.setSoTimeout(3000);
		} catch (IOException e) {
			e.printStackTrace();
			connected = false;
			return false;
		}
		connected = true;
		return true;
	}

	public boolean isConnected() {
		return connected;
	}

	public void close() {
		try {
			connected = false;
			if (socket != null) {
				socket.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void ticklePort(int port) {
		byte[] buf = { 0x01, 0x00, 0x00, 0x00 };
		try {
			if (socket != null) {
				OutputStream os = socket.getOutputStream();
				os.write(buf);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected InputStream getInputStream() {
		try {
			if (socket != null) {
				return socket.getInputStream();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void start() {
		if (thread == null || thread.getState() == Thread.State.TERMINATED) {
			String name = getClass().getSimpleName();
			thread = new Thread(this, name);
			thread.start();
		}
	}

}
