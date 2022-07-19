package monolithTests;

import org.junit.jupiter.api.Test;
import za.co.wethinkcode.expenses.weshare.app.model.Expense;


import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class ExpenseTests {

    @Test
    public void newExpenseHasAnId() {
        Expense e = TestData.lunchExpense(100.00);
        assertNotNull(e.getId());
    }

    @Test
    void emptyDescriptionDefaultsToUnspecified() {
        Expense e = new Expense(100.00, LocalDate.of(2021, 10, 30), "", TestData.me());
        assertEquals("Unspecified", e.getDescription());
    }

    @Test
    void nullDescriptionDefaultsToUnspecified() {
        Expense e = new Expense(100.00, LocalDate.of(2021, 10, 30), null, TestData.me());
        assertEquals("Unspecified", e.getDescription());
    }


}
