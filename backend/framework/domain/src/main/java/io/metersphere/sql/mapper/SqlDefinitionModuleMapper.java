package io.metersphere.sql.mapper;

import io.metersphere.sql.domain.SqlDefinitionModule;
import io.metersphere.sql.domain.SqlDefinitionModuleExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface SqlDefinitionModuleMapper {
    long countByExample(SqlDefinitionModuleExample example);

    int deleteByExample(SqlDefinitionModuleExample example);

    int deleteByPrimaryKey(String id);

    int insert(SqlDefinitionModule record);

    int insertSelective(SqlDefinitionModule record);

    List<SqlDefinitionModule> selectByExample(SqlDefinitionModuleExample example);

    SqlDefinitionModule selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") SqlDefinitionModule record, @Param("example") SqlDefinitionModuleExample example);

    int updateByExample(@Param("record") SqlDefinitionModule record, @Param("example") SqlDefinitionModuleExample example);

    int updateByPrimaryKeySelective(SqlDefinitionModule record);

    int updateByPrimaryKey(SqlDefinitionModule record);

    int batchInsert(@Param("list") List<SqlDefinitionModule> list);

    int batchInsertSelective(@Param("list") List<SqlDefinitionModule> list, @Param("selective") SqlDefinitionModule.Column ... selective);
}