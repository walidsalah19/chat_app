package com.example.myapplication;


public class massageclass {
    private String massage,id,type,noty;
    massageclass()
    {

    }
    public massageclass(String massage,String id,String type,String noty) {
        this.massage = massage;
        this.id=id;
        this.type=type;
        this.noty=noty;
    }

    public String getType() {
        return type;
    }

    public String getMassage() {
        return massage;
    }

    public String getId() {
        return id;
    }

    public String getNoty() {
        return noty;
    }
}

