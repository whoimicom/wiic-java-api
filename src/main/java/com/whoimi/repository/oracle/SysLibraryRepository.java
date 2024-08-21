package com.whoimi.repository.oracle;


import com.whoimi.model.SysLibrary;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SysLibraryRepository extends CrudRepository<SysLibrary, String> {
    /**
     *  Oracle version >=12C
     *  FETCH FIRST 100 ROWS ONLY   Caused by: java.sql.SQLSyntaxErrorException: ORA-00933: SQL
     */
    List<SysLibrary> findTop100ByOrderByLibIdDesc();

    @Query("SELECT * FROM SYS_LIBRARY WHERE ROWNUM < 100 ORDER BY SYS_LIBRARY.LIB_ID DESC")
    List<SysLibrary> findTop100OrderByLibIdDesc();


}
