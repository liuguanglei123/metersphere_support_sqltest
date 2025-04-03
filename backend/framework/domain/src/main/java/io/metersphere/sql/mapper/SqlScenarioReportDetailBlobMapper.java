package io.metersphere.sql.mapper;

import io.metersphere.sql.domain.SqlScenarioReportDetailBlob;
import io.metersphere.sql.domain.SqlScenarioReportDetailBlobExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface SqlScenarioReportDetailBlobMapper {
    long countByExample(SqlScenarioReportDetailBlobExample example);

    int deleteByExample(SqlScenarioReportDetailBlobExample example);

    int deleteByPrimaryKey(String id);

    int insert(SqlScenarioReportDetailBlob record);

    int insertSelective(SqlScenarioReportDetailBlob record);

    List<SqlScenarioReportDetailBlob> selectByExampleWithBLOBs(SqlScenarioReportDetailBlobExample example);

    List<SqlScenarioReportDetailBlob> selectByExample(SqlScenarioReportDetailBlobExample example);

    SqlScenarioReportDetailBlob selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") SqlScenarioReportDetailBlob record, @Param("example") SqlScenarioReportDetailBlobExample example);

    int updateByExampleWithBLOBs(@Param("record") SqlScenarioReportDetailBlob record, @Param("example") SqlScenarioReportDetailBlobExample example);

    int updateByExample(@Param("record") SqlScenarioReportDetailBlob record, @Param("example") SqlScenarioReportDetailBlobExample example);

    int updateByPrimaryKeySelective(SqlScenarioReportDetailBlob record);

    int updateByPrimaryKeyWithBLOBs(SqlScenarioReportDetailBlob record);

    int updateByPrimaryKey(SqlScenarioReportDetailBlob record);

    int batchInsert(@Param("list") List<SqlScenarioReportDetailBlob> list);

    int batchInsertSelective(@Param("list") List<SqlScenarioReportDetailBlob> list, @Param("selective") SqlScenarioReportDetailBlob.Column ... selective);
}