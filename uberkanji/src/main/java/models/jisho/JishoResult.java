package models.jisho;

import models.JishoTerm;

import java.util.ArrayList;
import java.util.List;

public class JishoResult {

    private Meta meta;
    private List<Data> data = new ArrayList<>();


    // ------------------------------------------------------------------------------------------ //
    // GETTER & SETTER
    // ------------------------------------------------------------------------------------------ //


    public Meta getMeta() {
        return this.meta;
    }

    public void setMeta(final Meta meta) {
        this.meta = meta;
    }

    public List<Data> getData() {
        return this.data;
    }

    public void setData(final List<Data> data) {
        this.data = data;
    }
}
