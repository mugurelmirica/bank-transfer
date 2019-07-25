package mmugur81.banktransfer.controller;

import io.javalin.http.Handler;
import io.javalin.plugin.json.JavalinJson;
import mmugur81.banktransfer.domain.Transfer;
import mmugur81.banktransfer.dto.TransferDto;
import mmugur81.banktransfer.service.TransferService;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class TransferController {
    public static final String PATH = "/api/transfer";

    private final TransferService transferService;

    @Inject
    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }

    public Handler createAndProcess() {
        return context -> {
            TransferDto dto = JavalinJson.fromJson(context.body(), TransferDto.class);

            Transfer transfer = transferService.create(dto);
            // Immediately process. It can also be done separately if desired
            transferService.process(transfer);

            context.json(transfer);
        };
    }
}
