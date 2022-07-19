package za.co.wethinkcode.weshare.endpoints;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import za.co.wethinkcode.weshare.ClaimServer;
import za.co.wethinkcode.weshare.app.db.memory.ClaimsMemoryDb;

import static org.junit.jupiter.api.Assertions.*;

public class EndpointTests {
    private static final int TEST_SERVER_PORT = 7073;
    private static final String BASE_URL = "http://localhost:" + TEST_SERVER_PORT;
    private static ClaimServer server;

    @BeforeAll
    public static void startServer() {
        server = new ClaimServer(ClaimsMemoryDb.INSTANCE);
        server.start(TEST_SERVER_PORT);
    }

    @AfterAll
    public static void stopServer() {
        server.close();
    }

    @Test
    @DisplayName("Server status: GET /claims/by/{email}?{settled=value}")
    void testClaimsBySuccessfulStatus() {
        HttpResponse<JsonNode> resp = Unirest.get(BASE_URL + "/claims/by/herman@wethinkcode.co.za?settled=false").asJson();
        assertEquals(resp.getStatus(), 200);
        assertNotNull(resp.getBody());

    }

    @Test
    @DisplayName("Server status: GET /claims/by/{email}?{settled=value}")
    void testClaimsByUnknownReturns404Status() {
        HttpResponse<JsonNode> resp = Unirest.get(BASE_URL + "/claims/by/unknownperson@wethinkcode.co.za?settled=false").asJson();
        assertEquals(resp.getStatus(), 404);

    }

    @Test
    @DisplayName("Server status: GET /claims/from/{email}?{settled=value}")
    void testClaimFromSuccessfulStatus() {
        HttpResponse<JsonNode> resp = Unirest.get(BASE_URL + "/claims/from/herman@wethinkcode.co.za?settled=false").asJson();
        assertEquals(resp.getStatus(), 200);
        assertNotNull(resp.getBody());
    }

    @Test
    @DisplayName("Server status: GET /claims/from/{email}?{settled=value}")
    void testClaimsFromUnknownReturns404Status() {
        HttpResponse<JsonNode> resp = Unirest.get(BASE_URL + "/claims/from/unknownperson@wethinkcode.co.za?settled=false").asJson();
        assertEquals(resp.getStatus(), 404);

    }

    @Test
    @DisplayName("Server status: GET /totalclaimvalue/from/{email}")
    void testGetTotalClaimsFromSuccessfulStatus() {
        HttpResponse<JsonNode> resp = Unirest.get(BASE_URL + "/totalclaimvalue/from/mike@student.wethinkcode.co.za").asJson();
        assertEquals(resp.getStatus(), 200);

    }

    @Test
    @DisplayName("Server status: GET /totalclaimvalue/by/{email}")
    void testGetTotalClaimsBySuccessfulStatus() {
        HttpResponse<JsonNode> resp = Unirest.get(BASE_URL + "/totalclaimvalue/by/herman@student.wethinkcode.co.za").asJson();
        assertEquals(resp.getStatus(), 200);

    }

//    @Test
//    @DisplayName("POST /claim")
//    void create() throws UnirestException {
//
//        Claim claim = new Claim(
//                "49ef6d8e-8ea9-4455-bd74-a7fa26cb3633",
//                "me@email.com",
//                "you@email.com",
//                50.0,
//                LocalDate.of(2022,2,22)
//        );
//
//        JSONObject body = new JSONObject();
//        body.put("expenseId", claim.getExpenseUUID());
//        body.put("claimedBy", claim.getClaimedBy());
//        body.put("claimedFrom", claim.getClaimedFrom());
//        body.put("amount", claim.getAmount().toString());
//        body.put("dueDate", claim.getDueDate().toString());
//
//        HttpResponse<JsonNode> response = Unirest.post(BASE_URL + "/claim")
//                .header("Content-Type", "application/json")
//                .body(body)
//                .asJson();
//        assertEquals(200, response.getStatus());
//
//        response = Unirest.get(BASE_URL + "/claim/"+claim.getId()).asJson();
//        assertEquals(200, response.getStatus());
//    }

}
