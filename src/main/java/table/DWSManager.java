package table;

import config.Config;
import utils.URLParser;

import java.sql.*;

import static config.ConfigKey.*;

class DWSManager {

    private Connection connection = null;
    private PreparedStatement statement = null;

    public DWSManager() throws SQLException {
        String url = Config.get(DWS_URL);
        String userName = Config.get(DWS_USER_NAME);
        String passWd = Config.get(DWS_PASSWD);

        // deal with url
        dealWithUrl(url);

        // create connection
        try {
            connection = DriverManager.getConnection(url, userName, passWd);
        } catch (SQLException e) {
            if (connection != null) {
                connection.close();
            }
            throw e;
        }
    }

    private void dealWithUrl(String url) {
        URLParser urlParser = URLParser.parse(url);
        String newUrl = urlParser.replaceDomain(Config.get(DWS_IP)).toString();
        Config.set(DWS_GEN_URL, newUrl);
    }

    public void close() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    public ResultSetMetaData getTableMeta(String tableName) throws SQLException {
        try {
            statement = connection.prepareStatement("select * from " + tableName + " where 1=0;");
            return statement.executeQuery().getMetaData();
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }
}
