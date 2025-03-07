package io.metersphere.sql.service.common.impl;

import io.metersphere.sql.domain.UserConnectionInfo;
import io.metersphere.sql.mapper.UserConnectionInfoMapper;
import io.metersphere.sql.service.common.UserConnectionInfoService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class UserConnectionInfoServiceImpl implements UserConnectionInfoService {

    @Resource
    UserConnectionInfoMapper userConnectionInfoMapper;

    public Long createUserConnectionInfo(UserConnectionInfo userConnectionInfo) {
        return (long) userConnectionInfoMapper.insert(userConnectionInfo);
    }

    public UserConnectionInfo getUserConnectionInfo(Integer id) {
        return userConnectionInfoMapper.selectByPrimaryKey(id);
    }

}
