package za.co.wethinkcode.weshare.claim;

import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;
import za.co.wethinkcode.weshare.app.db.DataRepository;
import za.co.wethinkcode.weshare.app.model.Claim;
import za.co.wethinkcode.weshare.app.model.Expense;
import za.co.wethinkcode.weshare.app.model.Person;

import java.util.Optional;

/**
 * Controller for handling API calls for Claims
 */
public class ClaimsApiController {
    public static final String PATH = "/api/claims";

    public static void create(@NotNull Context context) {
        ClaimViewModel claimViewModel = context.bodyAsClass(ClaimViewModel.class);
        Expense expense = context.sessionAttribute("expense");

        String claimFromEmail = claimViewModel.getClaimFromWho();

        Person p = DataRepository.getInstance().addPerson(new Person(claimFromEmail));
        Claim c = expense.createClaim(p, claimViewModel.getClaimAmount(), claimViewModel.dueDateAsLocalDate());
        DataRepository.getInstance().addClaim(c);
        DataRepository.getInstance().updateExpense(expense);

        Optional<Expense> maybeExpense = DataRepository.getInstance().getExpense(expense.getId());
        expense = maybeExpense.orElseThrow(() -> new RuntimeException("Could not reload expense"));
        context.sessionAttribute("expense", expense);

        claimViewModel.setId(expense.getClaims().size());
        context.status(201);
        context.json(claimViewModel);
    }
}