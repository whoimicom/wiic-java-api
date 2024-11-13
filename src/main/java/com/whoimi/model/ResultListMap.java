package com.whoimi.model;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author whoimi
 * @since 2024-10-12
 */
public class ResultListMap implements ResultSetExtractor<List<Map<String, Object>>> {
    @Override
    public List<Map<String, Object>> extractData(ResultSet rs) throws SQLException, DataAccessException {
        List<Map<String, Object>> results = new ArrayList<>();
        ResultSetMetaData resultSetMetaData = rs.getMetaData();
        int colCount = resultSetMetaData.getColumnCount();
        List<String> columnNameList = new ArrayList<>();
        for (int i = 0; i < colCount; i++) {
            columnNameList.add(resultSetMetaData.getColumnName(i + 1));
        }
        while (rs.next()) {
            Map<String, Object> map = new HashMap<>();
            for (String columnName : columnNameList) {
                Object value = rs.getString(columnName);
                map.put(columnName, value);
            }
            results.add(map);
        }
        return results;
    }
}
