package com.financialtracker.proxy;

import com.financialtracker.currency.CurrencyConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.Map;

/**
 * Proxy for CurrencyConverter that adds caching for repeated conversions and logs access.
 */
@Component
public class CurrencyConverterProxy implements CurrencyConverter {
    private static final Logger logger = LoggerFactory.getLogger(CurrencyConverterProxy.class);
    private final CurrencyConverter realConverter;
    // Simple cache: key is "from-to-amount"
    private final Map<String, Double> cache = new HashMap<>();

    @Autowired
    public CurrencyConverterProxy(CurrencyConverter realConverter) {
        this.realConverter = realConverter;
    }

    @Override
    public double convert(String fromCurrency, String toCurrency, double amount) {
        String key = fromCurrency + "-" + toCurrency + "-" + amount;
        if (cache.containsKey(key)) {
            logger.info("[PROXY] Cache hit for conversion: {}", key);
            return cache.get(key);
        }
        logger.info("[PROXY] Cache miss for conversion: {}. Delegating to real converter.", key);
        double result = realConverter.convert(fromCurrency, toCurrency, amount);
        cache.put(key, result);
        return result;
    }
}
