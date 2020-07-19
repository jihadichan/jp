package converters.meknow.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GoalItem {

    private Integer position;
    private List<IKSentence> sentences = null;

    public Integer getPosition() {
        return this.position;
    }

    public void setPosition(final Integer position) {
        this.position = position;
    }

    public List<IKSentence> getSentences() {
        return this.sentences;
    }

    public void setSentences(final List<IKSentence> sentences) {
        this.sentences = sentences;
    }

}
