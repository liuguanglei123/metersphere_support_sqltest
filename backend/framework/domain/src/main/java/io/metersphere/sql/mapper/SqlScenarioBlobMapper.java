package io.metersphere.sql.mapper;

import io.metersphere.sql.domain.SqlScenarioBlob;
import io.metersphere.sql.domain.SqlScenarioBlobExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface SqlScenarioBlobMapper {
    long countByExample(SqlScenarioBlobExample example);

    int deleteByExample(SqlScenarioBlobExample example);

    int deleteByPrimaryKey(String id);

    int insert(SqlScenarioBlob record);

    int insertSelective(SqlScenarioBlob record);

    List<SqlScenarioBlob> selectByExampleWithBLOBs(SqlScenarioBlobExample example);

    List<SqlScenarioBlob> selectByExample(SqlScenarioBlobExample example);

    SqlScenarioBlob selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") SqlScenarioBlob record, @Param("example") SqlScenarioBlobExample example);

    int updateByExampleWithBLOBs(@Param("record") SqlScenarioBlob record, @Param("example") SqlScenarioBlobExample example);

    int updateByExample(@Param("record") SqlScenarioBlob record, @Param("example") SqlScenarioBlobExample example);

    int updateByPrimaryKeySelective(SqlScenarioBlob record);

    int updateByPrimaryKeyWithBLOBs(SqlScenarioBlob record);

    int batchInsert(@Param("list") List<SqlScenarioBlob> list);

    int batchInsertSelective(@Param("list") List<SqlScenarioBlob> list, @Param("selective") SqlScenarioBlob.Column ... selective);
}