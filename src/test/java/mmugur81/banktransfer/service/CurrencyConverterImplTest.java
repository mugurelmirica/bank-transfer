package mmugur81.banktransfer.service;

import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Map;

import static mmugur81.banktransfer.TestUtils.*;
import static mmugur81.banktransfer.service.CurrencyConverterImpl.ROUND_DECIMALS;
import static org.junit.jupiter.api.Assertions.*;

class CurrencyConverterImplTest {

    private Map<Currency, BigDecimal> rates = ImmutableMap.of(
            EURO, BigDecimal.valueOf(1),
            DOLLAR, BigDecimal.valueOf(0.896961),
            POUNDS, BigDecimal.valueOf(1.12019)
    );

    private CurrencyConverterImpl converter = new CurrencyConverterImpl(rates);

    @Test
    void convert() {
        BigDecimal amount = BigDecimal.valueOf(100).setScale(ROUND_DECIMALS, RoundingMode.HALF_UP);
        BigDecimal actual = converter.convert(EURO, DOLLAR, amount);

        double result = amount.doubleValue() * rates.get(EURO).doubleValue() / rates.get(DOLLAR).doubleValue();
        BigDecimal expected = BigDecimal.valueOf(result).setScale(ROUND_DECIMALS, RoundingMode.HALF_UP);

        assertEquals(expected, actual);
    }

    @Test
    void convertAndRevertA() {
        BigDecimal initialAmount = BigDecimal.valueOf(100).setScale(ROUND_DECIMALS, RoundingMode.HALF_UP);
        BigDecimal converted = converter.convert(EURO, DOLLAR, initialAmount);
        BigDecimal reverted = converter.convert(DOLLAR, EURO, converted);

        assertEquals(initialAmount, reverted);
    }

    @Test
    void convertAndRevertB() {
        BigDecimal initialAmount = BigDecimal.valueOf(100).setScale(ROUND_DECIMALS, RoundingMode.HALF_UP);
        BigDecimal converted = converter.convert(DOLLAR, EURO, initialAmount);
        BigDecimal reverted = converter.convert(EURO, DOLLAR, converted);

        assertEquals(initialAmount, reverted);
    }
}