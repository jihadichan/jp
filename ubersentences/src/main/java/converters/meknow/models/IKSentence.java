package converters.meknow.models;

import java.util.HashMap;
import java.util.Map;

public class IKSentence {

    private Integer position;
    private Cue cue;
    private String sound;

    public Integer getPosition() {
        return this.position;
    }

    public void setPosition(final Integer position) {
        this.position = position;
    }

    public Cue getCue() {
        return this.cue;
    }

    public void setCue(final Cue cue) {
        this.cue = cue;
    }

    public String getSound() {
        return this.sound;
    }

    public void setSound(final String sound) {
        this.sound = sound;
    }

}
