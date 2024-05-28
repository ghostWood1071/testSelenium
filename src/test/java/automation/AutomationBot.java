package automation;


import org.apache.poi.ss.usermodel.Sheet;

import config.ActionKeywords;
import utilities.TextUtils;
import utilities.ExcelUtils;
import java.util.ArrayList;
import java.util.HashMap;



public class AutomationBot {

    private ArrayList<TestCase> testCases;
    private ArrayList<TestStep> testSteps;
    private ArrayList<String> results;
    private String browserType;

    
    public void setBrowserType(String browserType) {
        this.browserType = browserType;
    }

    public AutomationBot() {
        this.testCases = new ArrayList<TestCase>();
        this.testSteps = new  ArrayList<TestStep>();
        this.results = new ArrayList<String>();
    }

    public TestStep getStep(int index){
        return this.testSteps.get(index);
    }

    public ArrayList<String> getHeader(String sheetName){
        ArrayList<String> header = new ArrayList<String>();
        int col = 1;
        String data;
        while (true) {
            data = ExcelUtils.getCellData(0, col);
            if (data.equals("") || data == null)
                break;
            header.add(data);
            col+=1;
        }
        return header;
    }

    public ArrayList<TestCase> loadTestCases(String sheetName) {
        ArrayList<TestCase> testCases = new ArrayList<TestCase>();
        
        Sheet sheet = ExcelUtils.getSheet(sheetName);
        int rowCount = sheet.getLastRowNum();
        ArrayList<String> headers = getHeader(sheetName);
        for(int row = 1; row<=rowCount; row++){
            HashMap<String,String> testData = new HashMap<String,String>();
            for (int i = 0; i < headers.size(); i++){
                testData.put(headers.get(i), ExcelUtils.getCellData(sheetName, row, i+1));
            }
            testCases.add(new TestCase(ExcelUtils.getCellData(sheetName, row, 1) + "", testData)
            );
        }
        // this.testCases = testCases;
        return testCases;
    }

    public ArrayList<TestStep> loadStep(String sheetName) {
        ArrayList<TestStep> testSteps = new ArrayList<TestStep>();
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
        // this.testSteps = testSteps;
        return testSteps;
    }

    public ArrayList<TestCase>loadTestCases(ArrayList<HashMap<String, String>> data, String firstCol){
        ArrayList<TestCase> testCases = new ArrayList<TestCase>();
        for(HashMap<String,String> row: data){
            testCases.add(new TestCase(row.get(firstCol), row));
        }
        // this.testCases = testCases;
        return testCases;
    }

    public ArrayList<TestStep> loadStep(ArrayList<HashMap<String, String>> data) {
        ArrayList<TestStep> testSteps = new ArrayList<TestStep>();
        for (HashMap<String, String> row: data){
            testSteps.add(new TestStep(
                row.get("ScriptID"), 
                row.get("StepID"), 
                row.get("Description"), 
                row.get("Keywords"), 
                row.get("LocatorType"), 
                row.get("LocatorValue"), 
                row.get("TestData"))
            );
        }
        // this.testSteps = testSteps;
        return testSteps;
    }
    
    public void loadFromExcel(String filePath, String stepSheet, String caseSheet) throws Exception {
        ExcelUtils.setExcelFile(filePath, stepSheet);
        this.testCases = this.loadTestCases(caseSheet);
        this.testSteps = this.loadStep(stepSheet);
    }

    public void loadFromText(String stepFile, String caseFile){
        TextUtils textUtils = new TextUtils();
        textUtils.readData(stepFile);
        this.testSteps = this.loadStep(textUtils.getData());
        textUtils.readData(caseFile);
        this.testCases = this.loadTestCases(textUtils.getData(), textUtils.headers.get(0));
        
    }

    public void executeAction(int testCaseNum, String browserType, TestCase testCase, TestStep testStep) throws Exception {
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
                    if(testStep.testData.startsWith("var")) {
                        String dataVarKey = testStep.testData.replace("var", "");
                        isPass = ActionKeywords.verifyElementText(testStep.locatorType, testStep.locatorValue, testCase.caseData.get(dataVarKey));
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
    
    public void run() throws Exception {
        int caseNum = 1;
        for(TestCase testCase: this.testCases){
            for(TestStep step: this.testSteps){
                executeAction(caseNum, browserType, testCase, step);
            }
            caseNum += 1;
        }
    }

}
