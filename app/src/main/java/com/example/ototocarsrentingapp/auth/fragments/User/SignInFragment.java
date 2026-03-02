package com.example.ototocarsrentingapp.auth.fragments.User;

import static com.example.ototocarsrentingapp.auth.Validator.Validator.validateEmail;
import static com.example.ototocarsrentingapp.auth.Validator.Validator.validatePassword;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.ototocarsrentingapp.MainActivity;
import com.example.ototocarsrentingapp.auth.Validator.ValidationResult;
import com.example.ototocarsrentingapp.auth.ViewModel.SignUpStep;
import com.example.ototocarsrentingapp.auth.ViewModel.SignUpViewModel;
import com.example.ototocarsrentingapp.databinding.FragmentSignInBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class SignInFragment extends Fragment {
    private static final String TAG = "SignInFragment";
    private FragmentSignInBinding binding;
    private FirebaseAuth auth = auth = FirebaseAuth.getInstance();;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSignInBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SignUpViewModel vm = new ViewModelProvider(requireActivity()).get(SignUpViewModel.class);
        binding.btnBack.setOnClickListener(v -> {
            vm.onBack();
        });

        binding.btnSignIn.setOnClickListener(v -> {
            String email = binding.layoutEmail.getEditText().getText().toString().trim();
            String password = binding.tvPass.getText().toString().trim();

            //בדיקה שהאימייל תקין
            ValidationResult r = validateEmail(email);
            if(!r.getIsValid()){
                binding.layoutEmail.setError(r.getErrorMessage());
                return;
            }
            binding.layoutEmail.setError(null);

            //בדיקה אם הpassword תקין
            r = validatePassword(password);
            if(!r.getIsValid()){
                binding.layoutPassword.setError(r.getErrorMessage());
                return;
            }
            binding.layoutEmail.setError(null);

            // @TODO: implement sign in

            auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(resultTask -> {
                        if(resultTask.isSuccessful()) {
                            requireActivity().finish();
                            startActivity(new Intent(requireActivity(), MainActivity.class));
                        }
                        else {
                            Exception e = resultTask.getException();
                            String message = "שגיאה לא ידועה";

                            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                                // ERROR_INVALID_CREDENTIAL – פשוט אומרים למשתמש שהאימייל או הסיסמה שגויים
                                message = "אימייל או סיסמה לא נכונים";
                            }
                             else if (e instanceof FirebaseAuthException) {
                                message = ((FirebaseAuthException) e).getErrorCode();
                            }

                            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
                            Log.e(TAG, "signInWithEmail:failure", e);
                            }
                    });

        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
