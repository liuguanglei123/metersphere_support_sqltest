package io.metersphere.sql.mapper;

import io.metersphere.sql.domain.SqlExecTaskItem;
import io.metersphere.sql.domain.SqlExecTaskItemExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface SqlExecTaskItemMapper {
    long countByExample(SqlExecTaskItemExample example);

    int deleteByExample(SqlExecTaskItemExample example);

    int deleteByPrimaryKey(String id);

    int insert(SqlExecTaskItem record);

    int insertSelective(SqlExecTaskItem record);

    List<SqlExecTaskItem> selectByExample(SqlExecTaskItemExample example);

    SqlExecTaskItem selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") SqlExecTaskItem record, @Param("example") SqlExecTaskItemExample example);

    int updateByExample(@Param("record") SqlExecTaskItem record, @Param("example") SqlExecTaskItemExample example);

    int updateByPrimaryKeySelective(SqlExecTaskItem record);

    int updateByPrimaryKey(SqlExecTaskItem record);

    int batchInsert(@Param("list") List<SqlExecTaskItem> list);

    int batchInsertSelective(@Param("list") List<SqlExecTaskItem> list, @Param("selective") SqlExecTaskItem.Column ... selective);
}