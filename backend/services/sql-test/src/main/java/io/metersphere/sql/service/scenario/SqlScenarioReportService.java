package io.metersphere.sql.service.scenario;

import io.metersphere.api.domain.ApiScenarioReport;
import io.metersphere.sdk.constants.ExecStatus;
import io.metersphere.sdk.constants.ResultStatus;
import io.metersphere.sdk.domain.Environment;
import io.metersphere.sdk.domain.EnvironmentGroup;
import io.metersphere.sdk.dto.SocketMsgDTO;
import io.metersphere.sdk.dto.result.ListResult;
import io.metersphere.sdk.util.BeanUtils;
import io.metersphere.sdk.util.JSON;
import io.metersphere.sdk.util.SubListUtils;
import io.metersphere.sdk.util.Translator;
import io.metersphere.sql.domain.*;
import io.metersphere.sql.mapper.*;
import com.fasterxml.jackson.core.type.TypeReference;
import io.metersphere.sql.pojo.dto.report.SqlReportPageRequest;
import io.metersphere.sql.pojo.dto.report.SqlScenarioReportListDTO;
import io.metersphere.sql.pojo.dto.scenario.SqlScenarioReportDTO;
import io.metersphere.sql.pojo.dto.scenario.SqlScenarioReportStepDTO;
import io.metersphere.sql.pojo.model.DiffExecuteResult;
import io.metersphere.sql.pojo.model.ExecuteResult;
import io.metersphere.system.domain.TestResourcePool;
import io.metersphere.system.mapper.TestResourcePoolMapper;
import io.metersphere.system.mapper.UserMapper;
import io.metersphere.system.service.UserLoginService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.logging.log4j.util.Strings;
import org.mybatis.spring.SqlSessionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import io.metersphere.system.uid.IDGenerator;
import io.metersphere.sql.constant.SqlScenarioStepType;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class SqlScenarioReportService {

    @Resource
    SqlScenarioReportDetailMapper sqlScenarioReportDetailMapper;

    @Resource
    SqlScenarioReportDetailBlobMapper sqlScenarioReportDetailBlobMapper;

    @Resource
    private SqlScenarioReportMapper sqlScenarioReportMapper;

    @Resource
    private SqlScenarioRecordMapper sqlScenarioRecordMapper;

    @Resource
    private SqlReportRelateTaskMapper sqlReportRelateTaskMapper;

    @Resource
    private SqlSessionFactory sqlSessionFactory;

    @Resource
    private ExtSqlScenarioReportMapper extSqlScenarioReportMapper;

    @Resource
    private UserLoginService userLoginService;

    @Resource
    private TestResourcePoolMapper testResourcePoolMapper;

    @Resource
    private UserMapper userMapper;

    private static final String SPLITTER = "_";

    public void insertSqlScenarioReport(SqlScenarioReport report) {
        sqlScenarioReportMapper.insertSelective(report);
    }

    public void insertSqlScenarioReportDetail(SqlScenarioRecord record, SqlReportRelateTask taskRelation) {
        sqlScenarioRecordMapper.insertSelective(record);
        sqlReportRelateTaskMapper.insertSelective(taskRelation);
    }

    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRES_NEW)
    public void insertSqlScenarioReportStep(List<SqlScenarioReportStep> reportSteps) {
        if (CollectionUtils.isNotEmpty(reportSteps)) {
            SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH);
            SqlScenarioReportStepMapper stepMapper = sqlSession.getMapper(SqlScenarioReportStepMapper.class);
            SubListUtils.dealForSubList(reportSteps, 1000, subList -> {
                subList.forEach(stepMapper::insertSelective);
            });
            sqlSession.flushStatements();
            if (sqlSessionFactory != null) {
                SqlSessionUtils.closeSqlSession(sqlSession, sqlSessionFactory);
            }
        }
    }

    public void saveReportDetail(SocketMsgDTO socketMsgDTO){
        // 创建 TypeReference 指定泛型类型 ListResult<ExecuteResult>
        TypeReference<ListResult<DiffExecuteResult>> typeRef
                = new TypeReference<ListResult<DiffExecuteResult>>() {};
        ListResult<DiffExecuteResult> diffExecuteResult = JSON.parseListResult(socketMsgDTO.getTaskResult(),typeRef);

        if( Strings.isNotEmpty(diffExecuteResult.getErrorCode())
                || Strings.isNotEmpty(diffExecuteResult.getErrorMessage())
                || CollectionUtils.isNotEmpty(diffExecuteResult.getData())
        ){
            String stepId = diffExecuteResult.getData().getFirst().getStepId();
            String reportId = diffExecuteResult.getData().getFirst().getReportId();

            SqlScenarioReportDetail sqlScenarioReportDetail = new SqlScenarioReportDetail();
            String id = IDGenerator.nextStr();
            sqlScenarioReportDetail.setId(id);
            sqlScenarioReportDetail.setReportId(reportId);
            sqlScenarioReportDetail.setStepId(stepId);

            if(diffExecuteResult.getSuccess()){
                sqlScenarioReportDetail.setStatus("SUCCESS");
            }else{
                sqlScenarioReportDetail.setStatus("ERROR");
            }

            sqlScenarioReportDetail.setRequestTime(diffExecuteResult.getData().stream().mapToLong(ExecuteResult::getDuration).sum());
            sqlScenarioReportDetail.setResponseSize(0L);
            sqlScenarioReportDetail.setSort(0L);

            sqlScenarioReportDetailMapper.insert(sqlScenarioReportDetail);

            SqlScenarioReportDetailBlob sqlScenarioReportDetailBlob = new SqlScenarioReportDetailBlob();
            sqlScenarioReportDetailBlob.setReportId(reportId);
            sqlScenarioReportDetailBlob.setId(id);
            sqlScenarioReportDetailBlob.setContent(JSON.toJSONString(diffExecuteResult).getBytes());
            sqlScenarioReportDetailBlobMapper.insert(sqlScenarioReportDetailBlob);
        }

    }

    public List<SqlScenarioReportListDTO> getPage(SqlReportPageRequest request) {
        List<SqlScenarioReport> list = extSqlScenarioReportMapper.list(request);
        List<SqlScenarioReportListDTO> result = new ArrayList<>();
        //取所有的userid
        Set<String> userSet = list.stream()
                .flatMap(scenarioReport -> Stream.of(scenarioReport.getUpdateUser(), scenarioReport.getDeleteUser(), scenarioReport.getCreateUser()))
                .collect(Collectors.toSet());
        Map<String, String> userMap = userLoginService.getUserNameMap(new ArrayList<>(userSet));
        list.forEach(scenarioReport -> {
            SqlScenarioReportListDTO scenarioReportListDTO = new SqlScenarioReportListDTO();
            BeanUtils.copyBean(scenarioReportListDTO, scenarioReport);
            scenarioReportListDTO.setCreateUserName(userMap.get(scenarioReport.getCreateUser()));
            scenarioReportListDTO.setUpdateUserName(userMap.get(scenarioReport.getUpdateUser()));
            result.add(scenarioReportListDTO);
        });
        return result;
    }

    public SqlScenarioReportDTO get(String id) {
        SqlScenarioReportDTO scenarioReportDTO = new SqlScenarioReportDTO();
        SqlScenarioReport scenarioReport = checkResource(id);
        BeanUtils.copyBean(scenarioReportDTO, scenarioReport);
        //需要查询出所有的步骤
        List<SqlScenarioReportStepDTO> scenarioReportSteps = extSqlScenarioReportMapper.selectStepByReportId(id);

        List<SqlScenarioReportStepDTO> deatilList = extSqlScenarioReportMapper.selectStepDetailByReportId(id);
        //根据stepId进行分组
        Map<String, List<SqlScenarioReportStepDTO>> detailMap = deatilList.stream().collect(Collectors.groupingBy(SqlScenarioReportStepDTO::getStepId));
        //只处理请求的
        detailRequest(scenarioReportSteps, detailMap);

        //将scenarioReportSteps按照parentId进行分组 值为list 然后根据sort进行排序
        Map<String, List<SqlScenarioReportStepDTO>> scenarioReportStepMap = scenarioReportSteps.stream().collect(Collectors.groupingBy(SqlScenarioReportStepDTO::getParentId));

        List<SqlScenarioReportStepDTO> steps = Optional.ofNullable(scenarioReportStepMap.get("NONE")).orElse(new ArrayList<>(0));
        steps.sort(Comparator.comparingLong(SqlScenarioReportStepDTO::getSort));

        getStepTree(steps, scenarioReportStepMap);

        scenarioReportDTO.setStepTotal(steps.size());
        scenarioReportDTO.setRequestTotal(getRequestTotal(scenarioReportDTO));
        scenarioReportDTO.setChildren(steps);

        scenarioReportDTO.setStepErrorCount(steps.stream().filter(step -> StringUtils.equals(ResultStatus.ERROR.name(), step.getStatus())).count());
        scenarioReportDTO.setStepSuccessCount(steps.stream().filter(step -> StringUtils.equals(ResultStatus.SUCCESS.name(), step.getStatus())).count());
        scenarioReportDTO.setStepPendingCount(steps.stream().filter(step -> StringUtils.equals(ExecStatus.PENDING.name(), step.getStatus()) || StringUtils.isBlank(step.getStatus())).count());
        scenarioReportDTO.setStepFakeErrorCount(steps.stream().filter(step -> StringUtils.equals(ResultStatus.FAKE_ERROR.name(), step.getStatus())).count());
        // TODO：控制台信息 console
//        SqlScenarioReportLogExample example = new SqlScenarioReportLogExample();
//        example.createCriteria().andReportIdEqualTo(id);
//        List<SqlScenarioReportLog> apiScenarioReportLogs = sqlScenarioReportLogMapper.selectByExampleWithBLOBs(example);
//        if (CollectionUtils.isNotEmpty(apiScenarioReportLogs)) {
            //获取所有的console,生成集合
//            List<String> consoleList = apiScenarioReportLogs.stream().map(c -> new String(c.getConsole())).toList();
//            scenarioReportDTO.setConsole(String.join("\n", consoleList));
//        }
        // TODO：查询资源池名称
//        TestResourcePool testResourcePool = testResourcePoolMapper.selectByPrimaryKey(scenarioReport.getPoolId());
//        scenarioReportDTO.setPoolName(testResourcePool != null ? testResourcePool.getName() : null);
        // TODO：查询环境名称
//        String environmentName = null;
//        if (StringUtils.isNotBlank(scenarioReport.getEnvironmentId())) {
//            Environment environment = environmentMapper.selectByPrimaryKey(scenarioReport.getEnvironmentId());
//            if (environment != null) {
//                environmentName = environment.getName();
//            }
//            EnvironmentGroup environmentGroup = environmentGroupMapper.selectByPrimaryKey(scenarioReport.getEnvironmentId());
//            if (environmentGroup != null) {
//                environmentName = environmentGroup.getName();
//            }
//        }
//        scenarioReportDTO.setEnvironmentName(environmentName);
        scenarioReportDTO.setEnvironmentName("默认环境");
        scenarioReportDTO.setCreatUserName(userMapper.selectByPrimaryKey(scenarioReport.getCreateUser()).getName());
        return scenarioReportDTO;
    }

    public long getRequestTotal(SqlScenarioReport report) {
        return report.getErrorCount() + report.getPendingCount() + report.getSuccessCount() + report.getFakeErrorCount();
    }

    private SqlScenarioReport checkResource(String id) {
        SqlScenarioReportExample example = new SqlScenarioReportExample();
        example.createCriteria().andIdEqualTo(id);
        List<SqlScenarioReport> scenarioReport = sqlScenarioReportMapper.selectByExample(example);
        if (CollectionUtils.isEmpty(scenarioReport)) {
            throw new RuntimeException(Translator.get("api_scenario_report_not_exist"));
        }
        return scenarioReport.getFirst();
    }

    private static void detailRequest(List<SqlScenarioReportStepDTO> scenarioReportSteps, Map<String, List<SqlScenarioReportStepDTO>> detailMap) {
        List<String> stepTypes = Arrays.asList(SqlScenarioStepType.SQL.name());
        scenarioReportSteps.parallelStream().forEach(step -> {
            if (stepTypes.contains(step.getStepType())) {
                List<SqlScenarioReportStepDTO> details = detailMap.get(step.getStepId());
                if (CollectionUtils.isNotEmpty(details) && details.size() > 1) {
//                    details.sort(Comparator.comparingLong(SqlScenarioReportStepDTO::getLoopIndex));
                    if (details.size() > 1) {
                        //需要重新处理sort
                        for (int i = 0; i < details.size(); i++) {
                            SqlScenarioReportStepDTO detail = details.get(i);
                            detail.setSort((long) i + 1);
                            detail.setStepId(step.getStepId() + SPLITTER + detail.getSort());
                            detail.setStepType(step.getStepType());
                            detail.setName(detail.getRequestName());
                        }

                        step.setRequestTime(details.stream().mapToLong(SqlScenarioReportStepDTO::getRequestTime).sum());
                        step.setResponseSize(details.stream().mapToLong(SqlScenarioReportStepDTO::getResponseSize).sum());
                        List<String> requestStatus = details.stream().map(SqlScenarioReportStepDTO::getStatus).toList();
                        List<String> successStatus = requestStatus.stream().filter(status -> StringUtils.equals(ResultStatus.SUCCESS.name(), status)).toList();
                        if (requestStatus.contains(ResultStatus.ERROR.name())) {
                            step.setStatus(ResultStatus.ERROR.name());
                        } else if (requestStatus.contains(ResultStatus.FAKE_ERROR.name())) {
                            step.setStatus(ResultStatus.FAKE_ERROR.name());
                        } else if (successStatus.size() == details.size()) {
                            step.setStatus(ResultStatus.SUCCESS.name());
                        } else {
                            step.setStatus(ExecStatus.PENDING.name());
                        }

                        // 重试的话，取最后一次的状态
                        SqlScenarioReportStepDTO lastDetail = details.getLast();
                        if (lastDetail.getName().contains("MsRetry_")) {
                            step.setStatus(lastDetail.getStatus());
                        }
                    }
                    step.setChildren(details);
                } else if (CollectionUtils.isNotEmpty(details)) {
                    step.setName(details.getFirst().getRequestName());
                    step.setReportId(details.getFirst().getReportId());
                    step.setRequestTime(details.getFirst().getRequestTime());
                    step.setResponseSize(details.getFirst().getResponseSize());
                    step.setStatus(details.getFirst().getStatus());
                    step.setCode(details.getFirst().getCode());
                    step.setFakeCode(details.getFirst().getFakeCode());
                    step.setScriptIdentifier(details.getFirst().getScriptIdentifier());
                }
            }
        });
    }

    private static void getStepTree(List<SqlScenarioReportStepDTO> steps, Map<String, List<SqlScenarioReportStepDTO>> scenarioReportStepMap) {
        if (CollectionUtils.isNotEmpty(steps)) {
            steps.parallelStream().forEach(step -> {
                List<SqlScenarioReportStepDTO> children = scenarioReportStepMap.get(step.getStepId());
                if (CollectionUtils.isNotEmpty(children)) {
                    children.sort(Comparator.comparingLong(SqlScenarioReportStepDTO::getSort));
                    step.setChildren(children);
                    getStepTree(children, scenarioReportStepMap);
                    //如果是父级的报告，需要计算请求时间  请求时间是所有子级的请求时间之和 还需要计算请求的大小  还有请求的数量 以及请求成功的状态
                    step.setRequestTime(step.getChildren().stream().mapToLong(child -> child.getRequestTime() != null ? child.getRequestTime() : 0).sum());
                    step.setResponseSize(step.getChildren().stream().mapToLong(child -> child.getResponseSize() != null ? child.getResponseSize() : 0).sum());
                    //请求的状态， 如果是 LOOP_CONTROLLER IF_CONTROLLER ONCE_ONLY_CONTROLLER  则需要判断子级的状态 但是如果下面没有子集不需要判断状态
                    //需要把这些数据拿出来 如果没有子请求说明是最后一级的请求 不需要计算入状态
                    //获取所有的子请求的状态
                    List<String> requestStatus = children.stream().map(SqlScenarioReportStepDTO::getStatus).toList();
                    //获取为执行的状态
                    List<String> pendingStatus = requestStatus.stream().filter(status -> StringUtils.equals(ExecStatus.PENDING.name(), status) || StringUtils.isBlank(status)).toList();
                    //过滤出来SUCCESS的状态
                    List<String> successStatus = requestStatus.stream().filter(status -> StringUtils.equals(ResultStatus.SUCCESS.name(), status)).toList();
                    //只要包含ERROR 就是ERROR
                    if (requestStatus.contains(ResultStatus.ERROR.name())) {
                        step.setStatus(ResultStatus.ERROR.name());
                    } else if (requestStatus.contains(ResultStatus.FAKE_ERROR.name())) {
                        step.setStatus(ResultStatus.FAKE_ERROR.name());
                    } else if (successStatus.size() + pendingStatus.size() == children.size() && !successStatus.isEmpty()) {
                        step.setStatus(ResultStatus.SUCCESS.name());
                    }
                }
            });
        }
    }
}
