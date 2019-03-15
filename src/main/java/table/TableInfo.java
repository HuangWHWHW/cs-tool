package table;

import config.Config;
import utils.PartitionKeyMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static config.ConfigKey.*;

public class TableInfo {
    private String tableName;
    private String channel;
    private String partition;
    private String groupName;
    private boolean isNeedCreate;

    private List<String> sourceColumn = new ArrayList<>();

    // key for attr name in sink, value for attr name in dws table
    private HashMap<String, String> colMap = new HashMap<>();

    public TableInfo(HashMap<String, String> tableInfo) {
        check(tableInfo);

        tableName = tableInfo.get(TABLE);
        partition = PartitionKeyMap.getPartitonId(tableName, Integer.valueOf(Config.get(PARTITION_NUM)));
        channel = tableInfo.getOrDefault(TABLE_CHANNLE, Config.get(CHANNEL));
        groupName = tableInfo.get(GROUP_NAME);
        isNeedCreate = !tableInfo.get(NEED_CREATE).equals("n");

        if (!tableInfo.get(SOURCE_COLUMN).isEmpty()) {
            parseSourceColumn(tableInfo.get(SOURCE_COLUMN));
        }

        if (!tableInfo.get(COL_MAP).isEmpty()) {
            parseColMap(tableInfo.get(COL_MAP));

            // check source_column and col_map be the same
            if (!compareColumnInfo()) {
                throw new IllegalArgumentException("source_column and col_map must be the same in table " + tableName);
            }
        }
    }

    private void check(HashMap<String, String> parameters) {
        // check required key
        for (String requireKey : requireTableConfig) {
            if (parameters.get(requireKey).isEmpty()) {
                throw new IllegalArgumentException(requireKey + " is required");
            }
        }
    }

    private boolean compareColumnInfo() {
        if (sourceColumn.size() == 0 || colMap.size() == 0) {
            return true;
        }

        // check size
        if (sourceColumn.size() != colMap.size()) {
            return false;
        }

        // check column name
        for (int i = 0; i < sourceColumn.size(); i++) {
            if (!colMap.containsKey(sourceColumn.get(i).split("\\.")[1])) {
                return false;
            }
        }

        return true;
    }

    private void parseSourceColumn(String input) {
        String[] colSet = input.split(",");
        for (String col : colSet) {
            // add "after" for default
            if (!col.contains(".")) {
                sourceColumn.add("after." + col.trim());
            } else {
                sourceColumn.add(col.trim());
            }
        }
    }

    private void parseColMap(String input) {
        try {
            String[] mapSet = input.split(",");

            for (String element : mapSet) {
                colMap.put(element.split("=")[1].trim(), element.split("=")[0].trim());
            }
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalArgumentException("parse col_map error: " + input + " for table " +tableName);
        }
    }

    public boolean hasSourceCol() {
        return !sourceColumn.isEmpty();
    }

    public boolean hasSourceColKey(String key) {
        for (String col: sourceColumn) {
            if (col.split("\\.")[1].equals(key)) {
                return true;
            }
        }
        return false;
    }

    public String getSourceCol(String key) {
        for (String col: sourceColumn) {
            if (col.split("\\.")[1].equals(key)) {
                return col;
            }
        }
        throw new IllegalArgumentException("There is no column named " + key +
                " in " + SOURCE_COLUMN + " in table " + tableName);
    }

    public boolean hasColMap() {
        return !colMap.isEmpty();
    }

    public HashMap<String, String> getColMap() {
        return colMap;
    }

    public String getColMapKeyString() {
        StringBuffer sb = new StringBuffer();
        int count = 0;
        for (String value : colMap.values()) {
            if (count > 0) {
                sb.append(",");
            }
            sb.append(value);
            count++;
        }
        return sb.toString();
    }

    public String getPartition() {
        return partition;
    }

    public String getColNameInCS(String dwsColName) {
        if (!hasColMap()) {
            return dwsColName;
        }
        for (Map.Entry<String, String> entry : colMap.entrySet()) {
            if (entry.getValue().equals(dwsColName)) {
                return entry.getKey();
            }
        }
        return "";
    }

    public String getChannel() {
        return channel;
    }

    public String getGroupName() {
        return groupName;
    }

    public boolean isNeedCreate() {
        return isNeedCreate;
    }

    public String getTableName() {
        return tableName;
    }

    public String getDefaultAppName() {
        return channel + "_" + partition;
    }
}
