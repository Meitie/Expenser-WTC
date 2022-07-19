package za.co.wethinkcode.weshare.refactored_monolith;

import za.co.wethinkcode.weshare.app.db.DataRepository;
import za.co.wethinkcode.weshare.app.db.memory.ClaimsMemoryDb;
import za.co.wethinkcode.weshare.app.model.Claim;

import java.time.LocalDate;

public class TestData {

    public static String me() {
        return "me@mydomain.com";
    }

    public static Claim lunchClaim(double amount) {
        return new Claim("expense-lunch-1", me(), friend("jeff"), amount, LocalDate.of(2021, 10, 20));
    }

    public static String friend(String name) {
        return name + "@friends.com";
    }

    public static DataRepository dbWithNoExpenses() {
        return new ClaimsMemoryDb();
    }

    public static DataRepository dbWithExpensesAndClaimsForAndAgainst() {
        DataRepository db = new ClaimsMemoryDb();

        String herman = "herman@wethinkcode.co.za";
        String mike = "mike@wethinkcode.co.za";

        // herman claims from mike
        db.addClaim(new Claim("expense-braai", herman, mike, 200.00, LocalDate.of(2021, 11, 1)));

        return db;
    }
}