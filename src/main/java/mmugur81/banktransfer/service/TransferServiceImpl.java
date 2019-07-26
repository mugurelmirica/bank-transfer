package mmugur81.banktransfer.service;

import lombok.extern.java.Log;
import mmugur81.banktransfer.domain.Account;
import mmugur81.banktransfer.domain.Transfer;
import mmugur81.banktransfer.dto.TransferDto;
import mmugur81.banktransfer.exception.TransferException;

import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;

@Log
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
        myLog("init", dto.getSourceAccountId(), dto.getTargetAccountId());

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

        // [5] Create the transfer object
        transfer = new Transfer(source, target, dto.getCurrency(), dto.getAmount(), amountInSourceCurrency, amountInTargetCurrency);

        // [6] Perform some checks on accounts
        try {
            transfer.verify();
        } catch (TransferException e) {
            myLog("wil leave lock", source.getId(), target.getId());
            throw e;
        }

        // [7] store transfer for later processing
        return transferCRUDService.create(transfer);
    }

    @Override
    public void process(Transfer transfer) throws TransferException {
        // Do the same verification, because conditions might have changed
        myLog("processing", transfer.getSource().getId(), transfer.getTarget().getId());

        // Verify again
        transfer.verify();

        //Do the actual transfer
        transfer.process();

        // Mark the transfer as processed
        transferCRUDService.update(transfer);
    }

    private void myLog(String message, long sourceId, long targetId) {
//        log.info(String.format("[Pid:%s] Transfer [%s -> %s] ",
//                Thread.currentThread().getId(), sourceId, targetId).concat(message));
    }
}
