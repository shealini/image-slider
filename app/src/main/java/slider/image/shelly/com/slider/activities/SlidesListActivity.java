package slider.image.shelly.com.slider.activities;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

import slider.image.shelly.com.slider.R;
import slider.image.shelly.com.slider.adapter.InteractiveArrayAdapter;
import slider.image.shelly.com.slider.model.SlideShow;
import slider.image.shelly.com.slider.utils.DbHelper;

/**
 * Created by shelly on 27/12/15.
 */
public class SlidesListActivity extends ListActivity {

    private ArrayList<SlideShow> slideShows;

    ArrayAdapter<SlideShow> adapter;

    // Database helper instance
    DbHelper db;

    EditText edTitle;
    EditText edDesc;

    int npos = -1;

    // for display action bar menu item
    int actionBarState = 0;

    //menu items
    MenuItem addItem, acceptItem, editItem, deleteItem;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_slides_list);

        // open database instance
        db = new DbHelper(this);

        //Get list of created slide shows from database
        slideShows = (ArrayList<SlideShow>) db.getAllSlideShows();

        // add a dummy node if the list is empty
        setDummyList();

        adapter = new InteractiveArrayAdapter(this, slideShows);

        // setting list adapter
        setListAdapter(adapter);

        edTitle = (EditText) findViewById(R.id.showname);
        edDesc = (EditText) findViewById(R.id.showdesc);
        edTitle.setVisibility(View.GONE);// hide the editbox from the view
        edDesc.setVisibility(View.GONE);

        // implement action bar
        ActionBar bar = getActionBar();

        // set bar title and color
        bar.setTitle("Slides");
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0099FF")));

    }

    // initially the list is empty-display a message
    public void setDummyList() {
        if (slideShows.size() < 1) {
            SlideShow ss = new SlideShow();
            ss.setshowName("Press + button");
            ss.setshowDescription("to add Imageshow");
            ss.setId(-1);
            slideShows.add(ss);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.action_bar_item, menu);

        addItem = menu.findItem(R.id.new_show);
        acceptItem = menu.findItem(R.id.accept_show);
        editItem = menu.findItem(R.id.edit_show);
        deleteItem = menu.findItem(R.id.delete_show);

        if (menu != null) {

            // function to set menu item display
            setActionBar();
        }

        return super.onCreateOptionsMenu(menu);
    }

    protected void setActionBar() {

        // different state setting different actionBar icons
        switch (actionBarState) {
            case 0: // initial state
                deleteItem.setVisible(false);
                acceptItem.setVisible(false);
                addItem.setVisible(true);
                editItem.setVisible(false);

                break;
            case 1: // add item
                deleteItem.setVisible(true);
                acceptItem.setVisible(true);
                addItem.setVisible(false);
                editItem.setVisible(false);

                break;
            case 2: // item selected
                deleteItem.setVisible(true);
                acceptItem.setVisible(false);
                addItem.setVisible(false);
                editItem.setVisible(true);

                break;
            default: // every menu item is displayed
                deleteItem.setVisible(true);
                acceptItem.setVisible(true);
                addItem.setVisible(true);
                editItem.setVisible(true);
        }
    }

    // click on anywhere on the list the check box will be set
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        //only if it is not in add new slide show mode
        if (!edTitle.isShown()) {
            npos = position;

            for (int i = 0; i < slideShows.size(); i++) {
                if (i != position) {
                    // unSelect other
                    slideShows.get(i).setselected(false);
                } else {
                    // not a dummy list
                    if (slideShows.get(i).getId() != -1) {
                        slideShows.get(i).setselected(!slideShows.get(i).isSelected());
                        if (!slideShows.get(i).isSelected()) {
                            npos = -1;

                            // none selected then original action bar with + only
                            actionBarState = 0;
                        } else {
                            // action bar has edit, delete only
                            actionBarState = 2;
                        }

                        // reset the menu item on the action bar
                        invalidateOptionsMenu();
                    }
                }
            }
        }
        ((InteractiveArrayAdapter) adapter).notifyDataSetChanged();
    }

    @SuppressLint("NewApi")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {// on the action bar
        switch (item.getItemId()) {
            case R.id.edit_show:
                editSlide();

                return true;
            case R.id.delete_show:
                deleteSlide();

                return true;
            case R.id.new_show:
                actionBarState = 1;
                invalidateOptionsMenu();
                addNewShow();// setting up new input screen

                return true;
            case R.id.accept_show:
                acceptChoice();// verify the new input and move on

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void deleteSlide() {
        // cancel the new input if input box is visible and X is pressed
        if (edTitle.isShown()) {
            resetInputScreen();
        } else {
            // no selection
            if (npos != -1 && slideShows.get(npos).getId() != -1) {
                // the first entry, not the dummy entry
                alertBox("Delete Slide", "Are you sure you want to delete Slide "
                        + slideShows.get(npos).getshowName() + " ?");
            }
        }
        adapter.notifyDataSetChanged();
    }

    public void editSlide() {
        if (slideShows.get(0).getId() > -1) {
            boolean checked = false;

            // not a dummy one
            if (npos > -1) {
                checked = slideShows.get(npos).isSelected();
            }
            if (checked) {
                Intent intent = new Intent(this, SelectionActivity.class);
                intent.putExtra("Selected Slide Show", slideShows.get(npos).getId());

                // select the image and music activity
                startActivity(intent);
            }
        }
    }

    public void addNewShow() {
        if (slideShows.get(0).getId() > -1) {
            boolean checked = false;

            if (npos > -1) {
                checked = slideShows.get(npos).isSelected();
            }

            if (checked) {
                Intent intent = new Intent(this, SelectionActivity.class);
                intent.putExtra("Selected Slide Show", slideShows.get(npos).getId());
                startActivity(intent);
            } else {
                // display the input box
                edTitle.setVisibility(View.VISIBLE);
                edDesc.setVisibility(View.VISIBLE);
                edTitle.requestFocus();

            }
        } else {
            edTitle.setVisibility(View.VISIBLE);
            edDesc.setVisibility(View.VISIBLE);
            edTitle.requestFocus();
        }
    }

    // for adding new show after input
    public void acceptChoice() {
        String sTitle = edTitle.getText().toString();
        String sDesc = edDesc.getText().toString();

        if (!sTitle.isEmpty()) {
            SlideShow ss = new SlideShow(sTitle, sDesc);

            if (slideShows.size() > 0 && slideShows.get(0).getId() == -1) {
                // a dummy first element
                slideShows.remove(0);
            }

            // add to the top of the list
            slideShows.add(0, ss);
            ss.setId((int) db.createSlideShow(ss));

            // verify valid input entries
            resetInputScreen();

        } else {
            if (edTitle.isShown()) {
                alertBox("Cannot add slide",
                        "slide name cannot be blank");
            }
        }
    }

    public void setActivityBackgroundcolor(int color) {
        View view = this.getWindow().getDecorView();
        view.setBackgroundColor(color);
    }

    protected void resetInputScreen() {
        edTitle.setText("");
        edDesc.setText("");

        // hide the keyboard when input is done
        InputMethodManager inputMgr = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMgr.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);// toggling

        // keyboard shown/not shown
        edTitle.setVisibility(View.GONE);
        edDesc.setVisibility(View.GONE);

        adapter.notifyDataSetChanged();

        // reset the action bar to display menu item
        actionBarState = 0;

        // refresh the menu bar
        invalidateOptionsMenu();
    }

    // to display alert messages
    protected void alertBox(String title, String msg) {
        final String finalTitle = title;
        new AlertDialog.Builder(this)

                .setTitle(finalTitle)
                .setMessage(msg)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        if (finalTitle.toLowerCase().contains("delete")) {
                            db.deleteSlideShow((long) slideShows.get(npos).getId());

                            slideShows.remove(npos);

                            npos = -1;

                            // nothing on the list
                            setDummyList();

                            adapter.notifyDataSetChanged();

                            actionBarState = 0;

                            // reset menu bar items
                            invalidateOptionsMenu();
                        }
                        if (finalTitle.toLowerCase().contains("cannot add")) {
                            edTitle.requestFocus();
                        }
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {

                                // do nothing if it is delete
                                if (!finalTitle.toLowerCase()
                                        .contains("delete")) {
                                    resetInputScreen(); // hide/unhide editboxes
                                }

                            }
                        }).show();
    }

    // make sure database is closed to avoid memory leak
    void OnStop() {
        db.closeDB();
    }

}
