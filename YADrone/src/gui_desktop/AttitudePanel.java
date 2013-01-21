package gui_desktop;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme3.app.SimpleApplication;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeCanvasContext;

public class AttitudePanel extends SimpleApplication
{
	private JmeCanvasContext ctx;
	
	private Spatial plane;
	
	private boolean isStarted,doUpdate = false;
	private float pitch, roll, yaw;

	public AttitudePanel()
	{
		super();
		
		setShowSettings(false);
		setDisplayFps(false);
        setDisplayStatView(false);
        
        setPauseOnLostFocus(false);
        
        AppSettings settings = new AppSettings(true);
		settings.setUseInput(false);
		setSettings(settings);
		
		createCanvas();
		ctx = (JmeCanvasContext) getContext();
		ctx.setSystemListener(this);
		
		ctx.getCanvas().setBackground(Color.BLACK);
		ctx.getCanvas().setFocusable(false);
		ctx.getCanvas().setSize(new Dimension(320, 240));
		
		ctx.getCanvas().setFocusable(false);
	}

	public Canvas getPane()
	{
		return ctx.getCanvas();
	}
	
	public void setSize(Dimension size)
	{
		getPane().setSize(size);
	}

	public void simpleInitApp() {
		
		Logger.getLogger("").setLevel(Level.SEVERE);
		
		makeScene();
    }
	
	private void makeScene()
	{
		DirectionalLight sun = new DirectionalLight();
		sun.setColor(ColorRGBA.White);
		sun.setDirection(new Vector3f(-.5f,-.5f,-.5f).normalizeLocal());
		rootNode.addLight(sun);
		
		AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(1.3f));
        rootNode.addLight(al);
        
		String file ="gui_desktop/img/plane.j3o";
		
		plane = assetManager.loadModel(file);
		plane.setLocalScale(0.75f);
		
//		plane = new Geometry("blue cube", new Box(Vector3f.ZERO, 1, 1, 1));
//        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
//        mat.setColor("Color", ColorRGBA.Blue);
//        plane.setMaterial(mat);
        
		rootNode.attachChild(plane);		
	}
 
    /* This is the update loop */
    public void simpleUpdate(float tpf) {
      
    	synchronized(this)
    	{
    		if (doUpdate)
    		{
    			float[] angles = {(float)Math.toRadians(-pitch), (float)Math.toRadians(-yaw-180), (float)Math.toRadians(roll)};
    			Quaternion quat = plane.getLocalRotation().fromAngles(angles);

    			plane.setLocalRotation(quat);
    			
    			doUpdate = false;
    		}
    	}   
    }
    
    public void setAttitude(float pitch, float roll, float yaw, int altitude)
	{
		if (!isStarted)
		{
			isStarted = true;
			startCanvas();
			
			// The camera's default behaviour in SimpleApplication is to capture the mouse, which doesn't make sense in a Swing window. 
			// You have to deactivate and replace this behaviour by flyCam.setDragToRotate(true); 

//			flyCam.setDragToRotate(true);
		}
		
		synchronized(this)
		{
			this.pitch = pitch;
			this.roll = roll;
			this.yaw = yaw;
			this.doUpdate = true;
		}
	}
        
    public static void main(String[] args)
    {
    	AttitudePanel panel = new AttitudePanel();
    	panel.setAttitude(45f, 45f, 45f, 0);
    }
    
}
