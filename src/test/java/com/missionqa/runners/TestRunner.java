package com.missionqa.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
        features = "src/test/resources/features",
        glue = {
                "com.missionqa.hooks",
                "com.missionqa.ui.steps",
                "com.missionqa.api.steps"
        },
        plugin = {
                "pretty",
                "html:/app/artifacts/cucumber.html",
                "json:/app/artifacts/cucumber.json"
        },
        monochrome = true
)
public class TestRunner extends AbstractTestNGCucumberTests {
}
