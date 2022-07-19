package za.co.wethinkcode.weshare.settle;

import io.javalin.http.Context;
import io.javalin.http.HttpCode;

import kong.unirest.json.JSONObject;
import za.co.wethinkcode.weshare.app.HttpCommunicate;
import za.co.wethinkcode.weshare.app.db.DataRepository;
import za.co.wethinkcode.weshare.app.model.Claim;
import za.co.wethinkcode.weshare.app.model.Settlement;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public class SettlementController {
    private static final int EXPENSE_PORT = 8123;

    public static final String settleClaimPath = "/settlement";


    public static void settleClaim(Context context){
        try{
            HashMap body = context.bodyAsClass(HashMap.class);

            UUID claimId = UUID.fromString(body.get("claimId").toString());
            LocalDate settlementDate = LocalDate.parse(body.get("settlementDate").toString());

            Optional<Claim> maybeClaim = DataRepository.getInstance().getClaim(claimId);
            if (maybeClaim.isEmpty()) {
                context.status(HttpCode.BAD_REQUEST);
                context.result("Invalid claim.");
                return;
            }
            Claim claim = maybeClaim.get();

            if (claim.isSettled()) {
                context.status(HttpCode.BAD_REQUEST);
                context.json("Claim has already been settled.");
            }

            Settlement settlement = claim.settleClaim(settlementDate);
            DataRepository.getInstance().addSettlement(settlement);
            DataRepository.getInstance().updateClaim(claim);

            //Interaction with Expense API
            JSONObject expenseData = HttpCommunicate.get("http://localhost:8123/expenses/"+claim.getExpenseUUID());

            HashMap expense = new HashMap();

            expense.put("paidBy", claim.getClaimedBy());
            expense.put("description", expenseData.get("description").toString());
            expense.put("amount", Double.parseDouble(expenseData.get("amount").toString()));
            expense.put("date", LocalDate.parse(expenseData.get("date").toString()));
            expense.put("sumOfUnsettledClaims", DataRepository.getInstance().getTotalUnsettledClaimsClaimedBy(claim.getClaimedBy()));
            expense.put("sumOfSettledClaims", DataRepository.getInstance().getTotalSettledClaimsClaimedBy(claim.getClaimedBy()));

            try {
                HttpCommunicate.post("http://localhost:" + EXPENSE_PORT + "/expenses", expense);
            }catch (Exception e){
                context.json(e.getMessage());
                return;
            }

            context.status(201);
            context.json(settlement);
        } catch (Exception e){
            context.status(HttpCode.BAD_REQUEST);
            context.result("Invalid body");
        }
    }

}
