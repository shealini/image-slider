package slider.image.shelly.com.slider.model;

/**
 * Created by shelly on 27/12/15.
 */
public class ImageShow {

    private int id;
    private int showId;
    private String showImage;
    private boolean selected;

    // Setters
    public void setShowID(int d) {
        this.showId = d;
    }

    public void setShowImage(String i) {
        this.showImage = i;
    }

    public void setId(int i) {
        this.id = i;
    }

    // Getters
    public int getShowID() { return this.showId; }

    public String getShowImage() { return this.showImage; }

    public int getId() { return this.id; }

    public boolean isSelected() { return this.selected; }

    public void setselected(boolean b) { this.selected = b;}
}
