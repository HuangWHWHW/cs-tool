import config.Config;
import utils.URLParser;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static config.ConfigKey.*;

class DWSManager {

    private Connection connection = null;
    private PreparedStatement statement = null;
    private Config config;

    public DWSManager(Config config) throws SQLException {
        this.config = config;

        String url = config.get(DWS_URL);
        String userName = config.get(DWS_USER_NAME);
        String passWd = config.get(DWS_PASSWD);

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
        String newUrl = urlParser.replaceDomain(config.get(DWS_IP)).toString();
        config.set(DWS_GEN_URL, newUrl);
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

    public List<String> getPrimaryKeys(String tableName) throws SQLException {
        List<String> primaryKeys = new ArrayList<>();
        DatabaseMetaData dbmd = connection.getMetaData();
        ResultSet rs = dbmd.getPrimaryKeys(null, null, tableName);
        while (rs.next()) {
            // see the doc of "getPrimaryKeys", primary key name is in the 4th object
            primaryKeys.add(String.valueOf(rs.getObject(4)));
        }
        return primaryKeys;
    }
}
