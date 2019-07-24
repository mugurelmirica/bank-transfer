package mmugur81.banktransfer.service;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Singleton;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Map;

@Singleton
public class CurrencyConverterImpl implements CurrencyConverter {

    // Rates of currencies relative to EUR; usually read from another separate service
    Map<Currency, BigDecimal> rates = ImmutableMap.of(
            Currency.getInstance("EUR"), BigDecimal.valueOf(1),
            Currency.getInstance("USD"), BigDecimal.valueOf(0.896961),
            Currency.getInstance("GBP"), BigDecimal.valueOf(1.12019)
    );

    @Override
    public BigDecimal convert(Currency source, Currency target, BigDecimal amount) {
        //TODO implement
        return null;
    }
}
