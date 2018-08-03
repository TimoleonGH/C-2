package com.lamti.calculatorcurrencyconverter.retrofit_fixer;

import com.google.gson.annotations.SerializedName;

public class FixerConvertObject {

    @SerializedName("success")
    private boolean success;
    @SerializedName("date")
    private String date;
    @SerializedName("result")
    private double result;

    public FixerConvertObject() { }

    public FixerConvertObject(boolean success, String date, double result) {
        this.success = success;
        this.date = date;
        this.result = result;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getResult() {
        return result;
    }

    public void setResult(double result) {
        this.result = result;
    }
}
