import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import za.co.wethinkcode.expenses.weshare.WeShareExpensesServer;
import za.co.wethinkcode.expenses.weshare.app.db.DataRepository;
import za.co.wethinkcode.expenses.weshare.app.model.Expense;
import za.co.wethinkcode.expenses.weshare.app.model.Person;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class testsingsthings {
    private static final int TEST_SERVER_PORT = 8081;
    private static final String BASE_URL = "http://localhost:" + TEST_SERVER_PORT;
    private static WeShareExpensesServer server;

    @BeforeAll
    public static void startServer() {
        server = new WeShareExpensesServer();
        server.start(TEST_SERVER_PORT);
    }

    @AfterAll
    public static void stopServer() {
        server.stop();
    }

    @Test
    @DisplayName("Server status: GET /printID")
    void testPrintIDSSuccessfulReturn() {
        HttpResponse<String> resp = Unirest.get(BASE_URL + "/printID").asString();
        assertThat(resp.getStatus()).isEqualTo(200);
    }

    @Test
    @DisplayName("Server status: POST /expenses")
    void testUpdateNewExpense() {
        LocalDate date = LocalDate.of(2021, 9, 28);
        DataRepository database = DataRepository.getInstance();
        Person person = database.findPerson("herman@wethinkcode.co.za").get();
        Expense expense = new Expense(254, date, "Braai", person);

        HttpResponse<JsonNode> resp = Unirest.post(BASE_URL + "/expenses")
                .header("content-type", "application/json")
                .body(expense)
                .asJson();

        assertEquals(resp.getStatusText(), "OK");
    }

    @Test
    @DisplayName("Server status: POST /expenses")
    void testAddNewExpenseNotInDB() {
        LocalDate date = LocalDate.of(2022, 9, 28);
        DataRepository database = DataRepository.getInstance();
        Person person = database.findPerson("herman@wethinkcode.co.za").get();
        Expense expense = new Expense(254, date, "test data", person);

        HttpResponse<JsonNode> resp = Unirest.post(BASE_URL + "/expenses")
                .header("content-type", "application/json")
                .body(expense)
                .asJson();

        assertEquals(resp.getStatusText(), "OK");
    }

    @Test
    @DisplayName("Server status: POST /expenses")
    void testMistakeExpense() {
        LocalDate date = LocalDate.of(2020, 9, 28);
        DataRepository database = DataRepository.getInstance();
        Person person = database.findPerson("herman@wethinkcode.co.za").get();
        Expense expense = new Expense(254, date, "test data", person);

        HttpResponse<JsonNode> resp = Unirest.post(BASE_URL + "/expenses")
                .header("content-type", "application/json")
                .body(expense + "failuer")
                .asJson();

        assertEquals(resp.getStatusText(), "Bad Request");
    }
}
