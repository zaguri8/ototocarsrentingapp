package com.example.ototocarsrentingapp.auth.fragments.User;

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
import android.widget.Toast;

import com.example.ototocarsrentingapp.R;
import com.example.ototocarsrentingapp.auth.Validator.ValidationResult;
import com.example.ototocarsrentingapp.auth.ViewModel.SignUpViewModel;
import com.example.ototocarsrentingapp.databinding.FragmentSignUp1PersonalInfoBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;


public class SignUp1PersonalInfoFragment extends Fragment {
    private FragmentSignUp1PersonalInfoBinding binding;
    private static final String TAG = "SignUp1PersonalInfoFragment";




    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // יצירת Binding מתוך ה-XML של ה-Fragment
        binding = FragmentSignUp1PersonalInfoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        SignUpViewModel vm = new ViewModelProvider(requireActivity()).get(SignUpViewModel.class);//קישור בין fragment 1 לviewmodel
        //input שדות


        Button btnNext = view.findViewById(R.id.btnNext);
        TextView tvValidationMessage = view.findViewById(R.id.tvValidationMessage);


        //האזנה לשם הפרטי
        vm.get_first_name().observe(getViewLifecycleOwner(),firstName ->{
            if(firstName==null){
                return;
            }
            String current = binding.etFirstName.getText().toString();
            if(!current.equals(firstName)){//בדיקה האם הערך שהמשתמש הקליד זהה לערך בview model
                binding.etFirstName.setText(firstName);
                binding.etFirstName.setSelection(firstName.length());//שמים את הסמן בסוף
                Log.d(TAG,"firstName was updated by the view model");
            }
        });

        //האזנה לשם המשפחה
        vm.get_last_name().observe(getViewLifecycleOwner(),lastName ->{
            if(lastName==null){
                return;
            }
            String current = binding.etLastName.getText().toString();
            if(!current.equals(lastName)){//בדיקה האם הערך שהמשתמש הקליד זהה לערך בview model
                binding.etLastName.setText(lastName);
                binding.etLastName.setSelection(lastName.length());
                Log.d(TAG,"lastName was updated by the view model");
            }
        });

        //האזנה לאימייל
        vm.get_email().observe(getViewLifecycleOwner(),email ->{
            if(email==null){
                return;
            }
            String current = binding.etEmail.getText().toString();
            if(!current.equals(email)){//בדיקה האם הערך שהמשתמש הקליד זהה לערך בview model
                binding.etEmail.setText(email);
                binding.etEmail.setSelection(email.length());
                Log.d(TAG,"email was updated by the view model");
            }
        });

        //האזנה לסיסמה
        vm.getPassword().observe(getViewLifecycleOwner(), password ->{
            if(password==null){
                return;
            }
            String current = binding.etPassword.getText().toString();
            if(!current.equals(password)){//בדיקה האם הערך שהמשתמש הקליד זהה לערך בview model
                binding.etPassword.setText(password);
                binding.etPassword.setSelection(password.length());
                Log.d(TAG,"password was updated by the view model");
            }
        });

        //האזנה לאישור סיסמה
        vm.getConfirmPassword().observe(getViewLifecycleOwner(), confirmPassword ->{
            if(confirmPassword==null){
                return;
            }
            String current = binding.etConfirmPassword.getText().toString();
            if(!current.equals(confirmPassword)){//בדיקה האם הערך שהמשתמש הקליד זהה לערך בview model
                binding.etConfirmPassword.setText(confirmPassword);
                binding.etConfirmPassword.setSelection(confirmPassword.length());
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
            String firstName = binding.etFirstName.getText().toString();
            ValidationResult result=vm.setFirstName(firstName);
            if(!result.getIsValid()){
                Log.d(TAG,"firstName is not valid");
                binding.layoutFirstName.setError(result.getErrorMessage());
                return;
            }
            else{
                Log.d(TAG,"first name is valid");
                binding.layoutFirstName.setError(null);
            }
            //בדיקה עבור שם משפחה

            String lastName = binding.etLastName.getText().toString();
            ValidationResult result2=vm.set_last_name(lastName);
            if(!result2.getIsValid()){
                Log.d(TAG,"lastName is not valid");
                binding.layoutLastName.setError(result2.getErrorMessage());
                return;
            }
            else{
                Log.d(TAG,"Last name is valid");
                binding.layoutLastName.setError(null);
            }

            //בדיקה עבור אימייל
            String email = binding.etEmail.getText().toString();
            ValidationResult result3 = vm.set_email(email);
            if(!result3.getIsValid()){
                Log.d(TAG,"email is not valid");
                binding.layoutEmail.setError(result3.getErrorMessage());
                return;
            }
            else{
                Log.d(TAG,"email is valid");
                binding.layoutEmail.setError(null);
            }
            String password = binding.etPassword.getText().toString();
            ValidationResult result4=vm.setPassword(password);
            if(!result4.getIsValid()){
                Log.d(TAG,"password is not valid");
               binding.layoutPassword.setError(result4.getErrorMessage());
                return;
            }
            else{
                Log.d(TAG,"password is valid");
                binding.layoutPassword.setError(null);
            }

            String confirmPassword = binding.etConfirmPassword.getText().toString();
            ValidationResult result5=vm.setConfirmPassword(password,confirmPassword);
            if(!result5.getIsValid()){
                Log.d(TAG,"confirm password is not valid");
                binding.layoutConfirmPassword.setError(result5.getErrorMessage());
                return;
            }
            else{
                Log.d(TAG,"confirm password is valid");
                binding.layoutConfirmPassword.setError(null);
            }

            //all good :)
            vm.setUserInfoProvided();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // שחרור Binding למניעת memory leaks
        binding = null;
    }
}