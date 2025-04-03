package io.metersphere.sql.mapper;

import io.metersphere.sql.domain.SqlScenarioModule;
import io.metersphere.sql.domain.SqlScenarioModuleExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface SqlScenarioModuleMapper {
    long countByExample(SqlScenarioModuleExample example);

    int deleteByExample(SqlScenarioModuleExample example);

    int deleteByPrimaryKey(String id);

    int insert(SqlScenarioModule record);

    int insertSelective(SqlScenarioModule record);

    List<SqlScenarioModule> selectByExample(SqlScenarioModuleExample example);

    SqlScenarioModule selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") SqlScenarioModule record, @Param("example") SqlScenarioModuleExample example);

    int updateByExample(@Param("record") SqlScenarioModule record, @Param("example") SqlScenarioModuleExample example);

    int updateByPrimaryKeySelective(SqlScenarioModule record);

    int updateByPrimaryKey(SqlScenarioModule record);

    int batchInsert(@Param("list") List<SqlScenarioModule> list);

    int batchInsertSelective(@Param("list") List<SqlScenarioModule> list, @Param("selective") SqlScenarioModule.Column ... selective);
}