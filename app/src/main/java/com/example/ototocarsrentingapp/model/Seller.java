package com.example.ototocarsrentingapp.model;

public class Seller extends User  {
    private Car car;

    public Seller(String id, String firstName,
                  String lastName,
                  String email,
                  String address,
                  String city,
                  UserType userType,
                  Car c) {
        super(id, firstName,lastName,email,address,city,userType);
        this.car = c;
    }

    public Car getCar() {
        return car;
    }

    public Seller(){}

}
