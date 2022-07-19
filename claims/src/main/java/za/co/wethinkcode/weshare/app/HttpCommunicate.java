package za.co.wethinkcode.weshare.app;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;

import java.util.HashMap;

public class HttpCommunicate {

    public static JSONObject get(String url){
        HttpResponse<JsonNode> response = Unirest.get(url).asJson();
        JSONObject responseBody = response.getBody().getObject();
        return responseBody;
    }

    public static void post(String url, HashMap body){
        HttpResponse<JsonNode> response = Unirest.post(url)
                .header("Content-Type", "application/json")
                .body(body)
                .asJson();

        JSONObject responseBody = response.getBody().getObject();

//        System.out.println(responseBody);
    }

}
