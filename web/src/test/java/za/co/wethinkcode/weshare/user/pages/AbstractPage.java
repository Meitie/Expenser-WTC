package za.co.wethinkcode.weshare.user.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import za.co.wethinkcode.weshare.user.UserTestRunner;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

public abstract class AbstractPage {
    protected final UserTestRunner testRunner;
    protected final WebDriver driver;

    public AbstractPage(UserTestRunner testRunner) {
        this.testRunner = testRunner;
        this.driver = testRunner.driver();
    }

    protected Function<String, Function<Integer, By>> rowLocator() {
        return (id) -> (row) -> By.id(id + "_" + row);
    }

    public void open() {
        testRunner.navigateTo(this);
    }

    public abstract String path();

    public void fillText(By element, String keys) {
        var field = testRunner.driver().findElement(element);
        field.clear();
        field.sendKeys(keys);
    }

    public void click(By element) {
        testRunner.driver().findElement(element).click();
    }

    public void submit() {
        WebElement button = testRunner.driver().findElement(By.xpath("//input[@type='submit']"));
        button.submit();
    }

    public String textOf(By locator) {
        return driver.findElement(locator).getText();
    }

    public boolean isDisplayed(By locator) {
        return driver.findElement(locator).isDisplayed();
    }

    protected String hrefOf(By locator) {
        return driver.findElement(locator).getAttribute("href");
    }

    protected double moneyOf(By locator) {
        return Double.parseDouble(textOf(locator));

    }

    protected LocalDate localDateOf(By locator) {
        return LocalDate.parse(textOf(locator), DateTimeFormatter.ISO_DATE);
    }

    protected void fillDate(By field, LocalDate date) {
        fillText(field, date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
    }

    protected void fillMoney(By field, Double value) {
        DecimalFormat money = new DecimalFormat("#.##");
        fillText(field, money.format(value));
    }

    protected String valueOfField(By locator) {
        return driver.findElement(locator).getAttribute("value");
    }
}
