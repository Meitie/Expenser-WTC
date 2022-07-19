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

public class UserHasExpensesTests extends UserTestRunner {
    private final Person person = generateRandomPerson();

    @Test
    void hasExpenses() {
        NettExpensesPage page = login(person.getEmail());

        // expenses
        assertThat(page.hasExpenses()).isTrue();

        assertThat(page.expenseDescription(1)).isEqualTo("Road trip");
        assertThat(page.expenseDate(1)).isEqualTo(LocalDate.of(2021, 11, 10));
        assertThat(page.expenseAmount(1)).isEqualTo(600.00);

        assertThat(page.expenseDescription(2)).isEqualTo("Uber");
        assertThat(page.expenseDate(2)).isEqualTo(LocalDate.of(2021, 11, 12));
        assertThat(page.expenseAmount(2)).isEqualTo(100.00);

        assertThat(page.totalExpenses()).isEqualTo(700.00);

        // nett total
        assertThat(page.totalNettExpenses()).isEqualTo(700.00);
    }

    @Override
    protected void setupTestData() {
        ImmutableList<Expense> expenses = new ExpenseBuilder(person)
                .spent(600.00, "Road trip", LocalDate.of(2021, 11, 10))
                .spent(100.00, "Uber", LocalDate.of(2021, 11, 12))
                .build();
        expenses.forEach(expense -> DataRepository.getInstance().addExpense(expense));
        expenses.stream().map(Expense::getClaims).flatMap(Collection::stream)
                .forEach(claim -> DataRepository.getInstance().addClaim(claim));
    }
}
