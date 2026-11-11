package database.dao;

import database.model.ReplicacaoProcesso;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReplicacaoProcessoDAO {

    private static final String SQL_SELECT_ALL = "SELECT * FROM tb_replicacao_processo";

    private static final String SQL_SELECT_ALL_HABILITADOS = "SELECT * FROM tb_replicacao_processo WHERE habilitado = true";

    private static final String SQL_SELECT_BY_ID = "SELECT * FROM tb_replicacao_processo WHERE id = ?";

    private static final String SQL_INSERT = "INSERT INTO tb_replicacao_processo (processo, descricao, habilitado) VALUES (?, ?, ?)";

    private static final String SQL_UPDATE = "UPDATE tb_replicacao_processo SET processo = ?, descricao = ?, habilitado = ? WHERE id = ?";

    private static final String SQL_DELETE = "DELETE FROM tb_replicacao_processo WHERE id = ?";

    private final Connection conn;

    public ReplicacaoProcessoDAO(Connection conn) throws SQLException {
        this.conn = conn;
    }

    public List<ReplicacaoProcesso> selectAllProcessos() throws SQLException {
        try (Statement stSelectAll = conn.createStatement()) {
            List<ReplicacaoProcesso> processos = new ArrayList<>();
            try (ResultSet rsProcessos = stSelectAll.executeQuery(SQL_SELECT_ALL)) {
                while (rsProcessos.next()) {
                    processos.add(mapToModel(rsProcessos));
                }
            }
            return processos;
        }
    }

    public List<ReplicacaoProcesso> selectAllProcessosHabilitados() throws SQLException {
        try (Statement stSelectAll = conn.createStatement()) {
            List<ReplicacaoProcesso> processos = new ArrayList<>();
            try (ResultSet rsProcessos = stSelectAll.executeQuery(SQL_SELECT_ALL_HABILITADOS)) {
                while (rsProcessos.next()) {
                    processos.add(mapToModel(rsProcessos));
                }
            }
            return processos;
        }
    }

    public ReplicacaoProcesso selectProcessoById(long processoId) throws SQLException {
        try (PreparedStatement psSelectById = conn.prepareStatement(SQL_SELECT_BY_ID)) {
            psSelectById.setLong(1, processoId);
            try (ResultSet rsProcesso = psSelectById.executeQuery()) {
                return rsProcesso.next() ? mapToModel(rsProcesso) : null;
            }
        }
    }

    public void insertProcesso(ReplicacaoProcesso processo) throws SQLException {
        try (PreparedStatement psInsert = conn.prepareStatement(SQL_INSERT)) {
            psInsert.setString(1, processo.getProcesso());
            psInsert.setString(2, processo.getDescricao());
            psInsert.setBoolean(3, processo.isHabilitado());
            psInsert.executeUpdate();
        }
    }

    public void updateProcesso(ReplicacaoProcesso processo) throws SQLException {
        try (PreparedStatement psUpdate = conn.prepareStatement(SQL_UPDATE)) {
            psUpdate.setString(1, processo.getProcesso());
            psUpdate.setString(2, processo.getDescricao());
            psUpdate.setBoolean(3, processo.isHabilitado());
            psUpdate.setLong(4, processo.getId());
            psUpdate.executeUpdate();
        }
    }

    public void deleteProcesso(long processoId) throws SQLException {
        try (PreparedStatement psDelete = conn.prepareStatement(SQL_DELETE)) {
            psDelete.setLong(1,processoId);
            psDelete.executeUpdate();
        }
    }

    private ReplicacaoProcesso mapToModel(ResultSet rs) throws SQLException {
        ReplicacaoProcesso processo = new ReplicacaoProcesso();
        processo.setId(rs.getLong("id"));
        processo.setProcesso(rs.getString("processo"));
        processo.setDescricao(rs.getString("descricao"));
        processo.setHabilitado(rs.getBoolean("habilitado"));
        return processo;
    }
}
