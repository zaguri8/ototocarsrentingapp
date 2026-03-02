package com.example.ototocarsrentingapp.model;

public class Seller extends User  {
    private Car car;

    public Seller(String firstName,
                  String lastName,
                  String email,
                  String address,
                  String city,
                  UserType userType,
                  Car c) {
        super(firstName,lastName,email,address,city,userType);
        this.car = c;
    }
    public Seller(){}

}
