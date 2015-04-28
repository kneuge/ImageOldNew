package imageoldnew.fh.de.imageoldnew;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.SeekBar;

import java.io.BufferedInputStream;
import java.io.InputStream;


public class OldNewActivity extends ActionBarActivity {

    private void BoxFilter(int[][] filter, Bitmap newBitmap, Bitmap bitmap) {
        int offset = (filter.length - 1) / 2;
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

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
                        filterTotal += filter_value;
                    }
                }
                if (filterTotal == 0) filterTotal = 1;
                pixel_total = Math.max(0, Math.min(255, pixel_total / filterTotal));
                int newPx = (255 << 24) + (pixel_total << 16) + (pixel_total << 8) + pixel_total;
                //if (x == 0)
                //    Log.d("blau", "" + (newPx & 0xFFFFFF));
                newBitmap.setPixel(x, y, newPx);
            }
        }
    }

    private void BinAndFilter(boolean[][] filter, Bitmap newBitmap, Bitmap bitmap, int limit) {
        int offset = (filter.length - 1) / 2;
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        for (int y = offset; y < h - offset; y++) {
            for (int x = offset; x < w - offset; x++) {
                boolean keepPx = true;
                for (int fx = 0; fx < filter.length; fx++) {
                    for (int fy = 0; fy < filter.length; fy++) {
                        boolean filter_value = filter[fx][fy];
                        if (filter_value) {
                            int tmp_px = bitmap.getPixel(x - offset + fx, y - offset + fy);

                            //int a = (tmp_px >> 24 & 0xFF);
                            int r = (tmp_px >> 16 & 0xFF);
                            int g = (tmp_px >> 8 & 0xFF);
                            int b = (tmp_px & 0xFF);

                            if (((r + g + b) / 3) < limit)
                                keepPx = false;
                        }
                    }
                }
                int newPx = 0;
                if (keepPx)
                    newPx = (255 << 24) + (10 << 16) + (10 << 8) + 10;
                newBitmap.setPixel(x, y, newPx);
            }
        }
    }

    private void MinFilter(Bitmap newBitmap, Bitmap bitmap, int limit) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int tmp_px = bitmap.getPixel(x, y);

                //int a = (tmp_px >> 24 & 0xFF);
                int r = (tmp_px >> 16 & 0xFF);
                int g = (tmp_px >> 8 & 0xFF);
                int b = (tmp_px & 0xFF);
                tmp_px = (r + g + b) / 3;

                if (tmp_px > limit)
                    tmp_px = 255;
                else
                    tmp_px = 0;

                int newPx = (tmp_px << 24) + (tmp_px << 16) + (tmp_px << 8) + tmp_px;
                newBitmap.setPixel(x, y, newPx);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oldnew);

        try {
            InputStream inputStream = this.getResources().openRawResource(R.drawable.reinoldi2);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            Bitmap bitmap = BitmapFactory.decodeStream(bufferedInputStream);

            int w = bitmap.getWidth();
            int h = bitmap.getHeight();


            /*
            int[][] filter = new int[][]{
                    {1, 1, 1},
                    {0, 0, 0},
                    {-1, -1, -1}
            };
            */

            /*
            int[][] filter = new int[][]{
                    {-1, 0, 1},
                    {-1, 0, 1},
                    {-1, 0, 1}
            };
            */


            /*int[][] filter = new int[][]{
                    {0, 1, 0},
                    {1, -4, 1},
                    {0, 1, 0}
            };*/

            //Step1
            int[][] filter = new int[][]{
                    {0, 1, 0},
                    {1, -4, 1},
                    {0, 1, 0}
            };
            Bitmap step1Bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            this.BoxFilter(filter, step1Bitmap, bitmap);


            //Step2
            filter = new int[][]{
                    {1, 1, 1},
                    {1, 0, 1},
                    {1, 1, 1}
            };
            Bitmap step2Bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            this.BoxFilter(filter, step2Bitmap, step1Bitmap);

            /*boolean[][] bfilter = new boolean[][]{
                    {true, true, true},
                    {true, false, true},
                    {true, true, true}
            };
            Bitmap step3Bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            this.BinAndFilter(bfilter, step3Bitmap, step2Bitmap, 5);*/

            //Step3
            boolean[][] bfilter = new boolean[][]{
                    {false, false, false, false, false},
                    {false, false, false, false, false},
                    {true, true, true, false, false},
                    {false, true, false, false, false},
                    {false, false, false, false, false}
            };
            Bitmap step4Bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            this.BinAndFilter(bfilter, step4Bitmap, step2Bitmap, 5);

            ImageView imageView1 = (ImageView) findViewById(R.id.imageView1);
            imageView1.setImageBitmap(step4Bitmap);

        } catch (NullPointerException ex) {
            Log.e("Fehler", ex.getMessage());
        }

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
