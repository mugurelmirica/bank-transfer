package mmugur81.banktransfer;

import io.javalin.Javalin;

public class Application {
    public static void main(String[] args) {
        Javalin app = Javalin.create().start(7000);
        app.get("/", ctx -> ctx.result("Hello World"));
        app.get("/hello", ctx -> ctx.result("Hello Again"));
    }
}
