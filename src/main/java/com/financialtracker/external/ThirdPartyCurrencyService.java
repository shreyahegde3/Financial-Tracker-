package com.financialtracker.external;

// Simulate a third-party currency conversion API/service
public class ThirdPartyCurrencyService {
    // Returns the converted amount from one currency to another
    public double convert(String fromCurrency, String toCurrency, double amount) {
        if (fromCurrency.equals(toCurrency)) {
            return amount;
        }
        if (fromCurrency.equals("INR") && toCurrency.equals("USD")) {
            return amount * 0.012;
        }
        if (fromCurrency.equals("USD") && toCurrency.equals("INR")) {
            return amount * 83.0;
        }
        // Add more currency pairs as needed
        return amount; // fallback: no conversion
    }
}
