package io.metersphere.sql.mapper;

import io.metersphere.sql.domain.SqlScenarioStepBlob;
import io.metersphere.sql.domain.SqlScenarioStepBlobExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface SqlScenarioStepBlobMapper {
    long countByExample(SqlScenarioStepBlobExample example);

    int deleteByExample(SqlScenarioStepBlobExample example);

    int deleteByPrimaryKey(String id);

    int insert(SqlScenarioStepBlob record);

    int insertSelective(SqlScenarioStepBlob record);

    List<SqlScenarioStepBlob> selectByExampleWithBLOBs(SqlScenarioStepBlobExample example);

    List<SqlScenarioStepBlob> selectByExample(SqlScenarioStepBlobExample example);

    SqlScenarioStepBlob selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") SqlScenarioStepBlob record, @Param("example") SqlScenarioStepBlobExample example);

    int updateByExampleWithBLOBs(@Param("record") SqlScenarioStepBlob record, @Param("example") SqlScenarioStepBlobExample example);

    int updateByExample(@Param("record") SqlScenarioStepBlob record, @Param("example") SqlScenarioStepBlobExample example);

    int updateByPrimaryKeySelective(SqlScenarioStepBlob record);

    int updateByPrimaryKeyWithBLOBs(SqlScenarioStepBlob record);

    int updateByPrimaryKey(SqlScenarioStepBlob record);

    int batchInsert(@Param("list") List<SqlScenarioStepBlob> list);

    int batchInsertSelective(@Param("list") List<SqlScenarioStepBlob> list, @Param("selective") SqlScenarioStepBlob.Column ... selective);
}