package io.metersphere.sql.service.definition;

import io.metersphere.api.domain.ApiDefinitionModule;
import io.metersphere.api.domain.ApiTestCase;
import io.metersphere.plugin.api.spi.AbstractMsTestElement;
import io.metersphere.project.service.ProjectService;
import io.metersphere.sdk.dto.api.task.TaskRequestDTO;
import io.metersphere.sdk.util.BeanUtils;
import io.metersphere.sdk.util.JSON;
import io.metersphere.sdk.util.Translator;
import io.metersphere.sql.domain.SqlDefinition;
import io.metersphere.sql.domain.SqlDefinitionBlob;
import io.metersphere.sql.domain.SqlDefinitionModule;
import io.metersphere.sql.mapper.*;
import io.metersphere.sql.pojo.dto.definition.SqlDefinitionAddRequest;
import io.metersphere.sql.pojo.dto.definition.SqlDefinitionDTO;
import io.metersphere.sql.pojo.dto.definition.SqlDefinitionPageRequest;
import io.metersphere.sql.pojo.dto.definition.SqlDefinitionRunRequest;
import io.metersphere.sql.pojo.vo.ExecuteResultVO;
import io.metersphere.sql.utils.SqlDataUtils;
import io.metersphere.system.service.UserLoginService;
import io.metersphere.system.uid.IDGenerator;
import io.metersphere.system.utils.ServiceUtils;
import jakarta.annotation.Resource;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import io.metersphere.sql.constant.SqlCoverageConstants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional(rollbackFor = Exception.class)
public class SqlDefinitionService {

    @Resource
    private SqlDefinitionMapper sqlDefinitionMapper;

    @Resource
    private SqlDefinitionBlobMapper sqlDefinitionBlobMapper;

    @Resource
    private ExtSqlTestCaseMapper extSqlTestCaseMapper;

    @Resource
    private ExtSqlDefinitionModuleMapper extSqlDefinitionModuleMapper;

    @Resource
    private UserLoginService userLoginService;

    public TaskRequestDTO debug(SqlDefinitionRunRequest request) {
        // TODO：需要参考ms的api测试，看一下是否需要返回必要信息
        return null;
    }

    @Resource
    private ExtSqlDefinitionMapper extSqlDefinitionMapper;

    public List<String> getrequestAndResp(String id){
        SqlDefinitionBlob sqlDefinitionBlob = sqlDefinitionBlobMapper.selectByPrimaryKey(id);
        return List.of(new String(sqlDefinitionBlob.getRequest()),new String(sqlDefinitionBlob.getResponse()));
    }

    public SqlDefinition create(SqlDefinitionAddRequest request, String userId) {
        ProjectService.checkResourceExist(request.getProjectId());
        SqlDefinition sqlDefinition = new SqlDefinition();
        BeanUtils.copyBean(sqlDefinition, request);
        // TODO：下面的检查代码先略过，后面再加
//        sqlDefinition.setVersionId(StringUtils.defaultIfBlank(request.getVersionId(), extBaseProjectVersionMapper.getDefaultVersion(request.getProjectId())));
//        checkAddExist(sqlDefinition);
//        checkResponseNameCode(request.getResponse());
        sqlDefinition.setId(IDGenerator.nextStr());
        // TODO：这个字段应该是SQL测试用不到的，因为没有测试用例和场景的层级
//        sqlDefinition.setNum(getNextNum(request.getProjectId()));
        // TODO：这个是排序的id，暂时跳过不做
//        sqlDefinition.setPos(getNextOrder(request.getProjectId()));
        sqlDefinition.setLatest(true);
        sqlDefinition.setStatus(request.getStatus());
        sqlDefinition.setCreateUser(userId);
        sqlDefinition.setUpdateUser(userId);
        sqlDefinition.setCreateTime(System.currentTimeMillis());
        sqlDefinition.setUpdateTime(System.currentTimeMillis());
        sqlDefinition.setRefId(sqlDefinition.getId());
        // TODO：tags部分还需要规划一下，先跳过
//        if (CollectionUtils.isNotEmpty(request.getTags())) {
//            sqlDefinition.setTags(ServiceUtils.parseTags(request.getTags()));
//        }
        sqlDefinitionMapper.insertSelective(sqlDefinition);
        SqlDefinitionBlob sqlDefinitionBlob = new SqlDefinitionBlob();
        sqlDefinitionBlob.setId(sqlDefinition.getId());
        sqlDefinitionBlob.setRequest(getMsTestElementStr(request.getRequest()).getBytes());
        if (request.getResponse() != null) {
            List<ExecuteResultVO> msHttpResponse = request.getResponse();
            msHttpResponse.forEach(item -> item.setId(IDGenerator.nextStr()));
            sqlDefinitionBlob.setResponse(JSON.toJSONString(msHttpResponse).getBytes());
        }
        sqlDefinitionBlobMapper.insertSelective(sqlDefinitionBlob);
        // TODO：暂不清楚自定义字段的作用， 先保留代码注释部分
//        //保存自定义字段
////        List<SqlDefinitionCustomField> customFields = request.getCustomFields();
////        if (CollectionUtils.isNotEmpty(customFields)) {
////            customFields = customFields.stream().distinct().toList();
////            batchInsertCustomFields(sqlDefinition.getId(), customFields);
////        }
////
        return sqlDefinition;
    }

    private String getMsTestElementStr(Object request) {
        String requestStr = JSON.toJSONString(request);
        AbstractMsTestElement msTestElement = SqlDataUtils.parseObject(requestStr, AbstractMsTestElement.class);
        // 手动校验参数
        ServiceUtils.validateParam(msTestElement);
        return requestStr;
    }

    // TODO：ms的api接口测试中，这里有protocol协议的过滤，如果以后增加其他数据库协议支持且需要前端过滤的话，可以考虑在这里进行支持，虽然这里保留了protocol参数但是实际不起作用
//    public List<String> getQueryExcludeIds( Map<String, List<String>> queryFilter, String projectId, List<String> protocol ) {
//        if (queryFilter != null && CollectionUtils.isNotEmpty(queryFilter.get("unCoverFrom"))) {
//            String unCoverFrom = queryFilter.get("unCoverFrom").getFirst();
//            if (SqlCoverageConstants.SQL_CASE.equals(unCoverFrom)) {
//                return this.selectApiIdInCase(projectId,protocol);
//            } else if (SqlCoverageConstants.SQL_SCENARIO.equals(unCoverFrom)) {
////                return this.selectApiIdInScenarioStep(projectId, protocol, null);
//                return null;
//            }
//        }
//        return null;
//    }
//
//    private List<String> selectApiIdInCase(String projectId, List<String> protocols) {
//        return extSqlTestCaseMapper.selectSqlIdByProjectAndProtocol(projectId, protocols);
//    }

    public List<SqlDefinitionDTO> getApiDefinitionPage(SqlDefinitionPageRequest request, String userId) {
//        if (CollectionUtils.isEmpty(request.getProtocols())) {
//            return new ArrayList<>();
//        }
        List<SqlDefinitionDTO> list = extSqlDefinitionMapper.list(request);
        processApiDefinitions(list);
        return list;
    }

    public void processApiDefinitions(List<SqlDefinitionDTO> list) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        Set<String> userIds = extractUserIds(list);
        Map<String, String> userMap = userLoginService.getUserNameMap(new ArrayList<>(userIds));

        List<String> apiDefinitionIds = list.stream().map(SqlDefinitionDTO::getId).toList();
//        List<ApiTestCase> apiCaseList = extSqlDefinitionMapper.selectNotInTrashCaseIdsByApiIds(apiDefinitionIds);
//        Map<String, List<ApiTestCase>> apiCaseMap = apiCaseList.stream().
//                collect(Collectors.groupingBy(ApiTestCase::getApiDefinitionId));

        List<String> moduleIds = list.stream().map(SqlDefinitionDTO::getModuleId).toList();
        List<SqlDefinitionModule> modules = extSqlDefinitionModuleMapper.getNameInfoByIds(moduleIds);
        Map<String, String> moduleNameMap = modules.stream()
                .collect(Collectors.toMap(SqlDefinitionModule::getId, SqlDefinitionModule::getName));

        list.forEach(item -> {
            // Convert User IDs to Names
            item.setCreateUserName(userMap.get(item.getCreateUser()));
            item.setDeleteUserName(userMap.get(item.getDeleteUser()));
            item.setUpdateUserName(userMap.get(item.getUpdateUser()));

            // Calculate API Case Metrics
//            List<ApiTestCase> apiTestCases = apiCaseMap.get(item.getId());
//            if (apiTestCases != null) {
//                item.setCaseTotal(apiTestCases.size());
//            } else {
//                item.setCaseTotal(0);
//            }

            if (moduleNameMap.get(item.getModuleId()) == null) {
                item.setModuleName(Translator.get("api_unplanned_request"));
            } else {
                item.setModuleName(moduleNameMap.get(item.getModuleId()));
            }
        });
    }

    private Set<String> extractUserIds(List<SqlDefinitionDTO> list) {
        return list.stream()
                .flatMap(apiDefinition -> Stream.of(apiDefinition.getUpdateUser(), apiDefinition.getDeleteUser(), apiDefinition.getCreateUser()))
                .collect(Collectors.toSet());
    }
}
