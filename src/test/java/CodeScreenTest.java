import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.List;

public class CodeScreenTest {
        @Test
        public void testCase() throws InterruptedException {
            WebDriverManager.chromedriver().setup();
            WebDriver driver = new ChromeDriver();
            SoftAssert softAssert = new SoftAssert();

            //open website
            driver.get("https://www.webstaurantstore.com");
            //search keyword
            searchKeyword(driver, "stainless work table");

            //iterates through descriptions on each page
            checkDescriptions(driver, softAssert);

            //adds last found item to cart
            addLastFoundItemToCart(driver);

            //view cart
            viewCart(driver);

            //empty cart
            emptyCart(driver);

            softAssert.assertAll();

            driver.quit();
        }


    public static void searchKeyword(WebDriver driver, String keyword){
        WebElement textBox = driver.findElement(By.name("searchval"));
        textBox.click();
        textBox.sendKeys(keyword);
        WebElement searchButton = driver.findElement(By.cssSelector("button[value='Search']"));
        searchButton.click();
    }
    public static boolean checkNextArrowExists(WebDriver driver){
        List<WebElement> nextPageButton = driver.findElements(By.xpath("//*[name()='use' and @*[contains(.,'right-open')]]"));
        return !nextPageButton.isEmpty();
    }

    public static void checkDescriptions(WebDriver driver, SoftAssert softAssert){
    //if right pagination arrow does not appear, iterate over descriptions on screen and continue to next step
    //if right pagination arrow appears on page, iterate over descriptions on screen and go to next page, repeat
        boolean isPresent;
        do{
            List<WebElement> tables = driver.findElements(By.cssSelector("span[data-testid='itemDescription']"));
            for (WebElement table : tables) {
                String tableText = table.getText();
                softAssert.assertTrue(tableText.contains("Table"), "Description does not contain Table: " + tableText);
            }
            isPresent = checkNextArrowExists(driver);
            if(isPresent){
                WebElement nextPage = driver.findElement(By.xpath("//*[name()='use' and @*[contains(.,'right-open')]]"));
                nextPage.click();
            }
        } while(isPresent);
    }

    public static void addLastFoundItemToCart(WebDriver driver) throws InterruptedException {
        WebElement lastItemAddToCartButton = driver.findElement(By.xpath("(//input[@name='addToCartButton'])[last()]"));
        lastItemAddToCartButton.click();

        //close notification popup
        WebElement closeNotification = driver.findElement(By.cssSelector("button[class*='close']"));
        closeNotification.click();
        //waits 1 second for popup to be gone
        Thread.sleep(1000);
    }

    public static void viewCart(WebDriver driver){
        WebElement cartButton = driver.findElement(By.cssSelector("a[data-testid='cart-button']"));
        cartButton.click();
    }

    public static void emptyCart(WebDriver driver){
        WebElement emptyCartButton = driver.findElement(By.cssSelector("button[class*='emptyCartButton']"));
        emptyCartButton.click();

        WebElement emptyCartConfirmButton = driver.findElement(By.xpath("//footer/button[contains(text(),'Empty')]"));
        emptyCartConfirmButton.click();
    }
}
