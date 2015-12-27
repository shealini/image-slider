package slider.image.shelly.com.slider.model;

import java.util.ArrayList;

/**
 * Created by shelly on 27/12/15.
 */
public class SlideShow {
    private int id;

    private ArrayList<ImageShow> imgList;

    private String showName;
    private String showDescription;

    private boolean selected;

    public SlideShow() {}

    public SlideShow(String name, String desc) {
        this.showName = name;
        this.showDescription = desc;
    }

    //setters
    public void setId(int i) {this.id = i;}

    public void setshowName(String s) {
        this.showName = s;
    }

    public void setshowDescription(String s) {
        this.showDescription = s;
    }

    public void setselected(boolean b) {
        this.selected = b;
    }

    public void setshowimage(ArrayList<ImageShow> imglist) {
        this.imgList = imglist;
    }

    // getters
    public int getId() {
        return this.id;
    }

    public String getshowName() {
        return this.showName;
    }

    public String getshowDescription() {
        return this.showDescription;
    }

    public boolean isSelected() { return this.selected;}

    public ArrayList<ImageShow> getimglist() {
        return imgList;
    }
}
