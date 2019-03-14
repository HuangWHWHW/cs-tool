import config.Config;

import javax.swing.*;
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
            for (String tableName : config.getTableNames()) {
                // get sink ddl
                String sinkDDL = SinkGenerator.genCreateSql(config, tableName);

                // get source ddl
                String sourceDDL = SourceGenerator.genCreateSql(config, tableName);

                // get dml
                String dml = DMLGenerator.genInsertSql(tableName, config);

                System.out.println(sourceDDL);
                System.out.println(sinkDDL);
                System.out.println(dml);
            }
        } finally {
            SchemaManagerFactory.close();
        }
    }
}