import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author lferracini
 * @project = check-result-test
 * @since <pre>17/01/2022</pre>
 */
public class ResultTest {

    WebDriver driver;
    private String loginUser = "17312124";
    private String passdw = "7135";

    @BeforeAll
    static void setupClass() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void setupTest() {
        driver = new ChromeDriver();
    }

    @AfterEach
    void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void test() {
        // Your test logic here
        driver.get("https://matrixnet.hospitalsaocamilosp.org.br:8081/matrixnet/wfrmLogin.aspx");
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(2000));

        var userLogin = driver.findElement(By.name("userLogin"));

        userLogin.sendKeys(loginUser);
        var userPassword = driver.findElement(By.name("userPassword"));

        userPassword.sendKeys(passdw);
        var btnLogin = driver.findElement(By.id("btnEntrar"));
        btnLogin.click();

        //wait for menu-principal
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(By.id("menu-principal")));

        var elements = driver.findElements(By.className("nav-item"));

        elements.stream().map(e -> {
                    WebElement element = null;
                    if (e.getText().equals("Resultados")) {
                        element = e;
                    }
                    return element;
                }).collect(Collectors.toList())
                .stream()
                .findFirst()
                .orElseThrow()
                .click();

        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(2000));

        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.elementToBeClickable(By.id("collapsepedido"))).click();

        var exames = driver.findElements(By.tagName("td"));

        try {
            var buttons = driver.findElements(By.tagName("button"));
            buttons.forEach(button -> {
                if (button.getText().contains("Laudo Completo")) {
                    System.out.println("Laudo encontrdo: " + exames.get(2).getAttribute("id"));
                    assertEquals("Laudo Completo", button.getText());
                }
            });
        } catch (Exception e) {
            System.out.println("Laudo não encontrado: " + exames.get(2));
            Assertions.fail("Button não encontrado");
        }
    }
}
