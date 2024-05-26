package utilities;

import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ExcelUtils {
    private static FileInputStream fis;
    //    private static FileOutputStream fileOut;
    private static Workbook wb;
    private static Sheet ws;
    private static Cell cell;
    //    private static Row row;
    private static CellStyle cellStyle;

    private static String excelFilePath;
    private static Map<String, Integer> columns = new HashMap<String, Integer>();

    public static void setExcelFile(String ExcelPath, String SheetName) throws Exception {
        try {
            File f = new File(ExcelPath);

            if (!f.exists()) {
                f.createNewFile();
                System.out.println("File doesn't exist, so created!");
            }

            fis = new FileInputStream(ExcelPath);
            wb = WorkbookFactory.create(fis);
            ws = wb.getSheet(SheetName);
            if (ws == null) {
                ws = wb.createSheet(SheetName);
            }
            excelFilePath = ExcelPath;
            Row row = ws.getRow(0);

            if (row != null) {
                int lastCellNum = row.getLastCellNum();

                if (lastCellNum > 0) {
                    for (int i = 0; i < lastCellNum; i++) {
                        Cell cell = row.getCell(i);

                        if (cell != null) {
                            columns.put(cell.getStringCellValue(), cell.getColumnIndex());
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static Sheet getSheet(String SheetName) {
        Sheet ws=wb.getSheet(SheetName);
        if(!isSheet(SheetName)) {
            ws=wb.createSheet(SheetName);
        }
        return ws;
    }

    public static void open() throws IOException {
        File file=new File(excelFilePath);
        if(file.canRead()) {
            FileInputStream fileInput=new FileInputStream(file);
            wb = WorkbookFactory.create(fileInput);
            fileInput.close();
        }
    }

    public static void save()throws IOException{
        FileOutputStream streamOut=new FileOutputStream(excelFilePath);
        wb.write(streamOut);
        streamOut.flush();
        streamOut.close();
    }

    public static void saveAs(String path)throws IOException{
        FileOutputStream streamOut=new FileOutputStream(path);
        wb.write(streamOut);
        streamOut.flush();
        streamOut.close();
    }

    public static boolean isSheet(String SheetName) {
        return wb.getSheetIndex(SheetName)>=0;
    }

    public static void addSheet(String SheetName) {
        if(!isSheet(SheetName)) {
            wb.createSheet(SheetName);
        }
    }

    public static void removeSheet(int SheetIndex) {
        wb.removeSheetAt(SheetIndex);
    }

    public static void removeSheet(String SheetName) {
        int index=wb.getSheetIndex(SheetName);
        removeSheet(index);
    }

    public static void addColumn(Sheet sheet, String ColName) {
        cellStyle=wb.createCellStyle();
        cellStyle.setFillForegroundColor(HSSFColor.HSSFColorPredefined.RED.getIndex());
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Row row=sheet.getRow(0);
        if(row==null)
            row=sheet.createRow(0);

        Cell cell;
        if(row.getLastCellNum()==-1)
            cell=row.createCell(0);
        else
            cell=row.createCell(row.getLastCellNum());
        cell.setCellValue(ColName);
    }

    public static void addColumn(String SheetName,String ColName) {
        Sheet ws=getSheet(SheetName);
        addColumn(ws, ColName);
    }

    public static void removeColumn(String SheetName, int colNum) {
        Sheet sheet=getSheet(SheetName);

        int rowCount=sheet.getLastRowNum()+1;

        Row row;
        for(int i=0;i<rowCount;i++) {
            row=sheet.getRow(i);
            if(row!=null) {
                Cell cell =row.getCell(colNum);
                if(cell!=null) {
                    row.removeCell(cell);
                }
            }
        }
    }

    public static int convertColNameToColNum(Sheet sheet, String colName) {
        Row row=sheet.getRow(0);
        int cellRowNumber=row.getLastCellNum();
        int colNum=-1;

        for(int i=0;i<cellRowNumber;i++) {
            if(row.getCell(i).getStringCellValue().trim().equals(colName)) {
                colNum=i;
            }
        }
        return colNum;
    }

    public static Row getRow(Sheet sheet, int rowIndex) {
        Row row=sheet.getRow(rowIndex);
        if(row==null) {
            row=sheet.createRow(rowIndex);
        }
        return row;
    }

    public static int getRowCount(String SheetName) {
        ws = wb.getSheet(SheetName);
        int number = ws.getLastRowNum() + 1;
        return number;
    }

    public static String getCellData(int rownum, int colnum) {
        try {
            cell = ws.getRow(rownum).getCell(colnum);
            String cellData = null;

            switch (cell.getCellType()) {
                case STRING:
                    cellData = cell.getStringCellValue();
                    break;
                case NUMERIC:
                    cellData = String.valueOf(cell.getNumericCellValue());
                    break;
                case BOOLEAN:
                    cellData=Boolean.toString(cell.getBooleanCellValue());
                    break;
                case BLANK:
                    cellData="";
                    break;
                case ERROR:
                    cellData = Byte.toString(cell.getErrorCellValue());
                case FORMULA:
                    cellData=cell.getCellFormula();
                    break;
                default:
                    break;
            }
            return cellData;

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "";
        }
    }

    public static String getCellData(String sheetName, int rowIndex, int colIndex) {
        Sheet sheet = getSheet(sheetName);
        Row row=getRow(sheet, rowIndex);
        Cell cell=getCell(row, colIndex);
        return cell.getStringCellValue();
    }

    public static Cell getCell(Row row, int colIndex) {
        Cell cell=row.getCell(colIndex-1);
        if(cell==null) {
            cell=row.createCell(colIndex-1);
        }
        return cell;
    }

    public static void setCell(Sheet sheet, int rowIndex, int colIndex, String value) {
        Row row=getRow(sheet, rowIndex);
        Cell cell=getCell(row,colIndex);
        cell.setCellValue(value);
    }
    public static void setCell(String sheetName,int rowIndex,int colIndex, String value) {
        Sheet sheet=getSheet(sheetName);
        setCell(sheet, rowIndex, colIndex, value);
    }

    public static void setCell(String sheetName,String colName,int rowIndex, String value) {
        Sheet sheet=getSheet(sheetName);
        int colIndex=convertColNameToColNum(sheet,colName);
        setCell(sheet, rowIndex, colIndex, value);
    }
}
