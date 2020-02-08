package models.jisho;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Attribution {

    @SerializedName("jmdict")
    @Expose
    private Boolean jmdict;
    @SerializedName("jmnedict")
    @Expose
    private Boolean jmnedict;
    @SerializedName("dbpedia")
    @Expose
    private String dbpedia;

    public Boolean getJmdict() {
        return this.jmdict;
    }

    public void setJmdict(final Boolean jmdict) {
        this.jmdict = jmdict;
    }

    public Boolean getJmnedict() {
        return this.jmnedict;
    }

    public void setJmnedict(final Boolean jmnedict) {
        this.jmnedict = jmnedict;
    }

    public String getDbpedia() {
        return this.dbpedia;
    }

    public void setDbpedia(final String dbpedia) {
        this.dbpedia = dbpedia;
    }

}
