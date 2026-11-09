package view;

import database.dao.ReplicacaoProcessoDAO;
import database.model.ReplicacaoProcesso;
import view.dialog.ConsultaReplicacaoProcessoDialog;

import javax.swing.*;
import java.sql.Connection;
import java.sql.SQLException;

public class TelaReplicacaoProcessoView extends JFrame {

    private ModoCrudView modoCrud = ModoCrudView.NENHUM;

    private final Connection connection;
    private final ReplicacaoProcessoDAO replicacaoProcessoDao;

    private JTextField txfId;
    private JTextField txfProcesso;
    private JTextField txfDescricao;
    private JCheckBox chkHabilitado;

    private JButton btnSalvar;
    private JButton btnAdicionar;
    private JButton btnBuscar;
    private JButton btnExcluir;

    public TelaReplicacaoProcessoView(Connection connection) throws SQLException {
        this.connection = connection;
        this.replicacaoProcessoDao = new ReplicacaoProcessoDAO(connection);

        setTitle("Cadastro de Processos");
        setSize(620, 320);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(null);

        btnBuscar = new JButton("Buscar");
        btnAdicionar = new JButton("Adicionar");
        btnSalvar = new JButton("Salvar");
        btnExcluir = new JButton("Excluir");

        btnBuscar.setBounds(10, 10, 130, 30);
        btnAdicionar.setBounds(150, 10, 130, 30);
        btnSalvar.setBounds(290, 10, 130, 30);
        btnExcluir.setBounds(430, 10, 130, 30);

        getContentPane().add(btnBuscar);
        getContentPane().add(btnAdicionar);
        getContentPane().add(btnSalvar);
        getContentPane().add(btnExcluir);

        JLabel lbId = new JLabel("Id:");
        lbId.setBounds(10, 70, 120, 25);
        getContentPane().add(lbId);

        txfId = new JTextField();
        txfId.setBounds(140, 70, 200, 25);
        getContentPane().add(txfId);

        JLabel lbProcesso = new JLabel("Processo:");
        lbProcesso.setBounds(10, 105, 120, 25);
        getContentPane().add(lbProcesso);

        txfProcesso = new JTextField();
        txfProcesso.setBounds(140, 105, 420, 25);
        getContentPane().add(txfProcesso);

        JLabel lbDescricao = new JLabel("Descrição:");
        lbDescricao.setBounds(10, 140, 120, 25);
        getContentPane().add(lbDescricao);

        txfDescricao = new JTextField();
        txfDescricao.setBounds(140, 140, 420, 25);
        getContentPane().add(txfDescricao);

        chkHabilitado = new JCheckBox("Habilitado");
        chkHabilitado.setBounds(10, 175, 120, 25);
        getContentPane().add(chkHabilitado);

        addButtonListeners();

        txfId.setEnabled(false);
        txfProcesso.setEnabled(false);
        txfDescricao.setEnabled(false);
        chkHabilitado.setEnabled(false);
        btnSalvar.setEnabled(false);
        btnExcluir.setEnabled(false);
    }

    private void addButtonListeners() {
        btnSalvar.addActionListener(e -> {
            if (txfProcesso.getText().trim().isBlank()) {
                JOptionPane.showMessageDialog(null, "Informe o processo");
            }

            if (txfDescricao.getText().trim().isBlank()) {
                JOptionPane.showMessageDialog(null, "Informe a descrição do processo");
            }

            ReplicacaoProcesso processo = new ReplicacaoProcesso();
            processo.setProcesso(txfProcesso.getText().trim());
            processo.setDescricao(txfDescricao.getText().trim());
            processo.setHabilitado(chkHabilitado.isSelected());

            switch (modoCrud) {
                case ModoCrudView.INSERT -> {
                    try {
                        replicacaoProcessoDao.insertProcesso(processo);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Erro ao salvar o processo: %s".formatted(ex.getLocalizedMessage()));
                    }
                }
                case ModoCrudView.UPDATE -> {
                    if (txfId.getText().trim().isBlank()) {
                        JOptionPane.showMessageDialog(null, "Id não informado para realizar a atualização.");
                        return;
                    }

                    try {
                        processo.setId(Integer.parseInt(txfId.getText()));
                        replicacaoProcessoDao.updateProcesso(processo);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Erro ao atualizar o processo: %s".formatted(ex.getLocalizedMessage()));
                    }
                }
                default -> {
                    JOptionPane.showMessageDialog(this, "Selecione Adicionar ou Buscar antes de salvar.");
                    return;
                }
            }

            modoCrud = ModoCrudView.NENHUM;
            txfId.setText("");
            txfProcesso.setText("");
            txfDescricao.setText("");
            chkHabilitado.setSelected(false);

            txfProcesso.setEnabled(false);
            txfDescricao.setEnabled(false);
            chkHabilitado.setEnabled(false);
            btnSalvar.setEnabled(false);
            btnExcluir.setEnabled(false);
        });

        btnAdicionar.addActionListener(e -> {
            modoCrud = ModoCrudView.INSERT;

            txfId.setText("");
            txfProcesso.setText("");
            txfDescricao.setText("");
            chkHabilitado.setSelected(true);

            txfProcesso.setEnabled(true);
            txfDescricao.setEnabled(true);
            chkHabilitado.setEnabled(true);
            btnSalvar.setEnabled(true);
        });

        btnExcluir.addActionListener(e -> {
            if (txfId.getText().trim().isBlank()) {
                JOptionPane.showMessageDialog(null, "Id não informado para realizar a deleção.");
                return;
            }

            if (JOptionPane.showConfirmDialog(this, "Deseja realmente remover o registro?", "Exclusão", JOptionPane.YES_NO_OPTION) == 2) {
                return;
            }

            try {
                replicacaoProcessoDao.deleteProcesso(Integer.parseInt(txfId.getText()));
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Erro ao deletar o processo: %s".formatted(ex.getLocalizedMessage()));
            }

            modoCrud = ModoCrudView.NENHUM;

            txfId.setText("");
            txfProcesso.setText("");
            txfDescricao.setText("");
            chkHabilitado.setSelected(false);

            txfProcesso.setEnabled(false);
            txfDescricao.setEnabled(false);
            chkHabilitado.setEnabled(false);
            btnSalvar.setEnabled(false);
            btnExcluir.setEnabled(false);
        });

        btnBuscar.addActionListener(e -> {
            try {
                ConsultaReplicacaoProcessoDialog consultaDialog = new ConsultaReplicacaoProcessoDialog(this, replicacaoProcessoDao);
                consultaDialog.setVisible(true);

                if (consultaDialog.getSelecionado() == null) {
                    return;
                }

                modoCrud = ModoCrudView.UPDATE;
                txfId.setText(String.valueOf(consultaDialog.getSelecionado().getId()));
                txfProcesso.setText(consultaDialog.getSelecionado().getProcesso());
                txfDescricao.setText(consultaDialog.getSelecionado().getDescricao());
                chkHabilitado.setSelected(consultaDialog.getSelecionado().isHabilitado());

                txfProcesso.setEnabled(true);
                txfDescricao.setEnabled(true);
                chkHabilitado.setEnabled(true);
                btnSalvar.setEnabled(true);
                btnExcluir.setEnabled(true);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Erro ao carregar o processo: %s".formatted(ex.getLocalizedMessage()));
            }
        });
    }
}
