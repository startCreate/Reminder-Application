package com.application.remindme.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Date;

/**
 * Created by vv_voronov on 23.01.2017.
 */

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class RemindDTO {

    private long id;
    private String title;
    private Date remindDate;

    public RemindDTO(String title) {
        this.title = title;
    }

    public RemindDTO() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getRemindDate() {
        return remindDate;
    }

    public void setRemindDate(Date remindDate) {
        this.remindDate = remindDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}