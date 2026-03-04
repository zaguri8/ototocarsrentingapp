package com.example.ototocarsrentingapp.model;

public class Renter extends User {
    private String licenseNumber;//מספר  הרישיון

    public Renter(String id, String firstName,
                  String lastName,
                  String email,
                  String address,
                  String city,
                  String licenseNumber,
                  UserType type
                  ) {
        super(id, firstName,lastName,email,address,city,type);

        this.licenseNumber = licenseNumber;
    }
    public Renter(){}

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

}
