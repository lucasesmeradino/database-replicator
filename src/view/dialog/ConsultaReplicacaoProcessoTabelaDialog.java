package view.dialog;

import database.dao.ReplicacaoProcessoTabelaDAO;
import database.model.ReplicacaoProcessoTabela;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseEvent;
import java.sql.SQLException;

public class ConsultaReplicacaoProcessoTabelaDialog extends JDialog {

    private ReplicacaoProcessoTabela selecionado;

    private final JTable table;
    private final JButton btnSelecionar;
    private final JButton btnCancelar;

    public ConsultaReplicacaoProcessoTabelaDialog(JFrame parent, ReplicacaoProcessoTabelaDAO replicacaoProcessoTabelaDao) throws SQLException {
        super(parent, "Consulta das tabelas das replicações", true);
        setSize(800, 500);
        setLocationRelativeTo(parent);
        setResizable(false);
        setLayout(null);

        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.addColumn("Id");
        tableModel.addColumn("Processo");
        tableModel.addColumn("Origem");
        tableModel.addColumn("Destino");
        tableModel.addColumn("Ordem");
        tableModel.addColumn("Habilitado");

        replicacaoProcessoTabelaDao.selectAllTabelas().stream()
                .map(tabela -> new Object[] {tabela.getId(), tabela.getProcessoId(),
                        tabela.getTabelaOrigem(), tabela.getTabelaDestino(), tabela.getOrdem(), tabela.isHabilitado()}
                ).forEach(tableModel::addRow);

        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(10, 10, 680, 300);
        add(scrollPane);

        btnSelecionar = new JButton("Selecionar");
        btnSelecionar.setBounds(10, 320, 140, 30);
        add(btnSelecionar);

        btnCancelar = new JButton("Cancelar");
        btnCancelar.setBounds(160, 320, 140, 30);
        add(btnCancelar);

        addButtonsEventListeners(replicacaoProcessoTabelaDao);
    }

    private void addButtonsEventListeners(ReplicacaoProcessoTabelaDAO replicacaoProcessoTabelaDao) {
        btnCancelar.addActionListener(e -> {
            selecionado = null;
            dispose();
        });

        btnSelecionar.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();

            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Selecione uma tabela antes.");
                return;
            }

            try {
                ReplicacaoProcessoTabela tabela = replicacaoProcessoTabelaDao.selectTabelaById(Integer.parseInt(table.getValueAt(selectedRow, 0).toString()));
                selecionado = tabela;
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Falha na busca da tabela selecionada.");
                return;
            }

            dispose();
        });

        btnSelecionar.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2) {
                btnSelecionar.doClick();
            }
            }
        });
    }

    public ReplicacaoProcessoTabela getSelecionado() {
        return selecionado;
    }
}
