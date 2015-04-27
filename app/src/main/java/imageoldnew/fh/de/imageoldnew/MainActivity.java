package imageoldnew.fh.de.imageoldnew;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.hardware.Camera;
import android.widget.Button;
import android.widget.FrameLayout;
import android.util.Log;


import android.view.View;
import android.widget.ImageButton;


public class MainActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button startcam = (Button)findViewById(R.id.bStartCamera);
        startcam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Start Camera Activity
                Intent myIntent = new Intent(getApplicationContext(), CameraActivity.class);
                startActivity(myIntent);
            }
        });

        Button startoldnew = (Button)findViewById(R.id.bStartOldNew);
        startoldnew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Start Camera Activity
                Intent myIntent = new Intent(getApplicationContext(), OldNewActivity.class);
                startActivity(myIntent);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
