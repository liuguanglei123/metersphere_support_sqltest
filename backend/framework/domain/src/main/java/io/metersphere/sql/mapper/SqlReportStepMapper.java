package io.metersphere.sql.mapper;

import io.metersphere.sql.domain.SqlReportStep;
import io.metersphere.sql.domain.SqlReportStepExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface SqlReportStepMapper {
    long countByExample(SqlReportStepExample example);

    int deleteByExample(SqlReportStepExample example);

    int deleteByPrimaryKey(@Param("stepId") String stepId, @Param("reportId") String reportId);

    int insert(SqlReportStep record);

    int insertSelective(SqlReportStep record);

    List<SqlReportStep> selectByExample(SqlReportStepExample example);

    SqlReportStep selectByPrimaryKey(@Param("stepId") String stepId, @Param("reportId") String reportId);

    int updateByExampleSelective(@Param("record") SqlReportStep record, @Param("example") SqlReportStepExample example);

    int updateByExample(@Param("record") SqlReportStep record, @Param("example") SqlReportStepExample example);

    int updateByPrimaryKeySelective(SqlReportStep record);

    int updateByPrimaryKey(SqlReportStep record);

    int batchInsert(@Param("list") List<SqlReportStep> list);

    int batchInsertSelective(@Param("list") List<SqlReportStep> list, @Param("selective") SqlReportStep.Column ... selective);
}