package za.co.wethinkcode.weshare.ratings;

import io.javalin.http.Context;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;

import java.util.Map;


public class RatingsController {

    public static  final String PATH = "/ratings";

    public static void renderRatings(Context context){
        String by = context.queryParam("by");
        HttpResponse<JsonNode> res= Unirest.get("http://localhost:8080/ratings?by="+by).asJson();
        JSONArray ratings = new JSONArray();
        if (res.getBody() != null) {
            ratings = res.getBody().getArray();
        }

        Map<String , JSONArray> viewModel= Map.of("ratings", ratings);
        context.sessionAttribute("by", getHeader("by"));
        context.render("ratings.html", viewModel);
    }

    private static String getHeader(String by) {
        switch (by) {
            case "unsettled":
            case "settled":
                by += " Claims";
            case "nettexpenses":
                by = "nett Expenses";
            default:
                by = by.substring(0,1).toUpperCase() + by.substring(1);
        }

        return "Ratings by " + by;
    }
}
