import config.Config;

import java.net.MalformedURLException;
import java.sql.SQLException;

class DMLGenerator {
    public static String genInsertSql(String table, Config config) throws MalformedURLException, SQLException {
        String colNames = SchemaManagerFactory.getOrCreateSchemaManager(table, config).getColNameSet();
        String sinkName = SinkGenerator.genSinkName(table);
        String sourceName = SourceGenerator.genSourceName(table);
        String sourceTable = config.getTable(table).getSourceTable();
        return "INSERT INTO " + sinkName + " SELECT " + colNames + " FROM " + sourceName +
                " WHERE " + SourceGenerator.EXTERNAL_COL_NAME + "=\"" + sourceTable + "\";";
    }
}