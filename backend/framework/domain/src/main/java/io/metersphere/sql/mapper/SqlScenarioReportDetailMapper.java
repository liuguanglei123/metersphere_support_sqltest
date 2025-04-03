package io.metersphere.sql.mapper;

import io.metersphere.sql.domain.SqlScenarioReportDetail;
import io.metersphere.sql.domain.SqlScenarioReportDetailExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface SqlScenarioReportDetailMapper {
    long countByExample(SqlScenarioReportDetailExample example);

    int deleteByExample(SqlScenarioReportDetailExample example);

    int deleteByPrimaryKey(String id);

    int insert(SqlScenarioReportDetail record);

    int insertSelective(SqlScenarioReportDetail record);

    List<SqlScenarioReportDetail> selectByExample(SqlScenarioReportDetailExample example);

    SqlScenarioReportDetail selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") SqlScenarioReportDetail record, @Param("example") SqlScenarioReportDetailExample example);

    int updateByExample(@Param("record") SqlScenarioReportDetail record, @Param("example") SqlScenarioReportDetailExample example);

    int updateByPrimaryKeySelective(SqlScenarioReportDetail record);

    int updateByPrimaryKey(SqlScenarioReportDetail record);

    int batchInsert(@Param("list") List<SqlScenarioReportDetail> list);

    int batchInsertSelective(@Param("list") List<SqlScenarioReportDetail> list, @Param("selective") SqlScenarioReportDetail.Column ... selective);
}