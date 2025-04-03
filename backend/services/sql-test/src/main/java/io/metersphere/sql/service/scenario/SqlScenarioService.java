package io.metersphere.sql.service.scenario;

import io.metersphere.api.domain.ApiDefinition;
import io.metersphere.api.domain.ApiScenarioCsvStep;
import io.metersphere.api.domain.ApiScenarioStep;
import io.metersphere.api.domain.ApiTestCase;
import io.metersphere.plugin.api.spi.AbstractMsTestElement;
import io.metersphere.plugin.api.spi.SqlAbstractMsTestElement;
import io.metersphere.project.mapper.ExtBaseProjectVersionMapper;
import io.metersphere.project.service.MoveNodeService;
import io.metersphere.sdk.constants.ApplicationNumScope;
import io.metersphere.sdk.constants.ScheduleResourceType;
import io.metersphere.sdk.domain.Environment;
import io.metersphere.sdk.domain.EnvironmentExample;
import io.metersphere.sdk.domain.EnvironmentGroup;
import io.metersphere.sdk.domain.EnvironmentGroupExample;
import io.metersphere.sdk.exception.MSException;
import io.metersphere.sdk.util.*;
import io.metersphere.sql.constant.SqlScenarioStepRefType;
import io.metersphere.sql.controller.result.SqlResultCode;
import io.metersphere.sql.domain.*;
import io.metersphere.sql.mapper.*;
import io.metersphere.sql.parse.StepParserFactory;
import io.metersphere.sql.parse.step.StepParser;
import io.metersphere.sql.pojo.dto.scenario.*;
import io.metersphere.sql.service.definition.SqlDefinitionService;
import io.metersphere.system.domain.Schedule;
import io.metersphere.system.domain.ScheduleExample;
import io.metersphere.system.mapper.ScheduleMapper;
import io.metersphere.system.service.UserLoginService;
import io.metersphere.system.uid.IDGenerator;
import io.metersphere.system.uid.NumGenerator;
import io.metersphere.system.utils.ScheduleUtils;
import io.metersphere.system.utils.ServiceUtils;
import jakarta.annotation.Resource;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import io.metersphere.sql.constant.SqlScenarioStepType;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.metersphere.sql.controller.result.SqlResultCode.SQL_SCENARIO_CIRCULAR_REFERENCE;
import static io.metersphere.sql.controller.result.SqlResultCode.SQL_SCENARIO_EXIST;

@Service
@Transactional(rollbackFor = Exception.class)
public class SqlScenarioService extends MoveNodeService {

    @Resource
    ExtSqlScenarioMapper extSqlScenarioMapper;

    @Resource
    UserLoginService userLoginService;

    @Resource
    SqlScenarioModuleMapper sqlScenarioModuleMapper;

    @Resource
    ScheduleMapper scheduleMapper;

    @Resource
    SqlScenarioMapper sqlScenarioMapper;

    @Resource
    SqlScenarioBlobMapper sqlScenarioBlobMapper;

    @Resource
    SqlScenarioStepMapper sqlScenarioStepMapper;

    @Resource
    SqlDefinitionService sqlDefinitionService;


    @Resource
    private ExtBaseProjectVersionMapper extBaseProjectVersionMapper;

    @Resource
    private SqlScenarioStepBlobMapper sqlScenarioStepBlobMapper;

    @Resource
    private ExtSqlScenarioStepMapper extSqlScenarioStepMapper;

    public SqlScenario add(SqlScenarioAddRequest request, String creator) {
        checkAddExist(request);
        request.setTags(ServiceUtils.parseTags(request.getTags()));
        SqlScenario scenario = getAddSqlScenario(request, creator);
        scenario.setStepTotal(request.getSteps().size());
        sqlScenarioMapper.insert(scenario);

        // 更新场景配置
        SqlScenarioBlob sqlScenarioBlob = new SqlScenarioBlob();
        sqlScenarioBlob.setId(scenario.getId());
        sqlScenarioBlob.setConfig(JSON.toJSONString(request.getScenarioConfig()).getBytes());
        sqlScenarioBlobMapper.insert(sqlScenarioBlob);

        // 处理复制场景时的文件复制 TODO：暂时不需要
//        handleCopyFromScenarioFile(request, scenario, creator);

        // 处理csv文件 TODO：暂时不需要
//        handCsvFilesAdd(request, creator, scenario);

        // 处理添加的步骤
        handleStepAdd(request, scenario, creator);

        // 处理步骤文件 TODO：暂时不需要
//        apiScenarioFileService.handleStepFilesAdd(request, creator, scenario);

        return scenario;
    }

    private void handleStepAdd(SqlScenarioAddRequest request, SqlScenario scenario, String userId) {
        // 插入步骤
        if (CollectionUtils.isNotEmpty(request.getSteps())) {
            checkCircularRef(scenario.getId(), request.getSteps());
            // 获取待添加的步骤
// TODO：           List<SqlScenarioCsvStep> csvSteps = new ArrayList<>();
//            List<SqlScenarioStep> steps = getSqlScenarioSteps(null, request.getSteps(), csvSteps);
            List<SqlScenarioStep> steps = getSqlScenarioSteps(null, request.getSteps(), new ArrayList<>());
            steps.forEach(step -> step.setScenarioId(scenario.getId()));
            // 处理特殊的步骤详情
            SqlScenarioCopyStepMap sqlScenarioCopyStepMap = addSpecialStepDetails(request.getSteps(), request.getStepDetails());

            if (CollectionUtils.isNotEmpty(steps)) {
                sqlScenarioStepMapper.batchInsert(steps);
            }

            // TODO：处理copy的步骤文件
//            sqlScenarioFileService.handleSaveCopyStepFiles(sqlScenarioCopyStepMap, request.getStepDetails(), scenario, userId);

            List<SqlScenarioStepBlob> sqlScenarioStepBlobs = getUpdateStepBlobs(steps, request.getStepDetails());
            sqlScenarioStepBlobs.forEach(step -> step.setScenarioId(scenario.getId()));

            if (CollectionUtils.isNotEmpty(sqlScenarioStepBlobs)) {
                sqlScenarioStepBlobMapper.batchInsert(sqlScenarioStepBlobs);
            }

//            csvSteps.forEach(step -> step.setScenarioId(scenario.getId()));
//
//            csvSteps = filterNotExistCsv(request.getScenarioConfig(), csvSteps);
//            saveStepCsv(scenario.getId(), csvSteps);
        }
    }

    /**
     * 获取待更新的 SqlScenarioStepBlob 列表
     */
    public List<SqlScenarioStepBlob> getUpdateStepBlobs(List<SqlScenarioStep> sqlScenarioSteps, Map<String, Object> stepDetails) {
        if (MapUtils.isEmpty(stepDetails)) {
            return Collections.emptyList();
        }

        Map<String, SqlScenarioStep> scenarioStepMap = sqlScenarioSteps.stream()
                .collect(Collectors.toMap(SqlScenarioStep::getId, Function.identity()));

        List<SqlScenarioStepBlob> sqlScenarioStepBlobs = new ArrayList<>();
        stepDetails.forEach((stepId, stepDetail) -> {
            SqlScenarioStep step = scenarioStepMap.get(stepId);
            if (step == null) {
                return;
            }
            if (hasDetail(step.getStepType(), step.getRefType())) {
                // 非引用的步骤，如果有编辑内容，保存到blob表
                // 如果引用的是接口定义，也保存详情，因为应用接口定义允许修改参数
                SqlScenarioStepBlob sqlScenarioStepBlob = new SqlScenarioStepBlob();
                sqlScenarioStepBlob.setId(stepId);
                if (stepDetail instanceof byte[] detailBytes) {
                    sqlScenarioStepBlob.setContent(detailBytes);
                } else {
                    sqlScenarioStepBlob.setContent(JSON.toJSONString(stepDetail).getBytes());
                }
                sqlScenarioStepBlobs.add(sqlScenarioStepBlob);
            }
        });
        return sqlScenarioStepBlobs;
    }


    /**
     * 是否有步骤详情
     * 非完全引用的步骤
     *
     * @param stepType
     * @param refType
     * @return
     */
    private boolean hasDetail(String stepType, String refType) {
        return !isRef(refType) || isRefSql(stepType, refType);
    }

    private boolean isRef(String refType) {
        return StringUtils.equals(refType, SqlScenarioStepRefType.REF.name());
    }

    /**
     * 判断步骤是否是引用的接口定义
     * 引用的接口定义允许修改参数值，需要特殊处理
     */
    public boolean isRefSql(String stepType, String refType) {
        return isSql(stepType) && StringUtils.equals(refType, SqlScenarioStepRefType.REF.name());
    }

    public boolean isSql(String stepType) {
        return StringUtils.equals(stepType, SqlScenarioStepType.SQL.name());
    }

    /**
     * 补充
     * 部分引用的 detail
     * copyFromStepId 的 detail
     * isNew 的资源的 detail
     */
    public SqlScenarioCopyStepMap addSpecialStepDetails(List<SqlScenarioStepRequest> steps, Map<String, Object> stepDetails) {
        if (CollectionUtils.isEmpty(steps)) {
            return null;
        }

        // key 的 stepId，value 为 copyFrom 的步骤ID
        Map<String, String> copyFromStepIdMap = new HashMap<>();
        // key 的 stepId，value 为 copyFrom 的接口ID
        Map<String, String> isNewSqlResourceMap = new HashMap<>();
        // key 的 stepId，value 为 copyFrom 的接口用例ID
        Map<String, String> isNewSqlCaseResourceMap = new HashMap<>();

        // 遍历步骤树
        traversalStepTree(steps, (step) -> {
            SqlScenarioStepRequest stepRequest = (SqlScenarioStepRequest) step;

            // 补充部分引用的 detail
            addPartialRefStepDetail(stepDetails, step);

            // 如果该步骤没有步骤详情
            if (stepDetails.get(stepRequest.getId()) == null && hasDetail(step)) {
                if (isSql(step) && BooleanUtils.isTrue(((SqlScenarioStepRequest) step).getIsNew())) {
                    // 处理 isNew 的用例步骤
                    if (isSql(step.getStepType())) {
                        isNewSqlResourceMap.put(step.getId(), step.getResourceId());
                    } else {
                        isNewSqlCaseResourceMap.put(step.getId(), step.getResourceId());
                    }
                } else if (StringUtils.isNotBlank(stepRequest.getCopyFromStepId())) {
                    // 处理 copyFromStep 的情况
                    if (stepDetails.containsKey(stepRequest.getCopyFromStepId())) {
                        // 如果有传 copyFromStepId 的详情，优先使用
                        stepDetails.put(step.getId(), stepDetails.get(stepRequest.getCopyFromStepId()));
                    } else {
                        // 没有传，则记录ID，后续统一查库
                        copyFromStepIdMap.put(stepRequest.getId(), stepRequest.getCopyFromStepId());
                    }
                }
            }

            if (isRefOrPartialRef(step.getRefType())) {
                // 引用或者部分引用类型不解析子步骤
                return false;
            }
            return true;
        });

        // 处理新copy的接口定义的步骤详情
        putCopyStepDetails(stepDetails, isNewSqlResourceMap, (subIds, copyFromBlobMap) -> {
            sqlDefinitionService.getBlobByIds(subIds)
                    .forEach(sqlDefinitionBlob -> copyFromBlobMap.put(sqlDefinitionBlob.getId(), sqlDefinitionBlob.getRequest()));
        });

        // 处理新copy的步骤的步骤详情
        putCopyStepDetails(stepDetails, copyFromStepIdMap, (subIds, copyFromBlobMap) -> {
            SqlScenarioStepBlobExample example = new SqlScenarioStepBlobExample();
            example.createCriteria().andIdIn(subIds);
            sqlScenarioStepBlobMapper.selectByExampleWithBLOBs(example)
                    .forEach(scenarioStepBlob -> copyFromBlobMap.put(scenarioStepBlob.getId(), scenarioStepBlob.getContent()));
        });

        SqlScenarioCopyStepMap sqlScenarioCopyStepMap = new SqlScenarioCopyStepMap();
        sqlScenarioCopyStepMap.setCopyFromStepIdMap(copyFromStepIdMap);
        sqlScenarioCopyStepMap.setIsNewSqlResourceMap(isNewSqlResourceMap);
        return sqlScenarioCopyStepMap;
    }

    private boolean hasDetail(SqlScenarioStepCommonDTO step) {
        return hasDetail(step.getStepType(), step.getRefType());
    }

    private boolean isSql(SqlScenarioStepCommonDTO step) {
        return isSql(step.getStepType());
    }

    /**
     * @param stepDetails
     * @param copyFromIdMap        key 为 stepID，value 为复制的 resourceId 或者 stepId
     * @param handlePutBlobMapFunc
     */
    private void putCopyStepDetails(Map<String, Object> stepDetails, Map<String, String> copyFromIdMap, BiConsumer<List<String>, Map<String, byte[]>> handlePutBlobMapFunc) {
        if (MapUtils.isEmpty(copyFromIdMap)) {
            return;
        }
        // 处理新copy的步骤的步骤详情
        Map<String, byte[]> copyFromBlobMap = new HashMap<>();
        SubListUtils.dealForSubList(copyFromIdMap.values().stream().toList(), 50, (subIds) -> {
            handlePutBlobMapFunc.accept(subIds, copyFromBlobMap);
        });

        copyFromIdMap.forEach((stepId, copyFromId) -> {
            if (copyFromBlobMap.containsKey(copyFromId)) {
                stepDetails.put(stepId, copyFromBlobMap.get(copyFromId));
            }
        });
    }

    /**
     * 补充部分引用的 detail
     *
     * @param stepDetails
     * @param step
     */
    private void addPartialRefStepDetail(Map<String, Object> stepDetails, SqlScenarioStepCommonDTO step) {
        if (isPartialRef(step)) {
            // 如果是部分引用，blob表保存启用的子步骤ID
            stepDetails.put(step.getId(), getPartialRefStepDetail(step.getChildren()));
        }
    }

    /**
     * 获取步骤及子步骤中 enable 的步骤ID
     */
    private PartialRefStepDetail getPartialRefStepDetail(List<? extends SqlScenarioStepCommonDTO> steps) {
        PartialRefStepDetail partialRefStepDetail = new PartialRefStepDetail();
        if (CollectionUtils.isEmpty(steps)) {
            return partialRefStepDetail;
        }
        Set<String> enableSteps = partialRefStepDetail.getEnableStepIds();
        Set<String> disableStepIds = partialRefStepDetail.getDisableStepIds();
        for (SqlScenarioStepCommonDTO step : steps) {
            if (BooleanUtils.isTrue(step.getEnable())) {
                enableSteps.add(step.getId());
            } else {
                disableStepIds.add(step.getId());
            }
            // 完全引用和部分引用不解析子步骤
            if (!isRefOrPartialRef(step.getRefType())) {
                // 获取子步骤中 enable 的步骤
                PartialRefStepDetail childPartialRefStepDetail = getPartialRefStepDetail(step.getChildren());
                enableSteps.addAll(childPartialRefStepDetail.getEnableStepIds());
                disableStepIds.addAll(childPartialRefStepDetail.getDisableStepIds());
            }
        }
        return partialRefStepDetail;
    }

    /**
     * 判断是否是部分引用，暂时在SQL中用不到这个场景，该方法是从api测试中同步过来的
     * @param step
     * @return
     */
    private boolean isPartialRef(SqlScenarioStepCommonDTO step) {
        return isScenarioStep(step.getStepType()) &&
                StringUtils.equals(step.getRefType(), SqlScenarioStepRefType.PARTIAL_REF.name());
    }

    public static boolean isScenarioStep(String stepType) {
        return StringUtils.equals(stepType, SqlScenarioStepType.SQL_SCENARIO.name());
    }

    /**
     * 查询部分引用的步骤的详情
     *
     * @param steps
     * @return
     */
    public Map<String, String> getPartialRefStepDetailMap(List<? extends SqlScenarioStepCommonDTO> steps) {
        List<String> needBlobStepIds = steps.stream()
                .filter(this::isPartialRef)
                .map(SqlScenarioStepCommonDTO::getId)
                .toList();

        return getStepBlobByIds(needBlobStepIds).stream()
                .collect(Collectors.toMap(SqlScenarioStepBlob::getId, blob -> new String(blob.getContent())));
    }

    public List<SqlScenarioStepBlob> getStepBlobByIds(List<String> stepIds) {
        if (CollectionUtils.isEmpty(stepIds)) {
            return Collections.emptyList();
        }
        SqlScenarioStepBlobExample example = new SqlScenarioStepBlobExample();
        example.createCriteria().andIdIn(stepIds);
        return sqlScenarioStepBlobMapper.selectByExampleWithBLOBs(example);
    }

    /**
     * 解析步骤树结构
     * 获取待更新的 sqlScenarioStep 列表
     */
    public List<SqlScenarioStep> getSqlScenarioSteps(SqlScenarioStepCommonDTO parent,
                                                     List<SqlScenarioStepRequest> steps, List<SqlScenarioCsvStep> csvSteps) {
        if (CollectionUtils.isEmpty(steps)) {
            return Collections.emptyList();
        }
        List<SqlScenarioStep> sqlScenarioSteps = new ArrayList<>();
        long sort = 1;
        for (SqlScenarioStepRequest step : steps) {
            SqlScenarioStep sqlScenarioStep = new SqlScenarioStep();
            BeanUtils.copyBean(sqlScenarioStep, step);
            sqlScenarioStep.setSort(sort++);
            if (parent != null) {
                sqlScenarioStep.setParentId(parent.getId());
            }
            if (step.getConfig() != null) {
                sqlScenarioStep.setConfig(JSON.toJSONString(step.getConfig()));
            }
            sqlScenarioSteps.add(sqlScenarioStep);

            if (StringUtils.equals(step.getStepType(), SqlScenarioStepType.SQL_SCENARIO.name())
                    && StringUtils.equalsAny(step.getRefType(), SqlScenarioStepRefType.REF.name(), SqlScenarioStepRefType.PARTIAL_REF.name())) {
                // 引用的步骤不解析子步骤
                continue;
            }

            if (CollectionUtils.isNotEmpty(step.getCsvIds())) {
                //如果是csv文件  需要保存到sqlScenarioCsvStep表中
                step.getCsvIds().forEach(csvId -> {
                    SqlScenarioCsvStep csvStep = new SqlScenarioCsvStep();
                    csvStep.setId(IDGenerator.nextStr());
                    csvStep.setStepId(sqlScenarioStep.getId());
                    csvStep.setFileId(csvId);
                    csvSteps.add(csvStep);
                });
            }
            // 解析子步骤
            sqlScenarioSteps.addAll(getSqlScenarioSteps(step, step.getChildren(), csvSteps));
        }
        return sqlScenarioSteps;
    }
    /**
     * 检测循环依赖
     *
     * @param scenarioId
     * @param steps
     */
    public void checkCircularRef(String scenarioId, List<SqlScenarioStepRequest> steps) {
        traversalStepTree(steps, step -> {
            if (isRefOrPartialRef(step.getRefType()) && StringUtils.equals(step.getResourceId(), scenarioId)) {
                throw new MSException(SQL_SCENARIO_CIRCULAR_REFERENCE);
            }
            return true;
        });
    }

    public boolean isRefOrPartialRef(String refType) {
        return StringUtils.equalsAny(refType, SqlScenarioStepRefType.REF.name(), SqlScenarioStepRefType.PARTIAL_REF.name());
    }

    /**
     * 遍历步骤树
     */
    public void traversalStepTree(List<? extends SqlScenarioStepCommonDTO> steps, Function<SqlScenarioStepCommonDTO, Boolean> handleStepFunc) {
        if (CollectionUtils.isEmpty(steps)) {
            return;
        }
        for (SqlScenarioStepCommonDTO step : steps) {
            Boolean isParseChild = handleStepFunc.apply(step);
            if (BooleanUtils.isTrue(isParseChild)) {
                traversalStepTree(step.getChildren(), handleStepFunc);
            }
        }
    }

    private SqlScenario getAddSqlScenario(SqlScenarioAddRequest request, String creator) {
        SqlScenario scenario = new SqlScenario();
        BeanUtils.copyBean(scenario, request);
        scenario.setId(IDGenerator.nextStr());
        scenario.setNum(getNextNum(request.getProjectId()));
        scenario.setPos(getNextOrder(request.getProjectId()));
        scenario.setLatest(true);
        scenario.setCreateUser(creator);
        scenario.setUpdateUser(creator);
        scenario.setCreateTime(System.currentTimeMillis());
        scenario.setUpdateTime(System.currentTimeMillis());
        scenario.setVersionId(extBaseProjectVersionMapper.getDefaultVersion(request.getProjectId()));
        scenario.setRefId(scenario.getId());
        scenario.setLastReportStatus(StringUtils.EMPTY);
        scenario.setDeleted(false);
        scenario.setRequestPassRate("0");
        scenario.setStepTotal(CollectionUtils.isEmpty(request.getSteps()) ? 0 : request.getSteps().size());
        return scenario;
    }

    public long getNextNum(String projectId) {
        return NumGenerator.nextNum(projectId, ApplicationNumScope.SQL_SCENARIO);
    }

    private void checkAddExist(SqlScenarioAddRequest sqlScenario) {
        SqlScenarioExample example = new SqlScenarioExample();
        // 统一模块下名称不能重复
        example.createCriteria()
                .andNameEqualTo(sqlScenario.getName())
                .andModuleIdEqualTo(sqlScenario.getModuleId())
                .andDeletedEqualTo(false)
                .andProjectIdEqualTo(sqlScenario.getProjectId());
        if (sqlScenarioMapper.countByExample(example) > 0) {
            throw new MSException(SQL_SCENARIO_EXIST);
        }
    }

    public List<SqlScenarioDTO> getScenarioPage(SqlScenarioPageRequest request, boolean isRepeat, String testPlanId) {
        List<SqlScenarioDTO> list = extSqlScenarioMapper.list(request, isRepeat, testPlanId);
        if (CollectionUtils.isNotEmpty(list)) {
            processSqlScenario(list);
        }
        return list;
    }

    private void processSqlScenario(List<SqlScenarioDTO> scenarioLists) {
        Set<String> userIds = extractUserIds(scenarioLists);
        Map<String, String> userMap = userLoginService.getUserNameMap(new ArrayList<>(userIds));
        List<String> envIds = scenarioLists.stream().map(SqlScenarioDTO::getEnvironmentId).toList();
        EnvironmentExample environmentExample = new EnvironmentExample();
        environmentExample.createCriteria().andIdIn(envIds);
// TODO：        List<Environment> environments = environmentMapper.selectByExample(environmentExample);
//        Map<String, String> envMap = environments.stream().collect(Collectors.toMap(Environment::getId, Environment::getName));
//        EnvironmentGroupExample groupExample = new EnvironmentGroupExample();
//        groupExample.createCriteria().andIdIn(envIds);
//        List<EnvironmentGroup> environmentGroups = environmentGroupMapper.selectByExample(groupExample);
//        Map<String, String> groupMap = environmentGroups.stream().collect(Collectors.toMap(EnvironmentGroup::getId, EnvironmentGroup::getName));
        //取模块id为新的set
        List<String> moduleIds = scenarioLists.stream().map(SqlScenarioDTO::getModuleId).distinct().toList();
        SqlScenarioModuleExample moduleExample = new SqlScenarioModuleExample();
        moduleExample.createCriteria().andIdIn(moduleIds);
        List<SqlScenarioModule> modules = sqlScenarioModuleMapper.selectByExample(moduleExample);
        //生成map key为id value为name
        Map<String, String> moduleMap = modules.stream().collect(Collectors.toMap(SqlScenarioModule::getId, SqlScenarioModule::getName));
        //查询定时任务
        List<String> scenarioIds = scenarioLists.stream().map(SqlScenarioDTO::getId).toList();
        ScheduleExample scheduleExample = new ScheduleExample();
        scheduleExample.createCriteria().andResourceIdIn(scenarioIds).andResourceTypeEqualTo(ScheduleResourceType.SQL_SCENARIO.name());
        List<Schedule> schedules = scheduleMapper.selectByExample(scheduleExample);
        Map<String, Schedule> scheduleMap = schedules.stream().collect(Collectors.toMap(Schedule::getResourceId, t -> t));
        // TODO：关于报告的内容放在最后处理
        //  获取所有的lastResultId
//        List<String> lastResultIds = scenarioLists.stream().map(SqlScenarioDTO::getLastReportId).toList();
//        List<SqlScenarioReport> reports = sqlScenarioReportService.getApiScenarioReportByIds(lastResultIds);
        // 生成map key是id value是ScriptIdentifier  但是getScriptIdentifier为空的不放入
//        Map<String, String> reportMap = reports.stream().filter(report -> StringUtils.isNotBlank(report.getScriptIdentifier())).collect(Collectors.toMap(ApiScenarioReport::getId, ApiScenarioReport::getScriptIdentifier));
        scenarioLists.forEach(item -> {
            item.setCreateUserName(userMap.get(item.getCreateUser()));
            item.setDeleteUserName(userMap.get(item.getDeleteUser()));
            item.setUpdateUserName(userMap.get(item.getUpdateUser()));
            item.setModulePath(StringUtils.isNotBlank(moduleMap.get(item.getModuleId())) ? moduleMap.get(item.getModuleId()) : Translator.get("api_unplanned_scenario"));
// TODO：            if (!item.getGrouped() && envMap.containsKey(item.getEnvironmentId())) {
//                item.setEnvironmentName(envMap.get(item.getEnvironmentId()));
//            } else if (item.getGrouped() && groupMap.containsKey(item.getEnvironmentId())) {
//                item.setEnvironmentName(groupMap.get(item.getEnvironmentId()));
//            }
//            if (MapUtils.isNotEmpty(scheduleMap) && scheduleMap.containsKey(item.getId())) {
//                Schedule schedule = scheduleMap.get(item.getId());
//                SqlScenarioScheduleConfigRequest request = new SqlScenarioScheduleConfigRequest();
//                request.setEnable(schedule.getEnable());
//                request.setCron(schedule.getValue());
//                request.setScenarioId(item.getId());
//                if (schedule.getConfig() != null) {
//                    request.setConfig(JSON.parseObject(schedule.getConfig(), ApiRunModeConfigDTO.class));
//                }
//                item.setScheduleConfig(request);
//                if (schedule.getEnable()) {
//                    item.setNextTriggerTime(ScheduleUtils.getNextTriggerTime(schedule.getValue()));
//                }
//            }
//            if (MapUtils.isNotEmpty(reportMap) && reportMap.containsKey(item.getLastReportId())) {
//                item.setScriptIdentifier(reportMap.get(item.getLastReportId()));
//            }
        });
    }

    private Set<String> extractUserIds(List<SqlScenarioDTO> list) {
        return list.stream()
                .flatMap(sqlScenario -> Stream.of(sqlScenario.getUpdateUser(), sqlScenario.getDeleteUser(), sqlScenario.getCreateUser()))
                .collect(Collectors.toSet());
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

    public SqlScenarioDetailDTO getSqlScenarioDetailDTO(String scenarioId, String userId) {
        SqlScenarioDetail sqlScenarioDetail = get(scenarioId);
        SqlScenarioDetailDTO sqlScenarioDetailDTO = BeanUtils.copyBean(new SqlScenarioDetailDTO(), sqlScenarioDetail);
        Map<String, String> userNameMap = userLoginService.getUserNameMap(List.of(sqlScenarioDetail.getCreateUser(), sqlScenarioDetail.getUpdateUser()));
        sqlScenarioDetailDTO.setCreateUserName(userNameMap.get(sqlScenarioDetail.getCreateUser()));
        sqlScenarioDetailDTO.setUpdateUserName(userNameMap.get(sqlScenarioDetail.getUpdateUser()));

        // TODO：设置是否关注
//        SqlScenarioFollowerExample followerExample = new SqlScenarioFollowerExample();
//        followerExample.createCriteria().andSqlScenarioIdEqualTo(scenarioId);
//        followerExample.createCriteria().andUserIdEqualTo(userId);
//        List<SqlScenarioFollower> followers = sqlScenarioFollowerMapper.selectByExample(followerExample);
//        sqlScenarioDetailDTO.setFollow(CollectionUtils.isNotEmpty(followers));

        // TODO：设置关联的文件的最新信息
//        List<SqlFile> csvSqlFiles = sqlScenarioDetail.getScenarioConfig()
//                .getVariable()
//                .getCsvVariables()
//                .stream()
//                .map(CsvVariable::getFile)
//                .toList();
//        sqlCommonService.setLinkFileInfo(sqlScenarioDetail.getId(), csvSqlFiles);
        return sqlScenarioDetailDTO;
    }

    public SqlScenarioDetail get(String scenarioId) {
        SqlScenario sqlScenario = checkResourceIsNoDeleted(scenarioId);
        SqlScenarioDetail sqlScenarioDetail = BeanUtils.copyBean(new SqlScenarioDetail(), sqlScenario);
        sqlScenarioDetail.setSteps(List.of());
        SqlScenarioBlob sqlScenarioBlob = sqlScenarioBlobMapper.selectByPrimaryKey(scenarioId);

        if (sqlScenarioBlob != null) {
            sqlScenarioDetail.setScenarioConfig(JSON.parseObject(new String(sqlScenarioBlob.getConfig()), ScenarioConfig.class));
        }

        //存放csv变量
//        sqlScenarioDetail.getScenarioConfig().getVariable().setCsvVariables(sqlScenarioFileService.getCsvVariables(scenarioId));

        // 获取所有步骤
        List<SqlScenarioStepDTO> allSteps = getAllStepsByScenarioIds(List.of(scenarioId))
                .stream()
                .distinct() // 这里可能存在多次引用相同场景，步骤可能会重复，去重
                .collect(Collectors.toList());

        // 设置步骤的 csvIds
        setStepCsvIds(scenarioId, allSteps);

        // 构造 map，key 为场景ID，value 为步骤列表
        Map<String, List<SqlScenarioStepDTO>> scenarioStepMap = allSteps.stream()
                .collect(Collectors.groupingBy(step -> Optional.ofNullable(step.getScenarioId()).orElse(StringUtils.EMPTY)));

        // key 为父步骤ID，value 为子步骤列表
        if (MapUtils.isEmpty(scenarioStepMap)) {
            return sqlScenarioDetail;
        }

        Map<String, List<SqlScenarioStepDTO>> currentScenarioParentStepMap = scenarioStepMap.get(scenarioId)
                .stream()
                .collect(Collectors.groupingBy(step -> {
                    if (StringUtils.equals(step.getParentId(), "NONE")) {
                        step.setParentId(StringUtils.EMPTY);
                    }
                    return Optional.ofNullable(step.getParentId()).orElse(StringUtils.EMPTY);
                }));

        List<SqlScenarioStepDTO> steps = buildStepTree(currentScenarioParentStepMap.get(StringUtils.EMPTY), currentScenarioParentStepMap, scenarioStepMap, new HashSet<>());

        // 查询步骤详情
        Map<String, String> stepDetailMap = getPartialRefStepDetailMap(allSteps);

        // 设置部分引用的步骤的启用状态
        setPartialRefStepsEnable(steps, stepDetailMap);

        sqlScenarioDetail.setSteps(steps);

        return sqlScenarioDetail;
    }


    private void setStepCsvIds(String scenarioId, List<SqlScenarioStepDTO> allSteps) {
        List<String> refScenarioIds = allSteps.stream()
                .filter(this::isRefOrPartialScenario)
                .map(SqlScenarioStepCommonDTO::getResourceId)
                .collect(Collectors.toList());
        refScenarioIds.add(scenarioId);

        //获取所有步骤的csv的关联关系
        List<ApiScenarioCsvStep> csvSteps = extSqlScenarioStepMapper.getCsvStepByScenarioIds(refScenarioIds);
        // 构造 map，key 为步骤ID，value 为csv文件ID列表
        Map<String, List<String>> stepsCsvMap = csvSteps.stream()
                .collect(Collectors.groupingBy(ApiScenarioCsvStep::getStepId, Collectors.mapping(ApiScenarioCsvStep::getFileId, Collectors.toList())));

        //将stepsCsvMap根据步骤id放入到allSteps中
        if (CollectionUtils.isNotEmpty(allSteps)) {
            allSteps.forEach(step -> step.setCsvIds(stepsCsvMap.get(step.getId())));
        }
    }

    /**
     * 递归构造步骤树
     *
     * @param steps           当前场景下，当前层级的步骤
     * @param parentStepMap   当前场景所有的步骤，key 为父步骤ID，value 为子步骤列表
     * @param scenarioStepMap 所有场景步骤，key 为场景ID，value 为子步骤列表
     */
    public List<SqlScenarioStepDTO> buildStepTree(List<SqlScenarioStepDTO> steps,
                                                  Map<String, List<SqlScenarioStepDTO>> parentStepMap,
                                                  Map<String, List<SqlScenarioStepDTO>> scenarioStepMap,
                                                  Set<String> stepIdSet) {
        if (CollectionUtils.isEmpty(steps)) {
            return Collections.emptyList();
        }

        for (int i = 0; i < steps.size(); i++) {
            SqlScenarioStepDTO step = steps.get(i);
            if (stepIdSet.contains(step.getId())) {
                // 如果步骤ID已存在，说明引用了两个相同的场景，其子步骤ID可能会重复，导致引用的同一个对象
                // 这里重新new一个对象，避免执行时，处理为同一个步骤
                step = BeanUtils.copyBean(new SqlScenarioStepDTO(), step);
                steps.set(i, step);
            }
            stepIdSet.add(step.getId());

            // 获取当前步骤的子步骤
            List<SqlScenarioStepDTO> children = Optional.ofNullable(parentStepMap.get(step.getId())).orElse(new ArrayList<>(0));
            if (isRefOrPartialScenario(step)) {

                List<SqlScenarioStepDTO> scenarioSteps = scenarioStepMap.get(step.getResourceId());

                if (CollectionUtils.isEmpty(scenarioSteps)) {
                    continue;
                }

                scenarioSteps.forEach(item -> {
                    // 如果步骤的场景ID不等于当前场景的ID，说明是引用的步骤，如果 parentId 为空，说明是一级子步骤，重新挂载到对应的场景中
                    if (StringUtils.isEmpty(item.getParentId())) {
                        children.add(item);
                    }
                });

                if (CollectionUtils.isEmpty(children)) {
                    continue;
                }

                // 如果当前步骤是引用的场景，获取该场景的子步骤
                Map<String, List<SqlScenarioStepDTO>> childStepMap = scenarioSteps
                        .stream()
                        .collect(Collectors.groupingBy(item -> Optional.ofNullable(item.getParentId()).orElse(StringUtils.EMPTY)));
                step.setChildren(buildStepTree(children, childStepMap, scenarioStepMap, stepIdSet));
            } else {
                if (CollectionUtils.isEmpty(children)) {
                    continue;
                }
                step.setChildren(buildStepTree(children, parentStepMap, scenarioStepMap, stepIdSet));
            }
        }

        // 排序
        return steps.stream()
                .sorted(Comparator.comparing(SqlScenarioStepDTO::getSort))
                .collect(Collectors.toList());
    }

    /**
     * 设置部分引用的步骤的启用状态
     */
    public void setPartialRefStepsEnable(List<? extends SqlScenarioStepCommonDTO> steps, Map<String, String> stepDetailMap) {
        if (CollectionUtils.isEmpty(steps)) {
            return;
        }
        for (SqlScenarioStepCommonDTO step : steps) {
            setPartialRefStepEnable(step, stepDetailMap);
            if (CollectionUtils.isNotEmpty(step.getChildren())) {
                setPartialRefStepsEnable(step.getChildren(), stepDetailMap);
            }
        }
    }

    /**
     * 设置单个部分引用的步骤的启用状态
     */
    public void setPartialRefStepEnable(SqlScenarioStepCommonDTO step, Map<String, String> stepDetailMap) {
        String stepDetail = stepDetailMap.get(step.getId());
        if (!isPartialRef(step) || StringUtils.isBlank(stepDetail)) {
            return;
        }
        setChildPartialRefEnable(step.getChildren(), JSON.parseObject(stepDetail, PartialRefStepDetail.class));
    }

    /**
     * 设置部分引用的步骤的启用状态
     */
    private void setChildPartialRefEnable(List<? extends SqlScenarioStepCommonDTO> steps, PartialRefStepDetail partialRefStepDetail) {
        if (CollectionUtils.isEmpty(steps)) {
            return;
        }
        for (SqlScenarioStepCommonDTO step : steps) {
            if (StringUtils.equals(step.getRefType(), SqlScenarioStepRefType.REF.name())) {
                // 引用的启用不修改
                continue;
            }
            // 非完全引用的步骤，使用当前场景配置的启用状态
            Set<String> enableStepIds = partialRefStepDetail.getEnableStepIds();
            Set<String> disableStepIds = partialRefStepDetail.getDisableStepIds();

            // 如果是新添加的步骤，则不改变启用状态
            if (enableStepIds.contains(step.getId()) || disableStepIds.contains(step.getId())) {
                step.setEnable(enableStepIds.contains(step.getId()));
            }

            if (isPartialRef(step)) {
                // 如果是部分引用的场景，不递归解析了，上层的递归会解析
                continue;
            }
            // 非完全引用和部分引用的步骤，递归设置子步骤
            if (CollectionUtils.isNotEmpty(step.getChildren())) {
                setChildPartialRefEnable(step.getChildren(), partialRefStepDetail);
            }
        }
    }

    /**
     * 递归获取所有的场景步骤
     */
    public List<SqlScenarioStepDTO> getAllStepsByScenarioIds(List<String> scenarioIds) {
        List<SqlScenarioStepDTO> steps = getStepDTOByScenarioIds(scenarioIds);
        if (CollectionUtils.isEmpty(steps)) {
            return steps;
        }

        // 将 config 转换成对象
        steps.forEach(step -> {
            if (step.getConfig() != null && StringUtils.isNotBlank(step.getConfig().toString())) {
                if (step.getConfig() instanceof String configVal) {
                    step.setConfig(JSON.parseObject(configVal));
                }
            }
        });

        // 获取步骤中引用的场景ID
        List<String> childScenarioIds = steps.stream()
                .filter(this::isRefOrPartialScenario)
                .map(SqlScenarioStepDTO::getResourceId)
                .collect(Collectors.toList());

        // 嵌套获取引用的场景步骤
        steps.addAll(getAllStepsByScenarioIds(childScenarioIds));
        return steps;
    }

    /**
     * 判断步骤是否是引用的场景
     */
    public boolean isRefOrPartialScenario(SqlScenarioStepDTO step) {
        return isRefOrPartialRef(step.getRefType()) && isScenarioStep(step.getStepType());
    }

    private List<SqlScenarioStepDTO> getStepDTOByScenarioIds(List<String> scenarioIds) {
        if (CollectionUtils.isEmpty(scenarioIds)) {
            return Collections.emptyList();
        }
        return extSqlScenarioStepMapper.getStepDTOByScenarioIds(scenarioIds);
    }


    public SqlScenario checkResourceIsNoDeleted(String id) {
        SqlScenarioExample example = new SqlScenarioExample();
        example.createCriteria().andIdEqualTo(id).andDeletedEqualTo(false);
        List<SqlScenario> sqlScenarios = sqlScenarioMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(sqlScenarios)) {
            throw new MSException(SqlResultCode.CASE_NOT_EXIST);
        }
        return sqlScenarios.getFirst();
    }

    public boolean isRequestStep(SqlScenarioStepCommonDTO step) {
        SqlScenarioStepType scenarioStepType = EnumValidator.validateEnum(SqlScenarioStepType.class, step.getStepType());
        return scenarioStepType == null ? false : scenarioStepType.isRequest();
    }

    public Object getStepDetail(String stepId) {
        SqlScenarioStep step = sqlScenarioStepMapper.selectByPrimaryKey(stepId);
        return getStepDetail(BeanUtils.copyBean(new SqlScenarioStepDetailRequest(), step));
    }

    private Object getStepDetail(SqlScenarioStepDetailRequest step) {
        StepParser stepParser = StepParserFactory.getStepParser(step.getStepType());
        Object stepDetail = stepParser.parseDetail(step);
        if(stepDetail instanceof SqlAbstractMsTestElement sqlMsTestElement){
            // TODO：这里可能需要获取一些额外基本信息用于前端展示
        }

        return stepDetail;
    }

}