package spotifyTest;


import org.apache.poi.ss.usermodel.Sheet;
import utilities.ExcelUtils;

import java.util.ArrayList;

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

public class AutomationBot {
    public ArrayList<TestCase> loadTestCases(String sheetName) {
        ArrayList<TestCase> testCases = new ArrayList<TestCase>();
        Sheet sheet = ExcelUtils.getSheet(sheetName);
        int rowCount = sheet.getLastRowNum();
        for(int row = 1; row<=rowCount; row++){
            testCases.add(new TestCase(
                            ExcelUtils.getCellData(sheetName, row, 1) + "",
                            ExcelUtils.getCellData(sheetName, row, 2) + ""
                    )
            );
        }
        return testCases;
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
}
