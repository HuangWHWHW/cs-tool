package group;

import table.TableInfo;

import java.util.Collection;
import java.util.HashMap;

public class GroupManager {

    // group map
    private static HashMap<String, GroupInfo> groups = new HashMap<>();

    // all tables info
    private static HashMap<String, TableInfo> tables = new HashMap<>();

    public static void addTable(HashMap<String, String> tableParameters) {
        TableInfo tableInfo = new TableInfo(tableParameters);

        String name = tableInfo.getTableName();
        if (tables.containsKey(name)) {
            throw new IllegalArgumentException("Table name is repeated: " + name);
        }

        // store table info
        tables.put(name, tableInfo);

        // store group info
        String groupName = tableInfo.getGroupName();
        if (groups.containsKey(groupName)) {
            GroupInfo group = groups.get(groupName);
            group.addTable(name, tableInfo);
        } else {
            groups.put(groupName, new GroupInfo(name, tableInfo));
        }
    }

    public static TableInfo getTable(String tableName) {
        if (tables.containsKey(tableName)) {
            return tables.get(tableName);
        } else {
            return null;
        }
    }

    public static Collection<GroupInfo> getGroups() {
        return groups.values();
    }
}
