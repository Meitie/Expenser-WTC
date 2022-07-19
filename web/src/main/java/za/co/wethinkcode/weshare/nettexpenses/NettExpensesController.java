package za.co.wethinkcode.weshare.nettexpenses;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import io.javalin.http.Context;
import kong.unirest.GenericType;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import za.co.wethinkcode.weshare.app.db.DataRepository;
import za.co.wethinkcode.weshare.app.model.Claim;
import za.co.wethinkcode.weshare.app.model.Expense;
import za.co.wethinkcode.weshare.app.model.Person;

import java.sql.SQLOutput;
import java.util.*;

public class NettExpensesController {
    public static final String PATH = "/home";

    public static void renderHomePage(Context context) throws JsonProcessingException {
        Person currentPerson = context.sessionAttribute("user");

        HttpResponse<JsonNode> expensesJson = Unirest.get("http://localhost:8123/expenses/?email="+currentPerson.getEmail()).asJson();
        HashMap personData = Unirest.get("http://localhost:8123/person/?email=" + currentPerson.getEmail()).asObject(HashMap.class).getBody();
        String bod = Unirest.get("http://localhost:7073/claims/from/"+currentPerson.getEmail()+"?settled=false").asString().getBody();
        HttpResponse<JsonNode> owedToOthersJSON = Unirest.get("http://localhost:7073/claims/from/"+currentPerson.getEmail()+"?settled=false").asJson();
        HttpResponse<JsonNode> owedToMeJSON = Unirest.get("http://localhost:7073/claims/by/"+currentPerson.getEmail()+"?settled=false").asJson();
//        System.out.println(bod);
//        JSONArray cl = new ObjectMapper().readValue(bod, JSONArray.class);
//        System.out.println(
//                cl
//        );
        JsonNode owedToOthersBody = owedToOthersJSON.getBody();
        JsonNode owedToMeBody = owedToMeJSON.getBody();
        JsonNode expensesBody = expensesJson.getBody();
        JSONArray expenses = (expensesBody == null) ? new JSONArray() : expensesBody.getArray();
        JSONArray owedToOthers = (owedToOthersBody == null || owedToOthersBody.toString().contains("There are currently no claims")) ? new JSONArray() : owedToOthersBody.getArray();
        JSONArray owedToMe = (owedToMeBody == null || owedToMeBody.toString().contains("There are currently no claims")) ? new JSONArray() : owedToMeBody.getArray();
        owedToMe = addDescription(owedToMe);
        owedToOthers = addDescription(owedToOthers);
//        System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
//        System.out.println(expenses);
        Double totalIOwe = Unirest.get("http://localhost:7073/totalclaimvalue/from/"+currentPerson.getEmail()).asObject(Double.class).getBody();
//        System.out.println(owedToOthers);
//        System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
//        System.out.println(expenses);
//        System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");

        if (totalIOwe == null) {
            totalIOwe = 0.0;
        }
        if (personData == null) {
            personData = new HashMap();
        }
//        System.out.println(expenses.toString());
        Map<String, Object> viewModel =
                Map.of("expenses", (expenses == null) ? new ArrayList<>() : expenses,
                        "totalExpenses", personData.getOrDefault("totalExpensesFor", 0.0),
                        "owedToOthers", owedToOthers,
                        "totalIOwe", (totalIOwe == null) ? 0.0 : totalIOwe,
                        "owedToMe", owedToMe,
                        "totalOwedToMe", personData.getOrDefault("sumOfUnsettledClaims", 0.0),
                        "nettExpenses", personData.getOrDefault("nettExpensesFor", 0.0));

        context.render("home.html", viewModel);
    }

    private static JSONArray addDescription(JSONArray claims) {
        for (int i = 0; i < claims.length(); i++) {
            Object claim = claims.get(i);
//            System.out.println((JSONObject) claims.get(i));
            String expenseUUID = ((JSONObject) claims.get(i)).get("expenseUUID").toString();
//            System.out.println(expenseUUID);
            HttpResponse<JsonNode> res = Unirest.get("http://localhost:8123/expenses/"+expenseUUID).asJson();
//            System.out.println("expense ID = " + expenseUUID.toString());
//            System.out.println(res.getBody());
            if (res.getBody() != null) {
                JSONObject exp = res.getBody().getObject();
                String desc = exp.get("description").toString();
//                System.out.println(desc);
                ((JSONObject) claims.get(i)).put("description", desc);
            } else {
                ((JSONObject) claims.get(i)).put("description", "dummy");
            }
        }

        return claims;
    }
}