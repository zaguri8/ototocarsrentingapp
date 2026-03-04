package com.example.ototocarsrentingapp.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.ototocarsrentingapp.RequestsRvAdapter;
import com.example.ototocarsrentingapp.databinding.FragmentRequestsBinding;

import com.example.ototocarsrentingapp.main.ViewModel.MainViewModel;
import com.example.ototocarsrentingapp.model.CarRequest;
import com.example.ototocarsrentingapp.model.User;

public class RequestsFragment extends Fragment implements RequestsRvAdapter.OnRequestActionListener {

    private FragmentRequestsBinding binding;
    private MainViewModel viewModel;
    private RequestsRvAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRequestsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        viewModel.getUserLive().observe(getViewLifecycleOwner(), result -> {
            if (result == null || !result.isSuccess()) return;
            User current = result.getData();
            viewModel.getUserLive().removeObservers(getViewLifecycleOwner());
            viewModel.getRequests().observe(getViewLifecycleOwner(), requests -> {
                adapter = new RequestsRvAdapter(current, requests, this);
                binding.rvRequests.setAdapter(adapter);
            });
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    // On Approve Request
    @Override
    public void onApprove(CarRequest request) {
        viewModel.updateCarRequestStatus(request, CarRequest.RequestStatus.APPROVED)
                .addOnSuccessListener(succ -> {
                    Toast.makeText(requireContext(), "Request approved!, you may contact the renter", Toast.LENGTH_LONG).show();
                });
    }

    // On Deny Request
    @Override
    public void onDeny(CarRequest request) {
        viewModel.updateCarRequestStatus(request, CarRequest.RequestStatus.DENIED)
                .addOnSuccessListener(succ -> {
                    Toast.makeText(requireContext(), "Request denied, you may contact the renter", Toast.LENGTH_LONG).show();
                });
    }
}
