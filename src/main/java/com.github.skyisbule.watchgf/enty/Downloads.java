package com.github.skyisbule.watchgf.enty;

public class Downloads {

    public Integer id;
    public String  current_path;
    public Integer total_bytes;
    public String  referrer;

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getCurrent_path() {
        return current_path;
    }
    public void setCurrent_path(String current_path) {
        this.current_path = current_path;
    }
    public Integer getTotal_bytes() {
        return total_bytes;
    }
    public void setTotal_bytes(Integer total_bytes) {
        this.total_bytes = total_bytes;
    }
    public String getReferrer() {
        return referrer;
    }
    public void setReferrer(String referrer) {
        this.referrer = referrer;
    }

}
