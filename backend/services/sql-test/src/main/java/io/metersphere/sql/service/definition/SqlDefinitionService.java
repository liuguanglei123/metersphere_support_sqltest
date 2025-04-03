package io.metersphere.sql.service.definition;

import io.metersphere.api.domain.*;
import io.metersphere.plugin.api.spi.AbstractMsTestElement;
import io.metersphere.project.service.ProjectService;
import io.metersphere.sdk.dto.api.task.TaskRequestDTO;
import io.metersphere.sdk.dto.result.ListResult;
import io.metersphere.sdk.exception.MSException;
import io.metersphere.sdk.util.BeanUtils;
import io.metersphere.sdk.util.JSON;
import io.metersphere.sdk.util.SubListUtils;
import io.metersphere.sdk.util.Translator;
import io.metersphere.sql.controller.result.SqlResultCode;
import io.metersphere.sql.domain.*;
import io.metersphere.sql.mapper.*;
import io.metersphere.sql.pojo.dto.SqlDefinitionExecuteInfo;
import io.metersphere.sql.pojo.dto.definition.*;
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

import java.util.*;
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

    @Resource
    private SqlDefinitionLogService sqlDefinitionLogService;

    @Resource
    private SqlDefinitionNoticeService sqlDefinitionNoticeService;

    @Resource
    SqlDefinitionModuleMapper sqlDefinitionModuleMapper;

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
            ListResult<ExecuteResultVO> msHttpResponse = request.getResponse();
            msHttpResponse.getData().forEach(item -> item.setId(IDGenerator.nextStr()));
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

    public void deleteToGc(String id, boolean deleteAllVersion, String userId) {
        SqlDefinition sqlDefinition = checkSqlDefinition(id);
        handleDeleteSqlDefinition(Collections.singletonList(id), deleteAllVersion, sqlDefinition.getProjectId(), userId, false);
    }

    public void handleDeleteSqlDefinition(List<String> ids, boolean deleteAllVersion, String projectId, String userId, boolean isBatch) {
        if (deleteAllVersion) {
            //全部删除  进入回收站
            List<String> refIds = extSqlDefinitionMapper.getRefIds(ids, false);
            if (CollectionUtils.isNotEmpty(refIds)) {
                SubListUtils.dealForSubList(refIds, 2000, subRefIds -> {
                    List<String> delSqlIds = extSqlDefinitionMapper.getIdsByRefId(subRefIds, false);

                    // TODO： 记录删除到回收站的日志, 单条注解记录
//                    if (isBatch) {
//                        sqlDefinitionLogService.batchDelLog(delSqlIds, userId, projectId);
//                        sqlDefinitionNoticeService.batchSendNotice(delSqlIds, userId, projectId, NoticeConstants.Event.DELETE);
//                    }
                    extSqlDefinitionMapper.batchDeleteByRefId(subRefIds, userId, projectId);
                });
            }
        } else {
            // 列表删除
            if (!ids.isEmpty()) {
                SubListUtils.dealForSubList(ids, 2000, subList -> doDelete(subList, userId, projectId, isBatch));
            }
        }
    }

    private void doDelete(List<String> ids, String userId, String projectId, boolean isBatch) {
        if (CollectionUtils.isNotEmpty(ids)) {
            // 需要判断是否存在多个版本问题
            ids.forEach(id -> {
                SqlDefinition sqlDefinition = checkSqlDefinition(id);
                // 删除的数据是否为最新版本的数据，如果是则需要查询是否有多版本数据存在，需要去除当前删除的数据，更新剩余版本数据中最近的一条数据为最新的数据
                if (sqlDefinition.getLatest()) {
                    List<SqlDefinitionVersionDTO> sqlDefinitionVersions = extSqlDefinitionMapper.getSqlDefinitionByRefId(sqlDefinition.getRefId());
                    if (sqlDefinitionVersions.size() > 1) {
                        deleteAfterAction(sqlDefinitionVersions);
                    }
                }
            });
            // 记录删除到回收站的日志, 单条注解记录
            // TODO：
//            if (isBatch) {
//                sqlDefinitionLogService.batchDelLog(ids, userId, projectId);
//            }
            // 删除接口到回收站
            extSqlDefinitionMapper.batchDeleteById(ids, userId, projectId);
        }

    }

    private void deleteAfterAction(List<SqlDefinitionVersionDTO> sqlDefinitionVersions) {
        sqlDefinitionVersions.forEach(item -> {
            clearLatestVersion(item.getRefId(), item.getProjectId());
            SqlDefinition latestData = getLatestData(item.getRefId(), item.getProjectId());
            updateLatestVersion(latestData.getId(), latestData.getProjectId());
        });
    }

    // 清除多版本最新标识
    private void clearLatestVersion(String refId, String projectId) {
        extSqlDefinitionMapper.clearLatestVersion(refId, projectId);
    }

    // 更新最新版本标识
    private void updateLatestVersion(String id, String projectId) {
        extSqlDefinitionMapper.updateLatestVersion(id, projectId);
    }

    // 获取多版本最新一条数据
    private SqlDefinition getLatestData(String refId, String projectId) {
        SqlDefinitionExample sqlDefinitionExample = new SqlDefinitionExample();
        sqlDefinitionExample.createCriteria().andRefIdEqualTo(refId).andDeletedEqualTo(false).andProjectIdEqualTo(projectId);
        sqlDefinitionExample.setOrderByClause("update_time DESC");
        SqlDefinition sqlDefinition = sqlDefinitionMapper.selectByExample(sqlDefinitionExample).stream().findFirst().orElse(null);
        if (sqlDefinition == null) {
            throw new MSException(SqlResultCode.SQL_DEFINITION_NOT_EXIST);
        }
        return sqlDefinition;
    }

    /**
     * 校验SQL用例是否存在
     *
     * @param apiId 接口id
     */
    public SqlDefinition checkSqlDefinition(String apiId) {
        SqlDefinition sqlDefinition = sqlDefinitionMapper.selectByPrimaryKey(apiId);
        if (sqlDefinition == null) {
            throw new MSException(SqlResultCode.SQL_DEFINITION_NOT_EXIST);
        }
        return sqlDefinition;
    }

    public SqlDefinitionDTO get(String id, String userId) {
        // 1. 避免重复查询数据库，将查询结果传递给get方法
        SqlDefinition sqlDefinition = checkSqlDefinitionDeleted(id);
        return getSqlDefinitionInfo(id, userId, sqlDefinition);
    }

    public SqlDefinitionDTO getSqlDefinitionInfo(String id, String userId, SqlDefinition sqlDefinition) {
        SqlDefinitionDTO sqlDefinitionDTO = new SqlDefinitionDTO();
        BeanUtils.copyBean(sqlDefinitionDTO, sqlDefinition);
        // 2. 使用Optional避免空指针异常
        handleBlob(id, sqlDefinitionDTO);
        // 3. 查询自定义字段
//        handleCustomFields(id, apiDefinition.getProjectId(), apiDefinitionDTO);
        // 3. 使用Stream简化集合操作
        Set<String> userIds = extractUserIds(List.of(sqlDefinitionDTO));
        Map<String, String> userMap = userLoginService.getUserNameMap(new ArrayList<>(userIds));
        sqlDefinitionDTO.setCreateUserName(userMap.get(sqlDefinitionDTO.getCreateUser()));
        sqlDefinitionDTO.setUpdateUserName(userMap.get(sqlDefinitionDTO.getUpdateUser()));
        SqlDefinitionModule sqlDefinitionModule = sqlDefinitionModuleMapper.selectByPrimaryKey(sqlDefinitionDTO.getModuleId());
        if (sqlDefinitionModule != null) {
            sqlDefinitionDTO.setModuleName(sqlDefinitionModule.getName());
        } else {
            sqlDefinitionDTO.setModuleName(Translator.get("api_unplanned_request"));
        }
        return sqlDefinitionDTO;
    }

    public void handleBlob(String id, SqlDefinitionDTO sqlDefinitionDTO) {
        Optional<SqlDefinitionBlob> sqlDefinitionBlobOptional = Optional.ofNullable(sqlDefinitionBlobMapper.selectByPrimaryKey(id));
        sqlDefinitionBlobOptional.ifPresent(blob -> {
            AbstractMsTestElement msTestElement = SqlDataUtils.parseObject(new String(blob.getRequest()), AbstractMsTestElement.class);

//            sqlCommonService.setEnableCommonScriptProcessorInfo(msTestElement);
// TODO:待分析           sqlCommonService.setApiDefinitionExecuteInfo(msTestElement, sqlDefinitionDTO);

            sqlDefinitionDTO.setRequest(msTestElement);
            // blob.getResponse() 为 null 时不进行转换
            if (blob.getResponse() != null) {
                ListResult<ExecuteResultVO> sqlResponses = SqlDataUtils.parseArray(new String(blob.getResponse()), ExecuteResultVO.class);
                sqlDefinitionDTO.setResponse(sqlResponses);
            }
        });
    }

    public SqlDefinition checkSqlDefinitionDeleted(String apiId) {
        SqlDefinitionExample example = new SqlDefinitionExample();
        example.createCriteria().andIdEqualTo(apiId).andDeletedEqualTo(false);
        List<SqlDefinition> sqlDefinitions = sqlDefinitionMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(sqlDefinitions)) {
            throw new MSException(SqlResultCode.SQL_DEFINITION_NOT_EXIST);
        }
        return sqlDefinitions.getFirst();
    }

    public List<SqlDefinitionBlob> getBlobByIds(List<String> sqlIds) {
        if (CollectionUtils.isEmpty(sqlIds)) {
            return Collections.emptyList();
        }
        SqlDefinitionBlobExample sqlDefinitionBlobExample = new SqlDefinitionBlobExample();
        sqlDefinitionBlobExample.createCriteria().andIdIn(sqlIds);
        return sqlDefinitionBlobMapper.selectByExampleWithBLOBs(sqlDefinitionBlobExample);
    }

    public List<SqlDefinitionExecuteInfo> getModuleInfoByIds(List<String> apiIds) {
        return extSqlDefinitionMapper.getSqlDefinitionExecuteInfo(apiIds);
    }
}
