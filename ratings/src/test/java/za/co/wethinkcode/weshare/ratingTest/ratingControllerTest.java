package za.co.wethinkcode.weshare.ratingTest;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import org.junit.jupiter.api.Test;
import za.co.wethinkcode.weshare.ratingServerWeShare;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ratingControllerTest {
   ratingServerWeShare server = new ratingServerWeShare();
   public final static Integer TEST_PORT = 8081;

    public HttpResponse<JsonNode> getRatings(String sortBy) {
        return Unirest.get("http://localhost:" + TEST_PORT + "/ratings?by=" + sortBy)
                .asJson();
    }

    @Test
    public void getStatusTest(){
        server.start(TEST_PORT);
        HttpResponse<JsonNode> response = Unirest.get("http://localhost:" + TEST_PORT + "/ratings").asJson();
        assertEquals(response.getStatus(), 200);
        JSONArray getResponse = new JSONArray(getRatings("expense").getBody().toString());
        JSONObject person = (JSONObject) getResponse.get(0);
        assertEquals("test@test.com", person.get("email"));
        assertEquals("100.00", person.get("amount"));
        assertEquals(LocalDate.now().toString(), person.get("timeStamp"));
        assertEquals(1, getResponse.length());
        server.stop();
    }

    @Test
    public void createExpenseTest(){
        server.start(TEST_PORT);
        HttpResponse<JsonNode> response = Unirest.post("http://localhost:" + TEST_PORT + "/ratings?event=expense")
                .body("{" +
                        "\"email\":\"test@test.com\"," +
                        "\"amount\":\"100\"" +
                        "}")
                .asJson();
        assertEquals(201, response.getStatus());
        server.stop();
    }

}
