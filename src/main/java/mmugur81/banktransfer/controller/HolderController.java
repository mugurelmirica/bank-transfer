package mmugur81.banktransfer.controller;

import io.javalin.http.Handler;
import io.javalin.plugin.json.JavalinJson;
import mmugur81.banktransfer.domain.Holder;
import mmugur81.banktransfer.service.HolderService;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityNotFoundException;

@Singleton
public class HolderController {
    public static final String PATH = "/api/holder";

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

    public Handler get() {
        return context -> context.json(
                holderService.get(Long.valueOf(context.pathParam("id")))
                .orElseThrow(EntityNotFoundException::new)
        );
    }

    public Handler list() {
        return context -> context.json(holderService.list());
    }
}
