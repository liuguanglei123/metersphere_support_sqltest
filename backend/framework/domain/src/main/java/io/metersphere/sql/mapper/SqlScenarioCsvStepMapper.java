package io.metersphere.sql.mapper;

import io.metersphere.sql.domain.SqlScenarioCsvStep;
import io.metersphere.sql.domain.SqlScenarioCsvStepExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface SqlScenarioCsvStepMapper {
    long countByExample(SqlScenarioCsvStepExample example);

    int deleteByExample(SqlScenarioCsvStepExample example);

    int deleteByPrimaryKey(String id);

    int insert(SqlScenarioCsvStep record);

    int insertSelective(SqlScenarioCsvStep record);

    List<SqlScenarioCsvStep> selectByExample(SqlScenarioCsvStepExample example);

    SqlScenarioCsvStep selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") SqlScenarioCsvStep record, @Param("example") SqlScenarioCsvStepExample example);

    int updateByExample(@Param("record") SqlScenarioCsvStep record, @Param("example") SqlScenarioCsvStepExample example);

    int updateByPrimaryKeySelective(SqlScenarioCsvStep record);

    int updateByPrimaryKey(SqlScenarioCsvStep record);

    int batchInsert(@Param("list") List<SqlScenarioCsvStep> list);

    int batchInsertSelective(@Param("list") List<SqlScenarioCsvStep> list, @Param("selective") SqlScenarioCsvStep.Column ... selective);
}