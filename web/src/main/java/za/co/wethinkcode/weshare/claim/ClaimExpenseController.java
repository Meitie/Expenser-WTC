package za.co.wethinkcode.weshare.claim;

import com.google.common.collect.ImmutableList;
import io.javalin.http.Context;
import io.javalin.http.HttpCode;
import kong.unirest.GenericType;
import kong.unirest.Unirest;
import za.co.wethinkcode.weshare.app.db.DataRepository;
import za.co.wethinkcode.weshare.app.model.Claim;
import za.co.wethinkcode.weshare.app.model.Expense;
import za.co.wethinkcode.weshare.app.model.Person;

import java.time.LocalDate;
import java.util.*;

/**
 * Controller for handling calls from form submits for Claims
 */
public class ClaimExpenseController {
    public static final String PATH = "/claimexpense";

    public static void renderClaimExpensePage(Context context){
        UUID expenseId = UUID.fromString(context.queryParam("expenseId"));

//        Optional<Expense> maybeExpense = DataRepository.getInstance().getExpense(expenseId);
        Expense maybeExpense = Unirest.get("http://localhost:8123/expenses/"+expenseId).asObject(Expense.class).getBody();
        if (maybeExpense == null) {
            context.status(HttpCode.BAD_REQUEST);
            context.result("Invalid expense");
            return;
        }
        Expense expense = maybeExpense;

        List<Claim> claims = Unirest.get("http://localhost:7073/claims/by/"+maybeExpense.getPaidBy().getEmail()+"?settled=false").asObject(new GenericType<List<Claim>>() {}).getBody();
        if (claims == null) {
            claims = new ArrayList<>();
        }

        context.sessionAttribute("expense", expense);
        context.render("claimexpense.html",
                Map.of("newClaim", new Claim( expense.getId().toString(), expense.getPaidBy().getEmail(), "email@domain.com", 0.0, LocalDate.now() ),
                        "expense", expense,
                        "claims", claims));
    }
}