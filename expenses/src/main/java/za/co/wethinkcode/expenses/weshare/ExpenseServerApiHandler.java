package za.co.wethinkcode.expenses.weshare;

import com.fasterxml.jackson.databind.JsonNode;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.HttpCode;
import io.javalin.http.NotFoundResponse;
import za.co.wethinkcode.expenses.weshare.app.db.DataRepository;
import za.co.wethinkcode.expenses.weshare.app.model.Expense;
import za.co.wethinkcode.expenses.weshare.app.model.Person;

import java.time.LocalDate;
import java.util.*;

public class ExpenseServerApiHandler {

    public void getExpensesByUserEmail(Context context) {
        String userEmail = context.queryParamAsClass("email", String.class).get();
        DataRepository dataRepository = DataRepository.getInstance();
        List<Expense> userExpenses;
        Person user;

        if (dataRepository.findPerson(userEmail).isEmpty())   {
            throw new BadRequestResponse("Email not found: " + userEmail);
        }

        user = dataRepository.findPerson(userEmail).get();
        userExpenses = dataRepository.getExpenses(user);
//        System.out.println(userExpenses);
        context.json(userExpenses);
    }

    public void getExpensesByAnId(Context context) {
//        System.out.println(
//                "fuck"
//        );
        String expenseId = context.pathParamAsClass("id", String.class).get();
        DataRepository dataRepository = DataRepository.getInstance();
        Expense expenseFull;
        UUID id;
        try {
            id = UUID.fromString(expenseId);
            if (dataRepository.getExpense(id).isEmpty()){
//                System.out.println("empty");
                throw new NotFoundResponse("Expense not found: " + id);
            }
        } catch (IllegalArgumentException exception) {
            throw new BadRequestResponse("Malformed ID " + expenseId);
        }

        expenseFull = dataRepository.getExpense(id).get();

//        System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
//        System.out.println(expenseFull);
//        System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");

        context.json(expenseFull);
    }

    public void getPersonByEmailValue(Context context) {
        String usersEmail = context.queryParamAsClass("email", String.class).get();
        DataRepository dataRepository = DataRepository.getInstance();
;
        if (dataRepository.findPerson(usersEmail).isEmpty())   {
            throw new BadRequestResponse("Email not found: " + usersEmail);
        }
        Person user = new Person(usersEmail);

        double totalExpensesFor = dataRepository.getTotalExpensesFor(user);
        double nettExpensesFor = dataRepository.getNettExpensesFor(user);
        double sumOfUnsettledClaims = dataRepository.getSumOfUnsettledClaims(user);
        double sumOfSettledClaims = dataRepository.getSumOfSettledClaims(user);

        HashMap<String, Double> personsJson = new HashMap<>();
        personsJson.put("totalExpensesFor", totalExpensesFor);
        personsJson.put("nettExpensesFor", nettExpensesFor);
        personsJson.put("sumOfUnsettledClaims", sumOfUnsettledClaims);
        personsJson.put("sumOfSettledClaims", sumOfSettledClaims);
        context.json(personsJson);
//        System.out.println(personsJson);
    }

    public void postExpensesAddOrUpdate(Context context) {

        try {
            JsonNode postRequest = context.bodyAsClass(JsonNode.class);
            DataRepository dataRepository = DataRepository.getInstance();
            Expense expenseFromJson;
            String person;

            if (!postRequest.get("_children").isEmpty()) {
                postRequest = postRequest.get("_children");
                person = postRequest.get("paidBy").get("_value").asText();
                Person paidBy;
                if (dataRepository.findPerson(person).isEmpty()) {
                    paidBy = new Person(person);
                } else {
                    paidBy = dataRepository.findPerson(person).get();
                }
                expenseFromJson = new Expense(
                        postRequest.get("amount").get("_value").asDouble(),
                        LocalDate.parse(postRequest.get("date").get("_value").asText()),
                        postRequest.get("description").get("_value").asText(),
                        paidBy,
                        postRequest.get("sumOfUnsettledClaims").get("_value").asDouble(),
                        postRequest.get("sumOfSettledClaims").get("_value").asDouble()
                );
            } else {
                person = postRequest.get("paidBy").asText();
                Person paidBy;
                if (dataRepository.findPerson(person).isEmpty()) {
                    paidBy = new Person(person);
                } else {
                    paidBy = dataRepository.findPerson(person).get();
                }
                expenseFromJson = new Expense(
                        postRequest.get("amount").asDouble(),
                        LocalDate.parse(postRequest.get("date").asText()),
                        postRequest.get("description").asText(),
                        paidBy,
                        postRequest.get("sumOfUnsettledClaims").asDouble(),
                        postRequest.get("sumOfSettledClaims").asDouble()
                );
            }


            UUID oldId;
            List<Expense> allUserExpense = dataRepository.getExpenses(expenseFromJson.getPaidBy());

            double bob;
            double harry;

            boolean didUpdate = false;
            for (Expense expense : allUserExpense) {
//                System.out.println("expensee with id " + expense.getId() + " || " + expense.equals(expenseFromJson));
                if (expense.equals(expenseFromJson)) {
                    bob = expense.getSumOfUnsettledClaims();
                    harry = expense.getSumOfSettledClaims();
                    oldId = expense.getId();
//                    dataRepository.updateExpense(expenseFromJson);
                    expense.addClaim(postRequest.get("sumOfUnsettledClaims").asDouble() - bob);
                    expense.settleClaim(postRequest.get("sumOfSettledClaims").asDouble() + harry);
                    expenseFromJson.setID(oldId);
                    dataRepository.updateExpense(expenseFromJson);
//                    System.out.println("_______ THIS IS OLD EXPENSE");
//                    System.out.println(expense);
//                    System.out.println("_______ END OLD EXPENSE");
//                    System.out.println("_______ THIS IS NEW EXPENSE");
//                    System.out.println(expenseFromJson);
//                    System.out.println("_______ END NEW EXPENSE");
                    didUpdate = true;
                    break;
                }
            }
            if (didUpdate) {
//                System.out.println();
                context.status(HttpCode.OK);
            } else {
                dataRepository.addExpense(expenseFromJson);
                context.status(HttpCode.OK);
            }
//            System.out.println(
//                    "end of making expense"
//            );
        } catch (Exception e) {
            context.status(HttpCode.BAD_REQUEST);
            context.result("Json Body Bad");
        }


    }

    public void printID(Context context){
        DataRepository dataBase = DataRepository.getInstance();
        Optional<Person> person = dataBase.findPerson("herman@wethinkcode.co.za");
        List<Expense> expenses = dataBase.getExpenses(person.get());
        UUID id2 = expenses.get(1).getId();
        context.json(id2);
    }
}
