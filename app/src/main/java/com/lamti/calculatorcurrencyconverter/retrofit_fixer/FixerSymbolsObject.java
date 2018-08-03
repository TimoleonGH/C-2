package com.lamti.calculatorcurrencyconverter.retrofit_fixer;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;

public class FixerSymbolsObject {

    @SerializedName("success")
    private boolean success;
    @SerializedName("symbols")
    private HashMap<String, String> symbols;

    public FixerSymbolsObject() { }

    public FixerSymbolsObject(boolean success, HashMap<String, String> symbols) {
        this.success = success;
        this.symbols = symbols;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public HashMap<String, String> getSymbols() {
        return symbols;
    }

    public void setSymbols(HashMap<String, String> symbols) {
        this.symbols = symbols;
    }
}
