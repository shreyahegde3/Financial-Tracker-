package com.financialtracker.currency;

import com.financialtracker.external.ThirdPartyCurrencyService;
import org.springframework.stereotype.Component;

// Adapter: Implements your app's CurrencyConverter interface, wraps the third-party service
@Component
public class ThirdPartyCurrencyAdapter implements CurrencyConverter {
    private final ThirdPartyCurrencyService thirdPartyService;

    public ThirdPartyCurrencyAdapter() {
        this.thirdPartyService = new ThirdPartyCurrencyService();
    }

    @Override
    public double convert(String fromCurrency, String toCurrency, double amount) {
        return thirdPartyService.convert(fromCurrency, toCurrency, amount);
    }
}
