package com.liandev.materialcalendar.Object;

import android.content.ContentValues;
import java.util.Date;

public class Task {
    public String taskId;
    public String task_name;
    public String task_description = "";
    public Date task_end_date;
    public String task_end_hours = "";
    public Boolean isFinish = false;
    public Boolean isDeleted = false;

    public Task(
            String taskid,
            String taskname,
            String taskdescription,
            Date task_end_date,
            Boolean isfinish,
            Boolean isDeleted) {
        this.taskId = taskid;
        this.task_name = taskname;
        this.task_description = taskdescription != null ? taskdescription : "";
        this.task_end_date = task_end_date;
        //  this.task_end_hours = task.getAsString("task_end_hours");
        if (isfinish != null) this.isFinish = isfinish;
    }

    public String getTaskId() {
        return taskId;
    }

    public String getTaskName() {
        return task_name;
    }

    public String getTaskDescription() {
        return task_description;
    }

    public Date getTaskEndDate() {
        return task_end_date;
    }

    public String getTaskEndHours() {
        return task_end_hours;
    }

    public Boolean taskIsFinish() {
        return isFinish;
    }

    public Boolean getIsDeleted() {
        return this.isDeleted;
    }

    void setTaskId(java.lang.String taskId) {
        this.taskId = taskId;
    }

    void setTaskName(java.lang.String task_name) {
        this.task_name = task_name;
    }

    void setTaskDescription(java.lang.String task_description) {
        this.task_description = task_description;
    }

    void setTaskEndDate(java.util.Date task_end_date) {
        this.task_end_date = task_end_date;
    }

    public void setIsFinish(java.lang.Boolean isFinish) {
        this.isFinish = isFinish;
    }

    void setIsDeleted(java.lang.Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
}
