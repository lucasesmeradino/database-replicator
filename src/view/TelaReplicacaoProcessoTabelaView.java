package view;

import database.dao.ReplicacaoProcessoDAO;
import database.dao.ReplicacaoProcessoTabelaDAO;
import database.model.ReplicacaoProcesso;
import database.model.ReplicacaoProcessoTabela;
import view.dialog.ConsultaReplicacaoProcessoTabelaDialog;

import javax.swing.*;
import java.sql.Connection;
import java.sql.SQLException;

public class TelaReplicacaoProcessoTabelaView extends JFrame {

    private ModoCrudView modoCrud = ModoCrudView.NENHUM;

    private final Connection connection;
    private final ReplicacaoProcessoTabelaDAO replicacaoProcessoTabelaDao;
    private final ReplicacaoProcessoDAO replicacaoProcessoDao;

    private JTextField txfId;
    private JComboBox<ReplicacaoProcesso> cbxProcesso;
    private JTextField txfTabelaOrigem;
    private JTextField txfTabelaDestino;
    private JTextField txfOrdem;
    private JCheckBox chkHabilitado;
    private JTextArea txtWhere;

    private JButton btnSalvar;
    private JButton btnAdicionar;
    private JButton btnBuscar;
    private JButton btnExcluir;

    public TelaReplicacaoProcessoTabelaView(Connection connection) throws SQLException {
        this.connection = connection;
        this.replicacaoProcessoTabelaDao = new ReplicacaoProcessoTabelaDAO(connection);
        this.replicacaoProcessoDao = new ReplicacaoProcessoDAO(connection);

        setTitle("Cadastro de Tabelas");
        setSize(760, 460);
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
        lbId.setBounds(10, 70, 140, 25);
        getContentPane().add(lbId);

        txfId = new JTextField();
        txfId.setBounds(160, 70, 220, 25);
        getContentPane().add(txfId);

        JLabel lbProcesso = new JLabel("Processo:");
        lbProcesso.setBounds(10, 105, 140, 25);
        getContentPane().add(lbProcesso);

        cbxProcesso = new JComboBox<>();
        cbxProcesso.setBounds(160, 105, 520, 25);
        getContentPane().add(cbxProcesso);

        JLabel lbTabelaOrigem = new JLabel("Tabela Origem:");
        lbTabelaOrigem.setBounds(10, 140, 140, 25);
        getContentPane().add(lbTabelaOrigem);

        txfTabelaOrigem = new JTextField();
        txfTabelaOrigem.setBounds(160, 140, 520, 25);
        getContentPane().add(txfTabelaOrigem);

        JLabel lbTabelaDestino = new JLabel("Tabela Destino:");
        lbTabelaDestino.setBounds(10, 175, 140, 25);
        getContentPane().add(lbTabelaDestino);

        txfTabelaDestino = new JTextField();
        txfTabelaDestino.setBounds(160, 175, 520, 25);
        getContentPane().add(txfTabelaDestino);

        JLabel lbOrdem = new JLabel("Ordem:");
        lbOrdem.setBounds(10, 210, 140, 25);
        getContentPane().add(lbOrdem);

        txfOrdem = new JTextField();
        txfOrdem.setBounds(160, 210, 220, 25);
        getContentPane().add(txfOrdem);

        chkHabilitado = new JCheckBox("Habilitado");
        chkHabilitado.setBounds(10, 245, 140, 25);
        getContentPane().add(chkHabilitado);

        JLabel lbWhere = new JLabel("Where:");
        lbWhere.setBounds(10, 280, 140, 25);
        getContentPane().add(lbWhere);

        txtWhere = new JTextArea();
        txtWhere.setBounds(160, 280, 520, 80);
        getContentPane().add(txtWhere);

        // Carregamento das opções da combo de processos
        cbxProcesso.removeAllItems();
        replicacaoProcessoDao.selectAllProcessos().forEach(cbxProcesso::addItem);

        txfId.setEnabled(false);
        cbxProcesso.setEnabled(false);
        txfTabelaOrigem.setEnabled(false);
        txfTabelaDestino.setEnabled(false);
        txfOrdem.setEnabled(false);
        chkHabilitado.setEnabled(false);
        txtWhere.setEnabled(false);
        btnSalvar.setEnabled(false);
        btnExcluir.setEnabled(false);

        addButtonListeners();
    }

    private void addButtonListeners() {
        btnSalvar.addActionListener(e -> {
            if (cbxProcesso.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this, "Selecione um processo.");
                return;
            }

            if (txfTabelaOrigem.getText().trim().isBlank()) {
                JOptionPane.showMessageDialog(this, "Informe a tabela de origem.");
                return;
            }

            if (txfTabelaDestino.getText().trim().isBlank()) {
                JOptionPane.showMessageDialog(this, "Informe a tabela de destino.");
                return;
            }

            if (txfOrdem.getText().trim().isBlank()) {
                JOptionPane.showMessageDialog(this, "Informe a ordem da tabela.");
                return;
            }

            if (txtWhere.getText().trim().isBlank()) {
                JOptionPane.showMessageDialog(this, "Informe o where da tabela.");
                return;
            }

            ReplicacaoProcessoTabela tabela = new ReplicacaoProcessoTabela();
            tabela.setProcessoId(((ReplicacaoProcesso) cbxProcesso.getSelectedItem()).getId());
            tabela.setTabelaOrigem(txfTabelaOrigem.getText().trim());
            tabela.setTabelaDestino(txfTabelaDestino.getText().trim());
            tabela.setOrdem(Integer.parseInt(txfOrdem.getText().trim()));
            tabela.setDsWhere(txtWhere.getText().trim());
            tabela.setHabilitado(chkHabilitado.isSelected());

            switch (modoCrud) {
                case ModoCrudView.INSERT -> {
                    try {
                        replicacaoProcessoTabelaDao.insertTabela(tabela);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Erro ao salvar o tabela: %s".formatted(ex.getLocalizedMessage()));
                    }
                }
                case ModoCrudView.UPDATE -> {
                    if (txfId.getText().trim().isBlank()) {
                        JOptionPane.showMessageDialog(null, "Id não informado para realizar a atualização.");
                        return;
                    }

                    try {
                        tabela.setId(Integer.parseInt(txfId.getText()));
                        replicacaoProcessoTabelaDao.updateTabela(tabela);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Erro ao atualizar a tabela: %s".formatted(ex.getLocalizedMessage()));
                    }
                }
                default -> {
                    JOptionPane.showMessageDialog(this, "Selecione Adicionar ou Buscar antes de salvar.");
                    return;
                }
            }

            modoCrud = ModoCrudView.NENHUM;
            txfId.setText("");
            if (cbxProcesso.getItemCount() > 0) cbxProcesso.setSelectedIndex(0);
            txfTabelaOrigem.setText("");
            txfTabelaDestino.setText("");
            txfOrdem.setText("");
            txtWhere.setText("");
            chkHabilitado.setSelected(false);

            cbxProcesso.setEnabled(false);
            txfTabelaOrigem.setEnabled(false);
            txfTabelaDestino.setEnabled(false);
            txfOrdem.setEnabled(false);
            txtWhere.setEnabled(false);
            chkHabilitado.setEnabled(false);
            btnSalvar.setEnabled(false);
            btnExcluir.setEnabled(false);
        });

        btnAdicionar.addActionListener(e -> {
            modoCrud = ModoCrudView.INSERT;

            txfId.setText("");
            if (cbxProcesso.getItemCount() > 0) cbxProcesso.setSelectedIndex(0);
            txfTabelaOrigem.setText("");
            txfTabelaDestino.setText("");
            txfOrdem.setText("");
            txtWhere.setText("");
            chkHabilitado.setSelected(true);

            cbxProcesso.setEnabled(true);
            txfTabelaOrigem.setEnabled(true);
            txfTabelaDestino.setEnabled(true);
            txfOrdem.setEnabled(true);
            txtWhere.setEnabled(true);
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
                replicacaoProcessoTabelaDao.deleteTabela(Integer.parseInt(txfId.getText()));
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Erro ao deletar a tabela: %s".formatted(ex.getLocalizedMessage()));
            }

            modoCrud = ModoCrudView.NENHUM;

            txfId.setText("");
            if (cbxProcesso.getItemCount() > 0) cbxProcesso.setSelectedIndex(0);
            txfTabelaOrigem.setText("");
            txfTabelaDestino.setText("");
            txfOrdem.setText("");
            txtWhere.setText("");
            chkHabilitado.setSelected(false);

            cbxProcesso.setEnabled(false);
            txfTabelaOrigem.setEnabled(false);
            txfTabelaDestino.setEnabled(false);
            txfOrdem.setEnabled(false);
            txtWhere.setEnabled(false);
            chkHabilitado.setEnabled(false);
            btnSalvar.setEnabled(false);
            btnExcluir.setEnabled(false);
        });

        btnBuscar.addActionListener(e -> {
            try {
                ConsultaReplicacaoProcessoTabelaDialog consultaDialog = new ConsultaReplicacaoProcessoTabelaDialog(this, replicacaoProcessoTabelaDao);
                consultaDialog.setVisible(true);

                if (consultaDialog.getSelecionado() == null) return;

                ReplicacaoProcessoTabela tabelaSelecionada = consultaDialog.getSelecionado();
                txfId.setText(String.valueOf(tabelaSelecionada.getId()));
                txfTabelaOrigem.setText(tabelaSelecionada.getTabelaOrigem());
                txfTabelaDestino.setText(tabelaSelecionada.getTabelaDestino());
                txfOrdem.setText(String.valueOf(tabelaSelecionada.getOrdem()));
                chkHabilitado.setSelected(tabelaSelecionada.isHabilitado());
                txtWhere.setText(tabelaSelecionada.getDsWhere());

                modoCrud = ModoCrudView.UPDATE;
                cbxProcesso.setEnabled(true);
                txfTabelaOrigem.setEnabled(true);
                txfTabelaDestino.setEnabled(true);
                txfOrdem.setEnabled(true);
                txtWhere.setEnabled(true);
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
