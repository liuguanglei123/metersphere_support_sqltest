package io.metersphere.sql.service.debug;

import io.metersphere.plugin.api.spi.AbstractMsTestElement;
import io.metersphere.project.service.MoveNodeService;
import io.metersphere.project.service.ProjectService;
import io.metersphere.sdk.exception.MSException;
import io.metersphere.sdk.util.BeanUtils;
import io.metersphere.sdk.util.JSON;
import io.metersphere.sql.domain.SqlDebug;
import io.metersphere.sql.domain.SqlDebugBlob;
import io.metersphere.sql.domain.SqlDebugExample;
import io.metersphere.sql.mapper.SqlDebugBlobMapper;
import io.metersphere.sql.mapper.SqlDebugMapper;
import io.metersphere.sql.pojo.dto.debug.SqlDebugAddRequest;
import io.metersphere.sql.pojo.dto.debug.SqlDebugDTO;
import io.metersphere.sql.pojo.dto.debug.SqlDebugUpdateRequest;
import io.metersphere.sql.service.SqlCommonService;
import io.metersphere.sql.utils.SqlDataUtils;
import io.metersphere.system.uid.IDGenerator;
import io.metersphere.system.utils.ServiceUtils;
import jakarta.annotation.Resource;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static io.metersphere.sql.controller.result.SqlResultCode.SQL_DEBUG_EXIST;

@Service
@Transactional(rollbackFor = Exception.class)
public class SqlDebugService extends MoveNodeService {

    @Autowired
    SqlDebugMapper sqlDebugMapper;

    @Autowired
    SqlDebugBlobMapper sqlDebugBlobMapper;

    @Resource
    private SqlCommonService sqlCommonService;

    public SqlDebug add(SqlDebugAddRequest request, String createUser) {
        ProjectService.checkResourceExist(request.getProjectId());
        SqlDebug sqlDebug = new SqlDebug();
        BeanUtils.copyBean(sqlDebug, request);
        sqlDebug.setCreateUser(createUser);
        checkAddExist(sqlDebug, createUser);
        sqlDebug.setId(IDGenerator.nextStr());
        sqlDebug.setCreateTime(System.currentTimeMillis());
        sqlDebug.setUpdateTime(System.currentTimeMillis());
        sqlDebug.setUpdateUser(sqlDebug.getCreateUser());
        sqlDebug.setPos(getNextOrder(createUser));

        sqlDebugMapper.insert(sqlDebug);
        SqlDebugBlob sqlDebugBlob = new SqlDebugBlob();
        sqlDebugBlob.setId(sqlDebug.getId());
        sqlDebugBlob.setRequest(getMsTestElementStr(request.getRequest()).getBytes());
        sqlDebugBlobMapper.insert(sqlDebugBlob);

        return sqlDebug;
    }

    private void checkAddExist(SqlDebug sqlDebug, String userId) {
        SqlDebugExample example = new SqlDebugExample();
        example.createCriteria()
                .andNameEqualTo(sqlDebug.getName())
                .andCreateUserEqualTo(userId)
                .andModuleIdEqualTo(sqlDebug.getModuleId());
        if (CollectionUtils.isNotEmpty(sqlDebugMapper.selectByExample(example))) {
            throw new MSException(SQL_DEBUG_EXIST);
        }
    }

    private String getMsTestElementStr(Object request) {
        String requestStr = JSON.toJSONString(request);
        AbstractMsTestElement msTestElement = SqlDataUtils.parseObject(requestStr, AbstractMsTestElement.class);
        // 手动校验参数
        ServiceUtils.validateParam(msTestElement);
        return requestStr;
    }

    public SqlDebugDTO get(String id) {
        checkResourceExist(id);
        SqlDebug sqlDebug = sqlDebugMapper.selectByPrimaryKey(id);
        SqlDebugBlob sqlDebugBlob = sqlDebugBlobMapper.selectByPrimaryKey(id);
        SqlDebugDTO sqlDebugDTO = new SqlDebugDTO();
        BeanUtils.copyBean(sqlDebugDTO, sqlDebug);
        AbstractMsTestElement msTestElement = SqlDataUtils.parseObject(new String(sqlDebugBlob.getRequest()), AbstractMsTestElement.class);
        sqlCommonService.setEnableCommonScriptProcessorInfo(msTestElement);
        sqlDebugDTO.setRequest(msTestElement);
        sqlDebugDTO.setResponse(sqlDebugDTO.getResponse());
        return sqlDebugDTO;
    }

    public SqlDebug update(SqlDebugUpdateRequest request, String updateUser) {
        checkResourceExist(request.getId());
        SqlDebug sqlDebug = BeanUtils.copyBean(new SqlDebug(), request);
        SqlDebug originsqlDebug = sqlDebugMapper.selectByPrimaryKey(request.getId());
        checkUpdateExist(sqlDebug, originsqlDebug, updateUser);
        sqlDebug.setUpdateUser(updateUser);
        sqlDebug.setUpdateTime(System.currentTimeMillis());
        sqlDebugMapper.updateByPrimaryKeySelective(sqlDebug);

        if (request.getRequest() != null) {
            SqlDebugBlob sqlDebugBlob = new SqlDebugBlob();
            sqlDebugBlob.setId(request.getId());
            sqlDebugBlob.setRequest(getMsTestElementStr(request.getRequest()).getBytes());
            sqlDebugBlobMapper.updateByPrimaryKeySelective(sqlDebugBlob);
        }

        return sqlDebug;
    }

    private void checkUpdateExist(SqlDebug sqlDebug, SqlDebug originSqlDebug, String userId) {
        if (StringUtils.isBlank(sqlDebug.getName())) {
            return;
        }
        SqlDebugExample example = new SqlDebugExample();
        example.createCriteria()
                .andIdNotEqualTo(sqlDebug.getId())
                .andCreateUserEqualTo(userId)
                .andModuleIdEqualTo(sqlDebug.getModuleId() == null ? originSqlDebug.getModuleId() : sqlDebug.getModuleId())
                .andNameEqualTo(sqlDebug.getName());
        if (CollectionUtils.isNotEmpty(sqlDebugMapper.selectByExample(example))) {
            throw new MSException(SQL_DEBUG_EXIST);
        }
    }
    // TODO：研究一下下面的方法
    private SqlDebug checkResourceExist(String id) {
        return ServiceUtils.checkResourceExist(sqlDebugMapper.selectByPrimaryKey(id), "permission.api_debug.name");
    }



    @Override
    public long getNextOrder(String projectId) {
        return 0;
    }

    @Override
    public void updatePos(String id, long pos) {

    }

    @Override
    public void refreshPos(String testPlanId) {

    }
}
