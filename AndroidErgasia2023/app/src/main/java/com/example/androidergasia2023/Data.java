package com.example.androidergasia2023;

public class Data {
    public String spinvalue;
    public String  encoded;
    public String timeStamp;
    public String lati;
    public String longi;
    public String comments;

    public Data() {

    }

    public Data(String spinvalue, String encoded, String timeStamp, String lati, String longi,String comments) {
        this.spinvalue = spinvalue;
        this.encoded = encoded;
        this.timeStamp = timeStamp;
        this.lati = lati;
        this.longi = longi;
        this.comments=comments;
    }
}
