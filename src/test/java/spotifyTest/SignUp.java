package spotifyTest;

import automation.AutomationBot;
import automation.TestStep;


public class SignUp {
    public static void main(String[] args) throws Exception {
        String stepSheetName = "Add_eventPage";
        String caseSheetName = "DataOfAdd_event";
        String sPath = "src/test/Testdata.xlsx";
        AutomationBot bot = new AutomationBot();
        // bot.loadFromText("src/test/LoginStep.csv", "src/test/LoginCase.csv");
        bot.loadFromExcel(sPath, stepSheetName, caseSheetName);
        TestStep firstStep = bot.getStep(0);
        bot.setBrowserType(firstStep.testData);
    }
}
