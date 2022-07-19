package za.co.wethinkcode.weshare.user.pages;

import org.openqa.selenium.By;
import za.co.wethinkcode.weshare.user.UserTestRunner;

import java.text.DecimalFormat;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class SettlementPage extends AbstractPage {
    private final String path;
    private final By email = By.id("email");
    private final By dueDate = By.id("due_date");
    private final By description = By.id("description");
    private final By amount = By.id("claim_amount");

    public SettlementPage(UserTestRunner testRunner, String path) {
        super(testRunner);
        this.path = path;
    }

    @Override
    public String path() {
        return path;
    }

    public String email() {
        return valueOfField(email);
    }

    public LocalDate dueDate() {
        return LocalDate.parse(valueOfField(dueDate));
    }

    public String description() {
        return valueOfField(description);
    }

    public double amount() {
        return Double.parseDouble(valueOfField(amount));
    }

    public NettExpensesPage settleClaim() {
        submit();
        return new NettExpensesPage(testRunner);
    }
}
