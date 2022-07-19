package za.co.wethinkcode.weshare.claim;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import io.javalin.http.Context;
import io.javalin.http.HttpCode;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import org.eclipse.jetty.util.ajax.JSON;
import za.co.wethinkcode.weshare.ClaimServer;
import za.co.wethinkcode.weshare.app.HttpCommunicate;
import za.co.wethinkcode.weshare.app.db.DataRepository;
import za.co.wethinkcode.weshare.app.model.Claim;

import java.time.LocalDate;
import java.util.*;


public class ClaimsController {
    public static final String getByIdPath = "/claim/{id}";
    public static final String postClaimPath = "/claim/";
    public static final String putClaimPath = "/claim/";
    public static final String getClaimsFromPath = "/claims/from/{email}";
    public static final String getClaimsByPath = "/claims/by/{email}";
    public static final String getTotalClaimValueFrom = "/totalclaimvalue/from/{email}";
    public static final String getTotalClaimValueBy = "/totalclaimvalue/by/{email}";

    private static final DataRepository db = ClaimServer.dataRepository1;
    private static final int EXPENSE_PORT = 8123;

    public static void getOne(Context context){
        String id = context.pathParam("id");

        UUID uuid = null;
        try{
            uuid = UUID.fromString(id);
        }catch (Exception e){
            context.status(HttpCode.BAD_REQUEST);
            context.json("Error: "+e.getMessage());
            return;
        }

        Claim claim;
        try{
//            System.out.println("found claim");
            claim = db.getClaim(uuid).get();
        }catch (NoSuchElementException e){
            context.status(HttpCode.NOT_FOUND);
            context.json(new ArrayList<>());
            return;
        }
        context.json(claim);
    }

    public static void createClaim(Context context){

        try{
            HashMap claim = context.bodyAsClass(HashMap.class);

//            String expenseUUID = context.queryParam("expenseId");

            String expenseUUID = claim.get("expenseId").toString();


            String claimFromEmail = (String) claim.get("claimedFrom");


            String claimByEmail = claim.get("claimedBy").toString();


            Double claimAmount = Double.parseDouble(claim.get("amount").toString());

            LocalDate dueDate = LocalDate.parse(claim.get("dueDate").toString());

            Claim newClaim = db.addClaim(new Claim(expenseUUID, claimByEmail, claimFromEmail, claimAmount, dueDate));

            //Interaction with Expense API
            postExpense(context, newClaim);

            context.json(newClaim);
        }catch (Exception e){
            context.status(HttpCode.BAD_REQUEST);
            context.result("Invalid body");
        }
    }

    public static void updateClaim(Context context){
        try{
            HashMap claim = context.bodyAsClass(HashMap.class);

            String claimedFrom = claim.get("claimFromWho").toString();
            Double claimedAmount = Double.parseDouble(claim.get("claimAmount").toString());
            LocalDate dueDate = LocalDate.parse(claim.get("dueDate").toString());

            Claim updateClaim = db.getClaim(UUID.fromString(claim.get("id").toString())).get();
            Claim updatedClaim = new Claim(updateClaim.getExpenseUUID(), updateClaim.getClaimedBy(), claimedFrom, claimedAmount, dueDate);
            updatedClaim.setId(updateClaim.getId().toString());
            db.updateClaim(updatedClaim);

            //Interaction with Expense API
            postExpense(context, updatedClaim);

            context.json(updatedClaim);
        }catch (Exception e){
            context.status(HttpCode.BAD_REQUEST);
            context.result("Invalid body");
        }
    }

    public static void getAllClaimsFrom(Context context){

        String claimFromEmail = context.pathParam("email");
        String settled = context.queryParam("settled");

        List<Claim> claimList = null;
        JSONArray cl = new JSONArray();

        if (settled.equalsIgnoreCase("false")){
            claimList = db.getClaimsFrom(claimFromEmail, true);

            if (claimList.isEmpty()){
                context.json("There are currently no claims");
                context.status(HttpCode.NOT_FOUND);
                return;
            }

//            context.json(claimList);

        } else if (settled.equalsIgnoreCase("true")){

            claimList = db.getClaimsFrom(claimFromEmail, false);

            claimList = claimList.stream()
                    .filter(claim -> claim.getClaimedFrom().equals(claimFromEmail) && (claim.isSettled()))
                    .sorted(Comparator.comparing(Claim::getDueDate))
                    .collect(ImmutableList.toImmutableList());

            if (claimList.isEmpty()){
                context.json("There are currently no settled claims for "+claimFromEmail);
                context.status(HttpCode.NOT_FOUND);
                return;
            }
//            context.json(claimList);
        }

//        for (Claim c : claimList) {
//            JSONObject o = new JSONObject();
//            o.put("id", c.getId());
//            o.put("expenseUUID", c.getExpenseUUID());
//            o.put("claimedBy", c.getClaimedBy());
//            o.put("claimedFrom", c.getClaimedFrom());
//            o.put("amount", c.getAmount());
//            o.put("dueDate", c.getDueDate());
//
//            cl.put(o);
//        }
//        System.out.println("here is the claims list");
//        System.out.println(claimList);
//        System.out.println("end claimList");
        context.json(claimList);
//        System.out.println(context.body());
    }

    public static void getAllClaimsBy(Context context){
        String claimByEmail = context.pathParam("email");
        String settled = context.queryParam("settled");

        if (settled.equalsIgnoreCase("false")){
            List<Claim> claimList = db.getClaimsBy(claimByEmail, true);

            if (claimList.isEmpty()){
                context.json("There are currently no claims");
                context.status(HttpCode.NOT_FOUND);
                return;
            }

            context.json(claimList);

        } else if (settled.equalsIgnoreCase("true")){
            List<Claim> claimList = db.getClaimsBy(claimByEmail, false);

            claimList = claimList.stream()
                    .filter(claim -> claim.getClaimedFrom().equals(claimByEmail) && (claim.isSettled()))
                    .sorted(Comparator.comparing(Claim::getDueDate))
                    .collect(ImmutableList.toImmutableList());

            if (claimList.isEmpty()){
                context.json("There are currently no settled claims for "+claimByEmail);
                context.status(HttpCode.NOT_FOUND);
                return;
            }
            context.json(claimList);
        }
    }

    public static void getTotalClaimValueFrom(Context context){
        String claimFromEmail = context.pathParam("email");
        Double totalUnSettledClaimsClaimedFrom = db.getTotalUnsettledClaimsClaimedFrom(claimFromEmail);

        context.json(totalUnSettledClaimsClaimedFrom);
    }

    public static void getTotalClaimValueBy(Context context){
        String claimByEmail = context.pathParam("email");
        Double totalUnSettledClaimsClaimedBy = db.getTotalUnsettledClaimsClaimedBy(claimByEmail);

        context.json(totalUnSettledClaimsClaimedBy);
    }

    private static void postExpense(Context context, Claim claim) throws JsonProcessingException {
        //Interaction with Expense API
//        System.out.println("post expense");
        JSONObject expenseData = HttpCommunicate.get("http://localhost:8123/expenses/"+claim.getExpenseUUID());
//        System.out.println(expenseData);
        JSONObject expense = new JSONObject();

        expense.put("paidBy", claim.getClaimedBy());
        expense.put("description", expenseData.get("description").toString());
        expense.put("amount", Double.parseDouble(expenseData.get("amount").toString()));
        expense.put("date", LocalDate.parse(expenseData.get("date").toString()));
        expense.put("sumOfUnsettledClaims", db.getTotalUnsettledClaimsClaimedBy(claim.getClaimedBy()));
        expense.put("sumOfSettledClaims", db.getTotalSettledClaimsClaimedBy(claim.getClaimedBy()));

        ObjectMapper mapper = new ObjectMapper();
        JsonNode exp = mapper.readTree(expense.toString());

        try {
//            System.out.println(expense);
            HttpResponse res = Unirest.post("http://localhost:8123/expenses").body(exp).asJson();
//            System.out.println("sending to expense");
        }catch (Exception e){
            context.json(e.getMessage());
        }
    }

}
