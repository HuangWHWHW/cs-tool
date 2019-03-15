package config;

public class ConfigKey {
    public static final String CHANNEL = "dis_channel";
    public static final String DWS_URL = "dws_url";
    public static final String DWS_IP = "dws_ip";
    public static final String DWS_GEN_URL = "dws_gen_url";
    public static final String DWS_USER_NAME = "dws_user_name";
    public static final String DWS_PASSWD = "dws_passwd";
    public static final String TABLE = "table_name";
    public static final String PARTITION = "partition";
    public static final String PRIMARY_KAY = "primary_key";
    public static final String SOURCE_COLUMN = "source_column";
    public static final String COL_MAP = "col_map";
    public static final String SOURCE_TABLE = "source_table";

    public static final String[] require = {CHANNEL, DWS_URL, DWS_IP, DWS_USER_NAME, DWS_PASSWD};
}
