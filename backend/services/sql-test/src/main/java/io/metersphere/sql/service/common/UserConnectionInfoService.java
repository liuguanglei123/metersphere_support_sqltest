package io.metersphere.sql.service.common;

import io.metersphere.sql.domain.UserConnectionInfo;

public interface UserConnectionInfoService {
    Integer createUserConnectionInfo(UserConnectionInfo userConnectionInfo);

    UserConnectionInfo getUserConnectionInfo(Integer id);

}
