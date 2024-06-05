package automation;


import java.util.ArrayList;
import java.util.HashMap;

import org.apache.poi.ss.usermodel.Sheet;

import config.ActionKeywords;
import utilities.ExcelUtils;
import utilities.TextUtils;



public class AutomationBot {

    private ArrayList<TestCase> testCases;
    protected ArrayList<TestStep> testSteps;
    private ArrayList<String> results;
    protected String browserType;

    
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
            data = ExcelUtils.getCellData(sheetName, 0, col);
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

    public void openBrowser(String browserType) throws Exception{
        ActionKeywords.openBrowser(browserType);
    }

    public void navigate(String url){
        ActionKeywords.navigate(url);
    }

    public void setText(TestCase testCase, TestStep testStep){
        ActionKeywords.clearText(testStep.locatorType, testStep.locatorValue);
        if (testStep.testData.startsWith("var")){
            String key = testStep.testData.replace("var", "");
            ActionKeywords.setText(testStep.locatorType, testStep.locatorValue, testCase.caseData.get(key));
            return;
        }
        ActionKeywords.setText(testStep.locatorType, testStep.locatorValue, testStep.testData);
    }

    public void click(TestStep testStep){
        ActionKeywords.clickElement(testStep.locatorType, testStep.locatorValue);
    }

    public boolean verify(int testCaseNum, TestStep testStep, TestCase testCase){
        boolean isPass = false;
        if(testStep.testData.startsWith("var")) {
            String dataVarKey = testStep.testData.replace("var", "");
            isPass = ActionKeywords.verifyElementText(testStep.locatorType, testStep.locatorValue, testCase.caseData.get(dataVarKey));
        }
        else
            isPass = ActionKeywords.verifyElementText(testStep.locatorType, testStep.locatorValue, testStep.testData);
        return isPass;
    }

    public void quit() throws Exception {
        ActionKeywords.quitDriver();
    }

    public boolean verifyUrl(String url){
        return ActionKeywords.verifyUrl(url);
    }

    public void selectOptionByValue(TestCase testCase, TestStep testStep){
        if (testStep.testData.startsWith("var")){
            String key = testStep.testData.replace("var", "");
            ActionKeywords.selectOptionByValue(testStep.locatorType, testStep.locatorValue, testCase.caseData.get(key));
            return;
        }
        ActionKeywords.selectOptionByValue(testStep.locatorType, testStep.locatorValue, testStep.testData);
    }

    public ActionResult executeAction(int testCaseNum, String browserType, TestCase testCase, TestStep testStep) throws Exception {
        ActionResult result = new ActionResult();
        result.actionName = testStep.keywords;
        try {
            switch (testStep.keywords) {
                case "openBrowser":
                    this.openBrowser(browserType);
                    break;
                case "navigate":
                    this.navigate(testStep.testData);
                    break;
                case "setText":
                    this.setText(testCase, testStep);
                    break;
                case "clickElement":
                    this.click(testStep);
                    break;
                case "verifyElementText":
                    boolean isPass = this.verify(testCaseNum, testStep, testCase);
                    results.add("Test case " + (testCaseNum) + " pass: " + isPass);
                    result.actionResult = String.valueOf(isPass);
                    break;
                case "verifyUrl":
                    result.actionResult = String.valueOf(this.verifyUrl(testStep.testData));
                    break;
                case "closeBrowser":
                    this.quit();
                    break;
                case "selectOptionByValue":
                    this.selectOptionByValue(testCase, testStep);
                    break;
                default:
                    System.out.println("[>>ERROR<<]: |Keyword Not Found " + testStep.keywords);
            }
            
        } catch (Exception e) {
            e.getMessage();
            
        }
        return result;
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
