import config.Config;
import group.ChannelInfo;
import group.GroupInfo;
import group.GroupManager;
import group.PartitionInfo;
import table.TableInfo;
import utils.PartitionKeyMap;

import javax.swing.*;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;

class GenSql {

    public static void main(String[] args) throws SQLException, IOException {
        String configPath;
        JFileChooser jfc = new JFileChooser();
        if (jfc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            configPath = jfc.getSelectedFile().getPath();
        } else {
            System.out.println("[ERROR] Please chose config file.");
            return;
        }

        Config config = new Config(configPath);

        try {
            for (GroupInfo group : GroupManager.getGroups()) {
                String appName = group.getName();
                FileWriter writer = new FileWriter(appName);
                for (ChannelInfo channel : group.getChannels()) {
                    String channelName = channel.getName();
                    for (PartitionInfo partition : channel.getPartitions()) {
                        String partitionId = partition.getId();
                        String sourceName = channelName + "_" + partitionId;
                        SourceGenerator.clean();
                        SourceGenerator.setSourceName(sourceName);
                        String sinkDDL = "";
                        String dml = "";
                        for (TableInfo tableInfo : partition.getTables()) {
                            String tableName = tableInfo.getTableName();

                            // add table info to source
                            SourceGenerator.addTable(tableName, config);

                            if (tableInfo.isNeedCreate()) {
                                // get sink ddl
                                sinkDDL += "\n" + SinkGenerator.genCreateSql(config, tableName);

                                // get dml
                                dml += "\n" + DMLGenerator.genInsertSql(tableName, sourceName, config);
                            }
                        }
                        // get source ddl
                        String sourceDDL = SourceGenerator.genCreateSql(channelName, partitionId);

                        // write to file
                        writer.write(sourceDDL + "\n");
                        writer.write(sinkDDL + "\n");
                        writer.write(dml + "\n");
                    }
                }
                writer.close();
            }
        } finally {
            SchemaManagerFactory.close();
        }
    }
}