import config.Config;
import table.SchemaManagerFactory;

import java.sql.SQLException;

class DMLGenerator {
    public static String genInsertSql(String table, String sourceName, Config config) throws SQLException {
        String colNames;
        String sinkName = SinkGenerator.genSinkName(table);
        return "INSERT INTO " + sinkName + " SELECT " + colNames + " FROM " + sourceName +
                " WHERE " + SourceGenerator.EXTERNAL_COL_NAME + "=\"" + table + "\";";
    }
}