package com.example.ototocarsrentingapp.auth.fragments.seller;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.ototocarsrentingapp.R;
import com.example.ototocarsrentingapp.auth.Validator.ValidationResult;
import com.example.ototocarsrentingapp.auth.ViewModel.SignUpViewModel;


public class SignUp5SellerInfoFragment extends Fragment {
    public static final String TAG = "SignUp5SellerInfoFragment";

    public SignUp5SellerInfoFragment() {
        super(R.layout.fragment_sign_up5_seller_info);
    }





    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EditText etLicenseNumber = view.findViewById(R.id.licensePlateNumber);//לוחית רישוי
        EditText etKilometers = view.findViewById(R.id.etKilometers);//קילומטרים
        EditText etYears = view.findViewById(R.id.etYears);//שנים
        TextView tvValidationMessage = view.findViewById(R.id.tvValidationMessage);//הודעת תקינות
        Button btnBack = view.findViewById(R.id.btnBack);//כפתור אחורה
        Button btnNext = view.findViewById(R.id.btnNext);//כפתור קדימה
        SignUpViewModel vm = new ViewModelProvider(requireActivity()).get(SignUpViewModel.class);//חיבור לview model

        //האזזנה ללוחית רישוי
        vm.getLicensePlate().observe(getViewLifecycleOwner(), licensePlate ->{
            if(licensePlate==null){
                return;
            }
            String current = etLicenseNumber.getText().toString();
            if(!current.equals(licensePlate)){//בדיקה האם הערך שהמשתמש הקליד זהה לערך בview model
                etLicenseNumber.setText(licensePlate);
                etLicenseNumber.setSelection(licensePlate.length());
                Log.d(TAG,"licensePlate was updated by the view model");
            }
        });
        //האזנה לקילומטרים
        vm.getKilometer().observe(getViewLifecycleOwner(), kilometers ->{
            if(kilometers==null){
                return;
            }
            String current = etKilometers.getText().toString();
            if(!current.equals(kilometers)){//בדיקה האם הערך שהמשתמש הקליד זהה לערך בview model
                etKilometers.setText(kilometers);
                etKilometers.setSelection(kilometers.length());
                Log.d(TAG,"kilometers was updated by the view model");
            }
        });
        //האזנה לשנים
        vm.getYear().observe(getViewLifecycleOwner(), years ->{
            if(years==null){
                return;
            }
            String current = etYears.getText().toString();
            if(!current.equals(years)){//בדיקה האם הערך שהמשתמש הקליד זהה לערך בview model
                etYears.setText(years);
                etYears.setSelection(years.length());
                Log.d(TAG,"years was updated by the view model");
            }
        });

        btnBack .setOnClickListener(v -> {
            Log.d(TAG,"btnBack was clicked");
            vm.onBack();
        });
        btnNext.setOnClickListener(v -> {
            //בדיקה עבור לוחית רישוי
            Log.d(TAG,"btnNext was clicked");
            ValidationResult result = vm.setLicensePlate(etLicenseNumber.getText().toString());
            if(!result.getIsValid()){
                Log.d(TAG,"licenseNumber is not valid");
                etLicenseNumber.requestFocus();
                tvValidationMessage.setText(result.getErrorMessage());
                tvValidationMessage.setVisibility(View.VISIBLE);
                return;
            }
            Log.d(TAG,"licenseNumber is valid");
            tvValidationMessage.setVisibility(View.GONE);
            //עבור קילומטריםבדיקה
            result = vm.setKilometer(etKilometers.getText().toString());
            if(!result.getIsValid()){
                Log.d(TAG,"kilometers is not valid");
                etKilometers.requestFocus();
                tvValidationMessage.setText(result.getErrorMessage());
                tvValidationMessage.setVisibility(View.VISIBLE);
                return;
            }
            Log.d(TAG,"kilometers is valid");
            tvValidationMessage.setVisibility(View.GONE);
            //בדיקה עבור שנים
            result = vm.setYear(etYears.getText().toString());
            if(!result.getIsValid()){
                Log.d(TAG,"years is not valid");
                etYears.requestFocus();
                tvValidationMessage.setText(result.getErrorMessage());
                tvValidationMessage.setVisibility(View.VISIBLE);
                return;
            }
            Log.d(TAG,"years is valid");
            tvValidationMessage.setVisibility(View.GONE);
            vm.onNext();
        });

    }
}