package automation;

import java.util.ArrayList;
import java.util.HashMap;

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

    
    @Override
    public boolean verify(int testCaseNum, TestStep testStep, TestCase testCase) {
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
            if(actionR.actionName.equals("verifyUrl") && actionR.actionResult == "true"){
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
                    this.testSteps.get(stepIndex)
                );
                if(actionR.actionName.equals("verifyUrl") && actionR.actionResult == "true"){
                    stepIndex+=2;
                    break;
                }
                if(actionR.actionName.equals("verifyElementText") && actionR.actionResult.equals("true")){
                    System.out.println("testcase: " + actionR.actionResult);
                }
                stepIndex = step;
            }
        }
        return stepIndex;
    }

    @Override
    public void run() throws Exception {
        int testCaseNum = 1;
        int stepIndex = 0;
        while (stepIndex<this.testSteps.size()) {
            if (stepIndex >=2 && stepIndex <=5 ){
                stepIndex = this.doTestEmail(stepIndex, 2, 5);
            }

            else if (stepIndex >=6 && stepIndex<=8){
                stepIndex = this.doTestPassword(stepIndex, 6, 8);
            }

            else {
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
