package imageoldnew.fh.de.imageoldnew;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class CameraActivity extends ActionBarActivity {

    private Camera mCamera = null;
    private CameraView mCameraView = null;
    private int[] photos={R.drawable.f244,R.drawable.f668,R.drawable.f261,R.drawable.f404,R.drawable.f207,R.drawable.f402};
    private static int currentPhoto=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        setImage((currentPhoto) % photos.length);

        //Init Camera
        try {
            mCamera = Camera.open();//you can use open(int) to use different cameras

            if (mCamera != null) {
                mCameraView = new CameraView(this, mCamera);//create a SurfaceView to show camera data
                FrameLayout camera_view = (FrameLayout) findViewById(R.id.camera_view);
                camera_view.addView(mCameraView);//add the SurfaceView to the layout


            } else {
                throw new Exception("No camera found.");
            }
        } catch (Exception e) {
            Log.d("ERROR", "Failed to get camera: " + e.getMessage());

            //Show alert
            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            builder1.setMessage("Camera not working.");
            builder1.setCancelable(true);
            builder1.setPositiveButton("Okay",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            System.exit(0);
                        }
                    });
            AlertDialog alert11 = builder1.create();
            alert11.show();
        }

        //Button to close Application
        ImageButton imgClose = (ImageButton) findViewById(R.id.imgClose);
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.exit(0);
            }
        });

        final Button takePhoto = (Button)findViewById(R.id.bTakePhoto);
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCamera.takePicture(null, null, myPictureCallback_JPG);
                mCameraView.setPhotoTaken(true);
            }
        });


        final Button photoNext = (Button)findViewById(R.id.bPhotoNext);
        photoNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCamera.startPreview();
                setImage((++currentPhoto)%photos.length);
                mCameraView.setPhotoTaken(false);
            }
        });
    }

    public void setImage(int index){
        try {
            InputStream inputStream = this.getResources().openRawResource(photos[index]);
            ImageFilter imageFilter = new ImageFilter(inputStream);
            Bitmap bitmap = imageFilter.getFiltered(0);

            ImageView imageView1 = (ImageView) findViewById(R.id.imageView1);
            imageView1.setImageBitmap(bitmap);

        } catch (NullPointerException ex) {
            Log.e("Fehler", ex.toString());
        }
    }


    // Storage for camera image URI components
    private final static String CAPTURED_PHOTO_PATH_KEY = "mCurrentPhotoPath";
    private final static String CAPTURED_PHOTO_URI_KEY = "mCapturedImageURI";
    // Required for camera operations in order to save the image file on resume.
    private String mCurrentPhotoPath = null;
    private Uri mCapturedImageURI = null;
    @Override public void onSaveInstanceState(Bundle savedInstanceState) {
            if (mCurrentPhotoPath != null) {
                savedInstanceState.putString(CAPTURED_PHOTO_PATH_KEY, mCurrentPhotoPath); }
            if (mCapturedImageURI != null) {
                savedInstanceState.putString(CAPTURED_PHOTO_URI_KEY, mCapturedImageURI.toString());
        } super.onSaveInstanceState(savedInstanceState);
    } @Override protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(CAPTURED_PHOTO_PATH_KEY)) {
            mCurrentPhotoPath = savedInstanceState.getString(CAPTURED_PHOTO_PATH_KEY);
        } if (savedInstanceState.containsKey(CAPTURED_PHOTO_URI_KEY)) {
            mCapturedImageURI = Uri.parse(savedInstanceState.getString(CAPTURED_PHOTO_URI_KEY));
        } super.onRestoreInstanceState(savedInstanceState);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_camera, menu);
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

    Camera.PictureCallback myPictureCallback_JPG = new Camera.PictureCallback(){
    @Override
    public void onPictureTaken(byte[] arg0, Camera arg1) {
        Uri uriTarget = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());

        OutputStream imageFileOS;
        try {
            imageFileOS = getContentResolver().openOutputStream(uriTarget);
            imageFileOS.write(arg0);
            imageFileOS.flush();
            imageFileOS.close();

            Toast.makeText(CameraActivity.this,
                    "Foto gespeichert: \n" + uriTarget.toString(),
                    Toast.LENGTH_LONG).show();

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }};
}
