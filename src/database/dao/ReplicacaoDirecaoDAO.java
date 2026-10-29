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

    private static final String SQL_SELECT_BY_ID = "SELECT * FROM tb_replicacao_direcao WHERE id = ?";

    private static final String SQL_INSERT = "INSERT INTO tb_replicacao_direcao (processo_id, conexao_origem, conexao_destino, usuario_origem, usuario_destino, senha_origem, senha_destino, habilitado) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_UPDATE = "UPDATE tb_replicacao_direcao SET processo_id = ?, conexao_origem = ?, conexao_destino = ?, usuario_origem = ?, usuario_destino = ?, senha_origem = ?, senha_destino = ?, habilitado = ? WHERE id = ?";

    private static final String SQL_DELETE = "DELETE FROM tb_replicacao_direcao WHERE id = ?";

    private final Connection conn;

    private final PreparedStatement psSqlSelectAllHabilitadoByProcesso;
    private final PreparedStatement psSqlSelectById;
    private final PreparedStatement psSqlInsert;
    private final PreparedStatement psSqlUpdate;
    private final PreparedStatement psSqlDelete;

    public ReplicacaoDirecaoDAO(Connection conn) throws SQLException {
        this.conn = conn;
        this.psSqlSelectAllHabilitadoByProcesso = conn.prepareStatement(SQL_SELECT_ALL_HABILITADO_BY_PROCESSO);
        this.psSqlSelectById = conn.prepareStatement(SQL_SELECT_BY_ID);
        this.psSqlInsert = conn.prepareStatement(SQL_INSERT);
        this.psSqlUpdate = conn.prepareStatement(SQL_UPDATE);
        this.psSqlDelete = conn.prepareStatement(SQL_DELETE);
    }

    public List<ReplicacaoDirecao> selectAllDirecoesHabilitadasByProcesso(long processoId) throws SQLException {
        psSqlSelectAllHabilitadoByProcesso.setLong(1, processoId);
        List<ReplicacaoDirecao> replicacaoDirecoes = new ArrayList<>();
        try (ResultSet rsReplicacaoDirecoes = psSqlSelectAllHabilitadoByProcesso.executeQuery()) {
            while (rsReplicacaoDirecoes.next()) {
                replicacaoDirecoes.add(mapToModel(rsReplicacaoDirecoes));
            }
        }
        return replicacaoDirecoes;
    }

    public ReplicacaoDirecao selectDirecaoById(long processoId) throws SQLException {
        psSqlSelectById.setLong(1, processoId);
        try (ResultSet rsReplicacaoDirecao = psSqlSelectById.executeQuery()) {
            return rsReplicacaoDirecao.next() ? mapToModel(rsReplicacaoDirecao) : null;
        }
    }

    public void insertDirecao(ReplicacaoDirecao processoTabela) throws SQLException {
        psSqlUpdate.setLong(1, processoTabela.getProcessoId());
        psSqlUpdate.setString(2, processoTabela.getConexaoOrigem());
        psSqlUpdate.setString(3, processoTabela.getConexaoDestino());
        psSqlUpdate.setString(4, processoTabela.getUsuarioOrigem());
        psSqlUpdate.setString(5, processoTabela.getUsuarioDestino());
        psSqlUpdate.setString(6, processoTabela.getSenhaOrigem());
        psSqlUpdate.setString(7, processoTabela.getSenhaDestino());
        psSqlUpdate.setBoolean(8, processoTabela.isHabilitado());
        psSqlInsert.executeUpdate();
    }

    public void updateDirecao(ReplicacaoDirecao processoTabela) throws SQLException {
        psSqlUpdate.setLong(1, processoTabela.getProcessoId());
        psSqlUpdate.setString(2, processoTabela.getConexaoOrigem());
        psSqlUpdate.setString(3, processoTabela.getConexaoDestino());
        psSqlUpdate.setString(4, processoTabela.getUsuarioOrigem());
        psSqlUpdate.setString(5, processoTabela.getUsuarioDestino());
        psSqlUpdate.setString(6, processoTabela.getSenhaOrigem());
        psSqlUpdate.setString(7, processoTabela.getSenhaDestino());
        psSqlUpdate.setBoolean(8, processoTabela.isHabilitado());
        psSqlUpdate.setLong(9, processoTabela.getId());
        psSqlUpdate.executeUpdate();
    }

    public void deleteDirecao(long processoId) throws SQLException {
        psSqlDelete.setLong(1, processoId);
        psSqlDelete.executeUpdate();
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
