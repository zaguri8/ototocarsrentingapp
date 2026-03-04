package com.example.ototocarsrentingapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ototocarsrentingapp.model.Car;
import com.example.ototocarsrentingapp.model.Seller;
import com.google.android.material.button.MaterialButton;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SellersRvAdapter extends RecyclerView.Adapter<SellersRvAdapter.ViewHolder> {

    private  List<Seller> sellerList;

    public interface SellerItemClickListener {
        void onInfo(Seller seller);
    }

    private SellerItemClickListener delegate;

    public SellersRvAdapter(List<Seller> sellers,SellerItemClickListener delegate) {
        this.sellerList = sellers;
        this.delegate = delegate;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.car_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Seller seller = sellerList.get(position);
        Car car = seller.getCar();
        // Setting Text Data
        holder.tvTitle.setText(car.getCarManufacturer() + " " + car.getCarModelName());
        holder.tvTrim.setText(car.getCarModelTrim());
        holder.tvYearKm.setText(car.getYear() + " | " + car.getKilometers() + " km");
        holder.tvPrice.setText("₪" + car.getVehicleValueForOneDay() + "/day");

        Picasso.get().load(car.getImage())
                .into(holder.ivCar);
        holder.btnShowInfo.setOnClickListener(v -> {
            delegate.onInfo(seller);
        });
    }

    @Override
    public int getItemCount() {
        return sellerList != null ? sellerList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCar;
        TextView tvTitle, tvTrim, tvYearKm, tvPrice;
        MaterialButton btnShowInfo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            btnShowInfo = itemView.findViewById(R.id.btnShowInfo);
            ivCar = itemView.findViewById(R.id.ivCarImage);
            tvTitle = itemView.findViewById(R.id.tvCarTitle);
            tvTrim = itemView.findViewById(R.id.tvCarTrim);
            tvYearKm = itemView.findViewById(R.id.tvYearKm);
            tvPrice = itemView.findViewById(R.id.tvPrice);
        }
    }
}