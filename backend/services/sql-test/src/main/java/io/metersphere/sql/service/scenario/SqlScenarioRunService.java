package io.metersphere.sql.service.scenario;

import io.metersphere.api.domain.*;
import io.metersphere.api.domain.ApiScenarioReportStep;
import io.metersphere.plugin.api.spi.AbstractMsTestElement;
import io.metersphere.plugin.api.spi.SqlAbstractMsTestElement;
import io.metersphere.project.domain.Project;
import io.metersphere.project.mapper.ProjectMapper;
import io.metersphere.sdk.constants.*;
import io.metersphere.sdk.dto.api.task.TaskInfo;
import io.metersphere.sdk.dto.api.task.TaskItem;
import io.metersphere.sdk.dto.api.task.TaskRequestDTO;
import io.metersphere.sdk.exception.MSException;
import io.metersphere.sdk.util.DateUtils;
import io.metersphere.sdk.util.JSON;
import io.metersphere.sql.constant.SqlResourceType;
import io.metersphere.sql.constant.SqlScenarioStepType;
import io.metersphere.sql.domain.*;
import io.metersphere.sql.mapper.SqlScenarioBlobMapper;
import io.metersphere.sql.mapper.SqlScenarioMapper;
import io.metersphere.sql.parse.StepParserFactory;
import io.metersphere.sql.pojo.dto.SqlDefinitionExecuteInfo;
import io.metersphere.sql.pojo.dto.SqlScenarioParamConfig;
import io.metersphere.sql.pojo.dto.SqlScenarioParseTmpParam;
import io.metersphere.sql.pojo.dto.debug.SqlResourceRunRequest;
import io.metersphere.sql.pojo.dto.scenario.ScenarioConfig;
import io.metersphere.sql.pojo.dto.scenario.SqlScenarioCopyStepMap;
import io.metersphere.sql.pojo.dto.scenario.SqlScenarioDebugRequest;
import io.metersphere.sql.pojo.dto.scenario.SqlScenarioParseParam;
import io.metersphere.sql.pojo.request.MsSqlCaseElement;
import io.metersphere.sql.pojo.request.SqlMsScenario;
import io.metersphere.sql.constant.SqlScenarioStepRefType;
import io.metersphere.sql.service.SqlCommonService;
import io.metersphere.sql.service.definition.SqlDefinitionService;
import io.metersphere.system.service.BaseTaskHubService;
import io.metersphere.system.uid.IDGenerator;
import jakarta.annotation.Resource;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import io.metersphere.sql.pojo.dto.scenario.SqlScenarioStepCommonDTO;
import io.metersphere.sql.parse.step.StepParser;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
public class SqlScenarioRunService {

    @Resource
    private SqlDefinitionService sqlDefinitionService;

    @Resource
    private SqlScenarioMapper sqlScenarioMapper;

    @Resource
    private SqlScenarioBlobMapper sqlScenarioBlobMapper;

    @Resource
    private SqlScenarioService sqlScenarioService;

    @Resource
    private ProjectMapper projectMapper;

    @Resource
    private SqlCommonService sqlCommonService;

    @Resource
    private BaseTaskHubService baseTaskHubService;

    @Resource
    private SqlExecuteService sqlExecuteService;

    @Resource
    private SqlScenarioReportService sqlScenarioReportService;

    public TaskRequestDTO run(SqlScenarioDebugRequest request, String userId) {
        SqlScenario sqlScenario = sqlScenarioMapper.selectByPrimaryKey(request.getId());

        // 解析生成待执行的场景树
        SqlMsScenario msScenario = new SqlMsScenario();
        msScenario.setRefType(SqlScenarioStepRefType.DIRECT.name());
        msScenario.setScenarioConfig(getScenarioConfig(request, true));
        msScenario.setProjectId(request.getProjectId());

        // TODO：从这里以后的逻辑和api执行有很大不同，做了很多删减，如果后面有什么需要同步的地方，需要重新完整梳理流程

        // 处理复制来的步骤，如果场景中的步骤是从SQL用例复制来的，需要进行保存等特殊处理操作，如果是引用的，则不需要进行处理
        // TODO：目前前端只有引用选项，所以下面这个addSpecialStepDetails方法仅仅是占位
        SqlScenarioCopyStepMap sqlScenarioCopyStepMap = sqlScenarioService.addSpecialStepDetails(request.getSteps(), request.getStepDetails());
        // 处理copy的步骤文件
        // apiScenarioFileService.handleRunCopyStepFiles(request, apiScenarioCopyStepMap, request.getStepDetails());
        SqlResourceRunRequest runRequest = new SqlResourceRunRequest();

        return executeRun(sqlScenario, msScenario, request.getSteps(), request, runRequest, request.getReportId(), userId);
    }

    private ScenarioConfig getScenarioConfig(SqlScenarioDebugRequest request, boolean hasSave) {
        if (request.getScenarioConfig() != null) {
            // 优先使用前端传的配置
            return request.getScenarioConfig();
        } else if (hasSave) {
            // 没传并且保存过，则从数据库获取
            SqlScenarioBlob sqlScenarioBlob = sqlScenarioBlobMapper.selectByPrimaryKey(request.getId());
            if (sqlScenarioBlob != null) {
                return JSON.parseObject(new String(sqlScenarioBlob.getConfig()), ScenarioConfig.class);
            }
        }
        return new ScenarioConfig();
    }

    public TaskRequestDTO executeRun(SqlScenario sqlScenario,
                                     SqlMsScenario msScenario,
                                     List<? extends SqlScenarioStepCommonDTO> steps,
                                     SqlScenarioParseParam parseParam,
                                     SqlResourceRunRequest runRequest,
                                     String reportId,
                                     String userId) {

        Project project = projectMapper.selectByPrimaryKey(sqlScenario.getProjectId());

        SqlExecTask execTask = sqlCommonService.newExecTask(project.getId(), userId);
        execTask.setCaseCount(1L);
        execTask.setTaskName(sqlScenario.getName());
        execTask.setOrganizationId(project.getOrganizationId());
        execTask.setTriggerMode(TaskTriggerMode.MANUAL.name());
        execTask.setTaskType(ExecTaskType.SQL_SCENARIO.name());

        SqlExecTaskItem execTaskItem = sqlCommonService.newExecTaskItem(execTask.getId(), project.getId(), userId);
        execTaskItem.setOrganizationId(project.getOrganizationId());
        execTaskItem.setResourceType(ApiExecuteResourceType.API_SCENARIO.name());
        execTaskItem.setResourceId(sqlScenario.getId());
        execTaskItem.setCaseId(sqlScenario.getId());
        execTaskItem.setResourceName(sqlScenario.getName());

        baseTaskHubService.insertSqlExecTaskAndDetail(execTask, execTaskItem);

        msScenario.setResourceId(sqlScenario.getId());

        // 解析生成场景树，并保存临时变量
        SqlScenarioParseTmpParam tmpParam = parse(msScenario, steps, parseParam);
//
        runRequest = setSqlResourceRunRequestParam(msScenario, tmpParam, runRequest);
//
//        String poolId = apiExecuteService.getProjectApiResourcePoolId(apiScenario.getProjectId());
//
        TaskRequestDTO taskRequest = getTaskRequest(reportId, sqlScenario.getId(), sqlScenario.getProjectId(), ApiExecuteRunMode.RUN.name());
        TaskInfo taskInfo = taskRequest.getTaskInfo();
        TaskItem taskItem = taskRequest.getTaskItem();
        taskItem.setId(execTaskItem.getId());
        taskInfo.setTaskId(execTask.getId());
//        taskInfo.getRunModeConfig().setPoolId(poolId);
        taskInfo.setSaveResult(true);
        taskInfo.setTriggerMode(TaskTriggerMode.MANUAL.name());
        taskInfo.getRunModeConfig().setEnvironmentId(parseParam.getEnvironmentId());
        taskRequest.getTaskItem().setRequestCount(tmpParam.getRequestCount().get());
        taskInfo.setUserId(userId);

        if (StringUtils.isEmpty(taskItem.getReportId())) {
            taskInfo.setRealTime(false);
        } else {
            // 如果传了报告ID，则实时获取结果
            taskInfo.setRealTime(true);
        }

        // 传了报告ID，则预生成报告
        SqlScenarioReport scenarioReport = getScenarioReport(sqlScenario, userId);
        scenarioReport.setId(reportId);
        scenarioReport.setTriggerMode(TaskTriggerMode.MANUAL.name());
        scenarioReport.setRunMode(ApiBatchRunMode.PARALLEL.name());
//        scenarioReport.setPoolId(poolId);
        scenarioReport.setEnvironmentId(parseParam.getEnvironmentId());
// TODO：       scenarioReport.setWaitingTime(getGlobalWaitTime(parseParam.getScenarioConfig()));
        initSqlScenarioReport(taskItem.getId(), sqlScenario, scenarioReport);

        // 初始化报告步骤
        initScenarioReportSteps(steps, taskItem.getReportId());

        SqlScenarioParamConfig parseConfig = getApiScenarioParamConfig(sqlScenario.getProjectId(), parseParam);
        parseConfig.setTaskItemId(taskItem.getId());
        return sqlExecuteService.execute(runRequest, taskRequest, parseConfig);
    }

    public SqlScenarioParamConfig getApiScenarioParamConfig(String projectId, SqlScenarioParseParam request) {
        SqlScenarioParamConfig parseConfig = new SqlScenarioParamConfig();
        return parseConfig;
    }

        /**
         * 初始化场景报告步骤
         *
         * @param steps
         * @param reportId
         */
    public void initScenarioReportSteps(List<? extends SqlScenarioStepCommonDTO> steps, String reportId) {
        initScenarioReportSteps(null, steps, reportId);
    }

    public void initScenarioReportSteps(String parentId, List<? extends SqlScenarioStepCommonDTO> steps, String reportId) {
        List<SqlScenarioReportStep> scenarioReportSteps = getScenarioReportSteps(parentId, steps, reportId);
        sqlScenarioReportService.insertSqlScenarioReportStep(scenarioReportSteps);
    }

    /**
     * 获取场景报告步骤
     *
     * @param steps
     * @param reportId
     */
    public List<SqlScenarioReportStep> getScenarioReportSteps(String parentId, List<? extends SqlScenarioStepCommonDTO> steps, String reportId) {
        AtomicLong sort = new AtomicLong(1);
        List<SqlScenarioReportStep> scenarioReportSteps = new ArrayList<>();
        for (SqlScenarioStepCommonDTO step : steps) {
            if (StringUtils.isBlank(step.getUniqueId())) {
                // 如果没有步骤唯一ID，则生成唯一ID
                step.setUniqueId(IDGenerator.nextStr());
            }
            SqlScenarioReportStep scenarioReportStep = getScenarioReportStep(step, reportId, sort.getAndIncrement());
            scenarioReportStep.setParentId(parentId);
            scenarioReportSteps.add(scenarioReportStep);
            List<? extends SqlScenarioStepCommonDTO> children = step.getChildren();
            if (CollectionUtils.isNotEmpty(children)) {
                scenarioReportSteps.addAll(getScenarioReportSteps(step.getUniqueId(), children, reportId));
            }
        }
        return scenarioReportSteps;
    }

    private SqlScenarioReportStep getScenarioReportStep(SqlScenarioStepCommonDTO step, String reportId, long sort) {
        SqlScenarioReportStep scenarioReportStep = new SqlScenarioReportStep();
        scenarioReportStep.setReportId(reportId);
        scenarioReportStep.setStepId(step.getUniqueId());
        scenarioReportStep.setSort(sort);
        scenarioReportStep.setName(step.getName());
        scenarioReportStep.setStepType(step.getStepType());
        return scenarioReportStep;
    }

    /**
     * 预生成用例的执行报告
     *
     * @param sqlScenario
     * @return
     */
    public String initSqlScenarioReport(String taskItemId, SqlScenario sqlScenario, SqlScenarioReport scenarioReport) {
        // 初始化报告
        scenarioReport.setProjectId(sqlScenario.getProjectId());
        sqlScenarioReportService.insertSqlScenarioReport(scenarioReport);
        return initSqlScenarioReportDetail(taskItemId, sqlScenario.getId(), scenarioReport.getId());
    }

    public String initSqlScenarioReportDetail(String taskItemId, String sqlScenarioId, String reportId) {
        // 创建报告和用例的关联关系
        SqlScenarioRecord scenarioRecord = getSqlScenarioRecord(sqlScenarioId, reportId);
        // 初始化报告和任务的关联关系
        SqlReportRelateTask sqlReportRelateTask = sqlCommonService.getSqlReportRelateTask(taskItemId, reportId);

        sqlScenarioReportService.insertSqlScenarioReportDetail(scenarioRecord, sqlReportRelateTask);
        return scenarioRecord.getSqlScenarioReportId();
    }

    public SqlScenarioRecord getSqlScenarioRecord(String apiScenarioId, String reportId) {
        SqlScenarioRecord scenarioRecord = new SqlScenarioRecord();
        scenarioRecord.setSqlScenarioId(apiScenarioId);
        scenarioRecord.setSqlScenarioReportId(reportId);
        return scenarioRecord;
    }

    public SqlScenarioReport getScenarioReport(SqlScenario sqlScenario, String userId) {
        SqlScenarioReport scenarioReport = getScenarioReport(userId);
        scenarioReport.setName(sqlScenario.getName() + "_" + DateUtils.getTimeString(System.currentTimeMillis()));
        scenarioReport.setEnvironmentId(sqlScenario.getEnvironmentId());
        scenarioReport.setProjectId(sqlScenario.getProjectId());
        return scenarioReport;
    }

    public SqlScenarioReport getScenarioReport(String userId) {
        SqlScenarioReport scenarioReport = new SqlScenarioReport();
        scenarioReport.setId(IDGenerator.nextStr());
        scenarioReport.setDeleted(false);
        scenarioReport.setIntegrated(false);
        scenarioReport.setExecStatus(ExecStatus.PENDING.name());
        scenarioReport.setStartTime(System.currentTimeMillis());
        scenarioReport.setUpdateTime(System.currentTimeMillis());
        scenarioReport.setUpdateUser(userId);
        scenarioReport.setCreateUser(userId);
        return scenarioReport;
    }

    public TaskRequestDTO getTaskRequest(String reportId, String resourceId, String projectId, String runModule) {
        TaskRequestDTO taskRequest = new TaskRequestDTO();
        TaskInfo taskInfo = getTaskInfo(projectId, runModule);
        TaskItem taskItem = sqlExecuteService.getTaskItem(reportId, resourceId);
        taskRequest.setTaskInfo(taskInfo);
        taskRequest.setTaskItem(taskItem);
        return taskRequest;
    }

    public TaskInfo getTaskInfo(String projectId, String runModule) {
        TaskInfo taskInfo = sqlExecuteService.getTaskInfo(projectId);
        taskInfo.setResourceType(SqlResourceType.SQL_SCENARIO.name());
        taskInfo.setRunMode(runModule);
        return taskInfo;
    }

    /**
     * 将步骤转换成场景树
     * 并保存临时变量
     *
     * @param msScenario
     * @param steps
     * @param parseParam
     * @return
     */
    public SqlScenarioParseTmpParam parse(SqlMsScenario msScenario,
                                          List<? extends SqlScenarioStepCommonDTO> steps,
                                          SqlScenarioParseParam parseParam) {
        // 记录引用的资源ID
        Map<String, List<String>> refResourceMap = new HashMap<>();
        buildRefResourceIdMap(steps, refResourceMap);

        SqlScenarioParseTmpParam tmpParam = new SqlScenarioParseTmpParam();

        // 查询引用的资源详情
        tmpParam.setResourceDetailMap(getResourceDetailMap(refResourceMap));
// TODO：这里有个bug，上面的ResourceDetailMap和下面的StepDetailMap变得一样了，不确定有没有问题
        // 查询复制的步骤详情
        tmpParam.setStepDetailMap(getStepDetailMap(steps, parseParam.getStepDetails()));

        // 获取场景环境相关配置
        // TODO：
//        tmpParam.setScenarioParseEnvInfo(getScenarioParseEnvInfo(refResourceMap, parseParam.getEnvironmentId(), parseParam.getGrouped()));
        parseStep2MsElement(msScenario, steps, tmpParam, msScenario.getResourceId());

        // 设置 HttpElement 的模块信息
        setSqlDefinitionExecuteInfo(tmpParam.getUniqueIdStepMap(), tmpParam.getStepTypeHttpElementMap());

        return tmpParam;
    }

    /**
     * 设置 HttpElement 的模块信息
     * 用户环境中的模块过滤
     *
     * @param uniqueIdStepMap
     * @param stepTypeHttpElementMap
     */
    private void setSqlDefinitionExecuteInfo(Map<String, SqlScenarioStepCommonDTO> uniqueIdStepMap, Map<String, List<MsSqlCaseElement>> stepTypeHttpElementMap) {
        setSqlDefinitionExecuteInfo(uniqueIdStepMap, stepTypeHttpElementMap.get(SqlScenarioStepType.SQL.name()), sqlDefinitionService::getModuleInfoByIds);
    }

    /**
     * 设置 MsHTTPElement 中的 method 等信息
     *
     * @param httpElements
     * @param getDefinitionInfoFunc
     */
    public void setSqlDefinitionExecuteInfo(Map<String, SqlScenarioStepCommonDTO> uniqueIdStepMap, List<MsSqlCaseElement> httpElements, Function<List<String>, List<SqlDefinitionExecuteInfo>> getDefinitionInfoFunc) {
        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(httpElements)) {
            List<String> resourceIds = httpElements.stream().map(MsSqlCaseElement::getResourceId).collect(Collectors.toList());
            // 获取接口模块信息
            Map<String, SqlDefinitionExecuteInfo> resourceModuleMap = sqlCommonService.getSqlDefinitionExecuteInfoMap(getDefinitionInfoFunc, resourceIds);
            httpElements.forEach(httpElement -> {
                SqlDefinitionExecuteInfo definitionExecuteInfo = resourceModuleMap.get(httpElement.getResourceId());

                // httpElement 设置模块,请求方法等信息
                sqlCommonService.setSqlDefinitionExecuteInfo(httpElement, definitionExecuteInfo);

                SqlScenarioStepCommonDTO step = uniqueIdStepMap.get(httpElement.getStepId());

            });
        }
    }

    /**
     * 将步骤解析成 MsTestElement 树结构
     */
    private void parseStep2MsElement(SqlAbstractMsTestElement parentElement,
                                     List<? extends SqlScenarioStepCommonDTO> steps,
                                     SqlScenarioParseTmpParam parseParam,
                                     String scenarioId) {
        if (CollectionUtils.isNotEmpty(steps)) {
            parentElement.setSqlChildren(new LinkedList<>());
        }

        Map<String, String> stepDetailMap = parseParam.getStepDetailMap();
        Map<String, String> resourceDetailMap = parseParam.getResourceDetailMap();
        Map<String, List<MsSqlCaseElement>> stepTypeHttpElementMap = parseParam.getStepTypeHttpElementMap();
        for (SqlScenarioStepCommonDTO step : steps) {
            StepParser stepParser = StepParserFactory.getStepParser(step.getStepType());
            if (BooleanUtils.isFalse(step.getEnable())) {
                continue;
            }
            sqlScenarioService.setPartialRefStepEnable(step, stepDetailMap);

            if (sqlScenarioService.isRequestStep(step) && BooleanUtils.isTrue(step.getEnable())) {
                // 记录待执行的请求总数
                parseParam.getRequestCount().getAndIncrement();
            }

            if (StringUtils.isBlank(step.getUniqueId())) {
                // 如果调试的时候前端没有传步骤唯一ID，则生成唯一ID
                step.setUniqueId(IDGenerator.nextStr());
            }

            parseParam.getUniqueIdStepMap().put(step.getUniqueId(), step);

            // 将步骤详情解析生成对应的MsTestElement
            SqlAbstractMsTestElement msTestElement = stepParser.parseTestElement(step,
                    MapUtils.isNotEmpty(resourceDetailMap) ? resourceDetailMap.getOrDefault(step.getResourceId(), StringUtils.EMPTY) : StringUtils.EMPTY, stepDetailMap.get(step.getId()));
            if (msTestElement != null) {
                if (msTestElement instanceof MsSqlCaseElement msSqlCaseElement) {
                    // 暂存http类型的步骤
                    stepTypeHttpElementMap.putIfAbsent(step.getStepType(), new LinkedList<>());
                    stepTypeHttpElementMap.get(step.getStepType()).add(msSqlCaseElement);
                }
                msTestElement.setProjectId(step.getProjectId());
                msTestElement.setResourceId(step.getResourceId());
                msTestElement.setName(step.getName());
                // 步骤ID，设置为唯一ID
                msTestElement.setStepId(step.getUniqueId());
                msTestElement.setCsvIds(step.getCsvIds());

                // 记录引用的资源ID和项目ID，下载执行文件时需要使用
                parseParam.getRefProjectIds().add(step.getProjectId());
                if (sqlScenarioService.isRefOrPartialRef(step.getRefType()) && !sqlScenarioService.isRefSql(step.getStepType(), step.getRefType())) {
                    // 引用的步骤记录引用的资源ID
                    parseParam.getFileResourceIds().add(step.getResourceId());
                } else if (msTestElement instanceof MsSqlCaseElement) {
                    // TODO：非引用的步骤记录步骤ID
//                    parseParam.getFileResourceIds().add(step.getId());
//                    parseParam.getFileStepScenarioMap().put(step.getId(), scenarioId);
                }

                // 设置环境等，运行时场景参数
//                setMsScenarioParam(parseParam.getScenarioParseEnvInfo(), step, msTestElement);

                // 记录 msCommonElement
//                Optional.ofNullable(sqlCommonService.getMsCommonElement(msTestElement))
//                        .ifPresent(msCommonElement -> parseParam.getCommonElements().add(msCommonElement));
                // 组装树结构

                msTestElement.setReportId(parentElement.getReportId());
                parentElement.getSqlChildren().add(msTestElement);

                if (CollectionUtils.isNotEmpty(step.getChildren())) {
                    if (sqlScenarioService.isScenarioStep(step.getStepType()) && sqlScenarioService.isRefOrPartialRef(step.getRefType())) {
                        scenarioId = step.getResourceId();
                    }
                    parseStep2MsElement(msTestElement, step.getChildren(), parseParam, scenarioId);
                }
            }
        }
    }

    private Map<String, String> getStepDetailMap(List<? extends SqlScenarioStepCommonDTO> steps, Map<String, Object> stepDetailsParam) {
        List<String> needBlobStepIds = getHasDetailStepIds(steps, stepDetailsParam);
        Map<String, String> stepDetails = sqlScenarioService.getStepBlobByIds(needBlobStepIds).stream()
                .collect(Collectors.toMap(SqlScenarioStepBlob::getId, blob -> new String(blob.getContent())));
        // 前端有传，就用前端传的
        if (stepDetailsParam != null) {
            stepDetailsParam.forEach((stepId, detail) -> stepDetails.put(stepId, detail instanceof byte[] bytes ? new String(bytes) : JSON.toJSONString(detail)));
        }
        return stepDetails;
    }

    private List<String> getHasDetailStepIds(List<? extends SqlScenarioStepCommonDTO> steps, Map<String, Object> stepDetailsParam) {
        List<String> needBlobStepIds = new ArrayList<>();
        for (SqlScenarioStepCommonDTO step : steps) {
            List<? extends SqlScenarioStepCommonDTO> children = step.getChildren();
            if (CollectionUtils.isNotEmpty(children)) {
                needBlobStepIds.addAll(getHasDetailStepIds(children, stepDetailsParam));
            }
            if (BooleanUtils.isFalse(step.getEnable())) {
                continue;
            }
            if (!hasStepDetail(step.getStepType())) {
                continue;
            }
            if (stepDetailsParam != null && stepDetailsParam.containsKey(step.getId())) {
                // 前端传了blob，不需要再查
                continue;
            }
            needBlobStepIds.add(step.getId());
        }
        return needBlobStepIds;
    }

    /**
     * 非完全引用的步骤和接口定义的步骤，才需要查 blob
     *
     * @param stepType
     * @return
     */
    private boolean hasStepDetail(String stepType) {
        return !StringUtils.equals(stepType, SqlScenarioStepRefType.REF.name())
                || sqlScenarioService.isSql(stepType);
    }

    private Map<String, String> getResourceDetailMap(Map<String, List<String>> refResourceMap) {
        Map<String, String> resourceBlobMap = new HashMap<>();
        List<String> sqlIds = refResourceMap.get(SqlScenarioStepType.SQL.name());
        List<SqlDefinitionBlob> sqlDefinitionBlobs = sqlDefinitionService.getBlobByIds(sqlIds);
        sqlDefinitionBlobs.forEach(blob -> {
            String requestAndResp = new String(blob.getRequest());
            if (requestAndResp.endsWith("}")) {
                requestAndResp = requestAndResp.substring(0, requestAndResp.length() - 1) + "," + "\"responseDefinition\":" + new String(blob.getResponse()) + "}";
            }else{
                throw new MSException("异常数据");
            }
            resourceBlobMap.put(blob.getId(), requestAndResp);
        });
//        sqlDefinitionBlobs.forEach(blob -> resourceBlobMap.put(blob.getId(), new String(blob.getRequest())));

         List<String> sqlScenarioIds = refResourceMap.get(SqlScenarioStepType.SQL_SCENARIO.name());
        List<SqlScenarioBlob> sqlScenarioBlobs = getBlobByIds(sqlScenarioIds);
        sqlScenarioBlobs.forEach(blob -> resourceBlobMap.put(blob.getId(), new String(blob.getConfig())));
        return resourceBlobMap;
    }

    private List<SqlScenarioBlob> getBlobByIds(List<String> sqlScenarioIds) {
        if (CollectionUtils.isEmpty(sqlScenarioIds)) {
            return Collections.emptyList();
        }
        SqlScenarioBlobExample example = new SqlScenarioBlobExample();
        example.createCriteria().andIdIn(sqlScenarioIds);
        return sqlScenarioBlobMapper.selectByExampleWithBLOBs(example);
    }
    private void buildRefResourceIdMap(List<? extends SqlScenarioStepCommonDTO> steps, Map<String, List<String>> refResourceIdMap) {
        for (SqlScenarioStepCommonDTO step : steps) {
            if (sqlScenarioService.isRefOrPartialRef(step.getRefType()) && BooleanUtils.isTrue(step.getEnable())) {
                // 记录引用的步骤ID
                List<String> resourceIds = refResourceIdMap.computeIfAbsent(step.getStepType(), k -> new ArrayList<>());
                resourceIds.add(step.getResourceId());
            }

            if (CollectionUtils.isNotEmpty(step.getChildren())) {
                buildRefResourceIdMap(step.getChildren(), refResourceIdMap);
            }
        }
    }

    private SqlResourceRunRequest setSqlResourceRunRequestParam(SqlMsScenario msScenario, SqlScenarioParseTmpParam tmpParam, SqlResourceRunRequest runRequest) {
        runRequest.setRefProjectIds(tmpParam.getRefProjectIds());
        runRequest.setTestElement(msScenario);
        return runRequest;
    }

    public SqlExecTaskItem newExecTaskItem(String taskId, String projectId, String userId) {
        SqlExecTaskItem execTaskItem = new SqlExecTaskItem();
        execTaskItem.setCreateTime(System.currentTimeMillis());
        execTaskItem.setId(IDGenerator.nextStr());
        execTaskItem.setTaskId(taskId);
        execTaskItem.setProjectId(projectId);
        execTaskItem.setExecutor(userId);
        execTaskItem.setStatus(ExecStatus.PENDING.name());
        execTaskItem.setResourcePoolId(StringUtils.EMPTY);
        execTaskItem.setResourcePoolNode(StringUtils.EMPTY);
        return execTaskItem;
    }
}
