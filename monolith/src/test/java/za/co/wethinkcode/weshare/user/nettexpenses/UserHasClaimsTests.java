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

public class UserHasClaimsTests extends UserTestRunner {
    private final Person person1 = generateRandomPerson();
    private final Person person2 = generateRandomPerson();
    private final Person person3 = generateRandomPerson();

    @Test
    void hasClaims() {
        NettExpensesPage page = login(person1.getEmail());

        // claims
        assertThat(page.hasClaims()).isTrue();
        assertThat(page.totalClaims()).isEqualTo(400.00);

        // nett total
        assertThat(page.totalNettExpenses()).isEqualTo(300.00);
    }

    protected void setupTestData() {
        ImmutableList<Expense> expenses = new ExpenseBuilder(person1)
                .spent(600.00, "Road trip", LocalDate.of(2021, 11, 10))
                    .claim(person2, 200.00, LocalDate.of(2021, 11, 20))
                    .claim(person3, 200.00, LocalDate.of(2021, 11, 30))
                .spent(100.00, "Uber", LocalDate.of(2021, 11, 12))
                .build();
        expenses.forEach(expense -> DataRepository.getInstance().addExpense(expense));
        expenses.stream().map(Expense::getClaims).flatMap(Collection::stream)
                .forEach(claim -> DataRepository.getInstance().addClaim(claim));
    }
}
