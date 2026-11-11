import service.ExecutorReplicacaoService;
import view.TelaInicial;

import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ReplicadorStart {

    static void main(String[] args) {
        if (args != null && args.length > 0) {
            String dbConnection = System.getenv("db_connection");
            String dbUser = System.getenv("db_user");
            String dbPass = System.getenv("db_pass");

            if (dbConnection == null || dbUser == null || dbPass == null) {
                throw new IllegalArgumentException("Necessário informar as variáveis de conexão do banco de controle (db_connection, db_user e db_pass)");
            }

            final Connection connection;
            try {
                connection = DriverManager.getConnection(dbConnection, dbUser, dbPass);
            } catch (Exception ex) {
                throw new RuntimeException("Falha ao conectar com o banco de controle.");
            }

            if (args[0].equalsIgnoreCase("run_as_service=true")) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (!Thread.currentThread().isInterrupted()) {
                            try {
                                new ExecutorReplicacaoService(connection);
                            } catch (SQLException ex) {
                                JOptionPane.showMessageDialog(null, "Falha na execução da replicação: " + ex.getLocalizedMessage());
                            }
                            try {
                                Thread.sleep(60000);
                            } catch (InterruptedException ex) {
                                throw new RuntimeException(ex);
                            }
                        }
                    }
                }).start();
            } else {
                SwingUtilities.invokeLater(() -> new TelaInicial(connection).setVisible(true));
            }
        } else {
            throw new IllegalArgumentException("Necessário informar o parâmetro run_as_service");
        }
    }
}
