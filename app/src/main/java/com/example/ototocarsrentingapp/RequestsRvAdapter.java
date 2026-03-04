package com.example.ototocarsrentingapp;

import static android.view.View.GONE;

import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ototocarsrentingapp.model.CarRequest;
import com.example.ototocarsrentingapp.model.User;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RequestsRvAdapter extends RecyclerView.Adapter<RequestsRvAdapter.ViewHolder> {

    private List<CarRequest> requests;
    private OnRequestActionListener listener;
    private User current;

    public interface OnRequestActionListener {
        void onApprove(CarRequest request);

        void onDeny(CarRequest request);
    }

    public RequestsRvAdapter(User current,
                             List<CarRequest> requests, OnRequestActionListener listener) {
        this.requests = requests;
        this.listener = listener;
        this.current = current;
    }

    private boolean isIncoming() {
        return current.isSeller();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.request_item, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CarRequest request = requests.get(position);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        if (isIncoming()) {
            holder.tvUserLabel.setText("From: " + request.getRenter().getFirstName());
            holder.layoutActions.setVisibility(request.isPending() ? View.VISIBLE : GONE);
            holder.btnApprove.setOnClickListener(v -> {
                listener.onApprove(request);
                holder.btnApprove.setEnabled(false);
                holder.btnDeny.setEnabled(false);

            });
            holder.btnDeny.setOnClickListener(v -> {
                listener.onDeny(request);
                holder.btnApprove.setEnabled(false);
                holder.btnDeny.setEnabled(false);
            });
        } else {
            holder.btnApprove.setVisibility(GONE);
            holder.btnDeny.setVisibility(GONE);
            holder.tvUserLabel.setText("To: " + request.getSeller().getFirstName());
            holder.layoutActions.setVisibility(GONE);
        }

        holder.tvCarName.setText(request.getSeller().getCar().getCarManufacturer() + " " + request.getSeller().getCar().getCarModelName());
        holder.tvDates.setText(sdf.format(new Date(request.getStartDate())) + " - " + sdf.format(new Date(request.getEndDate())));

        holder.tvStatusBadge.setText(request.getStatus().name());
        updateStatusStyle(holder.tvStatusBadge, request.getStatus());


    }

    private void updateStatusStyle(TextView view, CarRequest.RequestStatus status) {
        int color = 0xFF9E9E9E; // Default Grey
        if (status == CarRequest.RequestStatus.APPROVED) color = 0xFF4CAF50; // Green
        if (status == CarRequest.RequestStatus.DENIED) color = 0xFFF44336; // Red
        if (status == CarRequest.RequestStatus.PENDING) color = 0xFFFF9800; // Orange

        GradientDrawable drawable = new GradientDrawable();
        drawable.setCornerRadius(20f);
        drawable.setColor(color);
        view.setBackground(drawable);
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserLabel, tvCarName, tvDates, tvStatusBadge;
        View layoutActions;
        Button btnApprove, btnDeny;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserLabel = itemView.findViewById(R.id.tvUserLabel);
            tvCarName = itemView.findViewById(R.id.tvCarName);
            tvDates = itemView.findViewById(R.id.tvDates);
            tvStatusBadge = itemView.findViewById(R.id.tvStatusBadge);
            layoutActions = itemView.findViewById(R.id.layoutActions);
            btnApprove = itemView.findViewById(R.id.btnApprove);
            btnDeny = itemView.findViewById(R.id.btnDeny);
        }
    }
}