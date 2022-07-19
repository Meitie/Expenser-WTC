package za.co.wethinkcode.weshare.user.pages;

import org.openqa.selenium.By;
import za.co.wethinkcode.weshare.user.UserTestRunner;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.function.Function;

public class NettExpensesPage extends AbstractPage {
    private final By user = By.id("user");
    private final By logout = By.id("logout");

    private final By noExpenses = By.id("no_expenses");
    private final By expenses = By.id("expenses");
    private final Function<Integer, By> expenseDescriptionRow = rowLocator().apply("claim_expense");
    private final Function<Integer, By> expenseAmountRow = rowLocator().apply("amount");
    private final Function<Integer, By> expenseDateRow = rowLocator().apply("date");
    private final By totalExpenses = By.id("expenses_total");

    private final By noClaims = By.id("nobody_owes");
    private final By claims = By.id("they_owe_me");
    private final By totalClaims = By.id("they_owe_me_total");

    private final By noSettlements = By.id("owe_nobody");
    private final By settlements = By.id("i_owe_them");
    private final Function<Integer, By> settlementDescriptionRow = rowLocator().apply("settle");
    private final By totalSettlements = By.id("i_owe_them_total");

    private final By totalNettExpenses = By.id("nett_expenses");

    private final By addExpenseLink = By.id("add_expense");

    public NettExpensesPage(UserTestRunner testRunner) {
        super(testRunner);
    }

    @Override
    public String path() {
        return "/home";
    }

    public String userEmail() {
        return driver.findElement(user).getText();
    }

    public String logoutText() {
        return driver.findElement(logout).getText();
    }

    public LoginPage logout() {
        click(logout);
        return new LoginPage(testRunner);
    }

    public boolean hasNoExpenses() {
        return textOf(noExpenses).equals("You don't have any expenses!");
    }

    public boolean hasNoClaims() {
        return textOf(noClaims).equals("Nobody owes you any money!");
    }

    public boolean hasNoSettlements() {
        return textOf(noSettlements).equals("You don't owe anyone!");
    }

    public boolean hasExpenses() {
        return isDisplayed(expenses);
    }

    public double totalExpenses() {
        return moneyOf(totalExpenses);
    }

    public String expenseDescription(int row) {
        return textOf(expenseDescriptionRow.apply(row));
    }

    public double expenseAmount(int row) {
        return moneyOf(expenseAmountRow.apply(row));
    }

    public LocalDate expenseDate(int row) {
        return localDateOf(expenseDateRow.apply(row));
    }

    public boolean hasClaims() {
        return isDisplayed(claims);
    }

    public double totalClaims() {
        return moneyOf(totalClaims);
    }

    public boolean hasSettlements() {
        return isDisplayed(settlements);
    }

    public String settlementDescription(int row) {
        return textOf(settlementDescriptionRow.apply(row));
    }

    public double totalSettlements() {
        return moneyOf(totalSettlements);
    }

    public double totalNettExpenses() {
        return moneyOf(totalNettExpenses);
    }

    public ExpensePage captureExpense() {
        click(addExpenseLink);
        return new ExpensePage(testRunner);
    }

    public ClaimPage clickOnExpenseAtRow(int row) {
        By claimLinkLocator = expenseDescriptionRow.apply(row);
        String href = hrefOf(claimLinkLocator);
        String path = "";
        try {
            URI uri = new URI(href);
            path = uri.getPath() + "?" + uri.getQuery();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        click(claimLinkLocator);
        return new ClaimPage(testRunner, path);
    }

    public SettlementPage clickOnSettlementAtRow(int row) {
        By settlementLinkLocator = settlementDescriptionRow.apply(row);
        String href = hrefOf(settlementLinkLocator);
        String path = "";
        try {
            URI uri = new URI(href);
            path = uri.getPath() + "?" + uri.getQuery();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        click(settlementLinkLocator);
        return new SettlementPage(testRunner, path);
    }
}
