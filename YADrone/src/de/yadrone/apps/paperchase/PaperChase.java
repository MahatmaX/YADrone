package de.yadrone.apps.paperchase;

import de.yadrone.base.ARDrone;
import de.yadrone.base.IARDrone;
import de.yadrone.base.command.VideoChannel;
import de.yadrone.base.command.VideoCodec;

public class PaperChase 
{
	
	public final static int IMAGE_WIDTH = 640; // 640 or 1280
	public final static int IMAGE_HEIGHT = 360; // 360 or 720
	
	public final static int TOLERANCE = 40;
	
	
	private IARDrone drone = null;
	
	
	public PaperChase()
	{
		drone = new ARDrone();
		drone.start();
		drone.getCommandManager().setVideoChannel(VideoChannel.VERT);
		
		PaperChaseController controller = new PaperChaseController(drone);
		PaperChaseGUI gui = new PaperChaseGUI(drone);
		
		QRCodeScanner scanner = new QRCodeScanner();
		scanner.addListener(gui);
		scanner.addListener(controller);
		
		drone.getVideoManager().addImageListener(gui);
		drone.getVideoManager().addImageListener(scanner);
		
		controller.start();
	}
	
	public static void main(String[] args)
	{
		new PaperChase();
	}
	
}
