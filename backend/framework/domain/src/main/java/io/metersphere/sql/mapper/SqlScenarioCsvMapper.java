package io.metersphere.sql.mapper;

import io.metersphere.sql.domain.SqlScenarioCsv;
import io.metersphere.sql.domain.SqlScenarioCsvExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface SqlScenarioCsvMapper {
    long countByExample(SqlScenarioCsvExample example);

    int deleteByExample(SqlScenarioCsvExample example);

    int deleteByPrimaryKey(String id);

    int insert(SqlScenarioCsv record);

    int insertSelective(SqlScenarioCsv record);

    List<SqlScenarioCsv> selectByExample(SqlScenarioCsvExample example);

    SqlScenarioCsv selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") SqlScenarioCsv record, @Param("example") SqlScenarioCsvExample example);

    int updateByExample(@Param("record") SqlScenarioCsv record, @Param("example") SqlScenarioCsvExample example);

    int updateByPrimaryKeySelective(SqlScenarioCsv record);

    int updateByPrimaryKey(SqlScenarioCsv record);

    int batchInsert(@Param("list") List<SqlScenarioCsv> list);

    int batchInsertSelective(@Param("list") List<SqlScenarioCsv> list, @Param("selective") SqlScenarioCsv.Column ... selective);
}