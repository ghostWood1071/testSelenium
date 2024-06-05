package spotifyTest;

import automation.AutomationBot;
import automation.MultipleCaseBot;
import automation.TestStep;


public class SignUp {
    public static void main(String[] args) throws Exception {
        String stepSheetName = "SignUpPage";
        String sPath = "/home/ghost/Desktop/TestStep_Spotify.xlsx";
        MultipleCaseBot bot = new MultipleCaseBot();
        bot.loadFromExcel(sPath, stepSheetName,"Email", "Password");
        TestStep firstStep = bot.getStep(0);
        bot.setBrowserType(firstStep.testData);
        bot.run();
    }
}
