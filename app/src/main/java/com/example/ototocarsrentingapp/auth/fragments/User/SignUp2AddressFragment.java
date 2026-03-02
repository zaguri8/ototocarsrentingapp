package com.example.ototocarsrentingapp.auth.fragments.User;

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


public class SignUp2AddressFragment extends Fragment {
    private static final String TAG = "SignUp2AddressFragment";

    public SignUp2AddressFragment() {
        super(R.layout.fragment_sign_up2_addrees_info);
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG,"onViewCreated successful");
        //חיבור בין הviews של הXML לjava
        EditText city = view.findViewById(R.id.city);
        EditText address = view.findViewById(R.id.address);
        Button btnNext = view.findViewById(R.id.btnNext);
        TextView tvValidationMessage = view.findViewById(R.id.tvValidationMessage);
        Button btnBack = view.findViewById(R.id.btnBack);

        //חיבור לSign Up view model
        SignUpViewModel vm = new ViewModelProvider(requireActivity()).get(SignUpViewModel.class);

        //האזנה לעיר
        vm.getCity().observe(getViewLifecycleOwner(), city1 ->{
            if(city1==null){
                return;
            }
            String current = city.getText().toString();
            if(!current.equals(city1)){//בדיקה האם הערך שהמשתמש הקליד זהה לערך בview model
                city.setText(city1);
                city.setSelection(city1.length());
                Log.d(TAG,"city was updated by the view model");
            }
        });

        //האזנה לכתובת
        vm.getAddress().observe(getViewLifecycleOwner(), addres ->{
            if(addres==null){
                return;
            }
            String current = address.getText().toString();
            if(!current.equals(addres)){//בדיקה האם הערך שהמשתמש הקליד זהה לערך בview model
                address.setText(addres);
                address.setSelection(addres.length());
                Log.d(TAG,"addres was updated by the view model");
            }
        });

        btnNext.setOnClickListener(v -> {
            //בדיקה שהcity תקין
            ValidationResult result= vm.setCity(city.getText().toString());
            if(!result.getIsValid()){
                Log.d(TAG,"city is not valid");
                tvValidationMessage.setText(result.getErrorMessage());
                tvValidationMessage.setVisibility(View.VISIBLE);
                city.requestFocus();
                return;
            }
            else{
                Log.d(TAG,"city is valid");
                tvValidationMessage.setVisibility(View.GONE);
            }
            //בדיקה שהaddress תקין
            ValidationResult result1 = vm.setAddress(address.getText().toString());
            if(!result1.getIsValid()){
                Log.d(TAG,"address is not valid");
                tvValidationMessage.setText(result1.getErrorMessage());
                tvValidationMessage.setVisibility(View.VISIBLE);
                address.requestFocus();
                return;
            }
            else{
                Log.d(TAG,"address is valid");
                tvValidationMessage.setVisibility(View.GONE);
            }
            //אפשר לעבור קדימה
            vm.onNext();
        });

        //לחיצה על הכפתור אחורה[
        btnBack.setOnClickListener(v -> {
            vm.onBack();
        });
    }
}