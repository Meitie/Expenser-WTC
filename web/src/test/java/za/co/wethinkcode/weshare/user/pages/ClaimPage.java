package za.co.wethinkcode.weshare.user.pages;

import org.openqa.selenium.By;
import za.co.wethinkcode.weshare.user.UserTestRunner;

import java.time.LocalDate;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

public class ClaimPage extends AbstractPage {
    private final String path;
    private final By expenseDescription = By.id("expense_description");
    private final By expenseDate = By.id("expense_date");
    private final By expenseAmount = By.id("expense_amount");
    private final By email = By.id("email");
    private final By claimAmount = By.id("claim_amount");
    private final By dueDate = By.id("due_date");
    private final By addClaimButton = By.id("add_claim");
    private final Function<Integer, By> priorClaimFrom = rowLocator().apply("claim_who");
    private final Function<Integer, By> priorClaimAmount = rowLocator().apply("claim_amount");
    private final Function<Integer, By> priorClaimSettled = rowLocator().apply("claim_settled");
    private final Function<Integer, By> priorClaimDueDate = rowLocator().apply("claim_date");

    public ClaimPage(UserTestRunner testRunner, String path) {
        super(testRunner);
        this.path = path;
    }

    @Override
    public String path() {
        return path;
    }

    public String expenseDescription() {
        return textOf(expenseDescription);
    }

    public LocalDate expenseDate() {
        return localDateOf(expenseDate);
    }

    public double expenseAmount() {
        return moneyOf(expenseAmount);
    }

    public void captureClaim(String email, double amount, LocalDate date) {
        fillText(this.email, email);
        fillMoney(claimAmount, amount);
        fillDate(dueDate, date);
        click(addClaimButton);
    }

    public String claimFrom(int row) {
        return textOf(priorClaimFrom.apply(row));
    }

    public double claimAmount(int row) {
        return moneyOf(priorClaimAmount.apply(row));
    }

    public String claimSettled(int row) {
        return textOf(priorClaimSettled.apply(row));
    }

    public LocalDate claimDueDate(int row) {
        return localDateOf(priorClaimDueDate.apply(row));
    }
}
