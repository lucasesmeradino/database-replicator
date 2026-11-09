package view.dialog;

import database.dao.ReplicacaoProcessoDAO;
import database.model.ReplicacaoProcesso;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseEvent;
import java.sql.SQLException;

public class ConsultaReplicacaoProcessoDialog extends JDialog {

    private ReplicacaoProcesso selecionado;

    private final JTable table;
    private final JButton btnSelecionar;
    private final JButton btnCancelar;

    public ConsultaReplicacaoProcessoDialog(JFrame parent, ReplicacaoProcessoDAO replicacaoProcessoDao) throws SQLException {
        super(parent, "Consulta de processos de replicação", true);
        setSize(700, 400);
        setLocationRelativeTo(parent);
        setResizable(false);
        setLayout(null);

        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.addColumn("Id");
        tableModel.addColumn("Processo");
        tableModel.addColumn("Descrição");
        tableModel.addColumn("Habilitado");

        replicacaoProcessoDao.selectAllProcessos().stream()
                .map(processo -> new Object[] {processo.getId(), processo.getProcesso(),
                        processo.getDescricao(), processo.isHabilitado()}
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

        addButtonsEventListeners(replicacaoProcessoDao);
    }

    private void addButtonsEventListeners(ReplicacaoProcessoDAO replicacaoProcessoDao) {
        btnCancelar.addActionListener(e -> {
            selecionado = null;
            dispose();
        });

        btnSelecionar.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();

            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Selecione um processo antes.");
                return;
            }

            ReplicacaoProcesso processo = new ReplicacaoProcesso();
            processo.setId(Integer.parseInt(table.getValueAt(selectedRow, 0).toString()));
            processo.setProcesso(table.getValueAt(selectedRow, 1).toString());
            processo.setDescricao(table.getValueAt(selectedRow, 2).toString());
            processo.setHabilitado(Boolean.parseBoolean(table.getValueAt(selectedRow, 3).toString()));
            selecionado = processo;

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

    public ReplicacaoProcesso getSelecionado() {
        return selecionado;
    }
}
