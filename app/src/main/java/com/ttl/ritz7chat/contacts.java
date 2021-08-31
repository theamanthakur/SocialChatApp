package com.ttl.ritz7chat;

public class contacts {

    public String name, status, task, image,isAdmin;

    public contacts() {
    }

    public contacts(String name, String status, String task, String image,String isAdmin) {
        this.name = name;
        this.status = status;
        this.task = task;
        this.image = image;
        this.isAdmin = isAdmin;
    }

    public String getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(String isAdmin) {
        this.isAdmin = isAdmin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
