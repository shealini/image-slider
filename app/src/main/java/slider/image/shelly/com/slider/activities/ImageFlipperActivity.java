package slider.image.shelly.com.slider.activities;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ViewFlipper;

import java.util.ArrayList;

import slider.image.shelly.com.slider.R;

/**
 * Created by shelly on 27/12/15.
 */
public class ImageFlipperActivity extends Activity {

    private static final String TAG = "MyImageActivity";

    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

    private ViewFlipper mViewFlipper;

    ArrayList<String> myImageUris;

    private Context mContext;

    private int position;

    private String currentURI;

    LinearLayout btnlayout;

    ImageView currentImage;

    private final GestureDetector detector = new GestureDetector(new SwipeGestureDetector());

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_flipper);

        Bundle bundle = this.getIntent().getExtras();

        myImageUris = bundle.getStringArrayList("ImageFilePaths");
        currentURI = bundle.getString("currentURI");
        position = bundle.getInt("position");

        btnlayout = (LinearLayout) findViewById(R.id.btnlayout);
        currentImage = (ImageView) findViewById(R.id.myimage);

        // Display the clicked Image
        if (currentURI != null) {
            Bitmap myBitmap = BitmapFactory.decodeFile(currentURI);
            currentImage.setImageBitmap(myBitmap);
        } else {
            Bitmap myBitmap = BitmapFactory.decodeFile(myImageUris.get(0));
            currentImage.setImageBitmap(myBitmap);
        }

        mContext = this;

        mViewFlipper = (ViewFlipper) findViewById(R.id.view_flipper);

        final LayoutInflater inflater = getLayoutInflater();

        if (!myImageUris.isEmpty()) {
            for (int i = 0; i < myImageUris.size(); i++) {
                final View typeView = inflater.inflate(R.layout.child_image_slider, null);

                Bitmap currentBitmap = BitmapFactory.decodeFile(myImageUris.get(i));

                Log.i(TAG, "adding view to FLIPPER AT INDEX " + i + "and CURRENT BITMAP IS " + currentBitmap);

                ImageView image = (ImageView) typeView.findViewById(R.id.imageview);
                image.setImageBitmap(currentBitmap);

                mViewFlipper.addView(typeView, i);
            }
        }

        //TODO ::
    /*
        mViewFlipper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.i(TAG, "btnLayout view clicked, image numbaer ");
                // scrollableLayout.setVisibility(scrollableLayout.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);

                if (btnLayout.getVisibility() == View.VISIBLE) {
                    Log.i(TAG, "scrollable layout was VISIBLE , animation SLIDE DOWN ");

                    btnLayout.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.slid_up));

                    btnLayout.setVisibility(View.GONE);


                } else {
                    Log.i(TAG, "scrollable layout was GONE , animation SLIDE UP");
                    btnLayout.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.slid_down));
                    btnLayout.setVisibility(View.VISIBLE);
                }
            }

        });

    */


        mViewFlipper.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View view, final MotionEvent event) {
                detector.onTouchEvent(event);

                return true;
            }
        });

        //Play Slide Show
        findViewById(R.id.play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //sets auto flipping
                mViewFlipper.setAutoStart(true);
                mViewFlipper.setFlipInterval(3000);
                mViewFlipper.setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.right_in));
                mViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(mContext, R.anim.left_out));
                mViewFlipper.startFlipping();
                mViewFlipper.removeView(currentImage);
                mViewFlipper.removeViewInLayout(currentImage);

                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

                currentImage.setVisibility(View.GONE);
            }
        });

        findViewById(R.id.stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //stop auto flipping
                mViewFlipper.stopFlipping();

                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
        });
    }

    class SwipeGestureDetector extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            // right to left swipe
            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                mViewFlipper.setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.right_in));
                mViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(mContext, R.anim.left_out));
                mViewFlipper.showNext();
                mViewFlipper.removeView(currentImage);
                mViewFlipper.removeViewInLayout(currentImage);

                currentImage.setVisibility(View.GONE);

                return true;
            } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                mViewFlipper.setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.left_in));
                mViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(mContext, R.anim.right_out));
                mViewFlipper.showPrevious();
                mViewFlipper.removeView(currentImage);
                mViewFlipper.removeViewInLayout(currentImage);

                currentImage.setVisibility(View.GONE);

                return true;
            }

            return false;
        }
    }

    Animation.AnimationListener mAnimationListener = new Animation.AnimationListener() {
        public void onAnimationStart(Animation animation) { }

        public void onAnimationRepeat(Animation animation) { }

        public void onAnimationEnd(Animation animation) { }
    };
}
