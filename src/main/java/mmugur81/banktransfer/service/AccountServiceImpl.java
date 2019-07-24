package mmugur81.banktransfer.service;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import mmugur81.banktransfer.domain.Account;
import mmugur81.banktransfer.domain.Holder;
import mmugur81.banktransfer.dto.AccountDto;

import javax.persistence.EntityNotFoundException;

@Singleton
public class AccountServiceImpl extends CRUDServiceImpl<Account> implements AccountService {

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

        Account account = new Account();

        account.setHolder(holder);
        account.setIban(dto.getIban());
        account.setCurrency(dto.getCurrency());

        // Call CRUDServiceImpl.create
        return create(account);
    }
}
