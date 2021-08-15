package converters.meknow.models;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

public class IKTransliterations {

    @SerializedName("Jpan")
    private String jpan;

    public String getJpan() {
        return jpan;
    }

    public void setJpan(String jpan) {
        this.jpan = jpan;
    }

}
