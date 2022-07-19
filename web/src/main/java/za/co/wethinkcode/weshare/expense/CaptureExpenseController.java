package za.co.wethinkcode.weshare.expense;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Context;
import io.javalin.http.HttpCode;
//import kong.unirest.ObjectMapper?;
import kong.unirest.GenericType;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;
import za.co.wethinkcode.weshare.app.db.DataRepository;
import za.co.wethinkcode.weshare.app.model.Expense;
import za.co.wethinkcode.weshare.app.model.Person;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * Controller for handling API calls for Expenses
 */
public class CaptureExpenseController {
    public static final String PATH = "/expenses";

    public static void createExpense(Context context) throws JsonProcessingException {
        Person currentPerson = context.sessionAttribute("user");

        //TODO proper server-side validation of form params
        String description = context.formParam("description");
        HashMap personData = Unirest.get(String.format("http://localhost:8123/person/?email=%s", currentPerson.getEmail())).asObject(HashMap.class).getBody();
        if (personData == null) {
            personData = new HashMap();
        }
        double amount;
        try {
            amount = Double.parseDouble(Objects.requireNonNull(context.formParam("amount")));
        } catch (NumberFormatException e){
            context.status(HttpCode.BAD_REQUEST);
            context.result("Invalid amount specified");
            return;
        }
        LocalDate date;
        try {
            date = LocalDate.parse(Objects.requireNonNull(context.formParam("date")));
        } catch (DateTimeException e){
            context.status(HttpCode.BAD_REQUEST);
            context.result("Invalid due date specified");
            return;
        }

        Double sumOfUnsettledClaims;
        Double sumOfSettledClaims;

        sumOfUnsettledClaims = Double.parseDouble(personData.getOrDefault("sumOfUnsettledClaims", 0.0).toString());
        sumOfSettledClaims = Double.parseDouble(personData.getOrDefault("sumOfSettledClaims", 0.0).toString());


        JSONObject expense = new JSONObject();
        expense.put("amount", amount);
        expense.put("date", date);
        expense.put("description", description);
        expense.put("paidBy", currentPerson.getEmail());
        expense.put("sumOfUnsettledClaims", sumOfUnsettledClaims);
        expense.put("sumOfSettledClaims", sumOfSettledClaims);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode exp = mapper.readTree(expense.toString());

        HttpResponse<kong.unirest.JsonNode> response = Unirest.post("http://localhost:8123/expenses").body(exp).asJson();

        JSONObject ratingsExpense = new JSONObject();
        ratingsExpense.put("email", currentPerson.getEmail());
        ratingsExpense.put("amount", String.valueOf(amount));
        HttpResponse res = Unirest.post("http://localhost:8080/ratings?event=expense").body(ratingsExpense).asJson();

//        Expense expense = new Expense( amount, date, description, currentPerson );
//        DataRepository.getInstance().addExpense(expense);

        context.redirect("/home");
    }
}