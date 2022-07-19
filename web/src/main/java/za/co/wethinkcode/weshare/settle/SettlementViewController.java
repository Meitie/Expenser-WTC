package za.co.wethinkcode.weshare.settle;

import io.javalin.http.Context;
import io.javalin.http.HttpCode;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import org.checkerframework.common.returnsreceiver.qual.This;
import za.co.wethinkcode.weshare.app.db.DataRepository;
import za.co.wethinkcode.weshare.app.model.Claim;
import za.co.wethinkcode.weshare.app.model.Settlement;

import java.time.LocalDate;
import java.util.*;

/**
 * Controller for handling calls from form submits for Claims
 */
public class SettlementViewController {
    public static final String PATH = "/settleclaim";

    public static void renderSettleClaimForm(Context context){
        UUID claimId = UUID.fromString(Objects.requireNonNull(context.queryParam("claimId")));

//        Optional<Claim> maybeClaim = DataRepository.getInstance().getClaim(claimId);
        HttpResponse<JsonNode> res= Unirest.get("http://localhost:7073/claim/"+claimId).asJson();
//        maybeClaim.ifPresent(c -> context.render("settleclaim.html", Map.of("claim", c)));
        JSONObject maybeClaim = (res.getBody() != null) ? res.getBody().getObject() : new JSONObject();
//        System.out.println(maybeClaim);
        if (maybeClaim.isEmpty()) {
            context.status(HttpCode.BAD_REQUEST);
            context.result("Invalid claim");
        } else {
            maybeClaim = addDescription(maybeClaim);
            context.render("settleclaim.html", Map.of("claim", maybeClaim));
        }
    }

    public static void submitSettlement(Context context) {
        UUID claimId = UUID.fromString(Objects.requireNonNull(context.formParam("id")));
//        Optional<Claim> maybeClaim = DataRepository.getInstance().getClaim(claimId);
        HttpResponse<JsonNode> res = Unirest.get("http://localhost:7073/claim/"+claimId).asJson();
        JSONObject maybeClaim = res.getBody().getObject();

        if (maybeClaim.isEmpty()) {
            context.status(HttpCode.BAD_REQUEST);
            context.result("Invalid claim");
            return;
        } else {
            HashMap settlement = new HashMap();
            settlement.put("claimId", claimId);
            settlement.put("settlementDate", LocalDate.now().toString());
            HttpResponse r = Unirest.post("http://localhost:7073/settlement").body(settlement).asJson();

            JSONObject ratingsSettlement = new JSONObject();
            ratingsSettlement.put("from", maybeClaim.get("claimedFrom"));
            ratingsSettlement.put("amount", maybeClaim.getString("amount"));
            ratingsSettlement.put("by", maybeClaim.get("claimedBy"));
//            System.out.println("*****THIS IS RATINGSsettlement*****");
//            System.out.println(ratingsSettlement);
            HttpResponse ratingsres = Unirest.post("http://localhost:8080/ratings?event=settled").body(ratingsSettlement).asJson();
        }
//        Claim claim = maybeClaim.get();
//        Settlement settlement = claim.settleClaim(LocalDate.now());
//        DataRepository.getInstance().addSettlement(settlement);
//        DataRepository.getInstance().updateClaim(claim);

        context.redirect("/home");
    }

    private static JSONObject addDescription(JSONObject claim) {
        String expenseUUID = claim.get("expenseUUID").toString();
//        System.out.println(expenseUUID);
        HttpResponse<JsonNode> res = Unirest.get("http://localhost:8123/expenses/"+expenseUUID).asJson();
//        System.out.println("expense ID = " + expenseUUID.toString());
//        System.out.println(res.getBody());
        if (res.getBody() != null) {
            JSONObject exp = res.getBody().getObject();
            String desc = exp.get("description").toString();
//            System.out.println(desc);
            claim.put("description", desc);
        } else {
            claim.put("description", "dummy");
        }

        return claim;
    }

}