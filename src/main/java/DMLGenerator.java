import config.Config;
import group.GroupManager;

import static config.ConfigKey.*;

class DMLGenerator {
    private static final String udfSQL = "CREATE FUNCTION udf_test AS 'com.huawei.udf.UdfScalarFunction';";
    private static final String udfName = "udf_test";
    private static final String tempStreamSQL = "CREATE TEMP STREAM XX(TEMP_1 int);";
    private static final String colName = "tableName, OP_TYPE, BEFORE, AFTER";

    public static String genInsertSql(String sourceName) {
        return udfSQL + "\n" +
                tempStreamSQL + "\n" +
                "INSERT INTO XX SELECT " + udfName + "(" + colName +
                "\"" + Config.get(DWS_GEN_URL) + "\", " +
                "\"" + Config.get(DWS_USER_NAME) + "\", " +
                "\"" + Config.get(DWS_PASSWD) + "\", " +
                "\"" + GroupManager.getTableMap() + "\"" +
                ") FROM " + sourceName;
    }
}