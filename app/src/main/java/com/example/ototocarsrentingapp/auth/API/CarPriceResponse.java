package com.example.ototocarsrentingapp.auth.API;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CarPriceResponse {

    @SerializedName("data")
    private Data data;

    public Data getData() { return data; }

    public static class Data {
        @SerializedName("market_value")
        private MarketValue marketValue;

        public MarketValue getMarketValue() { return marketValue; }
    }

    public static class MarketValue {
        @SerializedName("market_value_data")
        private List<TrimItem> marketValueData;

        public List<TrimItem> getMarketValueData() { return marketValueData; }
    }

    public static class TrimItem {
        @SerializedName("market value")
        private List<MarketValueDetail> marketValues;

        public List<MarketValueDetail> getMarketValues() { return marketValues; }
    }

    public static class MarketValueDetail {
        @SerializedName("Dealer Retail")
        private String dealerRetail;

        public String getDealerRetail() { return dealerRetail; }
    }

}