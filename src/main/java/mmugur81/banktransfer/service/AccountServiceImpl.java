package mmugur81.banktransfer.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.java.Log;
import mmugur81.banktransfer.domain.Account;
import mmugur81.banktransfer.domain.Holder;
import mmugur81.banktransfer.dto.AccountDto;

import javax.persistence.EntityNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Log
@Singleton
public class AccountServiceImpl extends CRUDServiceImpl<Account> implements AccountService {

    /**
     * Used as a workaround for synchronisation.
     */
    private static final Map<Long, Account> loadedAccounts = new HashMap<>();

    private final HolderService holderService;

    @Inject
    public AccountServiceImpl(HolderService holderService) {
        this.holderService = holderService;
    }

    @Override
    public Account create(AccountDto dto) {

        // Get holder
        long holderId = dto.getHolderId();
        Holder holder = holderService.get(holderId)
                .orElseThrow(() -> new EntityNotFoundException("Holder with id " + holderId + " not found"));

        Account account = new Account(holder, dto.getIban(), dto.getCurrency(), dto.getInitialAmount());

        // Call CRUDServiceImpl.create
        return create(account);
    }

    @Override
    public Optional<Account> get(long id) {
        Account account = loadedAccounts.get(id);

        if (account == null) {
            log.info("Account " + id + " got from hibernate");
            Optional<Account> managedAccount = super.get(id);

            managedAccount.ifPresent(value -> loadedAccounts.put(id, value));
            return managedAccount;
        }

        // Return a local copy which will be used as a lock for synchronisation
        log.info("Account " + id + " got from cache");
        return Optional.of(account);
    }
}
