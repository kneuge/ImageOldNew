package imageoldnew.fh.de.imageoldnew;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;

import java.io.BufferedInputStream;
import java.io.InputStream;


public class OldNewActivity extends ActionBarActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oldnew);

        SeekBar bar = (SeekBar) findViewById(R.id.seekBar);
        final ImageView imageView2 = (ImageView) findViewById(R.id.imageView2);

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


        try {
            InputStream inputStream = this.getResources().openRawResource(R.drawable.bahnhof);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            Bitmap bitmap = BitmapFactory.decodeStream(bufferedInputStream);

            int w = bitmap.getWidth();
            int h = bitmap.getHeight();
            Bitmap newBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            int[][] filter = new int[][]{
                    {0, 1, 0},
                    {1, -4, 1},
                    {0, 1, 0}
            };
            int offset = (filter.length - 1) / 2;
            for (int y = offset; y < h - offset; y++) {
                for (int x = offset; x < w - offset; x++) {
                    int pixel_total = 0;
                    int filterTotal = 0;
                    for (int fx = 0; fx < filter.length; fx++) {
                        for (int fy = 0; fy < filter.length; fy++) {

                            int tmp_px = bitmap.getPixel(x - offset + fx, y - offset + fy);
                            int filter_value = filter[fx][fy];

                            //int a = (tmp_px >> 24 & 0xFF);
                            int r = (tmp_px >> 16 & 0xFF);
                            int g = (tmp_px >> 8 & 0xFF);
                            int b = (tmp_px & 0xFF);

                            pixel_total += ((r + g + b) / 3) * filter_value;
                            filterTotal += Math.abs(filter_value);
                        }
                    }
                    pixel_total = pixel_total / filterTotal;
                    int newPx = (255 << 24) + (pixel_total << 16) + (pixel_total << 8) + pixel_total;
                    //if (x == 0)
                    //    Log.d("blau", "" + (newPx & 0xFFFFFF));
                    newBitmap.setPixel(x, y, newPx);
                }
            }

            ImageView imageView1 = (ImageView) findViewById(R.id.imageView1);
            imageView1.setImageBitmap(newBitmap);

        } catch (Exception ex) {
            Log.e("Fehler", ex.getMessage());
        }

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
