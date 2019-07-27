package mmugur81.banktransfer.controller;

import io.javalin.http.Handler;
import io.javalin.plugin.json.JavalinJson;
import mmugur81.banktransfer.domain.Account;
import mmugur81.banktransfer.dto.AccountDto;
import mmugur81.banktransfer.dto.TransferInfoDto;
import mmugur81.banktransfer.dto.TransferType;
import mmugur81.banktransfer.service.AccountService;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityExistsException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
        return context -> {
            long id = Long.parseLong(context.pathParam("id"));
            Account account = accountService.get(id)
                    .orElseThrow(EntityExistsException::new);
            context.json(account);
        };
    }

    public Handler list() {
        return context -> context.json(accountService.list());
    }

    public Handler listTransfersForAccount() {
        return context -> {
            long id = Long.parseLong(context.pathParam("id"));
            Account account = accountService.getManaged(id);

            Map<TransferType, Set<TransferInfoDto>> result = new HashMap<>();

            result.put(TransferType.DEPOSIT,
                    account.getDepositTransfers()
                            .stream()
                            .map(transfer -> new TransferInfoDto(transfer, TransferType.DEPOSIT))
                            .collect(Collectors.toSet()));

            result.put(TransferType.WITHDRAWAL,
                    account.getWithdrawTransfers()
                            .stream()
                            .map(transfer -> new TransferInfoDto(transfer, TransferType.WITHDRAWAL))
                            .collect(Collectors.toSet()));

            context.json(result);
        };
    }
}
