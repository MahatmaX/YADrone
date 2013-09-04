package de.yadrone.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import de.yadrone.base.IARDrone;
import de.yadrone.base.navdata.AttitudeListener;

public class NavDataActivity extends Activity implements AttitudeListener {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navdata);        
    }
    
    public void onResume()
    {
    	super.onResume();
    	YADroneApplication app = (YADroneApplication)getApplication();
    	IARDrone drone = app.getARDrone();
    	
    	drone.getNavDataManager().addAttitudeListener(this);
    }
    
    public void onPause()
    {
    	super.onPause();
    	YADroneApplication app = (YADroneApplication)getApplication();
    	IARDrone drone = app.getARDrone();
    	
    	drone.getNavDataManager().removeAttitudeListener(this);
    }
    
    public void attitudeUpdated(final float pitch, final float roll, final float yaw)
	{
    	final TextView text = (TextView)findViewById(R.id.text_navdata);
    	
		runOnUiThread(new Runnable() {
			public void run()
			{
				text.setText("Pitch: " + pitch + " Roll: " + roll + " Yaw: " + yaw);
			}
		});
	}
	
	public void attitudeUpdated(float arg0, float arg1) { }
	public void windCompensation(float arg0, float arg1) { }
	
    public boolean onCreateOptionsMenu(Menu menu) 
	{
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu_navdata, menu);	    
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
	    	case R.id.menuitem_main:
	    		i = new Intent(this, MainActivity.class);
	    		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    		startActivity(i);
		        return true;
//	    	case R.id.menuitem_video:
//	    		i = new Intent(this, VideoActivity.class);
//	    		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//	    		startActivity(i);
//		        return true;
		    default:
		        return super.onOptionsItemSelected(item);
	    }
	}

    
}
