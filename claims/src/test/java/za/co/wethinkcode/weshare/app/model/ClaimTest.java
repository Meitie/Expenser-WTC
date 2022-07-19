package za.co.wethinkcode.weshare.app.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ClaimTest {

    Claim claim = new Claim(
            "expenseId",
            "me@email.com",
            "friend@email.com",
            50.0,
            LocalDate.of(2022, 2, 21));

    @Test
    void getExpenseUUID() {
        assertEquals("expenseId", claim.getExpenseUUID());
    }

    @Test
    void getClaimedBy() {
        assertEquals("me@email.com", claim.getClaimedBy());

    }

    @Test
    void getClaimedFrom() {
        assertEquals("friend@email.com", claim.getClaimedFrom());
    }

    @Test
    void getDueDate() {
        assertEquals(LocalDate.of(2022, 2, 21), claim.getDueDate());
    }

    @Test
    void getAmount() {
        assertEquals(50.0, claim.getAmount());
    }

    @Test
    void isSettled() {
        claim.settleClaim(claim.getDueDate());
        assertTrue(claim.isSettled());
    }
}