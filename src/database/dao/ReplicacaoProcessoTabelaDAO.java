package database.dao;

import database.model.ReplicacaoProcessoTabela;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReplicacaoProcessoTabelaDAO {

    private static final String SQL_SELECT_ALL_HABILITADO_BY_PROCESSO = "SELECT * FROM tb_replicacao_processo_tabela WHERE habilitado = true AND processo_id = ?";

    private static final String SQL_SELECT_ALL = "SELECT * FROM tb_replicacao_processo_tabela";

    private static final String SQL_SELECT_BY_ID = "SELECT * FROM tb_replicacao_processo_tabela WHERE id = ?";

    private static final String SQL_SELECT_TABELA_DESTINO_BY_TABELA_ORIGEM = "SELECT tabela_destino FROM tb_replicacao_processo_tabela WHERE tabela_origem = ? AND processo_id = ? AND habilitado = true";

    private static final String SQL_INSERT = "INSERT INTO tb_replicacao_processo_tabela (processo_id, tabela_origem, tabela_destino, ordem, habilitado, ds_where) VALUES (?, ?, ?, ?, ?, ?)";

    private static final String SQL_UPDATE = "UPDATE tb_replicacao_processo_tabela SET processo_id = ?, tabela_origem = ?, tabela_destino = ?, ordem = ?, habilitado = ?, ds_where = ? WHERE id = ?";

    private static final String SQL_DELETE = "DELETE FROM tb_replicacao_processo_tabela WHERE id = ?";

    private final Connection conn;

    public ReplicacaoProcessoTabelaDAO(Connection conn) throws SQLException {
        this.conn = conn;
    }

    public List<ReplicacaoProcessoTabela> selectAllTabelasHabilitadasByProcesso(long processoId) throws SQLException {
        try (PreparedStatement psSelectAll = conn.prepareStatement(SQL_SELECT_ALL_HABILITADO_BY_PROCESSO)) {
            psSelectAll.setLong(1, processoId);
            List<ReplicacaoProcessoTabela> processoTabelas = new ArrayList<>();
            try (ResultSet rsProcessoTabelas = psSelectAll.executeQuery()) {
                while (rsProcessoTabelas.next()) {
                    processoTabelas.add(mapToModel(rsProcessoTabelas));
                }
            }
            return processoTabelas;
        }
    }

    public List<ReplicacaoProcessoTabela> selectAllTabelas() throws SQLException {
        try (Statement stSelectAll = conn.createStatement()) {
            List<ReplicacaoProcessoTabela> processoTabelas = new ArrayList<>();
            try (ResultSet rsProcessoTabelas = stSelectAll.executeQuery(SQL_SELECT_ALL)) {
                while (rsProcessoTabelas.next()) {
                    processoTabelas.add(mapToModel(rsProcessoTabelas));
                }
            }
            return processoTabelas;
        }
    }

    public ReplicacaoProcessoTabela selectTabelaById(long processoId) throws SQLException {
        try (PreparedStatement psSelectByid = conn.prepareStatement(SQL_SELECT_BY_ID)) {
            psSelectByid.setLong(1, processoId);
            try (ResultSet rsProcessoTabela = psSelectByid.executeQuery()) {
                return rsProcessoTabela.next() ? mapToModel(rsProcessoTabela) : null;
            }
        }
    }

    public String selectTabelaDestinoByTabelaOrigem(String nomeTabelaOrigem, long processoId) throws SQLException {
        try (PreparedStatement psSelectTabelaDestino = conn.prepareStatement(SQL_SELECT_TABELA_DESTINO_BY_TABELA_ORIGEM)) {
            psSelectTabelaDestino.setString(1, nomeTabelaOrigem);
            psSelectTabelaDestino.setLong(2, processoId);
            try (ResultSet rsProcessoTabela = psSelectTabelaDestino.executeQuery()) {
                return rsProcessoTabela != null && rsProcessoTabela.next() ? rsProcessoTabela.getString(1) : null;
            }
        }
    }

    public void insertTabela(ReplicacaoProcessoTabela processoTabela) throws SQLException {
        try (PreparedStatement sqlInsert = conn.prepareStatement(SQL_INSERT)) {
            sqlInsert.setLong(1, processoTabela.getProcessoId());
            sqlInsert.setString(2, processoTabela.getTabelaOrigem());
            sqlInsert.setString(3, processoTabela.getTabelaDestino());
            sqlInsert.setInt(4, processoTabela.getOrdem());
            sqlInsert.setBoolean(5, processoTabela.isHabilitado());
            sqlInsert.setString(6, processoTabela.getDsWhere());
            sqlInsert.executeUpdate();
        }
    }

    public void updateTabela(ReplicacaoProcessoTabela processoTabela) throws SQLException {
        try (PreparedStatement psUpdate = conn.prepareStatement(SQL_UPDATE)) {
            psUpdate.setLong(1, processoTabela.getProcessoId());
            psUpdate.setString(2, processoTabela.getTabelaOrigem());
            psUpdate.setString(3, processoTabela.getTabelaDestino());
            psUpdate.setInt(4, processoTabela.getOrdem());
            psUpdate.setBoolean(5, processoTabela.isHabilitado());
            psUpdate.setString(6, processoTabela.getDsWhere());
            psUpdate.setLong(7, processoTabela.getId());
            psUpdate.executeUpdate();
        }
    }

    public void deleteTabela(long processoId) throws SQLException {
        try (PreparedStatement psDelete = conn.prepareStatement(SQL_DELETE)) {
            psDelete.setLong(1, processoId);
            psDelete.executeUpdate();
        }
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
