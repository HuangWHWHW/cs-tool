package table;

import config.Config;
import utils.PartitionKeyMap;

import java.sql.SQLException;
import java.util.HashMap;

import static config.ConfigKey.*;

public class TableInfo {
    private String tableName;
    private String channel;
    private String partition;
    private String groupName;
    private String sourceTable;

    // key for attr name in sink, value for attr name in dws table
    private HashMap<String, String> colMap = new HashMap<>();

    public TableInfo(HashMap<String, String> tableInfo) throws SQLException {
        check(tableInfo);

        tableName = tableInfo.get(TABLE);
        partition = PartitionKeyMap.getPartitonId(tableName, Integer.valueOf(Config.get(PARTITION_NUM)));
        channel = tableInfo.getOrDefault(TABLE_CHANNLE, Config.get(CHANNEL));
        groupName = tableInfo.getOrDefault(GROUP_NAME, channel + "_" + partition);
        sourceTable = tableInfo.getOrDefault(SOURCE_TABLE, tableName);

        if (!tableInfo.get(COL_MAP).isEmpty()) {
            parseColMap(tableInfo.get(COL_MAP));
            SchemaManagerFactory.getOrCreateSchemaManager(tableName).checkColMap();
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

    private void parseColMap(String input) {
        try {
            String[] mapSet = input.split(",");

            for (String element : mapSet) {
                colMap.put(element.split("=")[1].trim(), element.split("=")[0].trim());
            }
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalArgumentException("parse col_map error: \"" + input + "\" for table " + tableName);
        }
    }

    public HashMap<String, String> getColMap() {
        return colMap;
    }

    public String getPartition() {
        return partition;
    }

    public String getChannel() {
        return channel;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getTableName() {
        return tableName;
    }

    public String getSourceTable() {
        return sourceTable;
    }
}
