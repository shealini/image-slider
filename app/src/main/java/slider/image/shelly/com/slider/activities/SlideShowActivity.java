package slider.image.shelly.com.slider.activities;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ViewFlipper;

import java.util.ArrayList;

import slider.image.shelly.com.slider.R;


/**
 * Created by shelly on 27/12/15.
 */

public class SlideShowActivity extends Activity {

    private static final String TAG = "SlideShowActivity";

    private ViewFlipper mViewFlipper;

    ArrayList<String> myImageUris;

    private Context mContext;

    LinearLayout btnLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slideshow);

        Bundle bundle = this.getIntent().getExtras();
        myImageUris = bundle.getStringArrayList("ImageFilePaths");

        btnLayout = (LinearLayout) findViewById(R.id.btnlayout);

        mContext = this;

        mViewFlipper = (ViewFlipper) findViewById(R.id.view_flipper);
        final LayoutInflater inflater = getLayoutInflater();

        if (!myImageUris.isEmpty()) {
            int i = 0;

            while (i < myImageUris.size()) {
                final View typeView = inflater.inflate(R.layout.child_image_slider, null);

                Bitmap currentBitmap = BitmapFactory.decodeFile(myImageUris.get(i));

                Log.i(TAG, "adding view to FLIPPER AT INDEX " + i + " and CURRENT BITMAP IS " + myImageUris.get(i));

                ImageView image = (ImageView) typeView.findViewById(R.id.imageview);
                image.setImageBitmap(currentBitmap);

                mViewFlipper.addView(typeView, i);

                i++;
            }
        }
        playSlide();
    }

    void playSlide() {
        mViewFlipper.setAutoStart(true);
        mViewFlipper.setFlipInterval(3000);
        mViewFlipper.setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.right_in));
        mViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(mContext, R.anim.left_out));
        mViewFlipper.startFlipping();

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onPause() {
        super.onPause();

        mViewFlipper.stopFlipping();

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
}
