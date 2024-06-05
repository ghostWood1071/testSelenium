package automation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import config.ActionKeywords;
import tech.grasshopper.pdf.pojo.cucumber.Step;
import utilities.ExcelUtils;

public class MultipleCaseBot extends AutomationBot {
    private HashMap<String, ArrayList<TestCase>> testCases;
    public MultipleCaseBot(){
        super();
        this.testCases = new HashMap<String, ArrayList<TestCase>>();
    }

    public void loadFromExcel(String filePath, String stepSheetName, String ... caseNames) throws Exception{
        ExcelUtils.setExcelFile(filePath, stepSheetName);
        this.testSteps = this.loadStep(stepSheetName);
        for(String caseName: caseNames) {
            ArrayList<TestCase> cases = this.loadTestCases(caseName);
            this.testCases.put(caseName, cases); 
        }
    }

    public boolean verifyMultipleCondPassword(int testCaseNum, TestStep testStep, TestCase testCase){
        List<WebElement> elems = ActionKeywords.getElements(testStep.locatorType, testStep.locatorValue);
        String[] values;
        ArrayList<String> elemVals = new ArrayList<String>();
        if(testStep.testData.startsWith("var")){
            String key = testStep.testData.replace("var", "");
            values = testCase.caseData.get(key).split(",",-1);
        } else {
            values = testStep.testData.split(",", -1);
        }
        for(WebElement elem: elems){
            WebElement icon = elem.findElement(By.tagName("svg"));
            WebElement iconVal = elem.findElement(By.tagName("span"));
            if (icon.getAttribute("data-encore-id") == null)
                elemVals.add(iconVal.getText().replace("\n", " ").replace(" Not met. ,", ""));
        }
        Set<String> set = new HashSet<>(Arrays.asList(values));
        boolean result = elemVals.stream().allMatch(set::contains);

        return result;
    }

    
    @Override
    public boolean verify(int testCaseNum, TestStep testStep, TestCase testCase) {
        if(testStep.testData.contains("#")){
            testStep.testData = testStep.testData.replace("#", "");
            boolean re = this.verifyMultipleCondPassword(testCaseNum, testStep, testCase);
            testStep.testData = testStep.testData+"#";
            return re;
        }
        return super.verify(testCaseNum, testStep, testCase);
    }

    public int doTestEmail(int stepIndex, int startStep, int endStep) throws Exception{
        ArrayList<TestCase> emailCases = this.testCases.get("Email");
        for (int index = 0; index < emailCases.size(); index++){
            for (int step = startStep; step<=endStep; step++){
                ActionResult actionR = this.executeAction(
                0, 
                browserType, 
                emailCases.get(index), 
                this.testSteps.get(step)
            );
            if(actionR.actionName.equals("verifyUrl") && actionR.actionResult.equals("true")){
                stepIndex = step + 2;
                return stepIndex;
            }
            if(actionR.actionName.equals("verifyElementText")){
                System.out.println("testcase: " + actionR.actionResult);
            }
            stepIndex = step;
            }
            
        }
        return stepIndex;
    }

    public int doTestPassword(int stepIndex, int startStep, int endStep) throws Exception{
        ArrayList<TestCase> pwdCases = this.testCases.get("Password");
        for (int index = 0; index < pwdCases.size(); index++){
            for (int step = startStep; step<=endStep; step++){
                ActionResult actionR = this.executeAction(
                    0, 
                    browserType, 
                    pwdCases.get(index), 
                    this.testSteps.get(step)
                );
                if(actionR.actionName.equals("verifyUrl") && actionR.actionResult.equals("true")){
                    stepIndex= step+2;
                    return stepIndex;
                }
                if(actionR.actionName.equals("verifyElementText")){
                    System.out.println("testcase: " + actionR.actionResult);
                }
                stepIndex = step;
            }
        }
        return stepIndex;
    }

    @Override
    public void run() throws Exception {
        // int testCaseNum = 1;
        int stepIndex = 0;
        while (stepIndex<this.testSteps.size()) {
            if (stepIndex >=2 && stepIndex <=5 ){
                stepIndex = this.doTestEmail(stepIndex, 2, 5);
            }

            else if (stepIndex >=6 && stepIndex<=9){
                stepIndex = this.doTestPassword(stepIndex, 6, 9);
            }

            else {
                System.out.println(stepIndex);
                ActionResult actionR = executeAction(0, 
                    browserType, 
                    null, 
                    testSteps.get(stepIndex)
                );  
                stepIndex+=1;
            }
        }   
            
    }
}
