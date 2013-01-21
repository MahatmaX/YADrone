package de.yadrone.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.shigeodayo.ardrone.ARDrone;
import com.shigeodayo.ardrone.navdata.javadrone.NavData;
import com.shigeodayo.ardrone.navdata.javadrone.NavDataListener;

public class NavDataActivity extends Activity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navdata);        
    }
    
    public void onResume()
    {
    	super.onResume();
    	final TextView text = (TextView)findViewById(R.id.text_navdata);
    	
    	YADroneApplication app = (YADroneApplication)getApplication();
    	ARDrone drone = app.getARDrone();
    	
    	drone.addNavDataListener(new NavDataListener() {
    		public void navDataUpdated(final NavData navData)
			{
				runOnUiThread(new Runnable() {
					public void run()
					{
						text.setText(navData + "");
					}
				});
			}
		});
    }
    
    public void onPause()
    {
    	super.onPause();
    	YADroneApplication app = (YADroneApplication)getApplication();
    	ARDrone drone = app.getARDrone();
    	
    	drone.removeNavDataListener();
    }
    
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
