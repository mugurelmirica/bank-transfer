package mmugur81.banktransfer.dto;

import lombok.Data;

import java.util.Currency;

@Data
public class AccountDto {

    private long holderId;

    private String iban;

    private Currency currency;
}
