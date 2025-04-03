package io.metersphere.sql.mapper;

import io.metersphere.sql.domain.SqlReportRelateTask;
import io.metersphere.sql.domain.SqlReportRelateTaskExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface SqlReportRelateTaskMapper {
    long countByExample(SqlReportRelateTaskExample example);

    int deleteByExample(SqlReportRelateTaskExample example);

    int deleteByPrimaryKey(@Param("taskResourceId") String taskResourceId, @Param("reportId") String reportId);

    int insert(SqlReportRelateTask record);

    int insertSelective(SqlReportRelateTask record);

    List<SqlReportRelateTask> selectByExample(SqlReportRelateTaskExample example);

    int updateByExampleSelective(@Param("record") SqlReportRelateTask record, @Param("example") SqlReportRelateTaskExample example);

    int updateByExample(@Param("record") SqlReportRelateTask record, @Param("example") SqlReportRelateTaskExample example);

    int batchInsert(@Param("list") List<SqlReportRelateTask> list);

    int batchInsertSelective(@Param("list") List<SqlReportRelateTask> list, @Param("selective") SqlReportRelateTask.Column ... selective);
}