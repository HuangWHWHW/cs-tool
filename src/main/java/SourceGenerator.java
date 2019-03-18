import config.Config;
import group.GroupManager;
import table.TableInfo;

import java.sql.SQLException;
import java.util.*;

import static config.ConfigKey.*;

class SourceGenerator {
    private static final String REGION = "cn-south-1";
    public static final String EXTERNAL_COL_NAME = "tableName";
    private static final String SOURCE_SCHEMA = "tableName STRING, OP_TYPE STRING, BEFORE STRING, AFTER STRING";
    private static final String JSON_CONFIG = "tableName=table; OP_TYPE=op_type; BEFORE=before; AFTER=after";

    private static String name;

    private static String genWithOption(String channel, String partitionId) {
        return "WITH(\n" +
                "\ttype = \"dis\",\n" +
                "\tregion = \"" + REGION + "\",\n" +
                "\tchannel = \"" + channel + "\",\n" +
                "\tpartition_range = \"[" + partitionId + ":" + partitionId + "]\",\n" +
                "\tencode = \"json\",\n" +
                "\tjson_config = \"" + JSON_CONFIG + "\"\n);";
    }

    private static String genStartWith() {
        return "CREATE SOURCE STREAM " + name;
    }

    public static void setSourceName(String sourceName) {
        name = sourceName;
    }

    public static String genCreateSql(String channel, String partitionId) {
        return genStartWith() + "(" + SOURCE_SCHEMA + ")\n" + genWithOption(channel, partitionId);
    }
}