package config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

import group.GroupManager;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import utils.Util;

import static config.ConfigKey.*;

public class Config {
    private final String BASE_CONFIG_SHEET_NAME = "BaseConfig";
    private final String TABLE_CONFIG_SHEET_NAME = "TableConfig";

    private static HashMap<String, String> parameters = new HashMap<>();

    public Config(String path) throws IOException {
        // read file
        Workbook workbook;
        try {
            // for office 2007+
            workbook = new XSSFWorkbook(new FileInputStream(path));
        } catch (Exception e) {
            workbook = new HSSFWorkbook(new FileInputStream(path));
        }

        // get base config
        Sheet sheet = workbook.getSheet(BASE_CONFIG_SHEET_NAME);
        int index = 1;
        while (index <= sheet.getLastRowNum()) {
            Row row = sheet.getRow(index);
            String key = row.getCell(0).getStringCellValue();
            String value = Util.getCellStringValue(row.getCell(1));
            parameters.put(key, value);
            index++;
        }

        // get table config
        sheet = workbook.getSheet(TABLE_CONFIG_SHEET_NAME);
        // row index start with 1
        index = 1;
        Row firstRow = sheet.getRow(0);
        while (index <= sheet.getLastRowNum()) {
            Row row = sheet.getRow(index);

            HashMap<String, String> tableParameters = new HashMap<>();
            for (int cellCount = 0; cellCount < firstRow.getLastCellNum(); cellCount++) {
                Cell cell = row.getCell(cellCount);
                String key = firstRow.getCell(cellCount).getStringCellValue();
                String value = Util.getCellStringValue(cell);
                if (value != null) {
                    tableParameters.put(key, value);
                }
            }

            // get table config
            GroupManager.addTable(tableParameters);
            index++;
        }

        // check config
        check();
    }

    private void check() {
        // check required key
        for (String requireKey : require) {
            if (!parameters.containsKey(requireKey)) {
                throw new IllegalArgumentException(requireKey + " is required");
            }
        }

        /**
         * check {@link DWS_IP} is a ip address
          */
        if (parameters.containsKey(DWS_IP)) {
            String ip = parameters.get(DWS_IP);
            if (!Util.isIPv4(ip)) {
                throw new IllegalArgumentException("Illegal ip address: " + ip + " in " + DWS_IP);
            }
        }
    }

    public static String get(String key) {
        if (parameters.containsKey(key)) {
            return parameters.get(key);
        } else {
            return null;
        }
    }

    public void set(String key, String value) {
        parameters.put(key, value);
    }
}
