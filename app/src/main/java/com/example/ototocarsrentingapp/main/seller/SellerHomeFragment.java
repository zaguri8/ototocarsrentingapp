package com.example.ototocarsrentingapp.main.seller;

import static androidx.navigation.fragment.FragmentKt.findNavController;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.ototocarsrentingapp.R;
import com.example.ototocarsrentingapp.RequestsRvAdapter;
import com.example.ototocarsrentingapp.databinding.FragmentRenterHomeBinding;
import com.example.ototocarsrentingapp.databinding.FragmentSellerHomeBinding;
import com.example.ototocarsrentingapp.main.ViewModel.MainViewModel;
import com.example.ototocarsrentingapp.model.Car;
import com.example.ototocarsrentingapp.model.CarRequest;
import com.example.ototocarsrentingapp.model.Seller;
import com.example.ototocarsrentingapp.model.User;
import com.squareup.picasso.Picasso;

public class SellerHomeFragment extends Fragment {

    private FragmentSellerHomeBinding binding;
    private MainViewModel viewModel;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSellerHomeBinding.inflate(inflater,container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);


        viewModel.getUserLive().observe(getViewLifecycleOwner(), result -> {
            if (result == null || !result.isSuccess()) return;

            User user = result.getData();
            viewModel.getUserLive().removeObservers(getViewLifecycleOwner());

            viewModel.getRequests().observe(getViewLifecycleOwner(), requests -> {
                showLoading(false);

                long approved = requests.stream().filter(CarRequest::isApproved).count();
                long denied = requests.stream().filter(CarRequest::isDenied).count();
                long pending = requests.stream().filter(CarRequest::isPending).count();

                binding.tvCountApproved.setText(String.valueOf(approved));
                binding.tvCountDenied.setText(String.valueOf(denied));
                binding.tvCountPending.setText(String.valueOf(pending));

                binding.tvSellerEmail.setText(user.getEmail());
                binding.tvSellerFullName.setText(user.getFirstName() + " " + user.getLastName());

                if (user.isSeller()) {
                    Seller seller = (Seller) user;
                    Car car = seller.getCar();

                    if (car != null) {
                        binding.tvDashboardCarTitle.setText(car.getCarManufacturer() + " " + car.getCarModelName());

                        String specs = String.format("%s • %s • %s Seats",
                                car.getYear(),
                                car.getCarColor(),
                                car.getSeatsNumber());
                        binding.tvDashboardCarSpecs.setText(specs);

                        binding.tvDashboardCarPrice.setText("₪" + car.getVehicleValueForOneDay());

                        if (car.getImage() != null && !car.getImage().isEmpty()) {
                            Picasso.get()
                                    .load(car.getImage())
                                    .placeholder(R.drawable.baseline_image_24)
                                    .error(R.drawable.baseline_image_24)
                                    .into(binding.ivDashboardCarImage);
                        }
                    }
                }
            });
        });
        binding.btnViewRequests.setOnClickListener(v -> {
            findNavController(this).navigate(R.id.action_global_to_requestsFragment2);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    void showLoading(boolean isLoading) {
        if (isLoading) {
            binding.loadingLayout.setVisibility(View.VISIBLE);
            binding.mainContentScroll.setVisibility(View.GONE);
        } else {
            binding.loadingLayout.setVisibility(View.GONE);
            binding.mainContentScroll.setVisibility(View.VISIBLE);
        }
    }
}
