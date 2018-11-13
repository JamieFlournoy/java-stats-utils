package com.pervasivecode.utils.stats.measure.testing.cucumber;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.truth.Truth.assertThat;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import com.pervasivecode.utils.stats.histogram.measure.example.ExampleApplication;
import com.pervasivecode.utils.stats.histogram.measure.example.PowerPlantCapacityHistogramExample;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class CodeExampleSteps {
  private String commandOutput = "";
  private ExampleApplication codeExample = null;

  @Given("^I am running the Power Plant Capacity Histogram Example$")
  public void iAmRunningThePowerPlantCapacityHistogramExample() {
    iAmRunningTheExample(new PowerPlantCapacityHistogramExample());
  }

  private void iAmRunningTheExample(ExampleApplication exampleClass) {
    this.codeExample = exampleClass;
    this.commandOutput = "";
  }

  @When("^I run the program$")
  public void iRunTheProgram() throws IOException {
    checkNotNull(this.codeExample, "did you forget an 'I am running the' example step?");
    StringWriter sw = new StringWriter();
    this.codeExample.runExample(new PrintWriter(sw, true));
    commandOutput = commandOutput.concat(sw.toString());
  }

  @Then("^I should see the output$")
  public void iShouldSeeTheOutput(String expected) {
    assertThat(this.commandOutput).isEqualTo(expected);
  }
}
