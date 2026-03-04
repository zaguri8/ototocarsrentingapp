package com.example.ototocarsrentingapp.main.renter;

import static android.view.View.GONE;

import static androidx.navigation.fragment.FragmentKt.findNavController;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.ototocarsrentingapp.R;
import com.example.ototocarsrentingapp.SellersRvAdapter;
import com.example.ototocarsrentingapp.databinding.FragmentRenterHomeBinding;
import com.example.ototocarsrentingapp.main.ViewModel.MainViewModel;
import com.example.ototocarsrentingapp.model.Seller;
import com.google.gson.Gson;

public class RenterHomeFragment extends Fragment implements SellersRvAdapter.SellerItemClickListener {

    private FragmentRenterHomeBinding binding;

    private SellersRvAdapter adapter;
    private MainViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentRenterHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity())
                .get(MainViewModel.class);

        viewModel.getSellers()
                .addOnSuccessListener(sellers -> {
                    adapter = new SellersRvAdapter(sellers, this);
                    binding.rvCars.setAdapter(adapter);
                    binding.pBar.setVisibility(GONE);
                });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onInfo(Seller seller) {
        Bundle b = new Bundle();
        b.putString("seller", new Gson().toJson(seller));
        findNavController(this).navigate(com.example.ototocarsrentingapp.R.id.action_renterHomeFragment_to_carInfoFragment,b);
    }
}
