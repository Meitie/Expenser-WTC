package za.co.wethinkcode.weshare.user.pages;

import org.openqa.selenium.By;
import za.co.wethinkcode.weshare.user.UserTestRunner;

public class LoginPage extends AbstractPage {
    private final By emailField = By.name("email");

    public LoginPage(UserTestRunner testRunner) {
        super(testRunner);
    }

    public NettExpensesPage loginUser(String email) {
        fillText(emailField, email);
        submit();
        return new NettExpensesPage(testRunner);
    }

    @Override
    public String path() {
        return "/index.html";
    }

}
