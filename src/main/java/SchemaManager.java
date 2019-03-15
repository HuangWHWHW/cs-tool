import config.Config;
import javafx.util.Pair;
import table.TableInfo;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static config.ConfigKey.*;

class SchemaManager {

    // colName and colType map
    private HashMap<String, String> schemaInfo = new HashMap<>();

    // build cs schema for keeping order
    private List<Pair<String, String> > csSchemaInfo = new ArrayList<>();
    private List<String> primaryKeys = new ArrayList<>();
    private TableInfo tableInfo;
    private String tableName;
    private ResultSetMetaData rsmd;

    public SchemaManager(DWSManager dwsManager, String table, Config config) throws SQLException {
        tableInfo = config.getTable(table);
        tableName = table;

        // get table meta
        rsmd = dwsManager.getTableMeta(table);

        // get primary keys
        primaryKeys.add(tableInfo.getPrimaryKey());
        checkPrimaryKeys();

        // general schema sql
        buildSchema();

        if (tableInfo.hasColMap()) {
            buildCSSchemaByColMap();
        } else {
            buildCSSchema();
        }
    }

    private void buildCSSchemaByColMap() {
        HashMap<String, String> colMap = tableInfo.getColMap();
        for (Map.Entry<String, String> entry : colMap.entrySet()) {
            if (!schemaInfo.containsKey(entry.getValue())) {
                throw new IllegalArgumentException("\"" + entry.getValue() + "\" is not in dws table \"" + tableName + "\"");
            }
            Pair<String, String> colInfo = new Pair<>(entry.getKey(), schemaInfo.get(entry.getValue()));
            csSchemaInfo.add(colInfo);
        }
    }

    private void buildCSSchema() throws SQLException {
        int colNum = rsmd.getColumnCount();
        for (int i = 0; i < colNum; i++) {
            String colName = rsmd.getColumnName(i + 1);
            Pair<String, String> colInfo = new Pair<>(colName, schemaInfo.get(colName));
            csSchemaInfo.add(colInfo);
        }
    }

    private void checkPrimaryKeys() {
        if (primaryKeys.size() == 0) {
            throw new IllegalArgumentException("There is not any primary key in table " + tableName);
        }

        if (tableInfo.hasColMap()) {
            HashMap<String, String> colMap = tableInfo.getColMap();
            for (String primaryKey : primaryKeys) {
                if (!colMap.containsValue(primaryKey)) {
                    throw new IllegalArgumentException("Primary key \"" + primaryKey +
                            "\" must be contained in \"" + COL_MAP + "\" in table \"" + tableName + "\"");
                }
            }
        } else if (tableInfo.hasSourceCol()) {
            // if col_map is not specified, check source_column to contain all the primary key.
            for (String primaryKey : primaryKeys) {
                if (!tableInfo.hasSourceColKey(primaryKey)) {
                    throw new IllegalArgumentException("Primary key \"" + primaryKey +
                            "\" must be contained in \"" + SOURCE_COLUMN + "\" in table \"" + tableName + "\"");
                }
            }
        }
    }

    private String getCSType(String dwsType, int numType, int size, int scale) {
        if (dwsType.equals("bit") || dwsType.equals("bypea")) {
            return "ARRAY[TINYINT]";
        } else if (dwsType.equals("int2")) {
            return "SMALLINT";
        } else if (dwsType.equals("int4")) {
            return "INT";
        } else if (dwsType.equals("int8")) {
            return "BIGINT";
        } else if (dwsType.equals("float4")) {
            return "FLOAT";
        } else if (dwsType.equals("float8") || dwsType.equals("money")) {
            return "DOUBLE";
        } else if (dwsType.equals("date")) {
            return "DATE";
        } else if (dwsType.equals("timestamp")
                || dwsType.equals("timestamptz")
                || dwsType.equals("time")
                || dwsType.equals("timetz")) {
            return "TIMESTAMP(3)";
        } else if (dwsType.equals("numeric")
                || dwsType.equals("decimal")) {
            return "DECIMAL(" + size + ", " + scale + ")";
        } else if (numType == Types.ARRAY) {
            // example for array[int]: _int4
            return "ARRAY[" + getCSType(dwsType.substring(1, dwsType.length()), numType, size, scale) + "]";
        } else {
            return "STRING";
        }
    }

    private void buildSchema() throws SQLException {
        int colNum = rsmd.getColumnCount();
        for (int i = 0; i < colNum; i++) {
            String colName = rsmd.getColumnName(i + 1);
            String colTypeName = rsmd.getColumnTypeName(i + 1);
            int colType = rsmd.getColumnType(i + 1);
            int size = rsmd.getPrecision(i + 1);
            int scale = rsmd.getScale(i + 1);
            String csType = getCSType(colTypeName, colType, size, scale);
            schemaInfo.put(colName, csType);
        }
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();

        int count = 0;
        for (Pair<String, String> entry : csSchemaInfo) {
            if (count > 0) {
                sb.append(",");
            }
            String colName = entry.getKey();
            String colType = entry.getValue();
            sb.append(colName + " " + colType);
            count++;
        }
        return sb.toString();
    }

    public String getColNameSet() {
        StringBuffer sb = new StringBuffer();

        int count = 0;
        for (Pair<String, String> entry : csSchemaInfo) {
            if (count > 0) {
                sb.append(", ");
            }
            sb.append(entry.getKey());
            count++;
        }
        return sb.toString();
    }

    public String getPrimaryKeysString() {
        StringBuffer sb = new StringBuffer();
        int count = 0;
        for (String key : primaryKeys) {
            if (count > 0) {
                sb.append(",");
            }
            sb.append(tableInfo.getColNameInCS(key));
            count++;
        }
        return sb.toString();
    }

    public List<String> getCSSchemaKeySet() {
        List<String> keySet = new ArrayList<>();
        for (Pair<String, String> entry : csSchemaInfo) {
            keySet.add(entry.getKey());
        }
        return keySet;
    }
}