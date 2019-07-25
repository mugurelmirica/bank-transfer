package mmugur81.banktransfer.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Currency;

@Data
public class TransferDto {

    private long sourceAccountId;

    private long targetAccountId;

    /**
     * Currency of the transfer; It can be different from both source and target accounts
     */
    private Currency currency;

    /**
     * Amount in transfer's currency
     */
    private BigDecimal amount;
}
