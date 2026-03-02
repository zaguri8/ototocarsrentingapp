package com.example.ototocarsrentingapp.auth.fragments.renter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.ototocarsrentingapp.R;
import com.example.ototocarsrentingapp.auth.Validator.ValidationResult;
import com.example.ototocarsrentingapp.auth.ViewModel.SignUpViewModel;


public class SignUp4RenterInfoFragment extends Fragment {
    private static String TAG = "SignUp4RenterInfoFragment";


    public SignUp4RenterInfoFragment() {
        super(R.layout.fragment_sign_up4_renter_info);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EditText etLicenseNumber = view.findViewById(R.id.licenseNumber);
        Button btnBack = view.findViewById(R.id.btnBack);//כפתור אחורה
        Button btnNext = view.findViewById(R.id.btnNext);//כפתור קדימה
        TextView tvValidationMessage = view.findViewById(R.id.tvValidationMessage);//הודעת תקינות

        SignUpViewModel vm = new ViewModelProvider(requireActivity()).get(SignUpViewModel.class);//חיבור לview model
        //האזנה למספר רישיון נהג
        vm.getLicenseNumber().observe(getViewLifecycleOwner(), licenseNumber ->{
            if(licenseNumber==null){
                return;
            }
            String current = etLicenseNumber.getText().toString();
            if(!current.equals(licenseNumber)){//בדיקה האם הערך שהמשתמש הקליד זהה לערך בview model
                etLicenseNumber.setText(licenseNumber);
                etLicenseNumber.setSelection(licenseNumber.length());
                Log.d(TAG,"licenseNumber was updated by the view model");
            }
        });
        //כפתור אחורה
        btnBack.setOnClickListener(v -> {
            Log.d(TAG,"btnBack was clicked");
            vm.onBack();
        });
        //בדיקה עבור מספר רישיון נהג
        btnNext.setOnClickListener(v -> {
            Log.d(TAG,"btnNext was clicked");
            ValidationResult result = vm.setLicenseNumber(etLicenseNumber.getText().toString());
            if(!result.getIsValid()){
                Log.d(TAG,"licenseNumber is not valid");
                etLicenseNumber.requestFocus();
                tvValidationMessage.setText(result.getErrorMessage());
                tvValidationMessage.setVisibility(View.VISIBLE);
                return;
            }
            Log.d(TAG,"licenseNumber is valid");
            tvValidationMessage.setVisibility(View.GONE);
            //יצירת אובייקט מסוג renter
            vm.createRenter();
            Log.d(TAG,"RENTER was created");
            vm.onNext();
        });

    }
}