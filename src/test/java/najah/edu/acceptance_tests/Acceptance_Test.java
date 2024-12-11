package najah.edu.acceptance_tests;

import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import io.cucumber.junit.CucumberOptions.SnippetType;

@RunWith(Cucumber.class)
@CucumberOptions(features = "My_features",
plugin = {"html: target/cucmber/wikipedia.html"},
monochrome = true,
snippets = SnippetType.CAMELCASE,
glue = {"najah.edu.acceptance_tests"} )

public class Acceptance_Test {

}
