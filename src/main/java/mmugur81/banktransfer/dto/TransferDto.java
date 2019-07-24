package mmugur81.banktransfer.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.Currency;

@Getter
@EqualsAndHashCode
@RequiredArgsConstructor
public abstract class TransferDto {

    private final long sourceAccountId;

    private final long targetAccountId;

    /**
     * Currency of the transfer; It can be different from both source and target accounts
     */
    private final Currency currency;

    /**
     * Amount in transfer's currency
     */
    private final BigDecimal amount;
}
