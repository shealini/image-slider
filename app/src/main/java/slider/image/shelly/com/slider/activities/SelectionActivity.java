package slider.image.shelly.com.slider.activities;

import android.app.ActionBar;
import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TabHost;

import slider.image.shelly.com.slider.R;
import slider.image.shelly.com.slider.model.SlideShow;
import slider.image.shelly.com.slider.utils.DbHelper;

/**
 * Created by shelly on 27/12/15.
 */
@SuppressWarnings("deprecation")
public class SelectionActivity extends TabActivity {

    DbHelper db;

    SlideShow selShow = new SlideShow();

    int selShowID;
    String sTitle;
    String sDesc;
    String sTitleDesc;

    Intent intent;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        intent = getIntent();

        // Get the ID of selected slide show from second screen
        selShowID = intent.getIntExtra("Selected Slide Show", 0);

        // Initiate db connection
        db = new DbHelper(this);

        //get the slide show from database
        selShow = db.getSlideShow(selShowID);

        //close db connection to avoid memory issues
        db.closeDB();

        //get slide show title
        sTitle = selShow.getshowName();

        //get show description
        sDesc = selShow.getshowDescription();

        if (sDesc.length() > 15) {

            // Cut the length of description if it is more than 15 characters
            sDesc = (String) sDesc.subSequence(0, 14) + "......";
        }

        sTitleDesc = (sTitle + (!sDesc.isEmpty() ? "-" : "") + sDesc);

        // Set the theme color to action bar
        ActionBar bar = getActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0099FF")));
        bar.setTitle(sTitleDesc);

        TabHost host = getTabHost();

        Resources resources = getResources();

        // Tab for images, sending the Slide show ID and Title description to ImageDisplayActivity
        TabHost.TabSpec imageTab = host.newTabSpec("Images");
        imageTab.setIndicator("Images", resources.getDrawable(R.drawable.ic_action_picture));

        Intent intent = new Intent(this, GridImagesActivity.class);
        intent.putExtra("TAB", "Images");
        intent.putExtra("showID", selShowID);

        imageTab.setContent(intent);

        host.addTab(imageTab);
    }
}
