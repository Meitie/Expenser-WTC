package za.co.wethinkcode.expenses.weshare;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.javalin.Javalin;
import io.javalin.plugin.json.JavalinJackson;

public class WeShareExpensesServer {
    private final Javalin server;
    private final int EXPENSE_PORT = 8123;

    public WeShareExpensesServer() {
//        System.out.println("hello");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        server = Javalin.create(javalinConfig -> javalinConfig.jsonMapper(new JavalinJackson(objectMapper)));
        ExpenseServerApiHandler expenseServerApiHandler = new ExpenseServerApiHandler();

        server.get("/expenses/", context -> expenseServerApiHandler.getExpensesByUserEmail(context));
        server.get("/expenses/{id}", context -> expenseServerApiHandler.getExpensesByAnId(context));
        server.get("/person/", context -> expenseServerApiHandler.getPersonByEmailValue(context));

        server.post("/expenses/", context -> expenseServerApiHandler.postExpensesAddOrUpdate(context));
        server.get("/printID/", context -> expenseServerApiHandler.printID(context));
    }

    public void start(int port) {
        int listen_port = port > 0 ? port : EXPENSE_PORT;
        this.server.start(listen_port);
    }

    public void stop() {
        this.server.stop();
    }

    public static void main(String[] args) {
        WeShareExpensesServer server = new WeShareExpensesServer();
        server.start(server.EXPENSE_PORT);
    }
}

