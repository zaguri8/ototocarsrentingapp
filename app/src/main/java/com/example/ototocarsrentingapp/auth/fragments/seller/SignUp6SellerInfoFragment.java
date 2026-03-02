package com.example.ototocarsrentingapp.auth.fragments.seller;

import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.ototocarsrentingapp.R;
import com.example.ototocarsrentingapp.auth.Validator.ValidationResult;
import com.example.ototocarsrentingapp.auth.ViewModel.SignUpViewModel;
import com.example.ototocarsrentingapp.model.CarColor;
import com.example.ototocarsrentingapp.model.CarManufacturer;


public class SignUp6SellerInfoFragment extends Fragment {

    private static final String TAG = "SignUp6SellerInfoFragment";


    public SignUp6SellerInfoFragment() {
        super(R.layout.fragment_sign_up6_seller_info);
    }



    private ActivityResultLauncher<String> mGetGalleryPermissions = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
        @Override
        public void onActivityResult(Boolean o) {
            if(o) {
                openGallery();
            }
        }
    });
    private ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
        @Override
        public void onActivityResult(Uri uri) {
            vm.setImageUri(uri);
            imageView.setImageURI(uri);
        }
    });


    private void openGallery() {
        mGetContent.launch("image/*");
    }

    private SignUpViewModel vm;
    private ImageView imageView;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EditText seatsNumber = view.findViewById(R.id.seatsNumber);//מספר מכוניות
        Spinner colorSpinner = view.findViewById(R.id.colorSpinner);//סוג רכב
        Spinner carsManufacturerSpinner = view.findViewById(R.id.carsManufacturerSpinner);//סוג רכב
        EditText carModel = view.findViewById(R.id.carModel);//דגם
        EditText carTrim = view.findViewById(R.id.carTrim);//רמת הגימור
        Button btnNext = view.findViewById(R.id.btnNext);//כפתור קדימה
        Button btnBack = view.findViewById(R.id.btnBack);//כפתור אחורה
        TextView tvValidationMessage = view.findViewById(R.id.tvValidationMessage);//הודעת שגיאה
        imageView = view.findViewById(R.id.carImage);
        view.findViewById(R.id.imageContainer).setOnClickListener(v -> {
                openGallery();
        });
        vm = new ViewModelProvider(requireActivity()).get(SignUpViewModel.class);//חיבור לview model


        //listening to seats numbers
        vm.getSeatsNumber().observe(getViewLifecycleOwner(), seats ->{
            if(seats==null){
                return;
            }
            String current = seatsNumber.getText().toString();
            if(!current.equals(seats)){//בדיקה האם הערך שהמשתמש הקליד זהה לערך בview model
                seatsNumber.setText(seats);
                seatsNumber.setSelection(seats.length());
                Log.d(TAG,"seatsNumber was updated by the view model");
            }
        });

        //listening to color
        vm.getCarColor().observe(getViewLifecycleOwner(), color ->{
            if(color==null){
                return;
            }
            String current = colorSpinner.getSelectedItem().toString();
            String colorString = color.name();//המרת enum לstring
            if(!current.equals(colorString)){//בדיקה האם הערך שהמשתמש הקליד זהה לערך בview model
                colorSpinner.setSelection(color.ordinal());
                Log.d(TAG,"color was updated by the view model");
            }
        });

        //listening to car manufacturer
        vm.getCarManufacturer().observe(getViewLifecycleOwner(), model ->{
            if(model==null){
                return;
            }
            String current = carsManufacturerSpinner.getSelectedItem().toString();
            String modelString = model.name();//המרת enum לstring
            if(!current.equals(modelString)){//בדיקה האם הערך שהמשתמש הקליד זהה לערך בview model
                carsManufacturerSpinner.setSelection(model.ordinal());
                Log.d(TAG,"model was updated by the view model");
            }
        });

        //listening to car model
        vm.getCarModelName().observe(getViewLifecycleOwner(), model ->{
            if(model==null){
                return;
            }
            String current = carModel.getText().toString();
            if(!current.equals(model)){//בדיקה האם הערך שהמשתמש הקליד זהה לערך בview model
                carModel.setText(model);
                carModel.setSelection(model.length());
                Log.d(TAG,"model was updated by the view model");
            }
        });

        //listeing to car trim
        vm.getCarTrim().observe(getViewLifecycleOwner(), trim ->{
            if(trim==null){
                return;
            }
            String current = carTrim.getText().toString();
            if(!current.equals(trim)){//בדיקה האם הערך שהמשתמש הקליד זהה לערך בview model
                carTrim.setText(trim);
                carTrim.setSelection(trim.length());
                Log.d(TAG,"trim was updated by the view model");
            }
        });

        colorSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    return;
                }
                String color = parent.getItemAtPosition(position).toString();
                CarColor carColor = CarColor.valueOf(color);
                vm.setCarColor(carColor);
                Log.d(TAG,"color was changed in the view model");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                vm.setCarColor(CarColor.לבן);
            }
        });

        carsManufacturerSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    return;
                }
                String model = parent.getItemAtPosition(position).toString();
                CarManufacturer carModel = CarManufacturer.valueOf(model);
                vm.setCarManufacturer(carModel);
                Log.d(TAG,"model was changed in the view model");
    }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                vm.setCarManufacturer(CarManufacturer.FIAT);
            }
        });
        //כפתור קדימה
        btnNext.setOnClickListener(v -> {
            Log.d(TAG,"btnNext was clicked");
            if(vm.getImageUri() == null) {
                Log.d(TAG,"Image was not picked");
                tvValidationMessage.setText("Image was not picked");
                tvValidationMessage.setVisibility(View.VISIBLE);
                return;
            }
            tvValidationMessage.setVisibility(View.GONE);
            //בדיקה שמספר המושבים תקין
            ValidationResult r = vm.setSeatsNumber(seatsNumber.getText().toString());
            if(!r.getIsValid()){
                Log.d(TAG,"Seats Numbers is not valid");
                tvValidationMessage.setText(r.getErrorMessage());
                seatsNumber.requestFocus();
                tvValidationMessage.setVisibility(View.VISIBLE);
                return;
            }
            tvValidationMessage.setVisibility(View.GONE);

            //בדיקה שדגם הרכב תקין
            r = vm.setCarModelName(carModel.getText().toString());
            if(!r.getIsValid()){
                Log.d(TAG,"Car Model is not valid");
                tvValidationMessage.setText(r.getErrorMessage());
                carModel.requestFocus();
                tvValidationMessage.setVisibility(View.VISIBLE);
                return;
            }
            Log.d(TAG,"Car Model is valid");
            tvValidationMessage.setVisibility(View.GONE);

            //בדיקה שרמת הגימור של הרכב תקינה
            r = vm.setCarTrim(carTrim.getText().toString());
            if(!r.getIsValid()){
                Log.d(TAG,"Car Trim is not valid");
                tvValidationMessage.setText(r.getErrorMessage());
                carTrim.requestFocus();
                tvValidationMessage.setVisibility(View.VISIBLE);
                return;
            }
            Log.d(TAG,"Car Trim is valid");
            tvValidationMessage.setVisibility(View.GONE);

            //יצירת אובייקט מסוג SELLER בFIRE BASE
            vm.createSeller();
            Log.d(TAG,"Seller was created successfully");

            //שימוש בAPI בשביל לחשב את שווי הרכב
            vm.getCarPriceApi();

            //מעבר לדף הבא
            vm.onNext();

        });
        //כפתור אחורה
        btnBack.setOnClickListener(v -> {
            Log.d(TAG,"btnBack was clicked");
            vm.onBack();
        });
    }
}