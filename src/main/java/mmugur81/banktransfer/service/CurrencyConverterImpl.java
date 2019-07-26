package mmugur81.banktransfer.service;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Singleton;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Map;

@Singleton
public class CurrencyConverterImpl implements CurrencyConverter {
    public static final int ROUND_DECIMALS = 2;

    // Default rates of currencies relative to EUR; usually read from another separate service
    private Map<Currency, BigDecimal> rates = ImmutableMap.of(
            Currency.getInstance("EUR"), BigDecimal.valueOf(1),
            Currency.getInstance("USD"), BigDecimal.valueOf(0.896961),
            Currency.getInstance("GBP"), BigDecimal.valueOf(1.12019)
    );

    public CurrencyConverterImpl() {
    }

    /**
     * Used more for testing purpose
     * @param rates the new rates
     */
    public CurrencyConverterImpl(Map<Currency, BigDecimal> rates) {
        this.rates = rates;
    }

    /**
     * Converts and rounds to 2 decimals. First it converts to EUR then converts to target currency
     *
     * @param source currency
     * @param target currency
     * @param amount the amount
     * @return converted amount
     */
    @Override
    public BigDecimal convert(Currency source, Currency target, BigDecimal amount) {
        if (source.equals(target)) {
            return amount;
        }

        // Setup rounding
        int scale = ROUND_DECIMALS + 3;
        MathContext mathContext = new MathContext(scale, RoundingMode.HALF_UP);
        BigDecimal adjustedAmount = amount.setScale(scale, RoundingMode.HALF_UP);

        BigDecimal sourceToEurRate = rateToEur(source);
        BigDecimal targetToEurRate = rateToEur(target);

        BigDecimal amountToEur = adjustedAmount.multiply(sourceToEurRate);
        BigDecimal amountToTarget = amountToEur.divide(targetToEurRate, mathContext);
        return amountToTarget.setScale(ROUND_DECIMALS, RoundingMode.HALF_UP);
    }

    private BigDecimal rateToEur(Currency currency) {
        BigDecimal rate = rates.get(currency);
        // If rate doesn't exist consider a rate of 1, or alternatively we can throw an exception
        if (rate == null) {
            return BigDecimal.ONE;
        }
        return rate;
    }
}
