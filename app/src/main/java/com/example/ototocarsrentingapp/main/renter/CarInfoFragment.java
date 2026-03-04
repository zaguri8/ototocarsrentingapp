package com.example.ototocarsrentingapp.main.renter;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.ototocarsrentingapp.R;
import com.example.ototocarsrentingapp.databinding.FragmentCarInfoBinding;
import com.example.ototocarsrentingapp.databinding.FragmentRenterHomeBinding;
import com.example.ototocarsrentingapp.databinding.FragmentSellerHomeBinding;
import com.example.ototocarsrentingapp.main.ViewModel.MainViewModel;
import com.example.ototocarsrentingapp.model.Car;
import com.example.ototocarsrentingapp.model.CarRequest;
import com.example.ototocarsrentingapp.model.Seller;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CarInfoFragment extends Fragment {

    private FragmentCarInfoBinding binding;

    private MainViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCarInfoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);
        binding.btnRequestCar.setEnabled(false);


        if (getArguments() != null) {
            String sellerJson = getArguments().getString("seller");
            Seller seller = new Gson().fromJson(sellerJson, Seller.class);
            Car car = seller.getCar();
            viewModel.getRequests().observe(getViewLifecycleOwner(), carRequests -> {
                binding.btnRequestCar.setEnabled(viewModel.canSendCarRequest(seller));

            });
            // Bind Car Data
            binding.tvFullTitle.setText(car.getCarManufacturer() + " " + car.getCarModelName());
            binding.tvDetailedTrim.setText(car.getCarModelTrim());
            binding.tvPriceTag.setText("₪" + car.getVehicleValueForOneDay() + " / day");
            binding.tvInfoYear.setText(car.getYear());
            binding.tvInfoKm.setText(car.getKilometers() + " km");
            binding.tvInfoSeats.setText(car.getSeatsNumber());
            binding.tvInfoColor.setText(car.getCarColor());

            // Bind Seller Data
            binding.tvSellerName.setText(seller.getFirstName() + " " + seller.getLastName());
            binding.tvSellerLocation.setText(seller.getCity() + ", " + seller.getAddress());

            Picasso.get()
                    .load(car.getImage())
                    .placeholder(R.drawable.baseline_image_24)
                    .into(binding.ivDetailedImage);
            // Button Listeners
            binding.btnEmailSeller.setOnClickListener(v -> {
                sendEmailToSeller(seller.getEmail(), car.getCarModelName());
            });

            binding.btnRequestCar.setOnClickListener(v -> {
                CalendarConstraints constraints = new CalendarConstraints.Builder()
                        .setValidator(DateValidatorPointForward.now())
                        .build();

                MaterialDatePicker<Pair<Long, Long>> datePicker = MaterialDatePicker.Builder.dateRangePicker()
                        .setTitleText("Select Rental Dates")
                        .setCalendarConstraints(constraints)
                        .setTheme(com.google.android.material.R.style.ThemeOverlay_MaterialComponents_MaterialCalendar_Fullscreen)
                        .build();

                datePicker.addOnPositiveButtonClickListener(selection -> {
                    if (selection != null && selection.first != null && selection.second != null) {
                        long startDate = selection.first;
                        long endDate = selection.second;

                        handleCarRequest(startDate, endDate, seller);
                    }
                });

                datePicker.show(getParentFragmentManager(), "DATE_PICKER");
            });
        }
    }


    // Example of the final request logic
    private void handleCarRequest(long start, long end, Seller seller) {
        // Convert millis to days for a summary (optional)
        long diff = end - start;
        long days = (diff / (1000 * 60 * 60 * 24)) + 1;

        new AlertDialog.Builder(requireContext())
                .setTitle("Confirm Request")
                .setMessage("Requesting " + seller.getCar().getCarModelName() + " for " + days + " days.\nTotal: ₪" + (days * seller.getCar().getVehicleValueForOneDay()))
                .setPositiveButton("Send Request", (dialog, which) -> {
                    binding.btnRequestCar.setEnabled(false);
                    Toast.makeText(getContext(), "Sending request to seller..", Toast.LENGTH_SHORT).show();
                    viewModel.sendCarRequest(seller, start, end).addOnSuccessListener(done -> {
                        Toast.makeText(getContext(), "Request sent to seller!", Toast.LENGTH_SHORT).show();
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void sendEmailToSeller(String email, String model) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Interest in renting: " + model);
        startActivity(Intent.createChooser(intent, "Send email..."));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
