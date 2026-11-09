package view.dialog;

import database.dao.ReplicacaoDirecaoDAO;
import database.dao.ReplicacaoProcessoDAO;
import database.model.ReplicacaoDirecao;
import database.model.ReplicacaoProcesso;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseEvent;
import java.sql.SQLException;

public class ConsultaReplicacaoProcessoDirecaoDialog extends JDialog {

    private ReplicacaoDirecao selecionado;

    private final JTable table;
    private final JButton btnSelecionar;
    private final JButton btnCancelar;

    public ConsultaReplicacaoProcessoDirecaoDialog(JFrame parent, ReplicacaoDirecaoDAO replicacaoDirecaoDao) throws SQLException {
        super(parent, "Consulta direções dos processos", true);
        setSize(900, 600);
        setLocationRelativeTo(parent);
        setResizable(false);
        setLayout(null);

        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.addColumn("Id");
        tableModel.addColumn("Processo");
        tableModel.addColumn("Origem");
        tableModel.addColumn("Destino");
        tableModel.addColumn("Habilitado");

        replicacaoDirecaoDao.selectAllDirecoes().stream()
                .map(direcao -> new Object[] {direcao.getId(), direcao.getProcessoId(),
                        direcao.getConexaoOrigem(), direcao.getConexaoDestino(), direcao.isHabilitado()}
                ).forEach(tableModel::addRow);

        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(10, 10, 720, 300);
        add(scrollPane);

        btnSelecionar = new JButton("Selecionar");
        btnSelecionar.setBounds(10, 320, 140, 30);
        add(btnSelecionar);

        btnCancelar = new JButton("Cancelar");
        btnCancelar.setBounds(160, 320, 140, 30);
        add(btnCancelar);

        addButtonsEventListeners(replicacaoDirecaoDao);
    }

    private void addButtonsEventListeners(ReplicacaoDirecaoDAO replicacaoDirecaoDao) {
        btnCancelar.addActionListener(e -> {
            selecionado = null;
            dispose();
        });

        btnSelecionar.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();

            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Selecione um direcao antes.");
                return;
            }

            try {
                ReplicacaoDirecao direcao = replicacaoDirecaoDao.selectDirecaoById(Long.parseLong(table.getValueAt(selectedRow, 0).toString()));
                selecionado = direcao;
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Falha na busca da direção durante a seleção.");
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

    public ReplicacaoDirecao getSelecionado() {
        return selecionado;
    }
}
