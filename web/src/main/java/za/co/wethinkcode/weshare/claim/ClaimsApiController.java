package za.co.wethinkcode.weshare.claim;

import io.javalin.http.Context;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;
import org.jetbrains.annotations.NotNull;
import za.co.wethinkcode.weshare.app.db.DataRepository;
import za.co.wethinkcode.weshare.app.model.Claim;
import za.co.wethinkcode.weshare.app.model.Expense;
import za.co.wethinkcode.weshare.app.model.Person;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

/**
 * Controller for handling API calls for Claims
 */
public class ClaimsApiController {
    public static final String PATH = "/api/claims";

    public static void create(@NotNull Context context) {
        ClaimViewModel claimViewModel = context.bodyAsClass(ClaimViewModel.class);
        Expense expense = context.sessionAttribute("expense");
        Person currentPerson = context.sessionAttribute("user");

        String claimFromEmail = claimViewModel.getClaimFromWho();
        String claimByEmail = currentPerson.getEmail();

        UUID expenseId = expense.getId();
        Double amount = claimViewModel.getClaimAmount();
        LocalDate date = claimViewModel.dueDateAsLocalDate();

        HashMap claim = new HashMap();
        claim.put("expenseId", expenseId);
        claim.put("amount", amount);
        claim.put("claimedBy", claimByEmail);
        claim.put("claimedFrom", claimFromEmail);
        claim.put("dueDate", date);

        HttpResponse res = Unirest.post("http://localhost:7073/claim/").body(claim).asJson();

        JSONObject ratingsClaims = new JSONObject();
        ratingsClaims.put("from", claimFromEmail);
        ratingsClaims.put("amount", String.valueOf(amount));

        HttpResponse ratingsres = Unirest.post("http://localhost:8080/ratings?event=claims").body(ratingsClaims).asJson();
//        Person p = DataRepository.getInstance().addPerson(new Person(claimFromEmail));
//        Claim c = expense.createClaim(p, claimViewModel.getClaimAmount(), claimViewModel.dueDateAsLocalDate());
//        DataRepository.getInstance().addClaim(c);
//        DataRepository.getInstance().updateExpense(expense);
//
//        Optional<Expense> maybeExpense = DataRepository.getInstance().getExpense(expense.getId());
//        expense = maybeExpense.orElseThrow(() -> new RuntimeException("Could not reload expense"));
        expense = Unirest.get("http://localhost:8123/expenses/"+expenseId).asObject(Expense.class).getBody();

        context.sessionAttribute("expense", expense);

        claimViewModel.setId(expense.getNumberOfClaims());
        context.status(201);
        context.json(claimViewModel);
    }
}