package id.mobilecomputing.mc_responsi.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class WeatherDetails implements Serializable {
    int id;
    @SerializedName("main")
    String shortDescription;
    @SerializedName("description")
    String longDescription;
    String icon;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getLongDescription() {
        return longDescription;
    }

    public void setLongDescription(String longDescription) {
        this.longDescription = longDescription;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
