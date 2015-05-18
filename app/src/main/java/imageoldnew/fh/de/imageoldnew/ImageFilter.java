package imageoldnew.fh.de.imageoldnew;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.InputStream;

/**
 * Created by Marlin on 18.05.2015.
 */
public class ImageFilter {

    public static int[][] BoxFilter(int[][] filter, int[][] bitmap) {
        int offset = (filter.length - 1) / 2;
        int w = bitmap.length;
        int h = bitmap[0].length;
        int[][] newBitmap = new int[w][h];

        for (int y = offset; y < h - offset; y++) {
            for (int x = offset; x < w - offset; x++) {
                int pixel_total = 0;
                int filterTotal = 0;
                for (int fx = 0; fx < filter.length; fx++) {
                    for (int fy = 0; fy < filter.length; fy++) {

                        int tmp_px = bitmap[x - offset + fx][y - offset + fy];
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
                newBitmap[x][y] = newPx;
            }
        }
        return newBitmap;
    }

    public static int[][] BinAndFilter(boolean[][] filter, int[][] bitmap, int limit) {
        int offset = (filter.length - 1) / 2;
        int w = bitmap.length;
        int h = bitmap[0].length;
        int[][] newBitmap = new int[w][h];

        for (int y = offset; y < h - offset; y++) {
            for (int x = offset; x < w - offset; x++) {
                boolean keepPx = true;
                for (int fx = 0; fx < filter.length; fx++) {
                    for (int fy = 0; fy < filter.length; fy++) {
                        boolean filter_value = filter[fx][fy];
                        if (filter_value) {
                            int tmp_px = bitmap[x - offset + fx][y - offset + fy];

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
                newBitmap[x][y] = newPx;
            }
        }
        return newBitmap;
    }


    private int w = 0;
    private int h = 0;
    int[][] step0Bitmap;

    public ImageFilter(InputStream inputStream) {
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
        Bitmap bitmap = BitmapFactory.decodeStream(bufferedInputStream);

        this.w = bitmap.getWidth();
        this.h = bitmap.getHeight();

        this.step0Bitmap = new int[w][h];

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                this.step0Bitmap[x][y] = bitmap.getPixel(x, y);
            }
        }
    }

    public Bitmap getFiltered() {
        //Step1
        int[][] filter = new int[][]{
                {0, 1, 0},
                {1, -4, 1},
                {0, 1, 0}
        };
        int[][] tmp = ImageFilter.BoxFilter(filter, this.step0Bitmap);

        //Step2
        filter = new int[][]{
                {1, 1, 1},
                {1, 0, 1},
                {1, 1, 1}
        };
        tmp = ImageFilter.BoxFilter(filter, tmp);

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
        tmp = ImageFilter.BinAndFilter(bfilter, tmp, 5);

        Bitmap resultBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int tmp_px1 = tmp[x][y];
                int tmp_px2 = this.step0Bitmap[x][y] & 0x00FFFFFF;
                int newpx = 0;
                if (((tmp_px1 & 0xFF000000) >>> 24) > 127) {
                    newpx = tmp_px2 | 0xFF000000;
                } else {
                    newpx = tmp_px2 | 0xAF000000;
                }
                resultBitmap.setPixel(x, y, newpx);
            }
        }

        return resultBitmap;
    }

}
