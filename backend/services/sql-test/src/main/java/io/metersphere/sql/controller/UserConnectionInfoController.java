package io.metersphere.sql.controller;

import io.metersphere.sql.aspect.ConnectionInfoAspect;
import io.metersphere.sql.context.Chat2DBContext;
import io.metersphere.sql.controller.data.source.request.DataSourceBaseRequest;
import io.metersphere.sql.domain.UserConnectionInfo;
import io.metersphere.sql.service.common.UserConnectionInfoService;
import io.metersphere.system.utils.SessionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

@ConnectionInfoAspect
@RequestMapping("/api/sql/datasource")
@RestController
public class UserConnectionInfoController {

    @Autowired
    UserConnectionInfoService userConnectionInfoService;

    @PostMapping(value = "/create-user-connection-info")
    public Integer createUserConnectionInfo(UserConnectionInfo userConnectionInfo){
        userConnectionInfo.setUserId(SessionUtils.getUserId());
        userConnectionInfo.setCreateUser(SessionUtils.getUserId());
        return userConnectionInfoService.createUserConnectionInfo(userConnectionInfo);
    }

    @PostMapping(value = "/get-user-connection-info")
    public UserConnectionInfo getUserConnectionInfo(@RequestBody DataSourceBaseRequest dataSourceBaseRequest) throws SQLException {
        Connection connection = Chat2DBContext.getConnection();
        // 获取数据库元数据
        DatabaseMetaData metaData = connection.getMetaData();

        // 获取 MySQL 服务器的版本
        String clientVersion = metaData.getDriverVersion();
        System.out.println("JDBC Driver Version: " + clientVersion);

        return userConnectionInfoService.getUserConnectionInfo(dataSourceBaseRequest.getDataSourceId());
    }

}
