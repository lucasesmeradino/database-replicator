package database.dao;

import java.sql.*;
import java.util.List;

public class ReplicacaoDadosDestinoDAO {

    private final Connection destinationDbConnection;

    public ReplicacaoDadosDestinoDAO(Connection destinationDbConnection) {
        this.destinationDbConnection = destinationDbConnection;
    }

    public void executeDeleteTableById(String nomeTabela, long id) throws SQLException {
        String sqlDeleteById = "DELETE FROM %s WHERE id = %d".formatted(nomeTabela, id);
        try (Statement stDeleteById = destinationDbConnection.createStatement(); ResultSet rsDeleteById = stDeleteById.executeQuery(sqlDeleteById)){}
    }

    public long getTableMaxId(String nomeTabela) throws SQLException {
        long maxId = 0;
        String sqlMaxId = "SELECT MAX(id) FROM " + nomeTabela;
        try (Statement maxIdStatement = destinationDbConnection.createStatement(); ResultSet rsMaxId = maxIdStatement.executeQuery(sqlMaxId)) {
            if (rsMaxId != null && rsMaxId.next()) {
                maxId = rsMaxId.getLong(1);
            }
        }
        return maxId;
    }

    public String createInsertByResultSet(List<String> columnNames, String nomeTabela) throws SQLException {
        StringBuilder dsColumnsInsert = new StringBuilder(), dsParametersInsert = new StringBuilder();
        for (int i = 0 ; i < columnNames.size(); i++) {
            dsColumnsInsert.append(columnNames.get(i)).append(", ");
            dsParametersInsert.append("?, ");
        }
        return String.format("INSERT INTO %s (%s) VALUES (%s)",
                nomeTabela,
                dsColumnsInsert.substring(0, dsColumnsInsert.length() - 2),
                dsParametersInsert.substring(0, dsParametersInsert.length() - 2));
    }
}
