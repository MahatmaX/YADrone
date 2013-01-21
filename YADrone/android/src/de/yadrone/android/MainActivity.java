package de.yadrone.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.shigeodayo.ardrone.ARDrone;

public class MainActivity extends Activity {

	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        final TextView text = (TextView)findViewById(R.id.text_init);
		text.append("\nConnected to " + wifi.getConnectionInfo().getSSID());
		
		initialize();
		
    }
    
    private void initialize()
    {
    	final TextView text = (TextView)findViewById(R.id.text_init);
    	
    	YADroneApplication app = (YADroneApplication)getApplication();
    	ARDrone drone = app.getARDrone();
    	
		try
		{
			text.append("\n\nConnect Drone Controller\n");
			drone.connect();
			text.append("Connect Drone Navdata Socket\n");
			drone.connectNav();
//			text.append("Connect Drone Video Socket\n");
//			ardrone.connectVideo();
			text.append("Start Drone\n");
			drone.start();
			
			text.append("Set max. Altitude to 5m (15ft)\n\n");
			drone.setMaxAltitude(5000); // max 5 meters
		}
		catch(Exception exc)
		{
			exc.printStackTrace();
			
			if (drone != null)
				drone.disconnect();
		}
	}

    /**
     * Upon pressing the BACK-button, the user has to confirm the connection to the drone is taken down.
     */
    public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
		{
			new AlertDialog.Builder(this).setMessage("Upon exiting, drone will be disconnected !").setTitle("Exit YADrone ?").setCancelable(false).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton)
				{
					YADroneApplication app = (YADroneApplication)getApplication();
			    	ARDrone drone = app.getARDrone();
			    	drone.disconnect();
			    	
					finish();
				}
			}).setNeutralButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton)
				{
					// User selected Cancel, nothing to do here.
				}
			}).show();

			return true;
		}

		return super.onKeyDown(keyCode, event);
	}
    
    public boolean onCreateOptionsMenu(Menu menu) 
	{
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu_main, menu);	    
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		Intent i;
	    switch (item.getItemId()) 
	    {
	    	case R.id.menuitem_control:
		    	i = new Intent(this, ControlActivity.class);
		    	i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		    	startActivity(i);
		        return true;
	    	case R.id.menuitem_navdata:
	    		i = new Intent(this, NavDataActivity.class);
	    		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    		startActivity(i);
		        return true;
	    	case R.id.menuitem_video:
	    		i = new Intent(this, VideoActivity.class);
	    		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    		startActivity(i);
		        return true;
		    default:
		        return super.onOptionsItemSelected(item);
	    }
	}

    
}
