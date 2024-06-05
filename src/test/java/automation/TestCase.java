package automation;

import java.util.HashMap;

public class TestCase {
    public TestCase(String caseName, HashMap<String,String> caseData) {
        this.caseName = caseName;
        this.caseData = caseData;
    }
    public String caseName;
    public HashMap<String,String> caseData;
}


