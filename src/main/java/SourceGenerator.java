import config.Config;
import group.GroupManager;
import table.TableInfo;

import java.sql.SQLException;
import java.util.*;

import static config.ConfigKey.*;

class SourceGenerator {
    private static final String REGION = "cn-south-1";
    public static final String EXTERNAL_COL_NAME = "tableName";

    private static String name;

    // table name -> schema info
    private static HashMap<String, SchemaManager> schemaInfo = new HashMap<>();

    // column name -> column type
    private static HashMap<String, String> columnInfo = new HashMap<>();

    private static String genJsonConfig() {
        StringBuffer sb = new StringBuffer();
        sb.append(EXTERNAL_COL_NAME + "=table");
        Set<String> colKey = new HashSet<>();
        for (Map.Entry<String, SchemaManager> entry : schemaInfo.entrySet()) {
            String tableName = entry.getKey();
            SchemaManager schema = entry.getValue();
            TableInfo tableInfo = GroupManager.getTable(tableName);
            List<String> schemaKeySet = schema.getCSSchemaKeySet();

            for (String colName : schemaKeySet) {
                if (!colKey.contains(colName)) {
                    sb.append("; ");
                    // if defined source_column then get the name in source_column else get after.column_name by default.
                    if (tableInfo.hasSourceCol()) {
                        if (!tableInfo.hasSourceColKey(colName)) {
                            throw new IllegalArgumentException("There is no column named " + colName +
                                    " in " + SOURCE_COLUMN + " in table " + tableName);
                        }
                        sb.append(colName + "=" + tableInfo.getSourceCol(colName));
                    } else {
                        sb.append(colName + "=after." + colName);
                    }
                    colKey.add(colName);
                }
            }
        }
        return sb.toString();
    }

    private static String genWithOption(String channel, String partitionId) {
        return "WITH(\n" +
                "\ttype = \"dis\",\n" +
                "\tregion = \"" + REGION + "\",\n" +
                "\tchannel = \"" + channel + "\",\n" +
                "\tpartition_range = \"[" + partitionId + ":" + partitionId + "]\",\n" +
                "\tencode = \"json\",\n" +
                "\tjson_config = \"" + genJsonConfig() + "\"\n);";
    }

    private static String genStartWith() {
        return "CREATE SOURCE STREAM " + name;
    }

    private static String genSchemaSql() {
        StringBuffer sb = new StringBuffer();

        for (Map.Entry<String, String> entry : columnInfo.entrySet()) {
            sb.append(", ");
            String colName = entry.getKey();
            String colType = entry.getValue();
            sb.append(colName + " " + colType);
        }
        return sb.toString();
    }

    public static void setSourceName(String sourceName) {
        name = sourceName;
    }

    public static void addTable(String table, Config config) throws SQLException {
        SchemaManager schemaManager = SchemaManagerFactory.getOrCreateSchemaManager(table, config);
        schemaInfo.put(table, schemaManager);
        for (String column : schemaManager.getCSSchemaKeySet()) {
            columnInfo.put(column, schemaManager.getColumnType(column));
        }
    }

    public static String genCreateSql(String channel, String partitionId) {
        // There is one more column named "tableName" in source
        String schema = "tableName STRING" + genSchemaSql();
        return genStartWith() + "(" + schema + ")\n" + genWithOption(channel, partitionId);
    }

    public static void clean() {
        schemaInfo.clear();
        columnInfo.clear();
    }
}