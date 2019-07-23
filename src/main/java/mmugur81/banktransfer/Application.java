package mmugur81.banktransfer;

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.javalin.Javalin;
import mmugur81.banktransfer.controller.HolderController;
import mmugur81.banktransfer.repository.HibernateUtil;

public class Application {
    public static void main(String[] args) {
        Injector injector = Guice.createInjector(/*new AppModule()*/);

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
        app.post("/holder", holderController.create());

    }
}
