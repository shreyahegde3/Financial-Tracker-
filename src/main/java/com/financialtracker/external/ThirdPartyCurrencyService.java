package com.financialtracker.external;

// Simulate a third-party currency conversion API/service
public class ThirdPartyCurrencyService {
    // Returns the converted amount from one currency to another
    public double convert(String fromCurrency, String toCurrency, double amount) {
        // Simulated conversion logic (for demo purposes, multiply by 0.012 for INR to USD, else 1.0)
        if (fromCurrency.equals("INR") && toCurrency.equals("USD")) {
            return amount * 0.012;
        }
        // Add more conversion logic as needed
        return amount;
    }
}
