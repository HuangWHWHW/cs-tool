package table;

import group.GroupManager;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

class SchemaManager {

    // colName and colType map
    private Set<String> schemaName = new HashSet<>();

    private TableInfo tableInfo;
    private String tableName;
    private ResultSetMetaData rsmd;

    public SchemaManager(DWSManager dwsManager, String table) throws SQLException {
        tableInfo = GroupManager.getTable(table);
        tableName = table;

        // get table meta
        rsmd = dwsManager.getTableMeta(table);

        // general schema sql
        buildSchema();
    }

    public void checkColMap() {
        HashMap<String, String> colMap = tableInfo.getColMap();
        for (Map.Entry<String, String> entry : colMap.entrySet()) {
            if (!schemaName.contains(entry.getValue())) {
                throw new IllegalArgumentException("\"" + entry.getValue() + "\" is not in dws table \"" + tableName + "\"");
            }
        }
    }

    private void buildSchema() throws SQLException {
        int colNum = rsmd.getColumnCount();
        for (int i = 0; i < colNum; i++) {
            String colName = rsmd.getColumnName(i + 1);
            schemaName.add(colName);
        }
    }
}