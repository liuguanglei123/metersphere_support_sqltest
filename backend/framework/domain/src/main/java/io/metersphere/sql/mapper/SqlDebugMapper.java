package io.metersphere.sql.mapper;

import io.metersphere.sql.domain.SqlDebug;
import io.metersphere.sql.domain.SqlDebugExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SqlDebugMapper {
    long countByExample(SqlDebugExample example);

    int deleteByExample(SqlDebugExample example);

    int deleteByPrimaryKey(String id);

    int insert(SqlDebug record);

    int insertSelective(SqlDebug record);

    List<SqlDebug> selectByExample(SqlDebugExample example);

    SqlDebug selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") SqlDebug record, @Param("example") SqlDebugExample example);

    int updateByExample(@Param("record") SqlDebug record, @Param("example") SqlDebugExample example);

    int updateByPrimaryKeySelective(SqlDebug record);

    int updateByPrimaryKey(SqlDebug record);

    int batchInsert(@Param("list") List<SqlDebug> list);

    int batchInsertSelective(@Param("list") List<SqlDebug> list, @Param("selective") SqlDebug.Column ... selective);
}