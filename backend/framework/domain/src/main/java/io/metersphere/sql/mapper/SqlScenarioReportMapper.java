package io.metersphere.sql.mapper;

import io.metersphere.sql.domain.SqlScenarioReport;
import io.metersphere.sql.domain.SqlScenarioReportExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface SqlScenarioReportMapper {
    long countByExample(SqlScenarioReportExample example);

    int deleteByExample(SqlScenarioReportExample example);

    int deleteByPrimaryKey(String id);

    int insert(SqlScenarioReport record);

    int insertSelective(SqlScenarioReport record);

    List<SqlScenarioReport> selectByExample(SqlScenarioReportExample example);

    SqlScenarioReport selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") SqlScenarioReport record, @Param("example") SqlScenarioReportExample example);

    int updateByExample(@Param("record") SqlScenarioReport record, @Param("example") SqlScenarioReportExample example);

    int updateByPrimaryKeySelective(SqlScenarioReport record);

    int updateByPrimaryKey(SqlScenarioReport record);

    int batchInsert(@Param("list") List<SqlScenarioReport> list);

    int batchInsertSelective(@Param("list") List<SqlScenarioReport> list, @Param("selective") SqlScenarioReport.Column ... selective);
}