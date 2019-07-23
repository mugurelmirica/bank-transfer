package mmugur81.banktransfer.controller;

import mmugur81.banktransfer.service.AccountService;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AccountController {

    private final AccountService accountService;

    @Inject
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }
}
