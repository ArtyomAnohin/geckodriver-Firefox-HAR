import com.codeborne.selenide.WebDriverRunner;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.WebDriverRunner.getWebDriver;

/**
 * Created by artyom on 11/22/16.
 */
public class SimpleTest {
    @Test
    public void test01() {
        open("http://google.com/ncr");
        getTimingAPI();
        getHar();
        $(By.name("q")).val("selenide").pressEnter();
        $$("#ires .g").shouldHave(size(10));
        $("#ires .g").shouldBe(visible).shouldHave(
                text("Selenide: concise UI tests in Java"),
                text("selenide.org"));
        getWebDriver().quit();
    }

    private void getTimingAPI() {
        String pageLoadTime = ((JavascriptExecutor) getWebDriver()).executeScript("return window.performance.timing.loadEventEnd - window.performance.timing.navigationStart").toString();
        String pageLoadTiming = ((JavascriptExecutor) getWebDriver()).executeScript("return window.performance.timing").toString();
        System.out.println(pageLoadTime);
        System.out.println(pageLoadTiming);
    }

    private void getHar() {
        String script = ((JavascriptExecutor) getWebDriver()).executeScript("return HAR.triggerExport({token: \"test\", getData: true})").toString();
        //System.out.println(script);
        List<String> lines = Collections.singletonList(script);
        try {
            //noinspection Since15
            Files.write(Paths.get("file.har"), lines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @BeforeTest
    private void setUp() {
        //set gecko binary
        System.setProperty("webdriver.gecko.driver", "geckodriver.exe");
        //set custom FF binary
        System.setProperty("webdriver.firefox.bin", "C:\\Program Files (x86)\\Firefox Developer Edition\\firefox.exe");
        //set caps
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("marionette", true);
        //install plugin
        FirefoxProfile firefoxProfile = new FirefoxProfile();
        File file = new File("harexporttrigger-0.5.0-beta.10.xpi");
        firefoxProfile.setPreference("xpinstall.signatures.required", false);
        firefoxProfile.addExtension(file);
        //enable HAR
        firefoxProfile.setPreference("extensions.netmonitor.har.enableAutomation", true);
        firefoxProfile.setPreference("extensions.netmonitor.har.contentAPIToken", "test");
        firefoxProfile.setPreference("extensions.netmonitor.har.autoConnect", true);
        firefoxProfile.setPreference("devtools.netmonitor.har.enableAutoExportToFile", true);
        //set FF profile
        caps.setCapability(FirefoxDriver.PROFILE, firefoxProfile);
        //create driver
        WebDriver driver = new FirefoxDriver(caps);
        WebDriverRunner.setWebDriver(driver);
    }
}
