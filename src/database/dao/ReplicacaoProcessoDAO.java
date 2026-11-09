package database.dao;

import database.model.ReplicacaoProcesso;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReplicacaoProcessoDAO {

    private static final String SQL_SELECT_ALL = "SELECT * FROM tb_replicacao_processo";

    private static final String SQL_SELECT_BY_ID = "SELECT * FROM tb_replicacao_processo WHERE id = ?";

    private static final String SQL_INSERT = "INSERT INTO tb_replicacao_processo (processo, descricao, habilitado) VALUES (?, ?, ?)";

    private static final String SQL_UPDATE = "UPDATE tb_replicacao_processo SET processo = ?, descricao = ?, habilitado = ? WHERE id = ?";

    private static final String SQL_DELETE = "DELETE FROM tb_replicacao_processo WHERE id = ?";

    private final Connection conn;

    private final PreparedStatement psSqlSelectAll;
    private final PreparedStatement psSqlSelectById;
    private final PreparedStatement psSqlInsert;
    private final PreparedStatement psSqlUpdate;
    private final PreparedStatement psSqlDelete;

    public ReplicacaoProcessoDAO(Connection conn) throws SQLException {
        this.conn = conn;
        this.psSqlSelectAll = conn.prepareStatement(SQL_SELECT_ALL);
        this.psSqlSelectById = conn.prepareStatement(SQL_SELECT_BY_ID);
        this.psSqlInsert = conn.prepareStatement(SQL_INSERT);
        this.psSqlUpdate = conn.prepareStatement(SQL_UPDATE);
        this.psSqlDelete = conn.prepareStatement(SQL_DELETE);
    }

    public List<ReplicacaoProcesso> selectAllProcessos() throws SQLException {
        List<ReplicacaoProcesso> processos = new ArrayList<>();
        try (ResultSet rsProcessos = psSqlSelectAll.executeQuery()) {
            while (rsProcessos.next()) {
                processos.add(mapToModel(rsProcessos));
            }
        }
        return processos;
    }

    public ReplicacaoProcesso selectProcessoById(long processoId) throws SQLException {
        psSqlSelectById.setLong(1, processoId);
        try (ResultSet rsProcesso = psSqlSelectById.executeQuery()) {
            return rsProcesso.next() ? mapToModel(rsProcesso) : null;
        }
    }

    public void insertProcesso(ReplicacaoProcesso processo) throws SQLException {
        psSqlInsert.setString(1, processo.getProcesso());
        psSqlInsert.setString(2, processo.getDescricao());
        psSqlInsert.setBoolean(3, processo.isHabilitado());
        psSqlInsert.executeUpdate();
    }

    public void updateProcesso(ReplicacaoProcesso processo) throws SQLException {
        psSqlUpdate.setString(1, processo.getProcesso());
        psSqlUpdate.setString(2, processo.getDescricao());
        psSqlUpdate.setBoolean(3, processo.isHabilitado());
        psSqlUpdate.setLong(4, processo.getId());
        psSqlUpdate.executeUpdate();
    }

    public void deleteProcesso(long processoId) throws SQLException {
        psSqlDelete.setLong(1,processoId);
        psSqlDelete.executeUpdate();
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
