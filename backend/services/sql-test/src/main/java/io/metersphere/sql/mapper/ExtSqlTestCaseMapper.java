package io.metersphere.sql.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author jianxing
 * @date : 2023-11-6
 */
@Mapper
public interface ExtSqlTestCaseMapper {

    List<String> selectSqlIdByProjectAndProtocol(@Param("projectId") String projectId, @Param("protocols") List<String> protocols);
}