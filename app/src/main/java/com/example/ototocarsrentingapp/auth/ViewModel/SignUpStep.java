package com.example.ototocarsrentingapp.auth.ViewModel;

public enum SignUpStep {
    ENTRY,
    SIGN_IN,
    PERSONAL_DETAILS,//שם, שם משפחה, תאריך יום הולדת, אימייל, מספר טלפון
    ADDRESS_DETAILS,//כתובת, עיר
    UserType,//סוג המשתמש
    RENTER_DETAILS,//פרטים על המשכיר
    SELLER_DETAILS1,//פרטים על המוכר
    SELLER_DETAILS2,//פרטים על המוכר
}
