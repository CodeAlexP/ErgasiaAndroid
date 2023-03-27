package com.example.androidergasia2023;

public class User {

    public String fname,lname,email;
    public boolean employee;

    public User(){

    }

    public User(String fname,String lname,String email,Boolean employee){
        this.fname=fname;
        this.lname=lname;
        this.email=email;
        this.employee=employee;
    }
}
