package com.lamti.calculatorcurrencyconverter.retrofit_fixer;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;

public class FixerObject {

    @SerializedName("success")
    private boolean success;
    @SerializedName("timestamp")
    private int timestamp;
    @SerializedName("base")
    private String base;
    @SerializedName("date")
    private String date;
    @SerializedName("rates")
    private HashMap<String, Double> rates;

    public FixerObject() { }

    public FixerObject(boolean success, int timestamp, String base, String date, HashMap<String, Double> rates) {
        this.success = success;
        this.timestamp = timestamp;
        this.base = base;
        this.date = date;
        this.rates = rates;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public HashMap<String, Double> getRates() {
        return rates;
    }

    public void setRates(HashMap<String, Double> rates) {
        this.rates = rates;
    }
}
