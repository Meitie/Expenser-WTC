package za.co.wethinkcode.weshare.user;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import za.co.wethinkcode.weshare.WeShareWebServer;
import za.co.wethinkcode.weshare.app.model.Person;
import za.co.wethinkcode.weshare.user.pages.AbstractPage;
import za.co.wethinkcode.weshare.user.pages.LoginPage;
import za.co.wethinkcode.weshare.user.pages.NettExpensesPage;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Base class that sets up the browser driver and has common user steps.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class UserTestRunner {
    protected WeShareWebServer server;
    protected String baseUrl;
    protected ChromeDriver driver;

    protected abstract void setupTestData();

    @BeforeAll
    void startServer() {
        server = new WeShareWebServer();
        server.start(0);
        baseUrl = "http://localhost:" + server.port();
    }

    @AfterEach
    void kill() {
        driver.quit();
    }

    @BeforeEach
    void start() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox"); // necessary for grading environment
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        driver = new ChromeDriver(options);
        setupTestData();
    }

    @AfterAll
    void stopServer() {
        server.close();
    }

    public WebDriver driver() {
        return driver;
    }

    public void navigateTo(AbstractPage page) {
        driver.get(appUrl(page.path()));
    }

    protected String randomEmail() {
        return RandomStringUtils.randomAlphabetic(10) + "@wethinkcode.co.za";
    }

    protected Person generateRandomPerson() {
        return new Person(randomEmail());
    }

    protected String appUrl(String pageUrl) {
        return baseUrl + pageUrl;
    }

    protected void shouldBeOnPage(AbstractPage page) {
        assertThat(currentPath()).isEqualToIgnoringCase(page.path());
    }

    private String currentPath() {
        return pathAndQueryString(driver.getCurrentUrl());
    }

    protected String pathAndQueryString(String uri) {
        try {
            URI currentUri = new URI(uri);
            if (Objects.isNull(currentUri.getQuery())) {
                return currentUri.getPath();
            } else {
                return currentUri.getPath() + "?" + currentUri.getQuery();
            }
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Could not parse [ " + driver.getCurrentUrl() + " ] to valid URI");
        }
    }

    protected NettExpensesPage login(String email) {
        LoginPage loginPage = new LoginPage(this);
        loginPage.open();
        return loginPage.loginUser(email);
    }
}
