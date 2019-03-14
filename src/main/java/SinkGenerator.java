import config.Config;
import table.TableInfo;

import java.net.MalformedURLException;
import java.sql.SQLException;

import static config.ConfigKey.*;

class SinkGenerator {

    private static String genStartWith(String tableName) {
        return "CREATE SINK STREAM " + genSinkName(tableName);
    }

    private static String genWithOption(String table, Config config) throws MalformedURLException, SQLException {

        String primaryKey = SchemaManagerFactory.getOrCreateSchemaManager(table, config).getPrimaryKeysString();

        // remove start with jdbc in url, only use: "postgresql://ip:port/database"
        String url = config.get(DWS_GEN_URL);
        String ignoreStartUrl = url.substring(5, url.length());

        String result = "WITH (\n" +
                "\ttype = \"rds\",\n" +
                "\tusername = \"" + config.get(DWS_USER_NAME) + "\",\n" +
                "\tpassword = \"" + config.get(DWS_PASSWD) + "\",\n" +
                "\tdb_url = \"" + ignoreStartUrl + "\",\n" +
                "\ttable_name = \"" + table + "\",\n" +
                "\tprimary_key = \"" + primaryKey + "\"";

        TableInfo tableInfo = config.getTable(table);
        if (tableInfo.hasColMap()) {
            result += ",\n\tdb_columns = \"" + tableInfo.getColMapKeyString() + "\"";
        }

        return result + "\n);";
    }

    public static String genSinkName(String table) {
        return table + "_SINK";
    }

    public static String genCreateSql(
            Config config,
            String tableName) throws SQLException, MalformedURLException {
        String schema = SchemaManagerFactory.getOrCreateSchemaManager(tableName, config).toString();
        return genStartWith(tableName) + "(" + schema + ")\n" + genWithOption(tableName, config);
    }
}