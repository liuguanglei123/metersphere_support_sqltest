package io.metersphere.sql.mapper;

import io.metersphere.sql.domain.SqlScenarioStep;
import io.metersphere.sql.domain.SqlScenarioStepExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface SqlScenarioStepMapper {
    long countByExample(SqlScenarioStepExample example);

    int deleteByExample(SqlScenarioStepExample example);

    int deleteByPrimaryKey(String id);

    int insert(SqlScenarioStep record);

    int insertSelective(SqlScenarioStep record);

    List<SqlScenarioStep> selectByExample(SqlScenarioStepExample example);

    SqlScenarioStep selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") SqlScenarioStep record, @Param("example") SqlScenarioStepExample example);

    int updateByExample(@Param("record") SqlScenarioStep record, @Param("example") SqlScenarioStepExample example);

    int updateByPrimaryKeySelective(SqlScenarioStep record);

    int updateByPrimaryKey(SqlScenarioStep record);

    int batchInsert(@Param("list") List<SqlScenarioStep> list);

    int batchInsertSelective(@Param("list") List<SqlScenarioStep> list, @Param("selective") SqlScenarioStep.Column ... selective);
}