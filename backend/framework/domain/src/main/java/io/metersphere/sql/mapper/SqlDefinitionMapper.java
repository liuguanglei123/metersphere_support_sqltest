package io.metersphere.sql.mapper;

import io.metersphere.sql.domain.SqlDefinition;
import io.metersphere.sql.domain.SqlDefinitionExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface SqlDefinitionMapper {
    long countByExample(SqlDefinitionExample example);

    int deleteByExample(SqlDefinitionExample example);

    int deleteByPrimaryKey(String id);

    int insert(SqlDefinition record);

    int insertSelective(SqlDefinition record);

    List<SqlDefinition> selectByExample(SqlDefinitionExample example);

    SqlDefinition selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") SqlDefinition record, @Param("example") SqlDefinitionExample example);

    int updateByExample(@Param("record") SqlDefinition record, @Param("example") SqlDefinitionExample example);

    int updateByPrimaryKeySelective(SqlDefinition record);

    int updateByPrimaryKey(SqlDefinition record);

    int batchInsert(@Param("list") List<SqlDefinition> list);

    int batchInsertSelective(@Param("list") List<SqlDefinition> list, @Param("selective") SqlDefinition.Column ... selective);
}