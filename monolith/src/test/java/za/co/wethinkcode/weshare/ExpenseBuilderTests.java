package za.co.wethinkcode.weshare;

import org.junit.jupiter.api.Test;
import za.co.wethinkcode.weshare.app.model.Claim;
import za.co.wethinkcode.weshare.app.model.Expense;
import za.co.wethinkcode.weshare.app.model.Person;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class ExpenseBuilderTests {
    @Test
    void expenseHasClaims() {
        Person p = new Person("p@example.com");
        Person q = new Person("q@example.com");
        Person r = new Person("r@example.com");
        List<Expense> expenses = (new ExpenseBuilder(p))
                .spent(100.00, "Junk", LocalDate.of(2020, 12, 31))
                .claim(q, 50.00, LocalDate.of(2021, 1, 1))
                .claim(r, 50.00, LocalDate.of(2021, 1, 1))
                .build();

        assertThat(expenses).asList().hasSize(1);
        Expense e = expenses.get(0);
        Set<Claim> claims = e.getClaims();
        assertThat(claims.size()).isEqualTo(2);

        claims.forEach(c -> {
            assertThat(c.getDescription()).isEqualTo("Junk");
            assertThat(c.getDueDate()).isEqualTo(LocalDate.of(2021, 1, 1));
            assertThat(c.getAmount()).isEqualTo(50.00);
            assertThat(c.getExpense()).isEqualTo(e);
            assertThat(c.isSettled()).isFalse();
            assertThat(c.getClaimedBy()).isEqualTo(p);
        });
    }

    @Test
    void justOneExpense() {
        Person p = new Person("p@example.com");
        List<Expense> expenses = (new ExpenseBuilder(p))
                .spent(100.00, "Junk", LocalDate.of(2020, 12, 31))
                .build();

        assertThat(expenses).asList().hasSize(1);
        Expense e = expenses.get(0);
        assertThat(e.getDescription()).isEqualTo("Junk");
        assertThat(e.getPaidBy()).isEqualTo(p);
        assertThat(e.getDate()).isEqualTo(LocalDate.of(2020, 12, 31));
        assertThat(e.getAmount()).isEqualTo(100.00);
    }

    @Test
    void twoExpenses() {
        Person p = new Person("p@example.com");
        List<Expense> expenses = (new ExpenseBuilder(p))
                .spent(100.00, "Junk", LocalDate.of(2020, 12, 31))
                .spent(200.00, "More Junk", LocalDate.of(2020, 12, 31))
                .build();
        assertThat(expenses).asList().hasSize(2);
    }
}
