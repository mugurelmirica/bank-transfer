package mmugur81.banktransfer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import io.javalin.Javalin;
import io.javalin.plugin.json.JavalinJackson;
import mmugur81.banktransfer.controller.AccountController;
import mmugur81.banktransfer.controller.HolderController;
import mmugur81.banktransfer.repository.HibernateUtil;
import org.apache.http.HttpStatus;

import javax.persistence.EntityNotFoundException;

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

        // Define routes
        app.get("/", ctx -> ctx.result("Bank transfer application"));

        HolderController holderController = injector.getInstance(HolderController.class);
        app.post(HolderController.PATH, holderController.create());
        app.get(HolderController.PATH, holderController.list());
        app.get(HolderController.PATH + "/:id", holderController.get());

        AccountController accountController = injector.getInstance(AccountController.class);
        app.post(AccountController.PATH, accountController.create());
        app.get(AccountController.PATH, accountController.list());
        app.get(AccountController.PATH + "/:id", accountController.get());

        // Handle some errors
        app.exception(EntityNotFoundException.class, (e, ctx) -> {
            ctx.status(HttpStatus.SC_NOT_FOUND);
            ctx.result(e.getMessage());
        });
    }
}
