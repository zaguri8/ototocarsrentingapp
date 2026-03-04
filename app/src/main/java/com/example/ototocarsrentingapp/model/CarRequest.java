package com.example.ototocarsrentingapp.model;

public class CarRequest {

    private Renter renter;
    private Seller seller;

    private long startDate;
    private long endDate;
    private String id;
    private RequestStatus status;

    public enum RequestStatus {
        APPROVED,
        DENIED,
        PENDING
    }

    public CarRequest(Renter renter, Seller seller, long startDate, long endDate) {
        this.renter = renter;
        this.seller = seller;
        this.startDate = startDate;
        this.endDate = endDate;
        this.id = renter.getId() + "_" + seller.getId();
        this.status = RequestStatus.PENDING;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public CarRequest() {
    }

    public Renter getRenter() {
        return renter;
    }

    public void setRenter(Renter renter) {
        this.renter = renter;
    }

    public boolean isPending() {
        return status == RequestStatus.PENDING;
    }

    public boolean isApproved() {
        return status == RequestStatus.APPROVED;
    }

    public boolean isDenied() {
        return status == RequestStatus.DENIED;
    }

    public Seller getSeller() {
        return seller;
    }

    public void setSeller(Seller seller) {
        this.seller = seller;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }
}
