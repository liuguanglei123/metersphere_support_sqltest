package io.metersphere.sql.mapper;

import io.metersphere.sql.domain.SqlScenarioReportStep;
import io.metersphere.sql.domain.SqlScenarioReportStepExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface SqlScenarioReportStepMapper {
    long countByExample(SqlScenarioReportStepExample example);

    int deleteByExample(SqlScenarioReportStepExample example);

    int deleteByPrimaryKey(@Param("stepId") String stepId, @Param("reportId") String reportId);

    int insert(SqlScenarioReportStep record);

    int insertSelective(SqlScenarioReportStep record);

    List<SqlScenarioReportStep> selectByExample(SqlScenarioReportStepExample example);

    SqlScenarioReportStep selectByPrimaryKey(@Param("stepId") String stepId, @Param("reportId") String reportId);

    int updateByExampleSelective(@Param("record") SqlScenarioReportStep record, @Param("example") SqlScenarioReportStepExample example);

    int updateByExample(@Param("record") SqlScenarioReportStep record, @Param("example") SqlScenarioReportStepExample example);

    int updateByPrimaryKeySelective(SqlScenarioReportStep record);

    int updateByPrimaryKey(SqlScenarioReportStep record);

    int batchInsert(@Param("list") List<SqlScenarioReportStep> list);

    int batchInsertSelective(@Param("list") List<SqlScenarioReportStep> list, @Param("selective") SqlScenarioReportStep.Column ... selective);
}