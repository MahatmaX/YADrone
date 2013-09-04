package de.yadrone.android.videodeprecated;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.VideoView;
import de.yadrone.android.ControlActivity;
import de.yadrone.android.MainActivity;
import de.yadrone.android.NavDataActivity;
import de.yadrone.android.R;
import de.yadrone.android.YADroneApplication;
import de.yadrone.android.R.id;
import de.yadrone.android.R.layout;
import de.yadrone.android.R.menu;
import de.yadrone.base.IARDrone;
import de.yadrone.base.command.CommandManager;

public class VideoActivity extends Activity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        YADroneApplication app = (YADroneApplication) getApplication();
        final IARDrone drone = app.getARDrone();
        final CommandManager cmdManager = drone.getCommandManager();
        
        Thread t = new Thread(new Runnable() {

			@Override
			public void run()
			{
				new StreamProxy(cmdManager).start();
				
		        runOnUiThread(new Runnable() {

					@Override
					public void run()
					{
						VideoView video = (VideoView) findViewById(R.id.video);
				        String url = "http://127.0.0.1:8888";
				        Uri uri = Uri.parse(url); 
				        
				        video.setVideoURI(uri);
				        video.start();
						
//						MediaPlayer urlPlayer = MediaPlayer.create(VideoActivity.this, Uri.parse("tcp://192.168.1.1:5555"));
					}
					
				});		        
			}
        	
        });
        t.start();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_video, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
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
	    	case R.id.menuitem_navdata:
	    		i = new Intent(this, NavDataActivity.class);
	    		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    		startActivity(i);
		        return true;
		    default:
		        return super.onOptionsItemSelected(item);
	    }

    }

}
