package io.metersphere.sql.mapper;

import io.metersphere.sql.domain.SqlDebugModule;
import io.metersphere.sql.domain.SqlDebugModuleExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface SqlDebugModuleMapper {
    long countByExample(SqlDebugModuleExample example);

    int deleteByExample(SqlDebugModuleExample example);

    int deleteByPrimaryKey(String id);

    int insert(SqlDebugModule record);

    int insertSelective(SqlDebugModule record);

    List<SqlDebugModule> selectByExample(SqlDebugModuleExample example);

    SqlDebugModule selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") SqlDebugModule record, @Param("example") SqlDebugModuleExample example);

    int updateByExample(@Param("record") SqlDebugModule record, @Param("example") SqlDebugModuleExample example);

    int updateByPrimaryKeySelective(SqlDebugModule record);

    int updateByPrimaryKey(SqlDebugModule record);

    int batchInsert(@Param("list") List<SqlDebugModule> list);

    int batchInsertSelective(@Param("list") List<SqlDebugModule> list, @Param("selective") SqlDebugModule.Column ... selective);
}