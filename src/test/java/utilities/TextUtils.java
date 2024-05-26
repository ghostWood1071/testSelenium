package utilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;


public class TextUtils {
    private ArrayList<HashMap<String, String>> data;
    public TextUtils(){
        this.data = new ArrayList<HashMap<String, String>>();
    }

    public ArrayList<HashMap<String, String>> getData() {
        return this.data;
    }

    private void addHeaders(String[] headerValues, ArrayList<String> headerInstance){
        for(String header: headerValues) {
            headerInstance.add(header);
        }
    }

    private void addCellValue(String[] rawData, ArrayList<String> headers){
        HashMap<String, String> cell = new HashMap<String, String>();
        for(int i=0; i<headers.size(); i++){
            cell.put(headers.get(i), rawData[i]);
        }
        this.data.add(cell);
    }
    public void readData(String filePath) {
        ArrayList<String> headers = new ArrayList<String>();
        boolean firstLine = true;
        try (Scanner scanner = new Scanner(new File(filePath))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] rawData = line.split(",", -1);
                if (firstLine){
                    this.addHeaders(rawData, headers);
                    firstLine = false;
                } else
                    addCellValue(rawData, headers);

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
