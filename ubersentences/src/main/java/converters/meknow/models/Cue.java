package converters.meknow.models;

import java.util.HashMap;
import java.util.Map;

public class Cue {

    private Integer id;
    private String language;
    private IKTransliterations transliterations;

    public Integer getId() {
        return this.id;
    }

    public void setId(final Integer id) {
        this.id = id;
    }

    public String getLanguage() {
        return this.language;
    }

    public void setLanguage(final String language) {
        this.language = language;
    }

    public IKTransliterations getTransliterations() {
        return this.transliterations;
    }

    public void setTransliterations(final IKTransliterations transliterations) {
        this.transliterations = transliterations;
    }

}
