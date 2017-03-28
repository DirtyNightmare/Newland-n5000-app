package com.ominidata.device.io;

import java.util.Date;

/**
 * Created by kewin on 20-12-2016.
 */

    public class LStatus {
    public String id;
    public String action;
    public String status;
    public Date time;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getAction() {
        return action;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public Date getTime() {
        return time;
    }
}