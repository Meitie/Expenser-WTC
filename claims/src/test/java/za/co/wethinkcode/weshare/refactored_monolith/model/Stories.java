package za.co.wethinkcode.weshare.refactored_monolith.model;

import org.junit.jupiter.api.Test;
import za.co.wethinkcode.weshare.app.db.DataRepository;
import za.co.wethinkcode.weshare.app.model.Claim;
import za.co.wethinkcode.weshare.app.model.Settlement;
import za.co.wethinkcode.weshare.refactored_monolith.TestData;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class Stories {

    @Test
    public void HermanClaimsAnExpenseFromSettAndMike() {
        DataRepository db = TestData.dbWithExpensesAndClaimsForAndAgainst();

        // Herman is a user
        String herman = "herman@wethinkcode.co.za";

        // Herman already has claims against some people
        List<Claim> hermansClaims = db.findUnsettledClaimsClaimedBy(herman);
        Double previousTotalClaims =  db.getTotalUnsettledClaimsClaimedBy(herman);
        int numberOfClaims = hermansClaims.size();

        // Herman makes a claim for lunch against Sett
        String sett = "sett@wethinkcode.co.za";
        Claim claimForSett = db.addClaim(new Claim("lunch-expense", herman, sett, 50.00, LocalDate.of(2021, 10, 30)));

        // Herman makes a claim for lunch against Mike
        String mike = "mike@wethinkcode.co.za";
        Claim claimForMike = db.addClaim(new Claim("braai-expense", herman, mike, 150.00, LocalDate.of(2021, 10, 30)));

        // Herman should have 2 more claims
        assertEquals(previousTotalClaims + claimForSett.getAmount() + claimForMike.getAmount(),
                db.getTotalUnsettledClaimsClaimedBy(herman));
        hermansClaims = db.findUnsettledClaimsClaimedBy(herman);
        assertEquals(numberOfClaims + 2, hermansClaims.size());

    }

    @Test
    public void HermanSettlesAClaimFromSett() {
        DataRepository db = TestData.dbWithExpensesAndClaimsForAndAgainst();

        // Herman is a user
        String herman = "herman@wethinkcode.co.za";
        // and so is Sett
        String sett = "sett@wethinkcode.co.za";

        // Sett has an expense and he claims it from Herman
        Claim settsClaim = db.addClaim(new Claim("sett-expense", sett, herman, 100.00, LocalDate.of(2021,10,17)));

        // find the claim from Sett that Herman has to settle
        Claim claimFromSett = db.getClaim(settsClaim.getId())
                .orElseThrow(() -> new RuntimeException("claim not found"));

        // Herman settles Sett's claim
        Settlement settlement = db.addSettlement(claimFromSett.settleClaim(LocalDate.of(2021,10,30)));

        // Settled sett claim should exist
        assertFalse(db.getClaimsBy(sett, false).isEmpty());
    }
}