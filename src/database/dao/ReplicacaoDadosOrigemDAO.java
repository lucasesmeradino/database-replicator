package database.dao;

import database.dao.result.QueryResult;
import database.model.PendenciaReplicacao;

import java.sql.*;
import java.util.*;

public class ReplicacaoDadosOrigemDAO {

    private static final String SQL_SELECT_ALL_UNPROCESSED_REPLICATIONS = "SELECT id, table_name, row_id, operation FROM replication_queue WHERE processed_at IS NULL ORDER BY ocurred_at DESC";

    private static final String UPDATE_REPLICATION_PROCESSED = "UPDATE replication_queue SET processed_at = NOW() WHERE id = ?";

    private final Connection originDbConnection;

    public ReplicacaoDadosOrigemDAO(Connection originDbConnection) {
        this.originDbConnection = originDbConnection;
    }

    public QueryResult executeSelectTableByMaxIdDestinationDb(String nomeTabela, String dsWhere, long lastId) throws SQLException {

        try (PreparedStatement psSelectTable = originDbConnection.prepareStatement("SELECT * FROM %s WHERE %s AND id > ?".formatted(nomeTabela, dsWhere))) {
            psSelectTable.setLong(1, lastId);

            try (ResultSet rsSelectTable = psSelectTable.executeQuery()) {
                return buildQueryResult(rsSelectTable);
            }
        }
    }

    public QueryResult executeSelectTableById(String nomeTabela, long id) throws SQLException {
        try (PreparedStatement psSelectTableById = originDbConnection.prepareStatement("SELECT * FROM %s WHERE id = ?".formatted(nomeTabela))) {
            psSelectTableById.setLong(1, id);

            try (ResultSet rsSelectTableById = psSelectTableById.executeQuery()) {
                return buildQueryResult(rsSelectTableById);
            }
        }
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

    private QueryResult buildQueryResult(ResultSet resultSet) throws SQLException {
        List<String> columnNames = new ArrayList<>();
        List<Map<String, Object>> rows = new ArrayList<>();

        ResultSetMetaData rsMetaData = resultSet.getMetaData();
        int columnCount = rsMetaData.getColumnCount();
        for (int i = 1; i <= columnCount; i++) {
            columnNames.add(rsMetaData.getColumnName(i));
        }

        while (resultSet.next()) {
            Map<String, Object> row = new LinkedHashMap<>();
            for (String columnName : columnNames) {
                row.put(columnName, resultSet.getObject(columnName));
            }
            rows.add(row);
        }

        return new QueryResult(columnNames, rows);
    }
}
