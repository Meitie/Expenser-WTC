package za.co.wethinkcode.weshare.user.pages;

import org.openqa.selenium.By;
import za.co.wethinkcode.weshare.user.UserTestRunner;

import java.time.LocalDate;

public class ExpensePage extends AbstractPage {

    private final By descriptionField = By.id("description");
    private final By amountField = By.id("amount");
    private final By dateField = By.id("date");

    public ExpensePage(UserTestRunner testRunner) {
        super(testRunner);
    }

    @Override
    public String path() {
        return "/newexpense";
    }

    public NettExpensesPage submitExpense(String description, double amount, LocalDate date) {
        fillText(descriptionField, description);
        fillMoney(amountField, amount);
        fillDate(dateField, date);
        submit();
        return new NettExpensesPage(testRunner);
    }
}