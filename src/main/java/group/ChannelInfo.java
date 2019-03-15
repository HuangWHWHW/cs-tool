package group;

import table.TableInfo;

import java.util.Collection;
import java.util.HashMap;

public class ChannelInfo {
    private String name;

    // partition id -> partition info
    private HashMap<String, PartitionInfo> partitions = new HashMap<>();

    public ChannelInfo(String name) {
        this.name = name;
    }

    public void addTable(String partitionId, TableInfo tableInfo) {
        PartitionInfo partitionInfo;
        if (partitions.containsKey(partitionId)) {
            partitionInfo = partitions.get(partitionId);
        } else {
            partitionInfo = new PartitionInfo(partitionId);
            partitions.put(partitionId, partitionInfo);
        }
        partitionInfo.addTable(tableInfo);
    }

    public String getName() {
        return name;
    }

    public Collection<PartitionInfo> getPartitions() {
        return partitions.values();
    }
}
