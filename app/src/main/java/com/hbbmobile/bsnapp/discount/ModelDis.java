package com.hbbmobile.bsnapp.discount;

import java.io.Serializable;

/**
 * Created by Me on 11/23/2016.
 */

public class ModelDis implements Serializable {
    private String title, time;
    private String join;

    public ModelDis(String title, String time) {
        this.title = title;
        this.time = time;
    }

    public ModelDis(String title, String time, String join) {
        this.title = title;
        this.time = time;
        this.join = join;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getJoin() {
        return join;
    }

    public void setJoin(String join) {
        this.join = join;
    }
}
