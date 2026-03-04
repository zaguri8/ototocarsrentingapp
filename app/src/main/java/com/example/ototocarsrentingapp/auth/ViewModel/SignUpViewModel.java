package com.example.ototocarsrentingapp.auth.ViewModel;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;//שימוש במחלקה viewmodel של אנדרויד סטודיו
import androidx.lifecycle.LiveData;//שימוש במחלקה liveData של אנדרויד סטודיו
import androidx.lifecycle.MutableLiveData;//שימוש במחלקה mutableliveData של אנדרויד סטודיו

import com.example.ototocarsrentingapp.ImageUploader;
import com.example.ototocarsrentingapp.auth.API.CarApi;
import com.example.ototocarsrentingapp.auth.API.CarPriceResponse;
import com.example.ototocarsrentingapp.auth.Validator.Validator;
import com.example.ototocarsrentingapp.auth.Validator.ValidationResult;
import com.example.ototocarsrentingapp.model.Car;
import com.example.ototocarsrentingapp.model.CarColor;
import com.example.ototocarsrentingapp.model.CarManufacturer;
import com.example.ototocarsrentingapp.model.Renter;
import com.example.ototocarsrentingapp.model.Seller;
import com.example.ototocarsrentingapp.model.User;
import com.example.ototocarsrentingapp.model.UserType;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SignUpViewModel extends ViewModel {

    private static final String TAG = "SignUpViewModel";

    //יצירת אובייקט retrofit תוך כדי שימוש בbuilder design
    private Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://api.vehicledatabases.com/market-value/") // צריך להחליף לכתובת אמיתית
            .addConverterFactory(GsonConverterFactory.create())
            .build();


    // אתחול של FirebaseAuth
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    //המצב הנוכחי
    private final MutableLiveData<SignUpStep> currentState = new MutableLiveData<>(SignUpStep.ENTRY);//השלב הנוכחי

    //נתונים שצריכים להיות לכל user
    private final MutableLiveData<String> first_name = new MutableLiveData<>();//שם פרטי
    private final MutableLiveData<String> last_name = new MutableLiveData<>();//שם משפחה
    private final MutableLiveData<String> email = new MutableLiveData<>();//אמייל
    private final MutableLiveData<String> address = new MutableLiveData<>();//כתובת
    private final MutableLiveData<String> city = new MutableLiveData<>();//עיר
    private final MutableLiveData<String> password = new MutableLiveData<>();//סיסמה
    private final MutableLiveData<Uri> image = new MutableLiveData<>(null);//תמונה
    private final MutableLiveData<String> confirm_password = new MutableLiveData<>();//אישור סיסימה
    private final MutableLiveData<UserType> user_type = new MutableLiveData<>();// סוג הUSER
    private MutableLiveData<Boolean> userCreated = new MutableLiveData<>();

    //נתונים שצריכים להיות עבור כל seller
    //אובייקט מסוג CAR
    private final MutableLiveData<String> licensePlate = new MutableLiveData<>();//לוחית רישוי
    private final MutableLiveData<CarColor> carColor = new MutableLiveData<>();//צבע רכב
    private final MutableLiveData<String> Kilometers = new MutableLiveData<>();//קילומטרים
    private final MutableLiveData<String> year = new MutableLiveData<>();//שנה
    private final MutableLiveData<CarManufacturer> carManufacturer = new MutableLiveData<>();//יצרן
    private final MutableLiveData<String> seatsNumber = new MutableLiveData<>();//מספר מושבים ברכב
    private final MutableLiveData<String> carModelName = new MutableLiveData<>();//שם הדגם
    private final MutableLiveData<String> carModelTrim = new MutableLiveData<>();//רמת גימור
    private final MutableLiveData<String> carPrice = new MutableLiveData<>();//מחיר

    //נתונים שצריכים להיות עבור כל renter
    private final MutableLiveData<String> licenseNumber = new MutableLiveData<>();//מספר רישיון
    private final MutableLiveData<Integer>  licenseExpirationDate = new MutableLiveData<>();//תוקף רישיון
    //צילום רישיון
    private final MutableLiveData<Boolean> is_seller_verified = new MutableLiveData<>();//אם המשתמש העלה תמונה אז זה מספיק לוורפיקציה

    public void setImageUri(Uri uri) {
        image.postValue(uri);
    }

    public Uri getImageUri() {
        return image.getValue();
    }
    //-----------------------------------------------------------------------
    //מתודות שמאפשרת לactivity לעבור לפי מסכים לפי הstate הנוכחי
    public void onNext(){
        // ==========================================================
        // שלב 1: פרטים אישיים
        // המטרה: לא לאפשר מעבר קדימה אם חסרים/לא תקינים:
        // שם פרטי, שם משפחה, תאריך לידה, אימייל, טלפון
        // ==========================================================
        SignUpStep step = currentState.getValue();
        if(step==null){
            step = SignUpStep.PERSONAL_DETAILS;
        }
        switch (step){
            case PERSONAL_DETAILS:
                currentState.setValue(SignUpStep.ADDRESS_DETAILS);
                break;
            case ADDRESS_DETAILS:
                currentState.setValue(SignUpStep.UserType);
                break;
            case UserType:
                UserType type = user_type.getValue();
                if(type==null){
                    Log.d(TAG,"user type was not entered");
                    return;
                }
                if(type==UserType.RENTER){
                    currentState.setValue(SignUpStep.RENTER_DETAILS);
                }
                else{
                    currentState.setValue(SignUpStep.SELLER_DETAILS1);
                }
                break;
            case SELLER_DETAILS1:
                currentState.setValue(SignUpStep.SELLER_DETAILS2);
                break;
        }
    }

    public void onBack(){
        SignUpStep step = currentState.getValue();
        if(step==null){
            step = SignUpStep.PERSONAL_DETAILS;
        }
        switch (step){
            case PERSONAL_DETAILS:
            case SIGN_IN:
                currentState.setValue(SignUpStep.ENTRY);
                break;
            case ADDRESS_DETAILS:
                currentState.setValue(SignUpStep.PERSONAL_DETAILS);
                break;
            case UserType:
                currentState.setValue(SignUpStep.ADDRESS_DETAILS);
                break;
            case RENTER_DETAILS:
                currentState.setValue(SignUpStep.UserType);
                break;
            case SELLER_DETAILS1:
                currentState.setValue(SignUpStep.UserType);
                break;
            case SELLER_DETAILS2:
                currentState.setValue(SignUpStep.SELLER_DETAILS1);
                break;
        }
    }
    //----------------------------------------------------------------------------------------
    //setters and getters
    //נתונים שצריכים להיות לכל USER

    //First name
    public ValidationResult setFirstName(String first_name) {
        ValidationResult result = Validator.validateFirstName(first_name);
        if(result.getIsValid()){
            this.first_name.setValue(first_name);
            Log.d(TAG,"first name was changed in the view model");
        }
       return result;
    }
    public LiveData<String> get_first_name() {
        return first_name;
    }

    //Last name
    public ValidationResult set_last_name(String last_name) {
        ValidationResult result = Validator.validateLastName(last_name);
        if(result.getIsValid()){
            this.last_name.setValue(last_name);
            Log.d(TAG,"Last name was changed in the view model");
        }
        return result;
    }
    public LiveData<String> get_last_name() {
        return last_name;
    }

    //Email
    public ValidationResult set_email(String email) {
        ValidationResult result = Validator.validateEmail(email);
        if(result.getIsValid()){
            this.email.setValue(email);
        }
        return result;
    }
    public LiveData<String> get_email() {
        return email;
    }

    //password
    public ValidationResult  setPassword(String value){
        ValidationResult result = Validator.validatePassword(value);
        if(result.getIsValid()){
            this.password.setValue(value);
            Log.d(TAG,"password was changed in the view model");
        }
            return result;
    }

    public LiveData<String> getPassword(){
        return this.password;
    }

    //confirmpassword
    public ValidationResult setConfirmPassword(String password, String confirmPassword){
        ValidationResult result = Validator.validateConfirmPassword(password,confirmPassword);
        if(result.getIsValid()){
            this.confirm_password.setValue(confirmPassword);
        }
        return result;
    }
    public LiveData<String>getConfirmPassword(){
        return this.confirm_password;
    }

    //כתובת
    public LiveData<String> getAddress(){
        return this.address;
    }
    public ValidationResult setAddress(String value){
        ValidationResult result = Validator.validateAddress(value);
        if(result.getIsValid()){
            this.address.setValue(value);
        }
        return result;
    }

    //עיר
    public LiveData<String>getCity(){
        return this.city;
    }
    public ValidationResult setCity(String value){
        ValidationResult result = Validator.validateCity(value);
        if(result.getIsValid()){
            this.city.setValue(value);
        }
      return result;
    }

    //סוג המשתמש
    public LiveData<UserType> getUserType(){
        return this.user_type;
    }

    public void setUserType(UserType value){
        this.user_type.setValue(value);
        Log.d(TAG,"user type was changed in the view model");
    }

    //מצב הנוכחי של המשתמש
    public LiveData<SignUpStep> getCurrentState(){
        return this.currentState;
    }
    public void setCurrentState(SignUpStep value){
        this.currentState.setValue(value);
        Log.d(TAG,"The state was changed to"+value+"  in the view model");
    }
    //האם המשתמש נוצר?
    public LiveData<Boolean> getUserCreated(){
        return userCreated;
    }

    //פעולות set וget RENTER
    //-------------------------------------------------------------------
        //license number(driver)
        public LiveData<String> getLicenseNumber(){
            return this.licenseNumber;
        }
        public ValidationResult setLicenseNumber(String value){
            ValidationResult result = Validator.validateLicenseNumber(value);
            if(result.getIsValid()){
                this.licenseNumber.setValue(value);
            }
            return result;
        }
    //------------------------------------------------------------------
    //פעולות get וset seller
    //license plate(seller)
    public LiveData<String> getLicensePlate() {
        return this.licensePlate;
    }
    public ValidationResult setLicensePlate(String value) {
        ValidationResult result = Validator.validateLicensePlate(value);
        if (result.getIsValid()) {
            this.licensePlate.setValue(value);
        }
        return result;
    }
    //kilometer(seller)
    public LiveData<String> getKilometer() {
        return this.Kilometers;
    }
    public ValidationResult setKilometer(String value) {
        ValidationResult result = Validator.validateKilometers(value);
        if (result.getIsValid()) {
            this.Kilometers.setValue(value);
        }
        return result;
    }
    public LiveData<String> getYear() {
        return this.year;
    }
    public ValidationResult setYear(String value) {
        ValidationResult result = Validator.validateYears(value);
        if (result.getIsValid()) {
            this.year.setValue(value);
        }
        return result;
    }
    //carColor
    public void setCarColor(CarColor carColor) {
        this.carColor.setValue(carColor);
        Log.d(TAG,"color was updated in the view model");
    }

    public LiveData<CarColor> getCarColor() {
        return carColor;
    }
    //carManufacturer
    public void setCarManufacturer(CarManufacturer carModel) {
        this.carManufacturer.setValue(carModel);
        Log.d(TAG,"manufacturer was updated in the view model");
    }

    public LiveData<CarManufacturer> getCarManufacturer() {
        return carManufacturer;
    }
    //seatsNumbers
    public ValidationResult setSeatsNumber(String seatsNumber) {
        ValidationResult result = Validator.validateSeatsNumbers(seatsNumber);
        if(result.getIsValid()){
            this.seatsNumber.setValue(seatsNumber);
        }
        return result;
    }
    public LiveData<String> getSeatsNumber() {
        return seatsNumber;
    }
    //CarModelName
    public MutableLiveData<String> getCarModelName() {
        return carModelName;
    }
    public ValidationResult setCarModelName(String modelName) {
        ValidationResult result = Validator.validateModel(carManufacturer.getValue(),modelName);
        if(result.getIsValid()){
            this.carModelName.setValue(modelName);
        }
        return result;
    }
    //carTrim
    public LiveData<String> getCarTrim() {
        return carModelTrim;
    }
    public ValidationResult setCarTrim(String carTrim) {
        ValidationResult result = Validator.validateTrim(carModelName.getValue(),carTrim);
        if(result.getIsValid()){
            this.carModelTrim.setValue(carTrim);
        }
        return result;
    }
    //carPrice
    private void setCarPrice(String price) {
        carPrice.setValue(price);
    }
    public LiveData<String> getCarPrice() {
        return carPrice;
    }
    //-----------------------------------------------------------------------------------
    //פונקציה שיוצרת user
    public Task<AuthResult> createUser(){
        String email = get_email().getValue();
        String password = getPassword().getValue();
        if (email == null || password == null) {
            Log.d(TAG,"email or password is null");
            return Tasks.forException(new Exception("email or password is null"));
        }
        return  mAuth.createUserWithEmailAndPassword(email, password);
    }
    
    //פונקציה שיוצרת RENTER
    public Task<User> createRenter() {
        return createUser().continueWithTask(task -> {
            if(!task.isSuccessful()) throw task.getException();
            Renter r = new Renter(
                    task.getResult().getUser().getUid(),
                    first_name.getValue(),
                    last_name.getValue(),
                    email.getValue(),
                    address.getValue(),
                    city.getValue(),
                    licenseNumber.getValue(),
                    user_type.getValue()
            );
            return db.collection("renters")
                    .document(mAuth.getCurrentUser().getUid())
                    .set(r)
                    .continueWith(task1 -> r);
        });
    }
    
    //פונקציה שיוצרת אובייקט מסוג CAR
    public Car createCar(String licensePlate,
                         String carColor,
                         String kilometers,
                         String seatsNumber,
                         String year,
                         String carModel,
                         String carModelName,
                         String carModelTrim,
                         String carImage) {

        return new Car(licensePlate,
                carColor,
                kilometers,
                seatsNumber,
                year,carModel,
                carModelName,
                carModelTrim,
                carImage);
    }
    
    //פונקציה שיוצרת אובביקט מסוג SELLER
    public Task<User> createSeller(){
       return  createUser().continueWithTask(task12 -> new ImageUploader().uploadImage("images/"+mAuth.getCurrentUser().getUid(), getImageUri())
               .continueWithTask(task ->{
                   if(!task.isSuccessful()) throw task.getException();
                   Seller s = new Seller(
                           task12.getResult().getUser().getUid(),
                           first_name.getValue(),
                           last_name.getValue(),
                           email.getValue(),
                           address.getValue(),
                           city.getValue(),
                           user_type.getValue(),
                           createCar(licensePlate.getValue(),
                                   carColor.getValue().name(),
                                   Kilometers.getValue(),
                                   seatsNumber.getValue(),
                                   year.getValue(),
                                   carManufacturer.getValue().name(),
                                   carModelName.getValue(),
                                   carModelTrim.getValue(),
                                   task.getResult())
                   );
                   return db.collection("sellers")
                           .document(mAuth.getCurrentUser().getUid()).set(s)
                           .continueWith(task1 -> s);
               }));
    }
    //-----------------------------------------------------------------------------------
    //פונקציה ששולחת נתונים לAPI ומקבלת את שווי הרכב
    public void getCarPriceApi(){
        String year = this.year.getValue();
        String manufactorer = this.carManufacturer.getValue().name();
        String model = this.carModelName.getValue();
        String apiKey = "8bafa8a60f2811f198970242ac120002";


        //יצירת הAPI
        CarApi api = retrofit.create(CarApi.class);//retrofit יוצר מחלקה דינמית בזמן הריצה. המחלקה הזו מממשת את הממשק שיצרנו.
        Log.d(TAG,"API object created");

        //יצירת הcall
        Call<CarPriceResponse> call = api.getCarPriceByYMM(apiKey,Integer.parseInt(year),manufactorer,model);
        Log.d(TAG,"Call object created");

//שליחה בקשת לשרת תוך כדי שימוש בretrofit
        call.enqueue(new Callback<CarPriceResponse>() {
            @Override
            public void onResponse(Call<CarPriceResponse> call, retrofit2.Response<CarPriceResponse> response) {
                Log.d(TAG, " entered onResponse funcion");
                Gson gson = new Gson();
                String json = gson.toJson(response.body());
                Log.d(TAG, "Response JSON: " + json);

                if (response.isSuccessful() && response.body() != null) {
                    CarPriceResponse.Data data = response.body().getData();
                    if (data != null) {
                        CarPriceResponse.MarketValue marketValue = data.getMarketValue();
                        if (marketValue != null && marketValue.getMarketValueData() != null && !marketValue.getMarketValueData().isEmpty()) {
                            CarPriceResponse.TrimItem trim = marketValue.getMarketValueData().get(0);
                            if (trim.getMarketValues() != null && !trim.getMarketValues().isEmpty()) {
                                String dealerPrice = trim.getMarketValues().get(0).getDealerRetail();
                                carPrice.postValue(dealerPrice);
                            }
                        }
                    }
                }

            }

            @Override
            public void onFailure(Call<CarPriceResponse> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
            }
        });

    }

    public void setUserInfoProvided() {
        userCreated.setValue(true);
    }
}




