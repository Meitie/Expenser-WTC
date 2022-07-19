package za.co.wethinkcode.weshare.user.expense;

import org.junit.jupiter.api.Test;
import za.co.wethinkcode.weshare.app.db.DataRepository;
import za.co.wethinkcode.weshare.app.model.Person;
import za.co.wethinkcode.weshare.user.UserTestRunner;
import za.co.wethinkcode.weshare.user.pages.ExpensePage;
import za.co.wethinkcode.weshare.user.pages.NettExpensesPage;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class CaptureExpenseTest extends UserTestRunner {
    Person person = generateRandomPerson();

    @Test
    void captureExpense() {
        // Login
        NettExpensesPage nettExpensesPage = login(person.getEmail());

        // load the page to capture an expense
        ExpensePage expensePage = nettExpensesPage.captureExpense();
        shouldBeOnPage(expensePage);

        nettExpensesPage = expensePage.submitExpense("Friday Lunch", 300.00, LocalDate.of(2021,10,17));
        assertThat(nettExpensesPage.hasExpenses()).isTrue();

        assertThat(nettExpensesPage.expenseDescription(1)).isEqualTo("Friday Lunch");
        assertThat(nettExpensesPage.expenseDate(1)).isEqualTo(LocalDate.of(2021, 10, 17));
        assertThat(nettExpensesPage.expenseAmount(1)).isEqualTo(300.00);

        assertThat(nettExpensesPage.totalExpenses()).isEqualTo(300.00);
        assertThat(nettExpensesPage.totalNettExpenses()).isEqualTo(300.00);

        assertThat(nettExpensesPage.hasNoClaims()).isTrue();
        assertThat(nettExpensesPage.hasNoSettlements()).isTrue();
    }

    @Override
    protected void setupTestData() {
        DataRepository.getInstance().addPerson(person);
    }
}
