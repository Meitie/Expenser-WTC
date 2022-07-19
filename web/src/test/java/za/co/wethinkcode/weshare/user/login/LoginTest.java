package za.co.wethinkcode.weshare.user.login;

import org.junit.jupiter.api.Test;
import za.co.wethinkcode.weshare.app.db.DataRepository;
import za.co.wethinkcode.weshare.app.model.Person;
import za.co.wethinkcode.weshare.user.UserTestRunner;
import za.co.wethinkcode.weshare.user.pages.LoginPage;
import za.co.wethinkcode.weshare.user.pages.NettExpensesPage;
import za.co.wethinkcode.weshare.util.Strings;

import static org.assertj.core.api.Assertions.assertThat;

public class LoginTest extends UserTestRunner {
    private final Person person = generateRandomPerson();

    @Test
    void redirectWhenNotLoggedIn() {
        NettExpensesPage nettExpensesPage = new NettExpensesPage(this);
        nettExpensesPage.open();
        shouldBeOnPage(new LoginPage(this));
    }

    @Test
    void successfulLogin() {
        LoginPage loginPage = new LoginPage(this);
        loginPage.open();
        shouldBeOnPage(loginPage);

        String user = person.getEmail();
        NettExpensesPage nettExpensesPage = loginPage.loginUser(user);
        shouldBeOnPage(nettExpensesPage);

        assertThat(nettExpensesPage.userEmail()).isEqualTo(user);
        assertThat(nettExpensesPage.logoutText())
                .isEqualTo("Logout " + Strings.capitaliseFirstLetter(user.split("@")[0]));
    }

    @Override
    protected void setupTestData() {
        DataRepository.getInstance().addPerson(person);
    }
}
