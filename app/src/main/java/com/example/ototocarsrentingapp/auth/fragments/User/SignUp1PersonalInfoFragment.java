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
import android.widget.Toast;

import com.example.ototocarsrentingapp.R;
import com.example.ototocarsrentingapp.auth.Validator.ValidationResult;
import com.example.ototocarsrentingapp.auth.ViewModel.SignUpViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;


public class SignUp1PersonalInfoFragment extends Fragment {

    private static final String TAG = "SignUp1PersonalInfoFragment";


    public SignUp1PersonalInfoFragment() {
        super(R.layout.fragment_sign_up1_personal_info);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        SignUpViewModel vm = new ViewModelProvider(requireActivity()).get(SignUpViewModel.class);//קישור בין fragment 1 לviewmodel
        //input שדות
        EditText etFirstName = view.findViewById(R.id.etFirstName);//שם פרטי
        EditText etLastName = view.findViewById(R.id.etLastName);//שם משפחה
        EditText etEmail = view.findViewById(R.id.etEmail);//אימייל
        EditText etPassword = view.findViewById(R.id.etPassword);//סיסמה
        EditText etConfirmPassword = view.findViewById(R.id.etConfirmPassword);//אישור סיסמה


        Button btnNext = view.findViewById(R.id.btnNext);
        TextView tvValidationMessage = view.findViewById(R.id.tvValidationMessage);


        //האזנה לשם הפרטי
        vm.get_first_name().observe(getViewLifecycleOwner(),firstName ->{
            if(firstName==null){
                return;
            }
            String current = etFirstName.getText().toString();
            if(!current.equals(firstName)){//בדיקה האם הערך שהמשתמש הקליד זהה לערך בview model
                etFirstName.setText(firstName);
                etFirstName.setSelection(firstName.length());//שמים את הסמן בסוף
                Log.d(TAG,"firstName was updated by the view model");
            }
        });

        //האזנה לשם המשפחה
        vm.get_last_name().observe(getViewLifecycleOwner(),lastName ->{
            if(lastName==null){
                return;
            }
            String current = etLastName.getText().toString();
            if(!current.equals(lastName)){//בדיקה האם הערך שהמשתמש הקליד זהה לערך בview model
                etLastName.setText(lastName);
                etLastName.setSelection(lastName.length());
                Log.d(TAG,"lastName was updated by the view model");
            }
        });

        //האזנה לאימייל
        vm.get_email().observe(getViewLifecycleOwner(),email ->{
            if(email==null){
                return;
            }
            String current = etEmail.getText().toString();
            if(!current.equals(email)){//בדיקה האם הערך שהמשתמש הקליד זהה לערך בview model
                etEmail.setText(email);
                etEmail.setSelection(email.length());
                Log.d(TAG,"email was updated by the view model");
            }
        });

        //האזנה לסיסמה
        vm.getPassword().observe(getViewLifecycleOwner(), password ->{
            if(password==null){
                return;
            }
            String current = etPassword.getText().toString();
            if(!current.equals(password)){//בדיקה האם הערך שהמשתמש הקליד זהה לערך בview model
                etPassword.setText(password);
                etPassword.setSelection(password.length());
                Log.d(TAG,"password was updated by the view model");
            }
        });

        //האזנה לאישור סיסמה
        vm.getConfirmPassword().observe(getViewLifecycleOwner(), confirmPassword ->{
            if(confirmPassword==null){
                return;
            }
            String current = etConfirmPassword.getText().toString();
            if(!current.equals(confirmPassword)){//בדיקה האם הערך שהמשתמש הקליד זהה לערך בview model
                etConfirmPassword.setText(confirmPassword);
                etConfirmPassword.setSelection(confirmPassword.length());
                Log.d(TAG,"confirm password was updated by the view model");
            }
        });
        //האזנה לאם המשתמש נוצר
        vm.getUserCreated().observe(getViewLifecycleOwner(), userCreated -> {
            if(userCreated){
                Log.d(TAG,"user was created successfully");
                vm.onNext();
            }
            else{
                Log.d(TAG,"user was not created successfully");
                tvValidationMessage.setText("המשתמש כבר נוצר");
                tvValidationMessage.setVisibility(View.VISIBLE);
            }
        });
        //כפתור next
        btnNext.setOnClickListener(v -> {
            //בדיקה שהם הפרטי תקין
            String firstName = etFirstName.getText().toString();
            ValidationResult result=vm.setFirstName(firstName);
            if(!result.getIsValid()){
                Log.d(TAG,"firstName is not valid");
                tvValidationMessage.setText(result.getErrorMessage());
                tvValidationMessage.setVisibility(View.VISIBLE);
                etFirstName.requestFocus();
                return;
            }
            else{
                Log.d(TAG,"first name is valid");
                tvValidationMessage.setVisibility(View.GONE);
            }
            //בדיקה עבור שם משפחה

            String lastName = etLastName.getText().toString();
            ValidationResult result2=vm.set_last_name(lastName);
            if(!result2.getIsValid()){
                Log.d(TAG,"lastName is not valid");
                tvValidationMessage.setText(result2.getErrorMessage());
                tvValidationMessage.setVisibility(View.VISIBLE);
                etLastName.requestFocus();
                return;
            }
            else{
                Log.d(TAG,"Last name is valid");
                tvValidationMessage.setVisibility(View.GONE);
            }

            //בדיקה עבור אימייל
            String email = etEmail.getText().toString();
            ValidationResult result3 = vm.set_email(email);
            if(!result3.getIsValid()){
                Log.d(TAG,"email is not valid");
                tvValidationMessage.setText(result3.getErrorMessage());
                tvValidationMessage.setVisibility(View.VISIBLE);
                etEmail.requestFocus();
                return;
            }
            else{
                Log.d(TAG,"email is valid");
                tvValidationMessage.setVisibility(View.GONE);
            }
            String password = etPassword.getText().toString();
            ValidationResult result4=vm.setPassword(password);
            if(!result4.getIsValid()){
                Log.d(TAG,"password is not valid");
                tvValidationMessage.setText(result4.getErrorMessage());
                tvValidationMessage.setVisibility(View.VISIBLE);
                etPassword.requestFocus();
                return;
            }
            else{
                Log.d(TAG,"password is valid");
                tvValidationMessage.setVisibility(View.GONE);
            }

            String confirmPassword = etConfirmPassword.getText().toString();
            ValidationResult result5=vm.setConfirmPassword(password,confirmPassword);
            if(!result5.getIsValid()){
                Log.d(TAG,"confirm password is not valid");
                tvValidationMessage.setText(result5.getErrorMessage());
                tvValidationMessage.setVisibility(View.VISIBLE);
                etConfirmPassword.requestFocus();
                return;
            }
            else{
                Log.d(TAG,"confirm password is valid");
                tvValidationMessage.setVisibility(View.GONE);
            }

            //all good :)
            vm.setUserInfoProvided();
        });






    }
}