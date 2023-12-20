package com.example.justwalk;

public class User {
    public String Username;
    public String Password;
    public String Email;
    public String Phone;
    public Double Weight;
    public Integer Height;
    public Integer Age;


    public User(String username,String password, String email, String phone, Double weight, Integer height, Integer age){
        Username = username;
        Email = email;
        Password = password;
        Phone = phone;
        Weight = weight;
        Height = height;
        Age = age;
    }
}