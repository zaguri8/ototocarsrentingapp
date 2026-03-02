package com.example.ototocarsrentingapp.auth.fragments.User;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.ototocarsrentingapp.R;
import com.example.ototocarsrentingapp.auth.ViewModel.SignUpViewModel;
import com.example.ototocarsrentingapp.model.UserType;


public class SignUp3UserTypeFragment extends Fragment {
    public static String TAG = "SignUp3UserTypeFragment";


    public SignUp3UserTypeFragment() {
        super(R.layout.fragment_sign_up3_user_type);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SignUpViewModel vm = new ViewModelProvider(requireActivity()).get(SignUpViewModel.class);

        Button btnRenter = view.findViewById(R.id.btnRenter);
        Button btnSeller = view.findViewById(R.id.btnSeller);
        Button btnBack = view.findViewById(R.id.btnBack);

        //מעבר לדף משכיר
        btnRenter.setOnClickListener(v -> {
            Log.d(TAG,"btnRenter was clicked");
            vm.setUserType(UserType.RENTER);
            vm.onNext();
        });

        //מעבר לדף שוכר
        btnSeller.setOnClickListener(v -> {
            Log.d(TAG,"btnSeller was clicked");
            vm.setUserType(UserType.SELLER);
            vm.onNext();
        });

        //חזרה אחורה
        btnBack.setOnClickListener(v -> {
            Log.d(TAG,"btnBack was clicked");
            vm.onBack();
        });
    }
}