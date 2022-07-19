package za.co.wethinkcode.weshare.nettexpenses;

import io.javalin.http.Context;
import za.co.wethinkcode.weshare.app.db.DataRepository;
import za.co.wethinkcode.weshare.app.model.Person;

import java.util.Map;

public class NettExpensesController {
    public static final String PATH = "/home";

    public static void renderHomePage(Context context){
        Person currentPerson = context.sessionAttribute("user");

        Map<String, Object> viewModel =
                Map.of("expenses", DataRepository.getInstance().getExpenses(currentPerson),
                       "totalExpenses", DataRepository.getInstance().getTotalExpensesFor(currentPerson),
                       "owedToOthers", DataRepository.getInstance().getClaimsFrom(currentPerson, true),
                       "totalIOwe", DataRepository.getInstance().getTotalUnsettledClaimsClaimedFrom(currentPerson),
                       "owedToMe", DataRepository.getInstance().getClaimsBy(currentPerson, true),
                       "totalOwedToMe", DataRepository.getInstance().getTotalUnsettledClaimsClaimedBy(currentPerson),
                       "nettExpenses", DataRepository.getInstance().getNettExpensesFor(currentPerson));

        context.render("home.html", viewModel);
    }
}