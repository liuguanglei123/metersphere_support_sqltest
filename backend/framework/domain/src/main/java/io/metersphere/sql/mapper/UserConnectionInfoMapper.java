package io.metersphere.sql.mapper;

import io.metersphere.sql.domain.UserConnectionInfo;
import io.metersphere.sql.domain.UserConnectionInfoExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface UserConnectionInfoMapper {
    long countByExample(UserConnectionInfoExample example);

    int deleteByExample(UserConnectionInfoExample example);

    int deleteByPrimaryKey(Integer id);

    long insert(UserConnectionInfo record);

    int insertSelective(UserConnectionInfo record);

    List<UserConnectionInfo> selectByExample(UserConnectionInfoExample example);

    UserConnectionInfo selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") UserConnectionInfo record, @Param("example") UserConnectionInfoExample example);

    int updateByExample(@Param("record") UserConnectionInfo record, @Param("example") UserConnectionInfoExample example);

    int updateByPrimaryKeySelective(UserConnectionInfo record);

    int updateByPrimaryKey(UserConnectionInfo record);

    int batchInsert(@Param("list") List<UserConnectionInfo> list);

    int batchInsertSelective(@Param("list") List<UserConnectionInfo> list, @Param("selective") UserConnectionInfo.Column ... selective);
}