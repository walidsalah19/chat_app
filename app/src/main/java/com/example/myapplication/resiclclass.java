package com.example.myapplication;

public class resiclclass {
    public String image,name,id;
    public resiclclass() {
    }

    public resiclclass( String image,String name,String id) {
        this.image = image;
        this.name = name;
        this.id=id;
    }

    public String getImage() {
        return image;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
