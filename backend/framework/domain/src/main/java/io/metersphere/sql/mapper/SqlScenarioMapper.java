package io.metersphere.sql.mapper;

import io.metersphere.sql.domain.SqlScenario;
import io.metersphere.sql.domain.SqlScenarioExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface SqlScenarioMapper {
    long countByExample(SqlScenarioExample example);

    int deleteByExample(SqlScenarioExample example);

    int deleteByPrimaryKey(String id);

    int insert(SqlScenario record);

    int insertSelective(SqlScenario record);

    List<SqlScenario> selectByExample(SqlScenarioExample example);

    SqlScenario selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") SqlScenario record, @Param("example") SqlScenarioExample example);

    int updateByExample(@Param("record") SqlScenario record, @Param("example") SqlScenarioExample example);

    int updateByPrimaryKeySelective(SqlScenario record);

    int updateByPrimaryKey(SqlScenario record);

    int batchInsert(@Param("list") List<SqlScenario> list);

    int batchInsertSelective(@Param("list") List<SqlScenario> list, @Param("selective") SqlScenario.Column ... selective);
}