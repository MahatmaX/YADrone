package de.yadrone.apps.controlcenter.plugins.video;


import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.MediaListenerAdapter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.mediatool.event.ICloseEvent;
import com.xuggle.mediatool.event.IVideoPictureEvent;
import com.xuggle.xuggler.ICodec;

import de.yadrone.apps.controlcenter.CCPropertyManager;
import de.yadrone.apps.controlcenter.ICCPlugin;
import de.yadrone.base.IARDrone;
import de.yadrone.base.command.VideoCodec;
import de.yadrone.base.video.ImageListener;

public class VideoPanel extends JPanel implements ICCPlugin, ImageListener
{
	public final static String FORMAT_MP4 = "MPEG-4";
	public final static String FORMAT_H264 = "H.264";
	public final static String FORMAT_MP4_USB = "MPEG-4 + USB";
	
	private VideoCanvas video;
	private IARDrone drone;
	
	private JButton recordButton;
	private JButton recordPathChooserButton;
	private JButton playButton;
	private JButton playFileChooserButton;
	private JComboBox recordFormatComboBox;
	private JTextField recordPathLocation;
	private JTextField playFileLocation;
	
	private CCPropertyManager props;
	
	private int imageWidth;
	private int imageHeight;
	private boolean doScaleImage;
	
	public VideoPanel()
	{
		super(new GridBagLayout());
		
		props = CCPropertyManager.getInstance();
		doScaleImage = props.isScaleVideo();
	}
	
	public void activate(IARDrone drone)
	{
		this.drone = drone;
		init();
		drone.getVideoManager().addImageListener(this);
	}

	private void init() 
	{
		video = new VideoCanvas(drone);

		// record -----------------------------------------------------------------------
		
		recordFormatComboBox = new JComboBox(new String[] {FORMAT_MP4, FORMAT_H264}); // + FORMAT_MP4_USB, 
		recordFormatComboBox.setSelectedItem(props.getVideoFormat());
		recordFormatComboBox.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) 
			{
				props.setVideoFormat(recordFormatComboBox.getSelectedItem()+"");
				
				if (recordFormatComboBox.getSelectedItem().equals(FORMAT_MP4))
					drone.getCommandManager().setVideoCodec(VideoCodec.MP4_360P);
				else if (recordFormatComboBox.getSelectedItem().equals(FORMAT_H264))
					drone.getCommandManager().setVideoCodec(VideoCodec.H264_720P);
				else if (recordFormatComboBox.getSelectedItem().equals(FORMAT_MP4_USB))
					drone.getCommandManager().setVideoCodec(VideoCodec.MP4_360P_H264_360P);
				
				drone.getVideoManager().reinitialize();
			}
		});
		
		if (props.getVideoFormat().equals(FORMAT_MP4))
		{
			drone.getCommandManager().setVideoCodec(VideoCodec.MP4_360P);
		}
		else if (props.getVideoFormat().equals(FORMAT_H264))
		{
			drone.getCommandManager().setVideoCodec(VideoCodec.H264_720P);
		}
//		else if (props.getVideoFormat().equals(FORMAT_MP4_USB))
//		{
//			drone.getCommandManager().setVideoCodec(VideoCodec.MP4_360P_H264_360P);
//		}
		
		// scale -----------------------------------------------------------------------
		
		final JCheckBox scaleImage = new JCheckBox("Scale image to fit window", props.isScaleVideo());
		ActionListener scaleListener = new ActionListener() {
			public void actionPerformed(ActionEvent event)
			{
				doScaleImage = scaleImage.isSelected();
				props.setScaleVideo(scaleImage.isSelected());
			}			
		};
		scaleImage.addActionListener(scaleListener);
		
		JPanel scalePanel = new JPanel(new GridBagLayout());
		scalePanel.add(scaleImage, new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0), 0, 0));
	    
		// record-----------------------------------------------------------------------
		
		recordButton = new JButton("Record");
		recordButton.addActionListener(new ActionListener() 
		{
			private IMediaWriter writer;
			private long startTime;
			
			private ImageListener imageListener = new ImageListener() {
				public void imageUpdated(BufferedImage image) 
				{
					try
					{
						writer.encodeVideo(0, image, System.nanoTime() - startTime, TimeUnit.NANOSECONDS);
					}
					catch(Exception exc)
					{
						exc.printStackTrace();
					}
				}
			};
			
			public void actionPerformed(ActionEvent e) 
			{
				if (recordButton.getText().equals("Record"))
				{
					boolean isMp4Usb = props.getVideoFormat().equals(FORMAT_MP4_USB);
					if (isMp4Usb)
					{
						drone.getCommandManager().setVideoCodec(VideoCodec.MP4_360P_H264_720P);
						drone.getCommandManager().setVideoOnUsb(true);
					}
					boolean isMp4 = props.getVideoFormat().equals(FORMAT_MP4) || isMp4Usb; // not working due to error in h.264 codec ... props.getVideoFormat().equals(FORMAT_MP4) || props.getVideoFormat().equals(FORMAT_MP4_H264);
					
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
					String fileName = recordPathLocation.getText() + File.separatorChar + sdf.format(Calendar.getInstance().getTime()) + (isMp4 ? ".mp4" : ".mp4");
					System.out.println("VideoPanel: Start recording to " + fileName);
					
					writer = ToolFactory.makeWriter(fileName);
					writer.addVideoStream(0,  0, isMp4 ? ICodec.ID.CODEC_ID_MPEG4 : ICodec.ID.CODEC_ID_H264, imageWidth, imageHeight);
					
					startTime = System.nanoTime();
					
					drone.getVideoManager().addImageListener(imageListener);
					
					recordButton.setText("Stop");
					recordPathChooserButton.setEnabled(false);
					recordPathLocation.setEnabled(false);
					recordFormatComboBox.setEnabled(false);
					
					playButton.setEnabled(false);
				}
				else // do stop
				{
					if (props.getVideoFormat().equals(FORMAT_MP4_USB))
					{
//						drone.getCommandManager().setVideoCodec(VideoCodec.MP4_360P); // reset video codec, just to make sure: Attention: what if the user chose H.264 codec ? Do I really need this ?
						drone.getCommandManager().setVideoOnUsb(false);
					}
					
					drone.getVideoManager().removeImageListener(imageListener);
					
					recordButton.setText("Record");
					recordPathChooserButton.setEnabled(true);
					recordPathLocation.setEnabled(true);
					recordFormatComboBox.setEnabled(true);
					
					playButton.setEnabled(true);
					
					try
					{
						writer.close();
					}
					catch(Throwable t)
					{
						t.printStackTrace();
					}
				}
			}
		});
		recordPathChooserButton = new JButton("...");
		recordPathChooserButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser(props.getVideoStoragePath());
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				
				int returnVal = fc.showOpenDialog(VideoPanel.this);
		        if (returnVal == JFileChooser.APPROVE_OPTION)
		        {
		        	String path = fc.getSelectedFile().getPath();
		        	props.setVideoStoragePath(path);
		        	recordPathLocation.setText(path);
		        }
			}
		});
		recordPathLocation = new JTextField(props.getVideoStoragePath());

		JPanel recordPanel = new JPanel(new GridBagLayout());
		recordPanel.setBorder(BorderFactory.createTitledBorder("Record"));
		
		recordPanel.add(recordButton, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.VERTICAL, new Insets(0,0,0,0), 0, 0));
		recordPanel.add(recordPathLocation, new GridBagConstraints(1, 0, 1, 1, 1, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0, 0));
		recordPanel.add(recordPathChooserButton, new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.VERTICAL, new Insets(0,0,0,0), 0, 0));
		recordPanel.add(recordFormatComboBox, new GridBagConstraints(3, 0, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(0,0,0,0), 0, 0));
		
		// play -----------------------------------------------------------------------
		
		playButton = new JButton("Play");
		playButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) 
			{
				if (playButton.getText().equals("Play"))
				{
					playButton.setText("Stop");
					drone.getVideoManager().removeImageListener(VideoPanel.this); // remove listener and add it again once stop is pressed
					
					new Thread(new Runnable() {
						public void run() {
							String fileName = recordPathLocation.getText() + File.separatorChar + playFileLocation.getText();
							System.out.println("Play " + fileName);
							
							IMediaReader reader = ToolFactory.makeReader(fileName);
							reader.setBufferedImageTypeToGenerate(BufferedImage.TYPE_3BYTE_BGR);
							MediaListenerAdapter adapter = new MediaListenerAdapter() {
								
								public void onVideoPicture(final IVideoPictureEvent event) 
								{
									SwingUtilities.invokeLater(new Runnable() {
										public void run() {
											video.imageUpdated(event.getImage());
										}
									});
								}
								
								public void onClose(ICloseEvent event)
								{
									playButton.doClick();
								}
							};
							
							reader.addListener(adapter);
							while (reader.readPacket() == null); // start reading
						}
					}).start();
					
					recordButton.setEnabled(false);
				}
				else
				{
					playButton.setText("Play");
					drone.getVideoManager().addImageListener(VideoPanel.this);
					
					recordButton.setEnabled(true);
				}
			}
		});
		playFileChooserButton = new JButton("...");
		playFileChooserButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				JFileChooser fc = new JFileChooser(props.getVideoStoragePath());
				fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				
				int returnVal = fc.showOpenDialog(VideoPanel.this);
		        if (returnVal == JFileChooser.APPROVE_OPTION)
		        {
		        	String path = fc.getSelectedFile().getName();
		        	props.setVideoPlayFile(path);
		        	playFileLocation.setText(path);
		        }
			}
		});
		playFileLocation = new JTextField(props.getVideoPlayFile());

		JPanel playPanel = new JPanel(new GridBagLayout());
		playPanel.setBorder(BorderFactory.createTitledBorder("Play"));
		
		playPanel.add(playButton, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.VERTICAL, new Insets(0,0,0,0), 0, 0));
		playPanel.add(playFileLocation, new GridBagConstraints(1, 0, 1, 1, 1, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0, 0));
		playPanel.add(playFileChooserButton, new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.VERTICAL, new Insets(0,0,0,0), 0, 0));
		
		// options -----------------------------------------------------------------------
		
		JPanel options = new JPanel(new GridBagLayout());
		options.add(scalePanel, new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0), 0, 0));
		options.add(recordPanel, new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0, 0));
		options.add(playPanel, new GridBagConstraints(0, 2, 1, 1, 1, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0, 0));
		
		Dimension dim = new Dimension(640, 100);
		options.setMinimumSize(dim);
		options.setMaximumSize(dim);
		options.setSize(dim);
		
		add(video, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0, 0));
		add(options, new GridBagConstraints(0, 1, 1, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(5,0,0,0), 0, 0));
		
	}

	public void deactivate()
	{
		drone.getVideoManager().removeImageListener(this);
	}

	public String getTitle()
	{
		return "Video";
	}
	
	public String getDescription()
	{
		return "Displays the current video stream";
	}

	public boolean isVisual()
	{
		return true;
	}

	public Dimension getScreenSize()
	{
		return new Dimension(650, 475);
	}
	
	public Point getScreenLocation()
	{
		return new Point(0, 0);
	}

	public JPanel getPanel()
	{
		return this;
	}

	@Override
	public void imageUpdated(BufferedImage image)
	{
		imageWidth = image.getWidth();
		imageHeight = image.getHeight();
		
		if (doScaleImage)
		{
			BufferedImage newImage = new BufferedImage(video.getWidth(), video.getHeight(), BufferedImage.TYPE_INT_RGB);

			Graphics g = newImage.createGraphics();
			g.drawImage(image, 0, 0, video.getWidth(), video.getHeight(), null);
			g.dispose();
			
			image = newImage;
		}
		video.imageUpdated(image);
	}
}
