package mmugur81.banktransfer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import io.javalin.Javalin;
import io.javalin.plugin.json.JavalinJackson;
import lombok.extern.java.Log;
import mmugur81.banktransfer.controller.AccountController;
import mmugur81.banktransfer.controller.HolderController;
import mmugur81.banktransfer.controller.TransferController;
import mmugur81.banktransfer.exception.TransferException;
import mmugur81.banktransfer.repository.HibernateUtil;
import org.apache.http.HttpStatus;
import org.hibernate.exception.ConstraintViolationException;

import javax.persistence.EntityNotFoundException;

@Log
public class Application {
    public static void main(String[] args) {
        // Guice DI
        Injector injector = Guice.createInjector();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        JavalinJackson.configure(objectMapper);

        Javalin app = Javalin.create();

        // Setup hibernate init and shutdown
        app.events(event -> {
            event.serverStarted(HibernateUtil::initiate);
            event.serverStopped(HibernateUtil::shutdown);
        });

        app.start(7000);

        // Define routes -----------------------------------------------------------------------------------------------
        app.get("/", ctx -> ctx.result("Bank transfer application"));

        HolderController holderController = injector.getInstance(HolderController.class);
        app.post(HolderController.PATH, holderController.create());
        app.get(HolderController.PATH, holderController.list());
        app.get(HolderController.PATH + "/:id", holderController.get());

        AccountController accountController = injector.getInstance(AccountController.class);
        app.post(AccountController.PATH, accountController.create());
        app.get(AccountController.PATH, accountController.list());
        app.get(AccountController.PATH + "/:id", accountController.get());
        app.get(AccountController.PATH + "/:id/transfers", accountController.listTransfersForAccount());

        TransferController transferController = injector.getInstance(TransferController.class);
        app.post(TransferController.PATH, transferController.createAndProcess());

        // Handle some errors ------------------------------------------------------------------------------------------
        app.exception(EntityNotFoundException.class, (e, ctx) -> {
            ctx.status(HttpStatus.SC_NOT_FOUND);
            ctx.result(e.getMessage());
        });

        app.exception(ConstraintViolationException.class, (e, ctx) -> {
            ctx.status(HttpStatus.SC_BAD_REQUEST);
            ctx.result(e.getCause().getMessage());
        });

        app.exception(TransferException.class, (e, ctx) -> {
            log.warning("[Pid:" + Thread.currentThread().getId() + "] " + e.getMessage());
            ctx.status(HttpStatus.SC_BAD_REQUEST);
            ctx.result(e.getMessage());
        });

        log.info(" *******************************************************************************************\n"
                + "                               APP STARTED                                                        \n"
                + "       *******************************************************************************************\n");
    }
}
