package mmugur81.banktransfer.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import mmugur81.banktransfer.domain.Transfer;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.ZonedDateTime;
import java.util.Currency;

@Data
public class TransferInfoDto {

    private long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd@HH:mm:ss.SSSZ")
    private ZonedDateTime created;

    private String accountIBAN;

    private Currency currency;

    private Double amount;

    private Double convertedAmount;


    public TransferInfoDto(Transfer transfer, TransferType type) {
        BigDecimal convertedAmount;
        if (type.equals(TransferType.WITHDRAWAL)) {
            this.accountIBAN = transfer.getTarget().getIban();
            convertedAmount = transfer.getAmountInTargetCurrency();
        } else {
            this.accountIBAN = transfer.getSource().getIban();
            convertedAmount = transfer.getAmountInSourceCurrency();
        }

        this.id = transfer.getId();
        this.created = transfer.getCreated();
        this.currency = transfer.getCurrency();

        MathContext mathContext = new MathContext(2, RoundingMode.HALF_UP);
        this.amount = transfer.getAmount().round(mathContext).doubleValue();
        this.convertedAmount = convertedAmount.round(mathContext).doubleValue();
    }
}
