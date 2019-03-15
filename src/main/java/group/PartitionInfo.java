package group;

import table.TableInfo;

import java.util.Collection;
import java.util.HashMap;

public class PartitionInfo {
    private String id;

    // table name -> table info
    private HashMap<String, TableInfo> tables = new HashMap<>();

    public PartitionInfo(String partitionId) {
        this.id = partitionId;
    }

    public void addTable(TableInfo tableInfo) {
        tables.put(tableInfo.getTableName(), tableInfo);
    }

    public String getId() {
        return id;
    }

    public Collection<TableInfo> getTables() {
        return tables.values();
    }
}
