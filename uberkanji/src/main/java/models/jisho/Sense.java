package models.jisho;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Sense {

    @SerializedName("english_definitions")
    @Expose
    private List<String> englishDefinitions = null;
    @SerializedName("parts_of_speech")
    @Expose
    private List<String> partsOfSpeech = null;
    @SerializedName("links")
    @Expose
    private List<Link> links = null;
    @SerializedName("tags")
    @Expose
    private List<String> tags = null;
    @SerializedName("restrictions")
    @Expose
    private List<Object> restrictions = null;
    @SerializedName("see_also")
    @Expose
    private List<String> seeAlso = null;
    @SerializedName("antonyms")
    @Expose
    private List<String> antonyms = null;
    @SerializedName("source")
    @Expose
    private List<Object> source = null;
    @SerializedName("info")
    @Expose
    private List<Object> info = null;
    @SerializedName("sentences")
    @Expose
    private List<Object> sentences = null;

    public List<String> getEnglishDefinitions() {
        return this.englishDefinitions;
    }

    public void setEnglishDefinitions(final List<String> englishDefinitions) {
        this.englishDefinitions = englishDefinitions;
    }

    public List<String> getPartsOfSpeech() {
        return this.partsOfSpeech;
    }

    public void setPartsOfSpeech(final List<String> partsOfSpeech) {
        this.partsOfSpeech = partsOfSpeech;
    }

    public List<Link> getLinks() {
        return this.links;
    }

    public void setLinks(final List<Link> links) {
        this.links = links;
    }

    public List<String> getTags() {
        return this.tags;
    }

    public void setTags(final List<String> tags) {
        this.tags = tags;
    }

    public List<Object> getRestrictions() {
        return this.restrictions;
    }

    public void setRestrictions(final List<Object> restrictions) {
        this.restrictions = restrictions;
    }

    public List<String> getSeeAlso() {
        return this.seeAlso;
    }

    public void setSeeAlso(final List<String> seeAlso) {
        this.seeAlso = seeAlso;
    }

    public List<String> getAntonyms() {
        return this.antonyms;
    }

    public void setAntonyms(final List<String> antonyms) {
        this.antonyms = antonyms;
    }

    public List<Object> getSource() {
        return this.source;
    }

    public void setSource(final List<Object> source) {
        this.source = source;
    }

    public List<Object> getInfo() {
        return this.info;
    }

    public void setInfo(final List<Object> info) {
        this.info = info;
    }

    public List<Object> getSentences() {
        return this.sentences;
    }

    public void setSentences(final List<Object> sentences) {
        this.sentences = sentences;
    }

}
