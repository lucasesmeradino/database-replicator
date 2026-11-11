package database.dao.result;

import java.util.List;
import java.util.Map;

public class QueryResult {

    private final List<String> columnNames;
    private final List<Map<String, Object>> rows;

    public QueryResult(List<String> columnNames, List<Map<String, Object>> rows) {
        this.columnNames = columnNames;
        this.rows = rows;
    }

    public List<String> getColumnNames() {
        return columnNames;
    }

    public List<Map<String, Object>> getRows() {
        return rows;
    }
}
