package io.metersphere.sql.mapper;

import io.metersphere.sql.domain.SqlScenarioRecord;
import io.metersphere.sql.domain.SqlScenarioRecordExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface SqlScenarioRecordMapper {
    long countByExample(SqlScenarioRecordExample example);

    int deleteByExample(SqlScenarioRecordExample example);

    int deleteByPrimaryKey(@Param("sqlScenarioReportId") String sqlScenarioReportId, @Param("sqlScenarioId") String sqlScenarioId);

    int insert(SqlScenarioRecord record);

    int insertSelective(SqlScenarioRecord record);

    List<SqlScenarioRecord> selectByExample(SqlScenarioRecordExample example);

    int updateByExampleSelective(@Param("record") SqlScenarioRecord record, @Param("example") SqlScenarioRecordExample example);

    int updateByExample(@Param("record") SqlScenarioRecord record, @Param("example") SqlScenarioRecordExample example);

    int batchInsert(@Param("list") List<SqlScenarioRecord> list);

    int batchInsertSelective(@Param("list") List<SqlScenarioRecord> list, @Param("selective") SqlScenarioRecord.Column ... selective);
}