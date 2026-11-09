package view;

import database.dao.ReplicacaoDirecaoDAO;
import database.dao.ReplicacaoProcessoDAO;
import database.model.ReplicacaoDirecao;
import database.model.ReplicacaoProcesso;
import view.dialog.ConsultaReplicacaoProcessoDirecaoDialog;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;

public class TelaReplicacaoProcessoDirecaoView extends JFrame {

    private ModoCrudView modoCrud = ModoCrudView.NENHUM;

    private final Connection connection;
    private final ReplicacaoDirecaoDAO replicacaoDirecaoDao;
    private final ReplicacaoProcessoDAO replicacaoProcessoDao;

    private JTextField txfId;
    private JComboBox<ReplicacaoProcesso> cbxProcesso;
    private JTextField txfConexaoOrigem;
    private JTextField txfConexaoDestino;
    private JTextField txfUsuarioOrigem;
    private JTextField txfUsuarioDestino;
    private JPasswordField txfSenhaOrigem;
    private JPasswordField txfSenhaDestino;
    private JCheckBox chkHabilitado;

    private JButton btnSalvar;
    private JButton btnAdicionar;
    private JButton btnBuscar;
    private JButton btnExcluir;

    public TelaReplicacaoProcessoDirecaoView(Connection connection) throws SQLException {
        this.connection = connection;
        this.replicacaoDirecaoDao = new ReplicacaoDirecaoDAO(connection);
        this.replicacaoProcessoDao = new ReplicacaoProcessoDAO(connection);

        setTitle("Cadastro de Direções");
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
        txfId.setBounds(70, 70, 220, 25);
        getContentPane().add(txfId);

        JLabel lbProcesso = new JLabel("Processo:");
        lbProcesso.setBounds(10, 105, 140, 25);
        getContentPane().add(lbProcesso);

        cbxProcesso = new JComboBox<>();
        cbxProcesso.setBounds(70, 105, 520, 25);
        getContentPane().add(cbxProcesso);

        JLabel lbOrigem = new JLabel("Origem");
        lbOrigem.setBounds(280, 150, 200, 25);
        lbOrigem.setFont(lbOrigem.getFont().deriveFont(Font.BOLD));
        getContentPane().add(lbOrigem);

        JLabel lbConexaoOrigem = new JLabel("Conexão:");
        lbConexaoOrigem.setBounds(10, 195, 140, 25);
        getContentPane().add(lbConexaoOrigem);

        txfConexaoOrigem = new JTextField();
        txfConexaoOrigem.setBounds(70, 195, 510, 25);
        getContentPane().add(txfConexaoOrigem);

        JLabel lbUsuarioOrigem = new JLabel("Usuário:");
        lbUsuarioOrigem.setBounds(10, 230, 140, 25);
        getContentPane().add(lbUsuarioOrigem);

        txfUsuarioOrigem = new JTextField();
        txfUsuarioOrigem.setBounds(70, 230, 220, 25);
        getContentPane().add(txfUsuarioOrigem);

        JLabel lbSenhaOrigem = new JLabel("Senha:");
        lbSenhaOrigem.setBounds(300, 230, 140, 25);
        getContentPane().add(lbSenhaOrigem);

        txfSenhaOrigem = new JPasswordField();
        txfSenhaOrigem.setBounds(360, 230, 220, 25);
        getContentPane().add(txfSenhaOrigem);

        JLabel lbDestino = new JLabel("Destino");
        lbDestino.setBounds(280, 275, 200, 25);
        lbDestino.setFont(lbDestino.getFont().deriveFont(Font.BOLD));
        getContentPane().add(lbDestino);

        JLabel lbConexaoDestino = new JLabel("Conexão:");
        lbConexaoDestino.setBounds(10, 320, 140, 25);
        getContentPane().add(lbConexaoDestino);

        txfConexaoDestino = new JTextField();
        txfConexaoDestino.setBounds(70, 320, 510, 25);
        getContentPane().add(txfConexaoDestino);

        JLabel lbUsuarioDestino = new JLabel("Usuário:");
        lbUsuarioDestino.setBounds(10, 355, 140, 25);
        getContentPane().add(lbUsuarioDestino);

        txfUsuarioDestino = new JTextField();
        txfUsuarioDestino.setBounds(70, 355, 220, 25);
        getContentPane().add(txfUsuarioDestino);

        JLabel lbSenhaDestino = new JLabel("Senha:");
        lbSenhaDestino.setBounds(300, 355, 140, 25);
        getContentPane().add(lbSenhaDestino);

        txfSenhaDestino = new JPasswordField();
        txfSenhaDestino.setBounds(360, 355, 220, 25);
        getContentPane().add(txfSenhaDestino);

        chkHabilitado = new JCheckBox("Habilitado");
        chkHabilitado.setBounds(10, 390, 140, 25);
        getContentPane().add(chkHabilitado);

        // Carregamento das opções da combo de processos
        cbxProcesso.removeAllItems();
        replicacaoProcessoDao.selectAllProcessos().forEach(cbxProcesso::addItem);

        txfId.setEnabled(false);
        cbxProcesso.setEnabled(false);
        txfConexaoOrigem.setEnabled(false);
        txfConexaoDestino.setEnabled(false);
        txfUsuarioOrigem.setEnabled(false);
        txfUsuarioDestino.setEnabled(false);
        txfSenhaOrigem.setEnabled(false);
        txfSenhaDestino.setEnabled(false);
        chkHabilitado.setEnabled(false);
        btnSalvar.setEnabled(false);
        btnExcluir.setEnabled(false);

        addButtonListeners();
    }

    private void addButtonListeners() {
        btnAdicionar.addActionListener(e -> {
            modoCrud = ModoCrudView.INSERT;

            txfId.setText("");
            if (cbxProcesso.getItemCount() > 0) cbxProcesso.setSelectedIndex(0);
            txfConexaoOrigem.setText("");
            txfConexaoDestino.setText("");
            txfUsuarioOrigem.setText("");
            txfUsuarioDestino.setText("");
            txfSenhaOrigem.setText("");
            txfSenhaDestino.setText("");
            chkHabilitado.setSelected(true);

            cbxProcesso.setEnabled(true);
            txfConexaoOrigem.setEnabled(true);
            txfConexaoDestino.setEnabled(true);
            txfUsuarioOrigem.setEnabled(true);
            txfUsuarioDestino.setEnabled(true);
            txfSenhaOrigem.setEnabled(true);
            txfSenhaDestino.setEnabled(true);
            chkHabilitado.setEnabled(true);
            btnSalvar.setEnabled(true);
        });

        btnSalvar.addActionListener(e -> {
            if (cbxProcesso.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(this, "Selecione um processo.");
                return;
            }

            if (txfConexaoOrigem.getText().trim().isBlank()) {
                JOptionPane.showMessageDialog(this, "Informe a conexão de origem.");
                return;
            }

            if (txfConexaoDestino.getText().trim().isBlank()) {
                JOptionPane.showMessageDialog(this, "Informe a conexão de destino.");
                return;
            }

            if (txfUsuarioOrigem.getText().trim().isBlank()) {
                JOptionPane.showMessageDialog(this, "Informe o usuário da origem.");
                return;
            }

            if (txfUsuarioDestino.getText().trim().isBlank()) {
                JOptionPane.showMessageDialog(this, "Informe o usuário da destino.");
                return;
            }

            if (txfSenhaOrigem.getText().trim().isBlank()) {
                JOptionPane.showMessageDialog(this, "Informe a senha da origem.");
                return;
            }

            if (txfSenhaDestino.getText().trim().isBlank()) {
                JOptionPane.showMessageDialog(this, "Informe a senha do destino.");
                return;
            }

            ReplicacaoDirecao direcao = new ReplicacaoDirecao();
            direcao.setProcessoId(((ReplicacaoProcesso) cbxProcesso.getSelectedItem()).getId());
            direcao.setUsuarioOrigem(txfUsuarioOrigem.getText().trim());
            direcao.setUsuarioDestino(txfUsuarioDestino.getText().trim());
            direcao.setConexaoOrigem(txfConexaoOrigem.getText().trim());
            direcao.setConexaoDestino(txfConexaoDestino.getText().trim());
            direcao.setSenhaOrigem(txfSenhaOrigem.getText().trim());
            direcao.setSenhaDestino(txfSenhaDestino.getText().trim());
            direcao.setHabilitado(chkHabilitado.isSelected());

            switch (modoCrud) {
                case ModoCrudView.INSERT -> {
                    try {
                        replicacaoDirecaoDao.insertDirecao(direcao);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(this, "Erro ao tentar inserir a direção: %s.".formatted(ex.getLocalizedMessage()));
                        return;
                    }
                }
                case ModoCrudView.UPDATE -> {
                    if (txfId.getText().trim().isBlank()) {
                        JOptionPane.showMessageDialog(this, "Necessário um Id para atualização da direção.");
                        return;
                    }
                    try {
                        direcao.setId(Long.parseLong(txfId.getText().trim()));
                        replicacaoDirecaoDao.updateDirecao(direcao);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(this, "Erro ao tentar inserir a direção: %s.".formatted(ex.getLocalizedMessage()));
                        return;
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
            txfConexaoOrigem.setText("");
            txfConexaoDestino.setText("");
            txfUsuarioOrigem.setText("");
            txfUsuarioDestino.setText("");
            txfSenhaOrigem.setText("");
            txfSenhaDestino.setText("");
            chkHabilitado.setSelected(false);

            cbxProcesso.setEnabled(false);
            txfConexaoOrigem.setEnabled(false);
            txfConexaoDestino.setEnabled(false);
            txfUsuarioOrigem.setEnabled(false);
            txfUsuarioDestino.setEnabled(false);
            txfSenhaOrigem.setEnabled(false);
            txfSenhaDestino.setEnabled(false);
            chkHabilitado.setEnabled(false);
            btnSalvar.setEnabled(false);
            btnExcluir.setEnabled(false);
        });

        btnExcluir.addActionListener(e -> {
            if (txfId.getText().trim().isBlank()) {
                JOptionPane.showMessageDialog(this, "Necessário um Id para deleção da direção.");
                return;
            }

            if (JOptionPane.showConfirmDialog(this, "Deseja realmente remover o registro?", "Exclusão", JOptionPane.YES_NO_OPTION) == 2) {
                return;
            }

            try {
                replicacaoDirecaoDao.deleteDirecao(Long.parseLong(txfId.getText().trim()));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao tentar inserir a direção: %s.".formatted(ex.getLocalizedMessage()));
                return;
            }

            modoCrud = ModoCrudView.NENHUM;
            txfId.setText("");
            if (cbxProcesso.getItemCount() > 0) cbxProcesso.setSelectedIndex(0);
            txfConexaoOrigem.setText("");
            txfConexaoDestino.setText("");
            txfUsuarioOrigem.setText("");
            txfUsuarioDestino.setText("");
            txfSenhaOrigem.setText("");
            txfSenhaDestino.setText("");
            chkHabilitado.setSelected(false);

            cbxProcesso.setEnabled(false);
            txfConexaoOrigem.setEnabled(false);
            txfConexaoDestino.setEnabled(false);
            txfUsuarioOrigem.setEnabled(false);
            txfUsuarioDestino.setEnabled(false);
            txfSenhaOrigem.setEnabled(false);
            txfSenhaDestino.setEnabled(false);
            chkHabilitado.setEnabled(false);
            btnSalvar.setEnabled(false);
            btnExcluir.setEnabled(false);
        });

        btnBuscar.addActionListener(e -> {
            try {
                ConsultaReplicacaoProcessoDirecaoDialog consultaDialog = new ConsultaReplicacaoProcessoDirecaoDialog(this, replicacaoDirecaoDao);
                consultaDialog.setVisible(true);

                if (consultaDialog.getSelecionado() == null) return;

                modoCrud = ModoCrudView.UPDATE;

                txfId.setText(String.valueOf(consultaDialog.getSelecionado().getId()));
                cbxProcesso.setSelectedItem(new ReplicacaoProcesso(consultaDialog.getSelecionado().getProcessoId()));
                txfConexaoOrigem.setText(consultaDialog.getSelecionado().getConexaoOrigem());
                txfConexaoDestino.setText(consultaDialog.getSelecionado().getConexaoDestino());
                txfUsuarioOrigem.setText(consultaDialog.getSelecionado().getUsuarioOrigem());
                txfUsuarioDestino.setText(consultaDialog.getSelecionado().getUsuarioDestino());
                txfSenhaOrigem.setText(consultaDialog.getSelecionado().getSenhaOrigem());
                txfSenhaDestino.setText(consultaDialog.getSelecionado().getSenhaDestino());
                chkHabilitado.setSelected(consultaDialog.getSelecionado().isHabilitado());

                cbxProcesso.setEnabled(true);
                txfConexaoOrigem.setEnabled(true);
                txfConexaoDestino.setEnabled(true);
                txfUsuarioOrigem.setEnabled(true);
                txfUsuarioDestino.setEnabled(true);
                txfSenhaOrigem.setEnabled(true);
                txfSenhaDestino.setEnabled(true);
                chkHabilitado.setEnabled(true);
                btnSalvar.setEnabled(true);
                btnExcluir.setEnabled(true);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Erro ao carregar a direção: %s".formatted(ex.getLocalizedMessage()));
            }
        });
    }

}
