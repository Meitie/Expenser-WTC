package za.co.wethinkcode.weshare;

import io.javalin.Javalin;
import org.jetbrains.annotations.NotNull;
import za.co.wethinkcode.weshare.app.db.DataRepository;
import za.co.wethinkcode.weshare.app.db.memory.ClaimsMemoryDb;
import za.co.wethinkcode.weshare.claim.ClaimsController;
import za.co.wethinkcode.weshare.settle.SettlementController;

import static io.javalin.apibuilder.ApiBuilder.*;

public class ClaimServer {
//    private static final int DEFAULT_PORT = Integer.parseInt(System.getenv("CLAIMS_PORT"));
    private static final int DEFAULT_PORT = 7073;
    public static DataRepository dataRepository1;

    private final Javalin app;

    public static void main(String[] args) {
        ClaimServer server = new ClaimServer(ClaimsMemoryDb.INSTANCE);
        server.start(DEFAULT_PORT);
    }

    public ClaimServer(DataRepository dataRepository) {
        dataRepository1 = dataRepository;
        app = createAndConfigureServer();
        setupRoutes(app);
    }

    public void start(int port) {
        app.start(port);
    }

    public int port() {
        return app.port();
    }

    public void close() {
        app.close();
    }

    private void setupRoutes(Javalin server) {
        server.routes(() -> {
            claimRoutes();
            settlementRoutes();
        });
    }

    private static void settlementRoutes() {
        post(SettlementController.settleClaimPath, SettlementController::settleClaim);
    }

    private static void claimRoutes() {
        get(ClaimsController.getByIdPath, ClaimsController::getOne);
        get(ClaimsController.getClaimsFromPath, ClaimsController::getAllClaimsFrom);
        get(ClaimsController.getClaimsByPath, ClaimsController::getAllClaimsBy);
        get(ClaimsController.getTotalClaimValueFrom, ClaimsController::getTotalClaimValueFrom);
        get(ClaimsController.getTotalClaimValueBy, ClaimsController::getTotalClaimValueBy);

        post(ClaimsController.postClaimPath, ClaimsController::createClaim);
//        post(ClaimsController.postDummyClaimPath, ClaimsController::postDummy);

        put(ClaimsController.putClaimPath, ClaimsController::updateClaim);
    }

    @NotNull
    private Javalin createAndConfigureServer() {
        return Javalin.create(config -> {
            config.defaultContentType = "application/json";
        });
    }

}
