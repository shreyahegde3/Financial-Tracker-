package com.financialtracker.currency;

public interface CurrencyConverter {
    double convert(String fromCurrency, String toCurrency, double amount);
}
