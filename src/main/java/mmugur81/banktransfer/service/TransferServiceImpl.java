package mmugur81.banktransfer.service;

import com.google.common.collect.ImmutableSet;
import mmugur81.banktransfer.domain.Account;
import mmugur81.banktransfer.domain.Transfer;
import mmugur81.banktransfer.dto.TransferDto;
import mmugur81.banktransfer.exception.AccountsIdenticalException;
import mmugur81.banktransfer.exception.InsufficientFundsException;
import mmugur81.banktransfer.exception.NegativeOrZeroException;
import mmugur81.banktransfer.exception.NotAllowedToDepositException;
import mmugur81.banktransfer.exception.NotAllowedToWithdrawException;
import mmugur81.banktransfer.exception.TransferException;

import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.Set;

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
        Transfer transfer;

        // TODO DEBUG and remove
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ignored) {
            ignored.printStackTrace();
        }

        // [1] Check ids are correct
        Account source = accountService.get(dto.getSourceAccountId()).orElseThrow(
                () -> new EntityNotFoundException("Account with id " + dto.getSourceAccountId() + " not found"));

        Account target = accountService.get(dto.getTargetAccountId()).orElseThrow(
                () -> new EntityNotFoundException("Account with id " + dto.getTargetAccountId() + " not found"));

        // [2] convert to source's currency
        BigDecimal amountInSourceCurrency = currencyConverter
                .convert(dto.getCurrency(), source.getCurrency(), dto.getAmount());

        // [3] convert to targets's currency
        BigDecimal amountInTargetCurrency = currencyConverter
                .convert(dto.getCurrency(), target.getCurrency(), dto.getAmount());

        // [4] Lock both accounts, while checks are performed
        Set<Account> lockedAccounts = ImmutableSet.of(source, target);
        synchronized (lockedAccounts) {

            // [5] Perform some checks on accounts
            verifyTransferPossible(source, amountInSourceCurrency, target);

            // [6] store transfer for later processing
            transfer = transferCRUDService.create(new Transfer(
                    source, target, dto.getCurrency(), dto.getAmount(), amountInSourceCurrency, amountInTargetCurrency));

        }// [7] Release locks on accounts

        return transfer;
    }

    @Override
    public void process(Transfer transfer) throws TransferException {
        // Do the same verification, because conditions might have changed

        // Lock both accounts, while checks are performed
        Set<Account> lockedAccounts = ImmutableSet.of(transfer.getSource(), transfer.getTarget());
        synchronized (lockedAccounts) {
            verifyTransferPossible(transfer.getSource(), transfer.getAmountInSourceCurrency(), transfer.getTarget());

            //Do the actual transfer
            transfer.process();

            // Mark the transfer as processed
            transferCRUDService.update(transfer);

        }// Release locks on accounts
    }

    private void verifyTransferPossible(Account source, BigDecimal amountInSourceCurrency, Account target)
            throws TransferException {

        // [1] Check accounts differ
        if (source.equals(target)) {
            throw new AccountsIdenticalException(source);
        }

        // [2] check source account has the right to withdraw
        if (!source.isWithdrawAllowed()) {
            throw new NotAllowedToWithdrawException(source);
        }

        // [3] check target account has the right to deposit
        if (!target.isDepositAllowed()) {
            throw new NotAllowedToDepositException(source);
        }

        // [4] check the amount is positive
        if (amountInSourceCurrency.compareTo(BigDecimal.ZERO) <= 0) {
            throw new NegativeOrZeroException(source);
        }

        // [5] check source account has enough money
        if (source.getAmount().compareTo(amountInSourceCurrency) < 0) {
            throw new InsufficientFundsException(source);
        }
    }
}
