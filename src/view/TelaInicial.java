package view;

import service.ExecutorReplicacaoService;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TelaInicial extends JFrame {

    private final Connection connection;

    private JDesktopPane desktopPane;

    public TelaInicial(Connection connection) {
        setTitle("Sistema de replicação de dados");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(null);

        this.connection = connection;
        this.desktopPane = new JDesktopPane();
        setContentPane(desktopPane);

        JMenu menuSistema = new JMenu("Sistema");
        JMenu menuCadastro = new JMenu("Cadastro");

        JMenuItem menuItemExecutar = new JMenuItem("Executar");
        menuItemExecutar.addActionListener(e -> {
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
        });

        JMenuItem menuItemSair = new JMenuItem("Sair");
        menuItemSair.addActionListener(e -> {
            try {
                connection.close();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            System.exit(0);
        });

        JMenuItem menuItemProcesso = new JMenuItem("Processo");
        menuItemProcesso.addActionListener(e -> openTelaProcessos());

        JMenuItem menuItemTabelaProcesso = new JMenuItem("Tabelas Processo");
        menuItemTabelaProcesso.addActionListener(e -> openTelaProcessoTabelas());

        JMenuItem menuItemDirecaoProcesso = new JMenuItem("Direções Processo");
        menuItemDirecaoProcesso.addActionListener(e -> openTelaProcessoDirecoes());


        menuSistema.add(menuItemExecutar);
        menuSistema.add(menuItemSair);

        menuCadastro.add(menuItemProcesso);
        menuCadastro.add(menuItemTabelaProcesso);
        menuCadastro.add(menuItemDirecaoProcesso);

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(menuSistema);
        menuBar.add(menuCadastro);
        setJMenuBar(menuBar);
    }

    private void openTelaProcessos() {
        try {
            TelaReplicacaoProcessoView replicacaoProcessoView = new TelaReplicacaoProcessoView(connection);
            JInternalFrame internalFrame = new JInternalFrame("Cadastro de processos", true, true, true, true);
            internalFrame.setSize(650, 360);
            internalFrame.setLayout(new BorderLayout());
            internalFrame.add(replicacaoProcessoView.getContentPane(), BorderLayout.CENTER);
            internalFrame.setVisible(true);
            desktopPane.add(internalFrame);
            internalFrame.setSelected(true);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao abrir a tela de processos" + ex.getLocalizedMessage());
        }
    }

    private void openTelaProcessoTabelas() {
        try {
            TelaReplicacaoProcessoTabelaView replicacaoProcessoView = new TelaReplicacaoProcessoTabelaView(connection);
            JInternalFrame internalFrame = new JInternalFrame("Cadastro de Tabelas", true, true, true, true);
            internalFrame.setSize(760, 460);
            internalFrame.setLayout(new BorderLayout());
            internalFrame.add(replicacaoProcessoView.getContentPane(), BorderLayout.CENTER);
            internalFrame.setVisible(true);
            desktopPane.add(internalFrame);
            internalFrame.setSelected(true);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao abrir a tela de processos" + ex.getLocalizedMessage());
        }
    }

    private void openTelaProcessoDirecoes() {
        try {
            TelaReplicacaoProcessoDirecaoView replicacaoProcessoView = new TelaReplicacaoProcessoDirecaoView(connection);
            JInternalFrame internalFrame = new JInternalFrame("Cadastro de direções", true, true, true, true);
            internalFrame.setSize(860, 460);
            internalFrame.setLayout(new BorderLayout());
            internalFrame.add(replicacaoProcessoView.getContentPane(), BorderLayout.CENTER);
            internalFrame.setVisible(true);
            desktopPane.add(internalFrame);
            internalFrame.setSelected(true);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erro ao abrir a tela de processos" + ex.getLocalizedMessage());
        }
    }

    static void main(String[] args) {
        try {
            Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/db_controle_replicacao", "postgres", "postgres");
            TelaInicial telaInicial = new TelaInicial(connection);
            SwingUtilities.invokeLater(() -> { telaInicial.setVisible(true); });
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Não foi possível iniciar a conexão com o banco de dados.");
        }
    }
}
