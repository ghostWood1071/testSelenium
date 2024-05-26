package spotifyTest;

import utilities.TextUtils;

public class SignUp {
    public static void main(String[] args) {
        TextUtils utils = new TextUtils();
        utils.readData("src/test/Testdata.csv");
        System.out.println(utils.getData().get(1).get("Description"));
    }
}
