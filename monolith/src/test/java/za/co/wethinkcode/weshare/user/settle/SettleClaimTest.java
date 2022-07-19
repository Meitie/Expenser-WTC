package za.co.wethinkcode.weshare.user.settle;

import com.google.common.collect.ImmutableList;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import za.co.wethinkcode.weshare.ExpenseBuilder;
import za.co.wethinkcode.weshare.app.db.DataRepository;
import za.co.wethinkcode.weshare.app.model.Expense;
import za.co.wethinkcode.weshare.app.model.Person;
import za.co.wethinkcode.weshare.user.UserTestRunner;
import za.co.wethinkcode.weshare.user.pages.LoginPage;
import za.co.wethinkcode.weshare.user.pages.NettExpensesPage;
import za.co.wethinkcode.weshare.user.pages.SettlementPage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

public class SettleClaimTest extends UserTestRunner {
    private final Person person1 = generateRandomPerson();
    private final Person person2 = generateRandomPerson();
    @Test
    void settleClaim() {
        NettExpensesPage expensesPage = login(person2.getEmail());

        // navigate to settlement page
        SettlementPage settlementPage = expensesPage.clickOnSettlementAtRow(1);
        shouldBeOnPage(settlementPage);
        assertThat(settlementPage.email()).isEqualTo(person1.getEmail());
        assertThat(settlementPage.dueDate()).isEqualTo(LocalDate.of(2021,11,30));
        assertThat(settlementPage.description()).isEqualTo("Road trip");
        assertThat(settlementPage.amount()).isEqualTo(250.00);

        // settle the claim
        expensesPage = settlementPage.settleClaim();
        shouldBeOnPage(expensesPage);

        // check that person has expense and nothing owing
        assertThat(expensesPage.hasNoSettlements()).isTrue();

        // check expense amount has been reduced
        assertThat(expensesPage.expenseAmount(1)).isEqualTo(250.00);

        // person1 should have a new expense
        LoginPage loginPage = expensesPage.logout();
        expensesPage = loginPage.loginUser(person1.getEmail());
        assertThat(expensesPage.expenseDescription(1)).isEqualTo("Road trip");
        assertThat(expensesPage.expenseDate(1)).isEqualTo(LocalDate.of(2021,11,10));
        assertThat(expensesPage.expenseAmount(1)).isEqualTo(250.00);
    }

    protected void setupTestData() {
        DataRepository.getInstance().addPerson(person1);

        DataRepository.getInstance().addPerson(person2);

        ImmutableList<Expense> expenses = new ExpenseBuilder(person1)
                .spent(500.00, "Road trip", LocalDate.of(2021, 11, 10))
                    .claim(person2, 250.00, LocalDate.of(2021, 11, 30))
                .build();

        expenses.forEach(expense -> DataRepository.getInstance().addExpense(expense));
        expenses.stream().map(Expense::getClaims).flatMap(Collection::stream)
                .forEach(claim -> DataRepository.getInstance().addClaim(claim));
    }

}
