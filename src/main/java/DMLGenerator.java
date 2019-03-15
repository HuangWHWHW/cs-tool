import config.Config;

import java.sql.SQLException;

class DMLGenerator {
    public static String genInsertSql(String table, String sourceName, Config config) throws SQLException {
        String colNames = SchemaManagerFactory.getOrCreateSchemaManager(table, config).getColNameSet();
        String sinkName = SinkGenerator.genSinkName(table);
        return "INSERT INTO " + sinkName + " SELECT " + colNames + " FROM " + sourceName +
                " WHERE " + SourceGenerator.EXTERNAL_COL_NAME + "=\"" + table + "\";";
    }
}