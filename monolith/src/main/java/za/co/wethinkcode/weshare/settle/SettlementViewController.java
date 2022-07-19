package za.co.wethinkcode.weshare.settle;

import io.javalin.http.Context;
import io.javalin.http.HttpCode;
import za.co.wethinkcode.weshare.app.db.DataRepository;
import za.co.wethinkcode.weshare.app.model.Claim;
import za.co.wethinkcode.weshare.app.model.Settlement;

import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Controller for handling calls from form submits for Claims
 */
public class SettlementViewController {
    public static final String PATH = "/settleclaim";

    public static void renderSettleClaimForm(Context context){
        UUID claimId = UUID.fromString(Objects.requireNonNull(context.queryParam("claimId")));

        Optional<Claim> maybeClaim = DataRepository.getInstance().getClaim(claimId);
        maybeClaim.ifPresent(c -> context.render("settleclaim.html", Map.of("claim", c)));

        if (maybeClaim.isEmpty()) {
            context.status(HttpCode.BAD_REQUEST);
            context.result("Invalid claim");
        }
    }

    public static void submitSettlement(Context context) {
        UUID claimId = UUID.fromString(Objects.requireNonNull(context.formParam("id")));
        Optional<Claim> maybeClaim = DataRepository.getInstance().getClaim(claimId);
        if (maybeClaim.isEmpty()) {
            context.status(HttpCode.BAD_REQUEST);
            context.result("Invalid claim");
            return;
        }
        Claim claim = maybeClaim.get();
        Settlement settlement = claim.settleClaim(LocalDate.now());
        DataRepository.getInstance().addSettlement(settlement);
        DataRepository.getInstance().updateClaim(claim);

        context.redirect("/home");
    }

}