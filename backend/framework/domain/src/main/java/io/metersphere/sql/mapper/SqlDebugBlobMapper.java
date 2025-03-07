package io.metersphere.sql.mapper;

import io.metersphere.sql.domain.SqlDebugBlob;
import io.metersphere.sql.domain.SqlDebugBlobExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface SqlDebugBlobMapper {
    long countByExample(SqlDebugBlobExample example);

    int deleteByExample(SqlDebugBlobExample example);

    int deleteByPrimaryKey(String id);

    int insert(SqlDebugBlob record);

    int insertSelective(SqlDebugBlob record);

    List<SqlDebugBlob> selectByExampleWithBLOBs(SqlDebugBlobExample example);

    List<SqlDebugBlob> selectByExample(SqlDebugBlobExample example);

    SqlDebugBlob selectByPrimaryKey(String id);

    int updateByExampleSelective(@Param("record") SqlDebugBlob record, @Param("example") SqlDebugBlobExample example);

    int updateByExampleWithBLOBs(@Param("record") SqlDebugBlob record, @Param("example") SqlDebugBlobExample example);

    int updateByExample(@Param("record") SqlDebugBlob record, @Param("example") SqlDebugBlobExample example);

    int updateByPrimaryKeySelective(SqlDebugBlob record);

    int updateByPrimaryKeyWithBLOBs(SqlDebugBlob record);

    int batchInsert(@Param("list") List<SqlDebugBlob> list);

    int batchInsertSelective(@Param("list") List<SqlDebugBlob> list, @Param("selective") SqlDebugBlob.Column ... selective);
}