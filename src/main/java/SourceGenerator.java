import config.Config;
import table.TableInfo;

import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import static config.ConfigKey.*;

class SourceGenerator {
    private static final String REGION = "cn-south-1";
    public static final String EXTERNAL_COL_NAME = "tableName";

    private static String genJsonConfig(String table, Config config) throws MalformedURLException, SQLException {
        TableInfo tableInfo = config.getTable(table);
        StringBuffer sb = new StringBuffer();
        List<String> schemaKeySet = SchemaManagerFactory.getOrCreateSchemaManager(table, config).getCSSchemaKeySet();

        sb.append(EXTERNAL_COL_NAME + "=table");
        for (String colName : schemaKeySet) {
            sb.append("; ");
            // if defined source_column then get the name in source_column else get after.column_name by default.
            if (tableInfo.hasSourceCol()) {
                if (!tableInfo.hasSourceColKey(colName)) {
                    throw new IllegalArgumentException("There is no column named " + colName +
                            " in " + SOURCE_COLUMN + " in table " + table);
                }
                sb.append(colName + "=" + tableInfo.getSourceCol(colName));
            } else {
                sb.append(colName + "=after." + colName);
            }
        }
        return sb.toString();
    }

    private static String genWithOption(String table, Config config) throws MalformedURLException, SQLException {
        TableInfo tableInfo = config.getTable(table);

        return "WITH(\n" +
                "\ttype = \"dis\",\n" +
                "\tregion = \"" + REGION + "\",\n" +
                "\tchannel = \"" + config.get(CHANNEL) + "\",\n" +
                "\tpartition_range = \"[" + tableInfo.getPartition() + ":" + tableInfo.getPartition() + "]\",\n" +
                "\tencode = \"json\",\n" +
                "\tjson_config = \"" + genJsonConfig(table, config) + "\"\n);";
    }

    private static String genStartWith(String table) {
        return "CREATE SOURCE STREAM " + genSourceName(table);
    }

    public static String genSourceName(String table) {
        String[] temp = table.split("\\.");
        if (temp.length > 1) {
            return temp[1] + "_SOURCE";
        }
        return table + "_SOURCE";
    }

    public static String genCreateSql(Config config, String table) throws MalformedURLException, SQLException {
        // There is one more column named "tableName" in source
        String schema = "tableName STRING, " + SchemaManagerFactory.getOrCreateSchemaManager(table, config).toString();
        return genStartWith(table) + "(" + schema + ")\n" + genWithOption(table, config);
    }
}