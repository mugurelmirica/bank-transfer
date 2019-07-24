package mmugur81.banktransfer.controller;

import io.javalin.http.Handler;
import io.javalin.plugin.json.JavalinJson;
import mmugur81.banktransfer.dto.AccountDto;
import mmugur81.banktransfer.service.AccountService;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AccountController {
    public static final String PATH = "/api/account";

    private final AccountService accountService;

    @Inject
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    public Handler create() {
        return context -> {
            AccountDto accountDto = JavalinJson.fromJson(context.body(), AccountDto.class);
            context.json(accountService.create(accountDto));
        };
    }

    public Handler get() {
        return context -> context.json(accountService.get(Long.valueOf(context.pathParam("id"))));
    }

    public Handler list() {
        return context -> context.json(accountService.list());
    }
}
