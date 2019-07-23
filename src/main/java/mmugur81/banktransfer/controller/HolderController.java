package mmugur81.banktransfer.controller;

import io.javalin.http.Handler;
import io.javalin.plugin.json.JavalinJson;
import mmugur81.banktransfer.domain.Holder;
import mmugur81.banktransfer.service.HolderService;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class HolderController {

    private final HolderService holderService;

    @Inject
    public HolderController(HolderService holderService) {
        this.holderService = holderService;
    }

    public Handler create() {
        return context -> {
            Holder holder = JavalinJson.fromJson(context.body(), Holder.class);
            context.json(holderService.create(holder));
        };
    }
}
