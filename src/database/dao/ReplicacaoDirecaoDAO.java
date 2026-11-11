package database.dao;

import database.model.ReplicacaoDirecao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReplicacaoDirecaoDAO {

    private static final String SQL_SELECT_ALL_HABILITADO_BY_PROCESSO = "SELECT * FROM tb_replicacao_direcao WHERE habilitado = true AND processo_id = ?";

    private static final String SQL_SELECT_ALL = "SELECT * FROM tb_replicacao_direcao";

    private static final String SQL_SELECT_BY_ID = "SELECT * FROM tb_replicacao_direcao WHERE id = ?";

    private static final String SQL_INSERT = "INSERT INTO tb_replicacao_direcao (processo_id, conexao_origem, conexao_destino, usuario_origem, usuario_destino, senha_origem, senha_destino, habilitado) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_UPDATE = "UPDATE tb_replicacao_direcao SET processo_id = ?, conexao_origem = ?, conexao_destino = ?, usuario_origem = ?, usuario_destino = ?, senha_origem = ?, senha_destino = ?, habilitado = ? WHERE id = ?";

    private static final String SQL_DELETE = "DELETE FROM tb_replicacao_direcao WHERE id = ?";

    private final Connection conn;

    public ReplicacaoDirecaoDAO(Connection conn) {
        this.conn = conn;
    }

    public List<ReplicacaoDirecao> selectAllDirecoesHabilitadasByProcesso(long processoId) throws SQLException {
        try (PreparedStatement psSelectAll = conn.prepareStatement(SQL_SELECT_ALL_HABILITADO_BY_PROCESSO)) {
            psSelectAll.setLong(1, processoId);
            List<ReplicacaoDirecao> replicacaoDirecoes = new ArrayList<>();
            try (ResultSet rsReplicacaoDirecoes = psSelectAll.executeQuery()) {
                while (rsReplicacaoDirecoes.next()) {
                    replicacaoDirecoes.add(mapToModel(rsReplicacaoDirecoes));
                }
            }
            return replicacaoDirecoes;
        }
    }

    public List<ReplicacaoDirecao> selectAllDirecoes() throws SQLException {
        try (PreparedStatement psSelectAll = conn.prepareStatement(SQL_SELECT_ALL)) {
            List<ReplicacaoDirecao> replicacaoDirecaos = new ArrayList<>();
            try (ResultSet rsReplicacaoDirecoes = psSelectAll.executeQuery()) {
                while (rsReplicacaoDirecoes.next()) {
                    replicacaoDirecaos.add(mapToModel(rsReplicacaoDirecoes));
                }
            }
            return replicacaoDirecaos;
        }
    }

    public ReplicacaoDirecao selectDirecaoById(long processoId) throws SQLException {
        try (PreparedStatement psSelectById = conn.prepareStatement(SQL_SELECT_BY_ID)) {
            psSelectById.setLong(1, processoId);
            try (ResultSet rsReplicacaoDirecao = psSelectById.executeQuery()) {
                return rsReplicacaoDirecao.next() ? mapToModel(rsReplicacaoDirecao) : null;
            }
        }
    }

    public void insertDirecao(ReplicacaoDirecao processoDirecao) throws SQLException {
        try (PreparedStatement psInsert = conn.prepareStatement(SQL_INSERT)) {
            psInsert.setLong(1, processoDirecao.getProcessoId());
            psInsert.setString(2, processoDirecao.getConexaoOrigem());
            psInsert.setString(3, processoDirecao.getConexaoDestino());
            psInsert.setString(4, processoDirecao.getUsuarioOrigem());
            psInsert.setString(5, processoDirecao.getUsuarioDestino());
            psInsert.setString(6, processoDirecao.getSenhaOrigem());
            psInsert.setString(7, processoDirecao.getSenhaDestino());
            psInsert.setBoolean(8, processoDirecao.isHabilitado());
            psInsert.executeUpdate();
        }
    }

    public void updateDirecao(ReplicacaoDirecao processoTabela) throws SQLException {
        try (PreparedStatement psUpdate = conn.prepareStatement(SQL_UPDATE)) {
            psUpdate.setLong(1, processoTabela.getProcessoId());
            psUpdate.setString(2, processoTabela.getConexaoOrigem());
            psUpdate.setString(3, processoTabela.getConexaoDestino());
            psUpdate.setString(4, processoTabela.getUsuarioOrigem());
            psUpdate.setString(5, processoTabela.getUsuarioDestino());
            psUpdate.setString(6, processoTabela.getSenhaOrigem());
            psUpdate.setString(7, processoTabela.getSenhaDestino());
            psUpdate.setBoolean(8, processoTabela.isHabilitado());
            psUpdate.setLong(9, processoTabela.getId());
            psUpdate.executeUpdate();
        }
    }

    public void deleteDirecao(long processoId) throws SQLException {
        try (PreparedStatement psDelete = conn.prepareStatement(SQL_DELETE)) {
            psDelete.setLong(1, processoId);
            psDelete.executeUpdate();
        }
    }

    private ReplicacaoDirecao mapToModel(ResultSet rs) throws SQLException {
        ReplicacaoDirecao replicacaoDirecao = new ReplicacaoDirecao();
        replicacaoDirecao.setId(rs.getLong("id"));
        replicacaoDirecao.setProcessoId(rs.getLong("processo_id"));
        replicacaoDirecao.setConexaoOrigem(rs.getString("conexao_origem"));
        replicacaoDirecao.setConexaoDestino(rs.getString("conexao_destino"));
        replicacaoDirecao.setUsuarioOrigem(rs.getString("usuario_origem"));
        replicacaoDirecao.setUsuarioDestino(rs.getString("usuario_origem"));
        replicacaoDirecao.setSenhaOrigem(rs.getString("senha_origem"));
        replicacaoDirecao.setSenhaDestino(rs.getString("senha_destino"));
        replicacaoDirecao.setHabilitado(rs.getBoolean("habilitado"));
        return replicacaoDirecao;
    }
}
