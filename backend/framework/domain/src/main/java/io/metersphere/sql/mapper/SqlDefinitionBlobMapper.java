package io.metersphere.sql.mapper;

import io.metersphere.sql.domain.SqlDefinitionBlob;
import io.metersphere.sql.domain.SqlDefinitionBlobExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface SqlDefinitionBlobMapper {
    long countByExample(SqlDefinitionBlobExample example);

    int deleteByExample(SqlDefinitionBlobExample example);

    int deleteByPrimaryKey(String id);

    int insert(SqlDefinitionBlob record);

    int insertSelective(SqlDefinitionBlob record);

    List<SqlDefinitionBlob> selectByExampleWithBLOBs(SqlDefinitionBlobExample example);

    List<SqlDefinitionBlob> selectByExample(SqlDefinitionBlobExample example);

    SqlDefinitionBlob selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") SqlDefinitionBlob record, @Param("example") SqlDefinitionBlobExample example);

    int updateByExampleWithBLOBs(@Param("record") SqlDefinitionBlob record, @Param("example") SqlDefinitionBlobExample example);

    int updateByExample(@Param("record") SqlDefinitionBlob record, @Param("example") SqlDefinitionBlobExample example);

    int updateByPrimaryKeySelective(SqlDefinitionBlob record);

    int updateByPrimaryKeyWithBLOBs(SqlDefinitionBlob record);

    int batchInsert(@Param("list") List<SqlDefinitionBlob> list);

    int batchInsertSelective(@Param("list") List<SqlDefinitionBlob> list, @Param("selective") SqlDefinitionBlob.Column ... selective);
}