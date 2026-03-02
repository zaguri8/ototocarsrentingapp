package com.example.ototocarsrentingapp.auth.fragments.User;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.ototocarsrentingapp.auth.ViewModel.SignUpStep;
import com.example.ototocarsrentingapp.auth.ViewModel.SignUpViewModel;
import com.example.ototocarsrentingapp.databinding.FragmentEntryBinding;

public class EntryFragment extends Fragment {

    private FragmentEntryBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentEntryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SignUpViewModel vm = new ViewModelProvider(requireActivity()).get(SignUpViewModel.class);
        binding.btnSignIn.setOnClickListener(v -> {
            vm.setCurrentState(SignUpStep.SIGN_IN);
        });
        binding.btnSignUp.setOnClickListener(v -> {
            vm.setCurrentState(SignUpStep.PERSONAL_DETAILS);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
