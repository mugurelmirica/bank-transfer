package mmugur81.banktransfer.service;

import com.google.inject.ImplementedBy;
import mmugur81.banktransfer.domain.Account;
import mmugur81.banktransfer.dto.AccountDto;

@ImplementedBy(AccountServiceImpl.class)
public interface AccountService extends CRUDService<Account> {

    Account create(AccountDto dto);
}
