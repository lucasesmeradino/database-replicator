package database.dao;

import database.model.PendenciaReplicacao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReplicacaoDadosOrigemDAO {

    private static final String SQL_SELECT_ALL_UNPROCESSED_REPLICATIONS = "SELECT id, table_name, row_id, operation FROM replication_queue WHERE processed_at IS NULL ORDER BY ocurred_at DESC";

    private static final String UPDATE_REPLICATION_PROCESSED = "UPDATE replication_queue SET processed_at = NOW() WHERE id = ?";

    private final Connection originDbConnection;

    public ReplicacaoDadosOrigemDAO(Connection originDbConnection) {
        this.originDbConnection = originDbConnection;
    }

    public ResultSet executeSelectTableByMaxIdDestinationDb(String nomeTabela, String dsWhere, long minId) throws SQLException {
        String sqlSelectAll = String.format("SELECT * FROM %s WHERE %s AND id > %d", nomeTabela, dsWhere, minId);
        return originDbConnection.createStatement().executeQuery(sqlSelectAll);
    }

    public ResultSet executeSelectTableById(String nomeTabela, long id) throws SQLException {
        String sqlSelectById = "SELECT * FROM %s WHERE id = %d".formatted(nomeTabela, id);

        return originDbConnection.createStatement().executeQuery(sqlSelectById);
    }

    public List<PendenciaReplicacao> selectAllUnprocessedReplications() throws SQLException {
        List<PendenciaReplicacao> replications = new ArrayList<>();
        try (ResultSet rsReplications = originDbConnection.createStatement().executeQuery(SQL_SELECT_ALL_UNPROCESSED_REPLICATIONS)) {
            while (rsReplications.next()) {
                replications.add(mapToReplicationModel(rsReplications));
            }
        }
        return replications;
    }

    public int updateReplicationProcessed(long id) throws SQLException {
        try (PreparedStatement psUpdateProcessed = originDbConnection.prepareStatement(UPDATE_REPLICATION_PROCESSED)) {
            psUpdateProcessed.setLong(1, id);
            return psUpdateProcessed.executeUpdate();
        }
    }

    private PendenciaReplicacao mapToReplicationModel(ResultSet rs) throws SQLException {
        PendenciaReplicacao replication = new PendenciaReplicacao();
        replication.setId(rs.getLong("id"));
        replication.setTableName(rs.getString("table_name"));
        replication.setRowId(rs.getLong("row_id"));
        replication.setOperation(rs.getString("operation"));
        return replication;
    }
}
