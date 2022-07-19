package za.co.wethinkcode.weshare.app.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class SettlementTest {

    Claim claim = new Claim(
            "expenseId",
            "me@email.com",
            "friend@email.com",
            50.0,
            LocalDate.of(2022, 2, 21));

    Settlement settlement = claim.settleClaim(claim.getDueDate());

    @Test
    void getSettlementDate() {
        assertEquals(LocalDate.of(2022, 2, 21), settlement.getSettlementDate());
    }

    @Test
    void getSettlementClaimUUID() {
        assertNotNull(settlement.getClaimUUID());
        assertEquals(claim.getId(), settlement.getClaimUUID());
    }
}