package io.metersphere.sql.mapper;

import io.metersphere.sql.domain.SqlExecTask;
import io.metersphere.sql.domain.SqlExecTaskExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface SqlExecTaskMapper {
    long countByExample(SqlExecTaskExample example);

    int deleteByExample(SqlExecTaskExample example);

    int deleteByPrimaryKey(String id);

    int insert(SqlExecTask record);

    int insertSelective(SqlExecTask record);

    List<SqlExecTask> selectByExample(SqlExecTaskExample example);

    SqlExecTask selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") SqlExecTask record, @Param("example") SqlExecTaskExample example);

    int updateByExample(@Param("record") SqlExecTask record, @Param("example") SqlExecTaskExample example);

    int updateByPrimaryKeySelective(SqlExecTask record);

    int updateByPrimaryKey(SqlExecTask record);

    int batchInsert(@Param("list") List<SqlExecTask> list);

    int batchInsertSelective(@Param("list") List<SqlExecTask> list, @Param("selective") SqlExecTask.Column ... selective);
}