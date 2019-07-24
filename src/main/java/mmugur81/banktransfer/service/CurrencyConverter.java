package mmugur81.banktransfer.service;

import com.google.inject.ImplementedBy;

import java.math.BigDecimal;
import java.util.Currency;

@ImplementedBy(CurrencyConverterImpl.class)
public interface CurrencyConverter {

    BigDecimal convert(Currency source, Currency target, BigDecimal amount);
}
