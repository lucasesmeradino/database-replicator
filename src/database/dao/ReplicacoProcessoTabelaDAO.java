package database.dao;

import database.model.ReplicacaoProcessoTabela;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReplicacoProcessoTabelaDAO {

    private static final String SQL_SELECT_ALL_HABILITADO_BY_PROCESSO = "SELECT * FROM tb_replicacao_processo_tabela WHERE habilitado = true AND processo_id = ?";

    private static final String SQL_SELECT_BY_ID = "SELECT * FROM tb_replicacao_processo_tabela WHERE id = ?";

    private static final String SQL_INSERT = "INSERT INTO tb_replicacao_processo_tabela (processo_id, tabela_origem, tabela_destino, ordem, habilitado, ds_where) VALUES (?, ?, ?, ?, ?, ?)";

    private static final String SQL_UPDATE = "UPDATE tb_replicacao_processo_tabela SET processo_id = ?, tabela_origem = ?, tabela_destino = ?, ordem = ?, habilitado = ?, ds_where = ? WHERE id = ?";

    private static final String SQL_DELETE = "DELETE FROM tb_replicacao_processo_tabela WHERE id = ?";

    private final Connection conn;

    private final PreparedStatement psSqlSelectAllHabilitadoByProcesso;
    private final PreparedStatement psSqlSelectById;
    private final PreparedStatement psSqlInsert;
    private final PreparedStatement psSqlUpdate;
    private final PreparedStatement psSqlDelete;

    public ReplicacoProcessoTabelaDAO(Connection conn) throws SQLException {
        this.conn = conn;
        this.psSqlSelectAllHabilitadoByProcesso = conn.prepareStatement(SQL_SELECT_ALL_HABILITADO_BY_PROCESSO);
        this.psSqlSelectById = conn.prepareStatement(SQL_SELECT_BY_ID);
        this.psSqlInsert = conn.prepareStatement(SQL_INSERT);
        this.psSqlUpdate = conn.prepareStatement(SQL_UPDATE);
        this.psSqlDelete = conn.prepareStatement(SQL_DELETE);
    }

    public List<ReplicacaoProcessoTabela> selectAllTabelasHabilitadasByProcesso(long processoId) throws SQLException {
        psSqlSelectAllHabilitadoByProcesso.setLong(1, processoId);
        List<ReplicacaoProcessoTabela> processoTabelas = new ArrayList<>();
        try (ResultSet rsProcessoTabelas = psSqlSelectAllHabilitadoByProcesso.executeQuery()) {
            while (rsProcessoTabelas.next()) {
                processoTabelas.add(mapToModel(rsProcessoTabelas));
            }
        }
        return processoTabelas;
    }

    public ReplicacaoProcessoTabela selectTabelaById(long processoId) throws SQLException {
        psSqlSelectById.setLong(1, processoId);
        try (ResultSet rsProcessoTabela = psSqlSelectById.executeQuery()) {
            return rsProcessoTabela.next() ? mapToModel(rsProcessoTabela) : null;
        }
    }

    public void insertTabela(ReplicacaoProcessoTabela processoTabela) throws SQLException {
        psSqlInsert.setLong(1, processoTabela.getProcessoId());
        psSqlInsert.setString(2, processoTabela.getTabelaOrigem());
        psSqlInsert.setString(3, processoTabela.getTabelaDestino());
        psSqlInsert.setInt(4, processoTabela.getOrdem());
        psSqlInsert.setBoolean(5, processoTabela.isHabilitado());
        psSqlInsert.setString(6, processoTabela.getDsWhere());
        psSqlInsert.executeUpdate();
    }

    public void updateTabela(ReplicacaoProcessoTabela processoTabela) throws SQLException {
        psSqlUpdate.setLong(1, processoTabela.getProcessoId());
        psSqlUpdate.setString(2, processoTabela.getTabelaOrigem());
        psSqlUpdate.setString(3, processoTabela.getTabelaDestino());
        psSqlUpdate.setInt(4, processoTabela.getOrdem());
        psSqlUpdate.setBoolean(5, processoTabela.isHabilitado());
        psSqlUpdate.setString(6, processoTabela.getDsWhere());
        psSqlUpdate.setLong(7, processoTabela.getId());
        psSqlUpdate.executeUpdate();
    }

    public void deleteTabela(long processoId) throws SQLException {
        psSqlDelete.setLong(1, processoId);
        psSqlDelete.executeUpdate();
    }

    private ReplicacaoProcessoTabela mapToModel(ResultSet rs) throws SQLException {
        ReplicacaoProcessoTabela processoTabela = new ReplicacaoProcessoTabela();
        processoTabela.setId(rs.getLong("id"));
        processoTabela.setProcessoId(rs.getLong("processo_id"));
        processoTabela.setTabelaOrigem(rs.getString("tabela_origem"));
        processoTabela.setTabelaDestino(rs.getString("tabela_destino"));
        processoTabela.setOrdem(rs.getInt("ordem"));
        processoTabela.setHabilitado(rs.getBoolean("habilitado"));
        processoTabela.setDsWhere(rs.getString("ds_where"));
        return processoTabela;
    }
}
