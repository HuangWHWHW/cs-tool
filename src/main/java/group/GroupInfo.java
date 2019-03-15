package group;

import table.TableInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class GroupInfo {
    private String name;
    private List<String> tableList = new ArrayList<>();

    // channel name -> channel info
    private HashMap<String, ChannelInfo> channels = new HashMap<>();

    public GroupInfo(String table, TableInfo tableInfo) {
        this.name = tableInfo.getGroupName();
        addTable(table, tableInfo);
    }

    public void addTable(String table, TableInfo tableInfo) {
        tableList.add(table);
        String channelName = tableInfo.getChannel();
        String partitionId = tableInfo.getPartition();
        if (channels.containsKey(channelName)) {
            channels.get(channelName).addTable(partitionId, tableInfo);
        } else {
            ChannelInfo channelInfo = new ChannelInfo(channelName);
            channelInfo.addTable(partitionId, tableInfo);
            channels.put(channelName, channelInfo);
        }
    }

    public String getName() {
        return this.name;
    }

    public Collection<ChannelInfo> getChannels() {
        return channels.values();
    }
}
