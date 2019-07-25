package mmugur81.banktransfer.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Currency;

@Data
public class AccountDto {

    private long holderId;

    private String iban;

    private Currency currency;

    private BigDecimal initialAmount;
}
