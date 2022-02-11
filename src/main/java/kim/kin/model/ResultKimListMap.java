package kim.kin.model;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResultKimListMap implements ResultSetExtractor<List<Map<String, Object>>> {
    @Override
    public List<Map<String, Object>> extractData(ResultSet rs) throws SQLException, DataAccessException {
/*        ArrayList<Map<String, Object>> list = new ArrayList<>();
        while (rs.next()) {
            HashMap<String, Object> map = new HashMap<>();
            String secondsBehindSource = rs.getString("Seconds_Behind_Source");
            String replicaIoRunning = rs.getString("Replica_IO_Running");
            String replicaSqlRunning = rs.getString("Replica_SQL_Running");
            String replicaSqlRunningState = rs.getString("Replica_SQL_Running_State");
            map.put("secondsBehindSource", secondsBehindSource);
            map.put("replicaIoRunning", replicaIoRunning);
            map.put("replicaSqlRunning", replicaSqlRunning);
            map.put("replicaSqlRunningState", replicaSqlRunningState);
            list.add(map);
        }
         return list;
        */

        List<Map<String, Object>> results = new ArrayList<>();
        ResultSetMetaData resultSetMetaData = rs.getMetaData();
        int colCount = resultSetMetaData.getColumnCount();
        List<String> columnNameList = new ArrayList<>();
        for (int i = 0; i < colCount; i++) {
            columnNameList.add(resultSetMetaData.getColumnName(i + 1));
        }
        while (rs.next()) {
            for (String columnName : columnNameList) {
                Map<String, Object> map = new HashMap<>();
                Object value = rs.getString(columnName);
                map.put(columnName, value);
                results.add(map);
            }
        }
        return results;

    }
}
