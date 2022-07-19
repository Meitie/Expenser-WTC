package za.co.wethinkcode.weshare.rating;

import com.google.common.collect.ImmutableList;
import io.javalin.http.Context;
import kong.unirest.json.JSONObject;
import za.co.wethinkcode.weshare.app.db.DataRepository;
import za.co.wethinkcode.weshare.app.model.Person;

import java.util.*;

import static kong.unirest.HttpStatus.BAD_REQUEST;
import static kong.unirest.HttpStatus.CREATED;

public class ratingController {
    public static final String PATH = "/ratings";
    public static void renderRatings(Context context){
        DataRepository dataRepository = DataRepository.getInstance();
        ImmutableList<Person> persons = dataRepository.allPersons();
        Map<String, String> data = new HashMap<>();
        List<Map<String, String>> person_details = new ArrayList<>();
        String[] values = {"email", "amount", "timeStamp"};
        for(Person person : persons){
            data.put(values[0], person.getEmail());
            data.put(values[1], amount(person, context.queryParam("by")));
            data.put(values[2], String.valueOf(person.getTime_stamp()));
            person_details.add(Map.copyOf(data));
        }
        person_details.sort(Comparator.comparingDouble((Map<String, String> byAmount)
                -> Double.parseDouble(byAmount.get("amount"))).reversed());
        context.json(person_details);
    }

    private static String amount(Person person, String amount) {
        double finalAmount;
        if (Objects.equals(amount, "expense")){
            finalAmount = person.getExpense();
        }else if (Objects.equals(amount, "claims")){
            finalAmount = person.getClaims();
        }else if (Objects.equals(amount, "unsettled")){
            finalAmount = person.getUnsettled();
        } else if (Objects.equals(amount, "settled")){
            finalAmount = person.getSettled();
        }else{
            finalAmount = person.getNet();
        }
        return String.format("%.2f",finalAmount).replace(",", ".");
    }

    public static void updatePerson(Context context){
        JSONObject body = new JSONObject(context.body());
        DataRepository dataRepository = DataRepository.getInstance();
        String event = context.queryParam("event");
        if (Objects.equals(event, "expense")) {
            Person person = dataRepository.addPerson(body.getString("email"));
            Double amount = Double.parseDouble(body.getString("amount"));
            person.person_expense(amount);
        } else if (Objects.equals(event, "claims")) {
            Person person = dataRepository.addPerson(body.getString("from"));
            Double amount = Double.parseDouble(body.getString("amount"));
            person.person_claimed_against(amount);
        } else if (Objects.equals(event, "settled")){
            Person personBy = dataRepository.addPerson(body.getString("by"));
            Person personFrom = dataRepository.addPerson(body.getString("from"));
            Double amount = Double.parseDouble(body.getString("amount"));
            personBy.person_reimburse(amount);
            personFrom.person_reimbursed(amount);
        } else{
            context.status(BAD_REQUEST);
            return;
        }
        context.status(CREATED);
    }
}
