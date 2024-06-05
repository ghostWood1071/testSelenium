package config;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
//import org.openqa.selenium.opera.OperaDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import io.github.bonigarcia.wdm.WebDriverManager;

public class ActionKeywords {
    public static WebDriver driver;
    private static final int timeoutWait = 20;
    private static final int timeoutWaitForPageLoaded = 30;
    private static Actions action;
    private static JavascriptExecutor js;
    private static WebDriverWait wait;

    //public static Properties OR = new Properties(System.getProperties());

    public static void waitForPageLoaded() {
        // wait for jQuery to loaded
        ExpectedCondition<Boolean> jQueryLoad = new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                try {
                    return ((Long) ((JavascriptExecutor) driver).executeScript("return jQuery.active") == 0);
                } catch (Exception e) {
                    return true;
                }
            }
        };

        // wait for Javascript to loaded
        ExpectedCondition<Boolean> jsLoad = new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver driver) {
                return ((JavascriptExecutor) driver).executeScript("return document.readyState").toString()
                        .equals("complete");
            }
        };

        try {
            wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutWaitForPageLoaded));
            wait.until(jQueryLoad);
            wait.until(jsLoad);
        } catch (Throwable error) {
            Assert.fail("Timeout waiting for Page Load request.");
        }
    }

    public static boolean verifyPageLoaded(String pageLoadedText) {
        waitForPageLoaded();
        Boolean res = false;

        List<WebElement> elementList = driver.findElements(By.xpath("//*contains(text(),'" + pageLoadedText + "')]"));
        if (elementList.size() > 0) {
            res = true;
            System.out.println("Page loaded(" + res + "): " + pageLoadedText);
        } else {
            res = false;
            System.out.println("Page loaded(" + res + "): " + pageLoadedText);
        }
        return res;
    }


    public static WebDriver openBrowser(String browserName) throws Exception {
        try {
            switch (browserName.trim().toLowerCase()) {
                case "chrome":
                    driver = initChromeDriver();
                    break;
                case "firefox":
                    driver = initFirefoxDriver();
                    break;
                case "edge":
                    driver = initEdgeDriver();
                    break;
                default:
                    throw new Exception("Browser: " + browserName
                            + " is invalid. Please choose either 'chrome', 'firefox', or 'edge'.");
            }
            return driver;
        } catch (Exception e) {
            // Log the error using a proper logging system
            // Log.error("Not able to open the Browser --- " + e.getMessage());
            throw new Exception("Not able to open the Browser --- " + e.getMessage());
        }
    }

    private static WebDriver initChromeDriver() {
        System.out.println("Launching Chrome browser...");
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(timeoutWaitForPageLoaded));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(timeoutWait));
        return driver;
    }

    private static WebDriver initEdgeDriver() {
        System.out.println("Launching Edge browser...");
        WebDriverManager.edgedriver().setup();
        driver = new EdgeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(timeoutWaitForPageLoaded));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(timeoutWait));
        return driver;
    }

    private static WebDriver initFirefoxDriver() {
        System.out.println("Launching Firefox browser...");
        WebDriverManager.firefoxdriver().setup();
        driver = new FirefoxDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(timeoutWaitForPageLoaded));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(timeoutWait));
        return driver;
    }

    public static void navigate(String url) {
        try {
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(timeoutWait));
            driver.get(url);
            driver.manage().window().maximize();
            driver.navigate().refresh();
        } catch (Exception e) {
            System.out.println("Error..." + e.getStackTrace());
        }
    }

    public static void quitDriver() throws Exception {
        if (driver.toString().contains("null")) {
            System.out.print("All Browser windows are closed ");
        } else {
            driver.manage().deleteAllCookies();
            driver.quit();
        }
    }

    public static void setText(WebElement element, String Value) {
        wait.until(ExpectedConditions.elementToBeClickable(element));
        element.clear();
        element.sendKeys(Value);
    }

    public static void clearText(String locatorType, String locatorValue) {
        WebElement element = GetElement(locatorType, locatorValue);
        waitForPageLoaded();
        wait.until(ExpectedConditions.visibilityOf(element));
        element.clear();
        while (!element.getAttribute("value").equals("")) {
          element.clear();  
          waitForPageLoaded();
          wait.until(ExpectedConditions.visibilityOf(element));
        } 
    }

    public static void setText(String locatorType, String locatorValue, String value) {
        WebElement element = GetElement(locatorType, locatorValue);
        waitForPageLoaded();
        wait.until(ExpectedConditions.visibilityOf(element));
        element.clear();
        System.out.println(value);
        element.sendKeys(value);
    }

    public static List<WebElement> getElements(String locatorType, String locatorValue) {
        List<WebElement> elements;
        
        if (locatorType.equalsIgnoreCase("className"))
            elements = driver.findElements(By.className(locatorValue));
        else if (locatorType.equalsIgnoreCase("cssSelector"))
            elements = driver.findElements(By.cssSelector(locatorValue));
        else if (locatorType.equalsIgnoreCase("id"))
            elements = driver.findElements(By.id(locatorValue));
        else if (locatorType.equalsIgnoreCase("partialLinkText"))
            elements = driver.findElements(By.partialLinkText(locatorValue));
        else if (locatorType.equalsIgnoreCase("name"))
            elements = driver.findElements(By.name(locatorValue));
        else if (locatorType.equalsIgnoreCase("xpath"))
            elements = driver.findElements(By.xpath(locatorValue));
        else if (locatorType.equalsIgnoreCase("tagName"))
            elements = driver.findElements(By.tagName(locatorValue));
        else
            elements = driver.findElements(By.xpath(locatorValue));

        return elements;
    }

    private static WebElement GetElement(String locatorType, String locatorValue) {
        WebElement element;

        if (locatorType.equalsIgnoreCase("className"))
            element = driver.findElement(By.className(locatorValue));
        else if (locatorType.equalsIgnoreCase("cssSelector"))
            element = driver.findElement(By.cssSelector(locatorValue));
        else if (locatorType.equalsIgnoreCase("id"))
            element = driver.findElement(By.id(locatorValue));
        else if (locatorType.equalsIgnoreCase("partialLinkText"))
            element = driver.findElement(By.partialLinkText(locatorValue));
        else if (locatorType.equalsIgnoreCase("name"))
            element = driver.findElement(By.name(locatorValue));
        else if (locatorType.equalsIgnoreCase("xpath"))
            element = driver.findElement(By.xpath(locatorValue));
        else if (locatorType.equalsIgnoreCase("tagName"))
            element = driver.findElement(By.tagName(locatorValue));
        else
            element = driver.findElement(By.xpath(locatorValue));

        return element;
    }

    public static void clickElement(WebElement element) {
        wait.until(ExpectedConditions.elementToBeClickable(element));
        element.click();
    }

    public static void clickElement(String locatorType, String locatorValue) {
        WebElement element;
        element = GetElement(locatorType, locatorValue);
        waitForPageLoaded();
        wait.until(ExpectedConditions.elementToBeClickable(element));
        element.click();
    }

//	public void clickElement(String element) {
//		waitForPageLoaded();
////		wait.until(ExpectedConditions.visibilityOf(element));
//		driver.findElement(By.xpath(OR.getProperty(element))).click();
//	}

    public static void clickElementWithJs(WebElement element) {
        js.executeScript("arguments[0].scrollIntoView(true)", element);
        wait.until(ExpectedConditions.elementToBeClickable(element));
        js.executeScript("arguments[0].click();", element);
    }


    public static boolean verifyUrl(String url) {
//		System.out.println(driver.getCurrentUrl());
//		System.out.println(url);

        return driver.getCurrentUrl().equals(url);
    }


    public static String getPageTitle() {
        waitForPageLoaded();
        return driver.getTitle();
    }

    public static boolean verifyPageTitle(String pageTitle) {
        return getPageTitle().equals(pageTitle);
    }

    public static void rightClickElement(WebElement element) {
        wait.until(ExpectedConditions.elementToBeClickable(element));
        action.contextClick().build().perform();
    }

    // Chon du lieu tu combobox
    public static void selectOptionByText(WebElement element, String text) {
        Select select = new Select(element);
        select.selectByVisibleText(text);
    }

    public static void selectOptionByText(String locatorType, String locatorValue, String text) {
        WebElement element = GetElement(locatorType, locatorValue);
        Select select = new Select(element);
        select.selectByVisibleText(text);
    }

    public static void selectOptionByValue(WebElement element, String value) {
        Select select = new Select(element);
        select.selectByValue(value);
    }

    public static void selectOptionByValue(String locatorType, String locatorValue, String value) {
        WebElement element = GetElement(locatorType, locatorValue);
        Select select = new Select(element);
        select.selectByValue(value);
    }

    public static void selectOptionByIndex(WebElement element, int index) {
        Select select = new Select(element);
        select.selectByIndex(index);
    }

    public static void selectOptionByIndex(String locatorType, String locatorValue, int index) {
        WebElement element = GetElement(locatorType, locatorValue);
        Select select = new Select(element);
        select.selectByIndex(index);
    }

    public static boolean verifyElementText(WebElement element, String textValue) {
        wait.until(ExpectedConditions.visibilityOf(element));
        return element.getText().equals(textValue);
    }


    public static boolean verifyElementText(String locatorType, String locatorValue, String text) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        WebElement element = GetElement(locatorType, locatorValue);
        wait.until(ExpectedConditions.visibilityOf(element));
        String value = element.getText(); 
        return value.equals(text);

    }

    public static boolean verifyLabel(WebElement element, String textValue) {
        wait.until(ExpectedConditions.visibilityOf(element));
        return element.getText().equals(textValue);
    }

    public static boolean verifyElementValue(String locatorType, String locatorValue, String value) {
        WebElement element = GetElement(locatorType, locatorValue);
        wait.until(ExpectedConditions.visibilityOf(element));
        return element.getAttribute("value").equals(value);
    }

    public static boolean verifyElementExist(By elementBy) {
        // Tạo list lưu tất cả các đối tượng WebElement
        List<WebElement> listElement = driver.findElements(elementBy);

        int total = listElement.size();
        if (total > 0) {
            return true;
        }
        return false;
    }

}
