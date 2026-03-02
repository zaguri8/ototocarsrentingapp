package com.example.ototocarsrentingapp.model;

public class User {
    // שדות בסיסיים
    protected String firstName;          // שם פרטי
    protected String lastName;           // שם משפחה
    protected String email;              // אימייל
    protected String address;            // כתובת
    protected String city;               // עיר
    protected UserType userType;         // ENUM: RENTER או SELLER


    //בנאי של user
    public User(String firstName,
               String lastName,
               String email,
               String address,
               String city,
                UserType type) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.address = address;
        this.city = city;
        this.userType = type;
    }
    public User() {}

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public UserType getUserType() {
        return userType;
    }


    public boolean isRenter() {
        return userType == UserType.RENTER;
    }
    public boolean isSeller() {
        return userType == UserType.SELLER;
    }
    public void setUserType(UserType userType) {
        this.userType = userType;
    }


}
