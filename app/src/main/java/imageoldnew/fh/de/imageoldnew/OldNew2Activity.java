package imageoldnew.fh.de.imageoldnew;

import android.graphics.Bitmap;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.SeekBar;
import java.io.InputStream;


public class OldNew2Activity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oldnew2);

        try {
            InputStream inputStream = this.getResources().openRawResource(+R.drawable.f207);
            ImageFilter imageFilter = new ImageFilter(inputStream);
            Bitmap bitmap = imageFilter.getFiltered(100);
            ImageView imageView1 = (ImageView) findViewById(R.id.imageView1b);
            imageView1.setImageBitmap(bitmap);

        } catch (NullPointerException ex) {
            Log.e("Fehler", ex.toString());
        }

        SeekBar bar = (SeekBar) findViewById(R.id.seekBarb);
        ImageView imageView1 = (ImageView) findViewById(R.id.imageView1b);
        final ImageView imageView2 = (ImageView) findViewById(R.id.imageView2b);

        bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                imageView2.setAlpha(i / 100f);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu, menu);
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