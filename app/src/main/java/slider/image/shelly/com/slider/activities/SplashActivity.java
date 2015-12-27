package slider.image.shelly.com.slider.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;

import slider.image.shelly.com.slider.R;

public class SplashActivity extends Activity {

    private ProgressBar spinner;

    //Splash activity time our 2sec
    private static int SPLASH_TIME_OUT = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        spinner = (ProgressBar) findViewById(R.id.progressBar2);
        spinner.setVisibility(View.VISIBLE);

        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                getActivity();

                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

    public void getActivity() {
        Intent menuChoice = new Intent(getApplicationContext(), SlidesListActivity.class);

        spinner.setVisibility(View.GONE);

        startActivity(menuChoice);

    }
}
