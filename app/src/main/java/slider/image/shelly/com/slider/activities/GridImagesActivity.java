package slider.image.shelly.com.slider.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import slider.image.shelly.com.slider.R;
import slider.image.shelly.com.slider.utils.DbHelper;

/**
 * Created by shelly on 27/12/15.
 */
public class GridImagesActivity extends Activity {

    private static final int SELECT_PICTURE = 1;
    private final String TAG = "ImageDisplayActivity";

    List<Drawable> splittedBitmaps;
    List<String> filePaths;
    List<String> imgURI;

    DbHelper db;
    int showID;
    ImageAdapter imgAdapter;
    private static final int CAMERA = 2;
    AdapterView.AdapterContextMenuInfo info;
    GridView imageGrid;
    ImageButton btnChooseGallery;
    ImageButton btnopencamera;
    ImageButton btnPlayShow;
    int imageId;
    String selected;
    public static final int MEDIA_TYPE_IMAGE = 1;
    private static final String IMAGE_DIRECTORY_NAME = "Unacademy";
    private Uri fileUri;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_grid_images);

        btnChooseGallery = (ImageButton) findViewById(R.id.addImageButton);
        btnPlayShow = (ImageButton) findViewById(R.id.playImageButton);
        btnopencamera = (ImageButton) findViewById(R.id.cameraButton);

        btnChooseGallery.setOnClickListener(btnOpenGallery);
        btnPlayShow.setOnClickListener(btnSlideShow);
        btnopencamera.setOnClickListener(btncamera);


        db = new DbHelper(this);
        showID = getIntent().getIntExtra("showID", 0);
        imgURI = db.getAllimageURI(showID); //Get all the image URIs associated to Slide show ID

        imageGrid = (GridView) findViewById(R.id.grid);

        // List of image URIs to render by setting the adapter to GridView
        imgAdapter = new ImageAdapter(this, imgURI);
        imageGrid.setAdapter(imgAdapter);
        registerForContextMenu(imageGrid);


        imageGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {


                Bundle b = new Bundle();
                String key = "ImageFilePaths";

                String currentURI = imgURI.get(position);

                // If there are no images in Images tab but music files in Music tab
                if (imgURI.isEmpty()) {

                    Toast.makeText(getApplicationContext(), "Cannot play Slideshow without images.  Please select atleast one image.", Toast.LENGTH_LONG).show();
                    // If both Images and Music tabs does not contain any files
                } else if (imgURI.isEmpty()) {

                    Toast.makeText(getApplicationContext(), "Please select music files & images to play slideshow", Toast.LENGTH_LONG).show();
                } else { // Start SlideShowActivity by sending Image URIs and Music URIs

                    b.putStringArrayList(key, (ArrayList<String>) imgURI);
                    b.putInt("position", position);
                    b.putLong("id", id);
                    b.putString("currentURI", currentURI);


                    Intent intent = new Intent(GridImagesActivity.this, ImageFlipperActivity.class);

                    intent.putExtras(b);
                    startActivity(intent);
                }


            }
        });


        imageGrid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                return false;
            }
        });


    }


    // Contextual menu for removing unwanted images from a Slideshow
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        info.targetView.setBackgroundDrawable(new ColorDrawable(GridImagesActivity.this.getResources().getColor(R.color.purple_dark)));
        imageId = (int) info.position;
        selected = imgAdapter.getURI(imageId);

        // Alert with Cancel and Delete buttons for deletion of selected image file
        menu.setHeaderTitle("Delete Image file:");
        menu.add(0, v.getId(), 0, "Cancel");
        menu.add(0, v.getId(), 0, "Delete");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        // If the selected button is "Delete" in Delete alert box
        if (item.getTitle() == "Delete") {
            Toast.makeText(getApplicationContext(), "Deleting image file-" + selected, Toast.LENGTH_LONG).show();
            db.deleteImageShow(showID, selected);
            imgURI.remove(imgAdapter.getURI(imageId));
            imgAdapter.notifyDataSetChanged();

            // If the selected button is "Delete" in Delete alert box
        } else if (item.getTitle() == "Cancel") {
            Toast.makeText(getApplicationContext(), "Cancelling delete", Toast.LENGTH_LONG).show();
            info.targetView.setBackgroundColor(GridImagesActivity.this.getResources().getColor(R.color.blue7));

        } else {
            return false;
        }
        selected = "";
        return true;
    }

    // Open the Photo gallery on click of "Add New Picture" button
    public View.OnClickListener btnOpenGallery = new View.OnClickListener() {

        @SuppressLint("InlinedApi")
        public void onClick(View view) {
            if (getIntent().getCharSequenceExtra("TAB").toString().contentEquals("Images")) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*"); // Set the type as image
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); // To allow selection of multiple images
                startActivityForResult(intent, SELECT_PICTURE);
            }
        }
    };

    public View.OnClickListener btncamera = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.i(TAG, "camera clicked");

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
//            intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, 100);

            startActivityForResult(intent, CAMERA);

        }
    };


    // Play Slide show on click of "Play" button
    public View.OnClickListener btnSlideShow = new View.OnClickListener() {

        @SuppressLint("InlinedApi")
        public void onClick(View view) {
            // Check whether the tab is "Images"
            if (getIntent().getCharSequenceExtra("TAB").toString().contentEquals("Images")) {
                playSlideShow();
            }
        }
    };

    public void playSlideShow() {
        Bundle b = new Bundle();
        String key = "ImageFilePaths";


        // If there are no images in Images tab but music files in Music tab
        if (imgURI.isEmpty()) {

            Toast.makeText(getApplicationContext(), "Cannot play Slideshow without images.  Please select atleast one image.", Toast.LENGTH_LONG).show();
            // If both Images and Music tabs does not contain any files

        } else { // Start SlideShowActivity by sending Image URIs and Music URIs

            b.putStringArrayList(key, (ArrayList<String>) imgURI);

            Intent intent = new Intent(this, SlideShowActivity.class);
            intent.putExtras(b);
            startActivity(intent);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        int totalImages = 0;
        filePaths = new ArrayList<String>();
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = null;
                String path;
                // If multiple image selection is enabled, ClipData gives the number of selected images
                if (data.getClipData() != null) {
                    totalImages = data.getClipData().getItemCount();
                } else if (data.getData() != null) {
                    totalImages = 1; // Take selected images count to be 1 if multiple selection is not enabled
                }
                for (int currentImage = 0; currentImage < totalImages; currentImage++) {
                    if (data.getClipData() != null) {
                        ClipData.Item currentClip = data.getClipData().getItemAt(
                                currentImage);
                        // Get the URI
                        selectedImageUri = currentClip.getUri();

                    } else if (data.getData() != null) {
                        // Get the URI
                        selectedImageUri = data.getData();
                    }

                    // Get the file path using the Image URI
                    if (selectedImageUri != null) {
                        path = getFilePath(selectedImageUri);

                        if (!path.isEmpty()) {
                            filePaths.add(path);//add the path to an arraylist
                            imgURI.add(path);//add to the arraylist of for the view
                        }
                    }
                }
                db.addImage(filePaths, showID); // Add filePaths list to database
            } else if (requestCode == CAMERA) {
//                String path = (fileUri.toString().replace("file://","")).toString();
                String path = fileUri.getPath();
                System.out.println(" ---- path ---- " + path);
                if (!path.isEmpty()) {
                    filePaths.add(path);//add the path to an arraylist
                    imgURI.add(path);//add to the arraylist of for the view
                }

                db.addImage(filePaths, showID); // Add filePaths list to database

            }


            imageGrid.setAdapter(imgAdapter); // Set the adapter to the Grid View
        }


    }


    // Retrieve the file path using the URI

    public String getFilePath(Uri currentUri) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        //to query the device library to get the path given the URI
        Cursor cursor = getContentResolver().query(currentUri, filePathColumn,
                null, null, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String filePath = cursor.getString(columnIndex);
        cursor.close();

        return filePath;
    }

    // Close the database on pause of activity
    void OnPause() {
        db.closeDB();
    }

    // Reopen the database on resuming the activity
    void OnResume() {
        db.reopen();
    }

    // Image Adapter
    public class ImageAdapter extends BaseAdapter {
        private Context mContext;
        private List<String> mThumbIds;

        public ImageAdapter(Context c, List<String> list) {
            mContext = c;
            mThumbIds = list;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mThumbIds.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return BitmapFactory.decodeFile(mThumbIds.get(position));
        }

        public String getURI(int position) {
            return mThumbIds.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            imageView = new ImageView(mContext);
            try {
                // TODO Auto-generated method stub
                imageView.setLayoutParams(new GridView.LayoutParams(150, 150));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(8, 8, 8, 8);

                final BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;

                // Decode the file to avoid java.lang.OutOfMemory exception
                BitmapFactory.decodeFile(mThumbIds.get(position), options);

                // Calculate inSampleSize
                options.inSampleSize = calculateInSampleSize(options, 110, 110);

                // Decode bitmap with inSampleSize set
                options.inJustDecodeBounds = false;
                Bitmap tempBitmap = BitmapFactory.decodeFile(
                        mThumbIds.get(position), options);
                imageView.setImageBitmap(tempBitmap);
                imageView.setBackgroundColor(GridImagesActivity.this.getResources().getColor(R.color.blue7));

            } catch (Error e) {
                e.printStackTrace();
            } catch (Exception e) {

            }
            return imageView;
        }


        public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
            // Raw height and width of image
            final int height = options.outHeight;
            final int width = options.outWidth;
            int inSampleSize = 1;

            if (height > reqHeight || width > reqWidth) {

                final int halfHeight = height / 2;
                final int halfWidth = width / 2;

                // Calculate the largest inSampleSize value that is a power of 2
                // and keeps both height and width larger than the requested height and width.
                while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                    inSampleSize *= 2;
                }
            }

            return inSampleSize;
        }


    }

    /*
 * returning image / video
 */
    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
                IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }

    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on scren orientation
        // changes
        outState.putParcelable("file_uri", fileUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");
    }


}