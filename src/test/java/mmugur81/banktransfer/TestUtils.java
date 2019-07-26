package mmugur81.banktransfer;

import mmugur81.banktransfer.domain.Account;
import mmugur81.banktransfer.domain.Holder;
import mmugur81.banktransfer.dto.TransferDto;

import java.math.BigDecimal;
import java.util.Currency;

public class TestUtils {
    public static final Currency EURO = Currency.getInstance("EUR");
    public static final Currency DOLLAR = Currency.getInstance("USD");
    public static final Currency POUNDS = Currency.getInstance("GBP");

    public static final BigDecimal ZERO = BigDecimal.ZERO;
    public static final BigDecimal no100 = BigDecimal.valueOf(100);

    public static final String IBAN1 = "GB11BARC20035347275952";
    public static final String IBAN2 = "GB16BARC20038073137852";

    private TestUtils() {
    }

    public static Account newAccount(long id, String iban, Currency currency, BigDecimal initialAmount) {
        Account account = new Account(newHolder(), iban, currency, initialAmount);
        account.setId(id);

        return account;
    }

    public static Holder newHolder() {
        Holder holder = new Holder();
        holder.setName("TEST");

        return holder;
    }

    public static TransferDto newTransferDto(long sourceAccountId, long targetAccountId, Currency currency, BigDecimal amount) {
        TransferDto dto = new TransferDto();

        dto.setSourceAccountId(sourceAccountId);
        dto.setTargetAccountId(targetAccountId);
        dto.setCurrency(currency);
        dto.setAmount(amount);

        return dto;
    }
}
