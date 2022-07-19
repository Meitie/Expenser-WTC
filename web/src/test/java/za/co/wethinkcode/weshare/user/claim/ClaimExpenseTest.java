package za.co.wethinkcode.weshare.user.claim;

import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import za.co.wethinkcode.weshare.ExpenseBuilder;
import za.co.wethinkcode.weshare.app.db.DataRepository;
import za.co.wethinkcode.weshare.app.model.Expense;
import za.co.wethinkcode.weshare.app.model.Person;
import za.co.wethinkcode.weshare.user.UserTestRunner;
import za.co.wethinkcode.weshare.user.pages.ClaimPage;
import za.co.wethinkcode.weshare.user.pages.ExpensePage;
import za.co.wethinkcode.weshare.user.pages.LoginPage;
import za.co.wethinkcode.weshare.user.pages.NettExpensesPage;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class ClaimExpenseTest extends UserTestRunner {
    private final Person person1 = generateRandomPerson();
    private final Person person2 = generateRandomPerson();
    private final Person person3 = generateRandomPerson();

    @Test
    void firstClaim() {
        NettExpensesPage nettExpensesPage = login(person1.getEmail());

        // load page to claim an expense
        ClaimPage claimPage = nettExpensesPage.clickOnExpenseAtRow(1);
        shouldBeOnPage(claimPage);
        assertThat(claimPage.expenseDescription()).isEqualTo("Friday Lunch");
        assertThat(claimPage.expenseDate()).isEqualTo(LocalDate.of(2021, 10, 17));
        assertThat(claimPage.expenseAmount()).isEqualTo(300.00);

        // capture a claim
        claimPage.captureClaim(person2.getEmail(), 50.00, LocalDate.of(2021,10,30));

        // check that the claim is visible on the page
        LoginPage loginPage = nettExpensesPage.logout();
        nettExpensesPage = login(person1.getEmail());
        navigateTo(nettExpensesPage);

        // check the totals are correct
        assertThat(nettExpensesPage.totalExpenses()).isEqualTo(600.00);
        assertThat(nettExpensesPage.totalClaims()).isEqualTo(50.00);
        assertThat(nettExpensesPage.totalNettExpenses()).isEqualTo(550.00);

        // check that the claim page shows previous claims
        claimPage = nettExpensesPage.clickOnExpenseAtRow(1);
        assertThat(claimPage.claimFrom(1)).isEqualTo(person2.getName());
        assertThat(claimPage.claimAmount(1)).isEqualTo(50.00);
        assertThat(claimPage.claimSettled(1)).isEqualTo("No");
        assertThat(claimPage.claimDueDate(1)).isEqualTo(LocalDate.of(2021,10,30));
    }

    @Test
    void secondClaim() {
        NettExpensesPage nettExpensesPage = login(person1.getEmail());

        // load page to claim an expense
        ClaimPage claimPage = nettExpensesPage.clickOnExpenseAtRow(2);
        shouldBeOnPage(claimPage);
        assertThat(claimPage.expenseDescription()).isEqualTo("Movies");
        assertThat(claimPage.expenseDate()).isEqualTo(LocalDate.of(2021, 10, 19));
        assertThat(claimPage.expenseAmount()).isEqualTo(300.00);

        // capture a claim
        claimPage.captureClaim(person3.getEmail(), 150.00, LocalDate.of(2021,10,31));

        // check the totals are correct
        navigateTo(nettExpensesPage);
        assertThat(nettExpensesPage.totalExpenses()).isEqualTo(600.00);
        assertThat(nettExpensesPage.totalClaims()).isEqualTo(200.00);
        assertThat(nettExpensesPage.totalNettExpenses()).isEqualTo(400.00);
    }

    @Override
    protected void setupTestData() {
        DataRepository.getInstance().addPerson(person1);
        DataRepository.getInstance().addPerson(person2);
        DataRepository.getInstance().addPerson(person3);

        ImmutableList<Expense> expenses = new ExpenseBuilder(person1)
                .spent(300.00, "Friday Lunch", LocalDate.of(2021, 10, 17))
                .spent(300.00, "Movies", LocalDate.of(2021, 10, 19))
                    .claim(person2, 50.00, LocalDate.of(2021,10,30))
                .build();
        expenses.forEach(expense -> DataRepository.getInstance().addExpense(expense));
    }
}
