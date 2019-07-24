package mmugur81.banktransfer.service;

import mmugur81.banktransfer.domain.Account;
import mmugur81.banktransfer.domain.Transfer;
import mmugur81.banktransfer.dto.TransferDto;
import mmugur81.banktransfer.exception.InsufficientFundsException;
import mmugur81.banktransfer.exception.NotAllowedToDepositException;
import mmugur81.banktransfer.exception.NotAllowedToWithdrawException;
import mmugur81.banktransfer.exception.TransferException;

import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;

public class TransferServiceImpl implements TransferService {

    private final CRUDServiceImpl<Transfer> transferCRUDService;
    private final AccountService accountService;
    private final CurrencyConverter currencyConverter;

    @Inject
    public TransferServiceImpl(CRUDServiceImpl<Transfer> transferCRUDService,
                               AccountService accountService,
                               CurrencyConverter currencyConverter) {
        this.transferCRUDService = transferCRUDService;
        this.accountService = accountService;
        this.currencyConverter = currencyConverter;
    }

    @Override
    public Transfer create(TransferDto dto) throws TransferException {
        // [0] Check ids are correct
        Account source = accountService.get(dto.getSourceAccountId()).orElseThrow(
                () -> new EntityNotFoundException("Account with id " + dto.getSourceAccountId() + " not found"));

        Account target = accountService.get(dto.getTargetAccountId()).orElseThrow(
                () -> new EntityNotFoundException("Account with id " + dto.getTargetAccountId() + " not found"));

        // [1] check source account has the right to withdraw
        if (!source.isWithdrawAllowed()) {
            throw new NotAllowedToWithdrawException(source);
        }

        // [2] check target account has the right to deposit
        if (!target.isDepositAllowed()) {
            throw new NotAllowedToDepositException(source);
        }

        // [3] convert to source's currency
        BigDecimal amountInSourceCurrency = currencyConverter
                .convert(dto.getCurrency(), source.getCurrency(), dto.getAmount());

        // [4] check source account has enough money
        if (source.getAmount().compareTo(amountInSourceCurrency) < 0) {
            throw new InsufficientFundsException(source);
        }

        // [5] convert to targets's currency
        BigDecimal amountInTargetCurrency = currencyConverter
                .convert(dto.getCurrency(), target.getCurrency(), dto.getAmount());

        // [6] store transfer for later processing
        Transfer transfer = new Transfer(
                source, target, dto.getCurrency(), dto.getAmount(), amountInSourceCurrency, amountInTargetCurrency);

        return transferCRUDService.create(transfer);
    }

    @Override
    public void process(Transfer transfer) {
        //TODO implement

    }

}
