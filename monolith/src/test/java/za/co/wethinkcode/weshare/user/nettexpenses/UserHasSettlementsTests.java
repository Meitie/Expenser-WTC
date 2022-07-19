package za.co.wethinkcode.weshare.user.nettexpenses;

import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.Test;
import za.co.wethinkcode.weshare.ExpenseBuilder;
import za.co.wethinkcode.weshare.app.db.DataRepository;
import za.co.wethinkcode.weshare.app.model.Expense;
import za.co.wethinkcode.weshare.app.model.Person;
import za.co.wethinkcode.weshare.user.UserTestRunner;
import za.co.wethinkcode.weshare.user.pages.NettExpensesPage;

import java.time.LocalDate;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

public class UserHasSettlementsTests extends UserTestRunner {
    private final Person person1 = generateRandomPerson();
    private final Person person2 = generateRandomPerson();
    private final Person person3 = generateRandomPerson();

    @Test
    void hasSettlements() {
        NettExpensesPage page = login(person1.getEmail());

        // settlements
        assertThat(page.hasSettlements()).isTrue();
        assertThat(page.settlementDescription(1)).isEqualTo("Movies");
        assertThat(page.settlementDescription(2)).isEqualTo("Lunch");

        assertThat(page.totalSettlements()).isEqualTo(200.00);

        // nett total
        assertThat(page.totalNettExpenses()).isEqualTo(200.00);
    }

    @Override
    protected void setupTestData() {
        ImmutableList<Expense> student2Expenses = new ExpenseBuilder(person2)
                .spent(200.00, "Lunch", LocalDate.of(2021, 11, 1))
                .claim(person1, 100.00, LocalDate.of(2021, 11, 15))
                .build();

        ImmutableList<Expense> student3Expenses = new ExpenseBuilder(person3)
                .spent(200.00, "Movies", LocalDate.of(2021, 11, 7))
                .claim(person1, 100.00, LocalDate.of(2021, 11, 9))
                .build();

        saveExpenseAndClaims(student2Expenses);
        saveExpenseAndClaims(student3Expenses);
    }

    private void saveExpenseAndClaims(ImmutableList<Expense> expenses) {
        expenses.forEach(expense -> DataRepository.getInstance().addExpense(expense));
        expenses.stream().map(Expense::getClaims).flatMap(Collection::stream)
                .forEach(claim -> DataRepository.getInstance().addClaim(claim));
    }
}
