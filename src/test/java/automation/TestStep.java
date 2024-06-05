package automation;

public class TestStep {
    public TestStep(String scriptId, String stepId, String description, String keywords, String locatorType, String locatorValue, String testData) {
        this.scriptId = scriptId;
        this.stepId = stepId;
        this.description = description;
        this.keywords = keywords;
        this.locatorType = locatorType;
        this.locatorValue = locatorValue;
        this.testData = testData;
    }

    public TestStep clone() {
        return new TestStep(scriptId, stepId, description, keywords, locatorType, locatorValue, testData);
    }

    public String scriptId;
    public String stepId;
    public String description;
    public String keywords;
    public String locatorType;
    public String locatorValue;
    public String testData;

}