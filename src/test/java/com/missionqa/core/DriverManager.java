package com.missionqa.core;

import com.missionqa.config.TestConfig;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.util.HashMap;
import java.util.Map;

/**
 * DriverManager
 *
 * Goal: Prefer Selenium Manager (built into Selenium 4.6+) to avoid WebDriverManager's
 * Apache HttpClient 5 dependency path (which is currently causing NoSuchMethodError).
 *
 * Behavior:
 * - By default, uses Selenium Manager (no WebDriverManager calls).
 * - If you ever NEED WebDriverManager again, set:
 *      -DuseWdm=true
 */
public final class DriverManager {

    private static final boolean USE_WDM =
            Boolean.parseBoolean(System.getProperty("useWdm", "false"));

    private DriverManager() {}

    public static WebDriver createDriver() {
        String browser = TestConfig.getProperty("browser").toLowerCase();

        switch (browser) {
            case "chrome":
                setupIfNeeded("chrome");
                return new ChromeDriver(buildChromeOptions(false));

            case "chromeheadless":
                setupIfNeeded("chrome");
                return new ChromeDriver(buildChromeOptions(true));

            case "edge":
                setupIfNeeded("edge");
                return new EdgeDriver();

            case "firefox":
                setupIfNeeded("firefox");
                return new FirefoxDriver();

            default:
                throw new IllegalArgumentException("Unsupported browser in config.properties: " + browser);
        }
    }

    /**
     * Selenium Manager automatically resolves the driver binary for Selenium 4.6+.
     * If USE_WDM=true, fall back to WebDriverManager (useful for edge cases).
     */
    private static void setupIfNeeded(String browser) {
        if (!USE_WDM) {
            // Selenium Manager path: no setup required.
            return;
        }

        // WebDriverManager fallback path (avoid HttpClient if possible).
        System.setProperty("wdm.avoidHttpClient", "true");

        switch (browser) {
            case "chrome":
                WebDriverManager.chromedriver().setup();
                break;
            case "edge":
                WebDriverManager.edgedriver().setup();
                break;
            case "firefox":
                WebDriverManager.firefoxdriver().setup();
                break;
            default:
                // no-op
        }
    }

    private static ChromeOptions buildChromeOptions(boolean headless) {
        ChromeOptions options = new ChromeOptions();

        if (headless) {
            options.addArguments("--headless=new");
        }

        // Fresh profile each run (prevents state/popup leakage across runs)
        String tmpProfile = System.getProperty("java.io.tmpdir")
                + "/missionqa-chrome-" + System.currentTimeMillis();
        options.addArguments("--user-data-dir=" + tmpProfile);

        // Reduce Chrome UI noise
        options.addArguments("--no-first-run");
        options.addArguments("--no-default-browser-check");
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-dev-shm-usage");

        // KEY: disable password leak detection popup
        options.addArguments("--disable-features=PasswordLeakDetection");

        // Disable password manager + credential prompts + leak detection
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        prefs.put("profile.password_manager_leak_detection", false);
        options.setExperimentalOption("prefs", prefs);

        return options;
    }

    public static void quitDriver(WebDriver driver) {
        if (driver == null) return;
        try {
            driver.quit();
        } catch (Exception ignored) {
        }
    }
}
