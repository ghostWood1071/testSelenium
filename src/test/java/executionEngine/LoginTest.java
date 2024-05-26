package executionEngine;

import config.ActionKeywords;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.qameta.allure.Step;
import io.qameta.allure.Description;
import org.apache.poi.ss.usermodel.Sheet;
import org.testng.annotations.Test;
import utilities.ExcelUtils;

import java.util.ArrayList;

@Epic("Epic: Login Tests")
@Feature("Feature: User Login")

public class LoginTest {
    public static ActionKeywords actionKeywords;
    public static String sActionKeyword;
    public static String locatorType;
    public static String locatorValue;
    public static String testData;

    ArrayList<String> arrUsername = new ArrayList<String>();
    ArrayList<String> arrPassword = new ArrayList<String>();
    ArrayList<String> arrResult = new ArrayList<String>();

    String stestCaseID;

    @Test(description = "Execute Login Test Case")
    @Description("Test Description: Verify user can login with multiple sets of credentials")
    public void excute_TestCase() throws Exception {

        String sPath = "src/test/Testdata.xlsx";

        ExcelUtils.setExcelFile(sPath, "SignInPage");

        Sheet sheet = ExcelUtils.getSheet("SignInPage");
        int rowCount = sheet.getLastRowNum();

        String tmp;

        ExcelUtils.setExcelFile(sPath, "DataOfSignIn");
        sheet = ExcelUtils.getSheet("DataOfSignIn");
        int dataRowCount = sheet.getLastRowNum();

        for(int row = 1; row<=dataRowCount; row++){
            tmp = ExcelUtils.getCellData("DataOfSignIn", row, 1) + "";
            arrUsername.add(tmp);

            tmp = ExcelUtils.getCellData("DataOfSignIn", row, 2) + "";
            arrPassword.add(tmp);

            tmp = ExcelUtils.getCellData("DataOfSignIn", row, 3) + "";
            arrResult.add(tmp);

        }
        sActionKeyword = ExcelUtils.getCellData("SignInPage", 1, 4);
        testData = ExcelUtils.getCellData("SignInPage", 1, 7);
        ActionKeywords.openBrowser(testData);

        for (int i = 0; i < arrUsername.size(); i++) {
            for (int iRow = 1; iRow <= rowCount; iRow++) {
                sActionKeyword = ExcelUtils.getCellData("SignInPage", iRow, 4);
                locatorType = ExcelUtils.getCellData("SignInPage", iRow, 5);
                locatorValue = ExcelUtils.getCellData("SignInPage", iRow, 6);
                testData = ExcelUtils.getCellData("SignInPage", iRow, 7);
                execute_Actions(i, testData, arrUsername.get(i), arrPassword.get(i), arrResult.get(i));
            }
        }
        // Close browser at the end
        sActionKeyword = ExcelUtils.getCellData("SignInPage", rowCount, 4);
        ActionKeywords.quitDriver();
    }

//    @Step("Execute action: {sActionKeyword}")
    public static void execute_Actions(int testCaseNum, String testData, String sUsername, String sPassword, String sResult) throws Exception {
        try {
            switch (sActionKeyword) {
                case "openBrowser":
                    ActionKeywords.openBrowser(testData);
                    break;
                case "navigate":
                    ActionKeywords.navigate(testData);
                    break;
                case "setText":
                    if (testData.equalsIgnoreCase("varUsername"))
                        ActionKeywords.setText(locatorType, locatorValue, sUsername);
                    else
                        ActionKeywords.setText(locatorType, locatorValue, sPassword);
                    break;
                case "clickElement":
                    ActionKeywords.clickElement(locatorType, locatorValue);
                    break;
                case "verifyElementText":
                    boolean isPass = ActionKeywords.verifyElementText(locatorType, locatorValue, sResult);
                    System.out.println("Test case " + (testCaseNum + 1) + " pass: " + isPass);
                    break;
                case "closeBrowser":
                    ActionKeywords.quitDriver();
                    break;
                default:
                    System.out.println("[>>ERROR<<]: |Keyword Not Found " + sActionKeyword);
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }
}
