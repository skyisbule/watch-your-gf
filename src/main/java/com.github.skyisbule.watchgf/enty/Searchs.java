package com.github.skyisbule.watchgf.enty;

public class Searchs {

    Integer urlId;
    String  term;

    public Integer getUrlId() {
        return urlId;
    }
    public void setUrlId(Integer urlId) {
        this.urlId = urlId;
    }
    public String getTerm() {
        return term;
    }
    public void setTerm(String term) {
        this.term = term;
    }
    public String toString(){
        return urlId+" "+term+"\n";
    }
}
