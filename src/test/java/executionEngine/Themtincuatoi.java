package executionEngine;

import java.util.ArrayList;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Step;
import org.apache.poi.ss.usermodel.Sheet;
import org.testng.annotations.Test;
import utilities.ExcelUtils;
import config.ActionKeywords;

class TestCase {
    public TestCase(String caseName, String caseData) {
        this.caseName = caseName;
        this.caseData = caseData;
    }
    public String caseName;
    public String caseData;
}

class TestStep {
    public TestStep(String scriptId, String stepId, String description, String keywords, String locatorType, String locatorValue, String testData) {
        this.scriptId = scriptId;
        this.stepId = stepId;
        this.description = description;
        this.keywords = keywords;
        this.locatorType = locatorType;
        this.locatorValue = locatorValue;
        this.testData = testData;
    }

    public String scriptId;
    public String stepId;
    public String description;
    public String keywords;
    public String locatorType;
    public String locatorValue;
    public String testData;

}

@Epic("Epic: Add my news")
@Feature("Feature: Add event")
public class Themtincuatoi {
    public static ActionKeywords actionKeywords;
    public static String sActionKeyword;
    public static String locatorType;
    public static String locatorValue;
    public static String browserType;

    ArrayList<TestCase> testCases = new ArrayList<TestCase>();
    ArrayList<TestStep> testSteps = new ArrayList<TestStep>();

    ArrayList<String> results = new ArrayList<String>();

    String stestCaseID;

    public void loadTestCases(String sheetName) {
       Sheet sheet = ExcelUtils.getSheet(sheetName);
       int rowCount = sheet.getLastRowNum();
       for(int row = 1; row<=rowCount; row++){
            testCases.add(new TestCase(
                    ExcelUtils.getCellData(sheetName, row, 1) + "",
                    ExcelUtils.getCellData(sheetName, row, 2) + ""
                    )
            );
       }
    }

    public void loadStep(String sheetName) {
        Sheet sheet = ExcelUtils.getSheet(sheetName);
        int rowCount = sheet.getLastRowNum();
        String scriptId = ExcelUtils.getCellData(sheetName, 1, 1) + "";
        for(int row = 1; row<=rowCount; row++){
            testSteps.add(new TestStep(
                    scriptId,
                    ExcelUtils.getCellData(sheetName, row, 2) + "",
                    ExcelUtils.getCellData(sheetName, row, 3) + "",
                    ExcelUtils.getCellData(sheetName, row, 4) + "",
                    ExcelUtils.getCellData(sheetName, row, 5) + "",
                    ExcelUtils.getCellData(sheetName, row, 6) + "",
                    ExcelUtils.getCellData(sheetName, row, 7) + ""
                    )
            );
        }
    }

    @Test(description = "Execute Add Event Testcase")
    @Description("Test Description: Verify user can add event with multiple sets of data")
    public void excute_TestCase() throws Exception {
        // load test cases and test steps
        String stepSheetName = "Add_eventPage";
        String caseSheetName = "DataOfAdd_event";
        String sPath = "D:\\DO AN\\DATN\\DATN_Selenium\\Testdata.xlsx";
        ExcelUtils.setExcelFile(sPath, stepSheetName);
        loadTestCases(caseSheetName);
        loadStep(stepSheetName);

        // Open browser at the beginning
        TestStep firstStep = testSteps.get(0);
        sActionKeyword = firstStep.keywords;
        browserType = firstStep.testData;

        // let's test
        int test = 1;
        for (TestCase testCase: testCases) {
//            System.out.println("do " + test + "....");
            for (TestStep testStep: testSteps) {
//                System.out.println("execute " + testStep.keywords + "....");
                execute_Actions(test, browserType, testCase, testStep);
            }
            test+=1;
        }
        // Close browser at the end
        System.out.println("--------------------------------------------------------------");
        for(String result: results){
            System.out.println(result);
        }
        ActionKeywords.quitDriver();
    }

//    @Step("Execute action: {step.keywords}")
    public void execute_Actions(int testCaseNum, String browserType, TestCase testCase, TestStep testStep) throws Exception {
        try {
            switch (testStep.keywords) {
                case "openBrowser":
                    ActionKeywords.openBrowser(browserType);
                    break;
                case "navigate":
                    ActionKeywords.navigate(testStep.testData);
                    break;
                case "setText":
                        if (testStep.testData.equals("varCase")){
                            ActionKeywords.setText(testStep.locatorType, testStep.locatorValue, testCase.caseName);
                        } else
                            ActionKeywords.setText(testStep.locatorType, testStep.locatorValue, testStep.testData);
                    break;
                case "clickElement":
                    ActionKeywords.clickElement(testStep.locatorType, testStep.locatorValue);
                    break;
                case "verifyElementText":
                    boolean isPass;
                    if(testStep.testData.equals("varResult")) {
                        isPass = ActionKeywords.verifyElementText(testStep.locatorType, testStep.locatorValue, testCase.caseData);
                        results.add("Test case " + (testCaseNum) + " pass: " + isPass);
                    }
                    else
                        isPass = ActionKeywords.verifyElementText(testStep.locatorType, testStep.locatorValue, testStep.testData);
                    break;
                case "closeBrowser":
                    ActionKeywords.quitDriver();
                    break;
                default:
                    System.out.println("[>>ERROR<<]: |Keyword Not Found " + testStep.keywords);
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }
}
