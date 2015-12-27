package slider.image.shelly.com.slider.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import slider.image.shelly.com.slider.model.SlideShow;

/**
 * Created by shelly on 27/12/15.
 */
public class DbHelper extends SQLiteOpenHelper {

    // Database version
    private static final int DATABASE_VERSION = 6;

    // Database Name
    private static final String DATABASE_NAME = "slider";

    //Table Name
    private static final String TABLE_SLIDESHOW = "slideshows";
    private static final String TABLE_IMAGESHOW = "imageshows";

    //Common column name
    private static final String KEY_ID = "id";
    private static final String KEY_SHOWNAME = "showname";
    private static final String KEY_SHOWID = "showId";

    // slideshow Table - column names
    private static final String KEY_DESCRIPTION = "description";

    // imageshow Table - column names
    private static final String KEY_IMAGE = "showimage";

    private Context context;

    // slideshow table create statement
    private static final String CREATE_TABLE_SLIDESHOW = "CREATE TABLE "
            + TABLE_SLIDESHOW + "(" + KEY_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_SHOWNAME + " TEXT,"
            + KEY_DESCRIPTION + " TEXT" + ")";

    // imageshow table create statement
    private static final String CREATE_TABLE_IMAGESHOW = "CREATE TABLE "
            + TABLE_IMAGESHOW + "(" + KEY_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_SHOWID + " INTEGER, "
            + KEY_IMAGE + " TEXT" + ")";

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;

    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create tables on create
        db.execSQL(CREATE_TABLE_SLIDESHOW);
        db.execSQL(CREATE_TABLE_IMAGESHOW);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SLIDESHOW);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_IMAGESHOW);

        onCreate(db);
    }

    /**
     * Creates slide show
     * @param slideShow
     * @return
     */
    public long createSlideShow(SlideShow slideShow) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_SHOWNAME, slideShow.getshowName());
        values.put(KEY_DESCRIPTION, slideShow.getshowDescription());
        return db.insert(TABLE_SLIDESHOW, null, values);
    }

    /***
     * Get slide show
     * @param showid
     * @return
     */
    public SlideShow getSlideShow(long showid) {

        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_SLIDESHOW + " WHERE "
                + KEY_ID + " = " + showid;
        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null)
            c.moveToFirst();
        SlideShow td = new SlideShow();

        td.setId(c.getInt(c.getColumnIndex(KEY_ID)));
        td.setshowName(c.getString(c.getColumnIndex(KEY_SHOWNAME)));
        td.setshowDescription(c.getString(c.getColumnIndex(KEY_DESCRIPTION)));

        return td;
    }

    /***
     * Get all the created slide show
     * @return
     */
    public List<SlideShow> getAllSlideShows() {
        List<SlideShow> slideshows = new ArrayList<SlideShow>();
        String selectQuery = "SELECT * FROM " + TABLE_SLIDESHOW + " ORDER BY "
                + KEY_ID + " DESC";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                SlideShow td = new SlideShow();
                td.setId(c.getInt((c.getColumnIndex(KEY_ID))));
                td.setshowName(c.getString(c.getColumnIndex(KEY_SHOWNAME)));
                td.setshowDescription(c.getString(c
                        .getColumnIndex(KEY_DESCRIPTION)));

                // adding to slideshow list
                slideshows.add(td);
            } while (c.moveToNext());
        }
        return slideshows;
    }

    /***
     * Delete slide show with slideshowid
     * @param slideshowid
     */
    public void deleteSlideShow(long slideshowid) {
        SQLiteDatabase db = this.getWritableDatabase();
        // before delete the slideshow, all images and music should be deleted
        db.delete(TABLE_IMAGESHOW, KEY_SHOWID + " = ?",
                new String[]{String.valueOf(slideshowid)});
        db.delete(TABLE_SLIDESHOW, KEY_ID + " = ?",
                new String[]{String.valueOf(slideshowid)});
    }

    /***
     * Create image show
     * @param imgs
     * @param showid
     */
    public void createImageShow(String imgs, long showid) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_SHOWID, showid);
        // need to find the file location and store Uri as string
        values.put(KEY_IMAGE, imgs);

        // insert row
        db.insert(TABLE_IMAGESHOW, null, values);
    }

    /***
     * Add image to image show
     * @param filePaths
     * @param showid
     */
    public void addImage(List<String> filePaths, long showid) {
        SQLiteDatabase db = this.getWritableDatabase();
        for (String strimg : filePaths) {

            ContentValues values = new ContentValues();
            values.put(KEY_SHOWID, showid);
            values.put(KEY_IMAGE, strimg);
            // insert row
            db.insert(TABLE_IMAGESHOW, null, values);
        }
    }

    /**
     * Get all the images URIs
     * @param showid
     * @return
     */
    public List<String> getAllimageURI(long showid) {
        List<String> imageshows = new ArrayList<String>();
        String selectQuery = "SELECT * FROM " + TABLE_IMAGESHOW + " WHERE "
                + KEY_SHOWID + " = " + showid;
        SQLiteDatabase db = this.getReadableDatabase();//read only
        Cursor c = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                // adding to imageshow list
                imageshows.add(c.getString(c.getColumnIndex(KEY_IMAGE)));
            } while (c.moveToNext());

        }

        return imageshows;
    }

    /**
     * Delete an image show
     * @param showid
     * @param uri
     */
    public void deleteImageShow(int showid, String uri) {

        SQLiteDatabase db = this.getWritableDatabase();
        String deletesql = " DELETE FROM  " + TABLE_IMAGESHOW + " WHERE "
                + KEY_SHOWID + " = " + showid + " AND " + KEY_IMAGE
                + " = (SELECT " + KEY_IMAGE + " FROM " + TABLE_IMAGESHOW
                + " WHERE " + KEY_SHOWID + " = " + showid + " AND " + KEY_IMAGE
                + " = '" + uri + "' ORDER BY id LIMIT 1);";

        db.execSQL(deletesql);
    }

    // closing database connection
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }

    // Re-opening database connection
    public void reopen() {
        SQLiteDatabase db = this.getWritableDatabase();
        if (db == null || !db.isOpen()) {
            close();
            DbHelper dbHelper = new DbHelper(context);
        }
    }
}
