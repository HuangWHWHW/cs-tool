package config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import table.TableInfo;
import utils.Util;

import static config.ConfigKey.*;

public class Config {
    private final String BASE_CONFIG_SHEET_NAME = "BaseConfig";
    private final String TABLE_CONFIG_SHEET_NAME = "TableConfig";

    private HashMap<String, String> parameters = new HashMap<>();
    private HashMap<String, TableInfo> tables = new HashMap<>();

    public Config(String path) throws IOException {
        // read file
        Workbook workbook = null;
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
            String value = row.getCell(1).getStringCellValue();

            parameters.put(key, value);
            index++;
        }

        // get table config
        sheet = workbook.getSheet(TABLE_CONFIG_SHEET_NAME);
        index = 1;
        while (index <= sheet.getLastRowNum()) {
            Row row = sheet.getRow(index);

            String tableName = row.getCell(0).getStringCellValue();
            String partition = String.valueOf((int)(row.getCell(1).getNumericCellValue()));
            String sourceCol = row.getCell(2).getStringCellValue();
            String colMap = row.getCell(3).getStringCellValue();
            String primaryKey = row.getCell(4).getStringCellValue();

            if (tables.containsKey(tableName)) {
                throw new IllegalArgumentException("Table name is repeated: " + tableName);
            }

            // store table name
            HashMap<String, String> tableParameters = new HashMap<>();
            tableParameters.put(TABLE, tableName);
            tableParameters.put(PARTITION, partition);
            tableParameters.put(PRIMARY_KAY, primaryKey);
            if (!sourceCol.isEmpty()) {
                tableParameters.put(SOURCE_COLUMN, sourceCol);
            }
            if (!colMap.isEmpty()) {
                tableParameters.put(COL_MAP, colMap);
            }

            // get table config
            tables.put(tableName, new TableInfo(tableParameters));

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

        // must set at least one table
        if (tables.isEmpty()) {
            throw new IllegalArgumentException("Please specify \"" + TABLE + "\"");
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

    public String get(String key) {
        if (parameters.containsKey(key)) {
            return parameters.get(key);
        } else {
            return null;
        }
    }

    public TableInfo getTable(String tableName) {
        if (tables.containsKey(tableName)) {
            return tables.get(tableName);
        } else {
            return null;
        }
    }

    public Set<String> getTableNames(){
        return tables.keySet();
    }

    public boolean hasKey(String key) {
        return parameters.containsKey(key);
    }

    public void set(String key, String value) {
        parameters.put(key, value);
    }
}
