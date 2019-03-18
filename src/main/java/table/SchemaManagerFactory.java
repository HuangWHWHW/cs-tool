package table;

import java.sql.SQLException;
import java.util.HashMap;

public class SchemaManagerFactory {

    // table name -> schema
    private static HashMap<String, SchemaManager> schemaManagerInfo = new HashMap<>();
    private static DWSManager dwsManager = null;

    public static SchemaManager getOrCreateSchemaManager(String table) throws SQLException {
        if (schemaManagerInfo.containsKey(table)) {
            // return current schema manager by table
            return schemaManagerInfo.get(table);
        } else {
            // create new schema manager
            if (dwsManager == null) {
                // create dws manager
                dwsManager = new DWSManager();
            }

            // create schema manager and store it
            schemaManagerInfo.put(table, new SchemaManager(dwsManager, table));
            return schemaManagerInfo.get(table);
        }
    }

    public static void close() throws SQLException {
        if (dwsManager != null) {
            dwsManager.close();
        }
    }
}