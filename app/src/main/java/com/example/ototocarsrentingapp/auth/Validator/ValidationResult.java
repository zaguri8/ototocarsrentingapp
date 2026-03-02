package com.example.ototocarsrentingapp.auth.Validator;

public class ValidationResult {//מחלקה שתשמש אותנו לשלוח הודעות שגיאה
    private final String errorMessage;
    private final boolean isValid;

    public ValidationResult(String errorMessage, boolean isValid) {
        this.errorMessage = errorMessage;
        this.isValid = isValid;
    }
    public boolean getIsValid() {
        return isValid;
    }
    public String getErrorMessage() {
        return errorMessage;
    }
}
