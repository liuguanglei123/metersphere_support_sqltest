package io.metersphere.sql.service.scenario;

import io.metersphere.plan.domain.TestPlanConfig;
import io.metersphere.project.dto.ModuleCountDTO;
import io.metersphere.project.service.ModuleTreeService;
import io.metersphere.sdk.constants.ModuleConstants;
import io.metersphere.sdk.exception.MSException;
import io.metersphere.sdk.util.Translator;
import io.metersphere.sql.domain.SqlScenarioModule;
import io.metersphere.sql.domain.SqlScenarioModuleExample;
import io.metersphere.sql.mapper.ExtSqlDefinitionModuleMapper;
import io.metersphere.sql.mapper.ExtSqlScenarioMapper;
import io.metersphere.sql.mapper.ExtSqlScenarioModuleMapper;
import io.metersphere.sql.mapper.SqlScenarioModuleMapper;
import io.metersphere.sql.pojo.dto.ModuleCreateRequest;
import io.metersphere.sql.pojo.dto.ModuleUpdateRequest;
import io.metersphere.sql.pojo.dto.definition.SqlModuleRequest;
import io.metersphere.sql.pojo.dto.scenario.SqlScenarioModuleRequest;
import io.metersphere.system.dto.sdk.BaseTreeNode;
import io.metersphere.system.uid.IDGenerator;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional(rollbackFor = Exception.class)
public class SqlScenarioModuleService extends ModuleTreeService {
    private static final String UNPLANNED_SCENARIO = "api_unplanned_scenario";
    private static final String DEBUG_MODULE_COUNT_ALL = "all";
    private static final String MODULE_NO_EXIST = "api_module.not.exist";

    @Resource
    private ExtSqlScenarioModuleMapper extSqlScenarioModuleMapper;

    @Resource
    private ExtSqlScenarioMapper extSqlScenarioMapper;

    @Resource
    private SqlScenarioModuleMapper sqlScenarioModuleMapper;

    public List<BaseTreeNode> getTree(SqlScenarioModuleRequest request) {
        //接口的树结构是  模块：子模块+接口 接口为非delete状态的
        List<BaseTreeNode> fileModuleList = extSqlScenarioModuleMapper.selectBaseByRequest(request);
        return super.buildTreeAndCountResource(fileModuleList, true, Translator.get(UNPLANNED_SCENARIO));
    }


    public Map<String, Long> moduleCount(SqlScenarioModuleRequest request, boolean deleted) {
        if (StringUtils.isNotEmpty(request.getTestPlanId())) {
            this.checkTestPlanRepeatCase(request);
        }
        request.setModuleIds(null);
        //查找根据moduleIds查找模块下的接口数量 查非delete状态的
        List<ModuleCountDTO> moduleCountDTOList = extSqlScenarioMapper.countModuleIdByRequest(request, deleted);
        long allCount = getAllCount(moduleCountDTOList);
        request.setKeyword(null);
        request.setScenarioId(null);
        Map<String, Long> moduleCountMap = getModuleCountMap(request, moduleCountDTOList);
        moduleCountMap.put(DEBUG_MODULE_COUNT_ALL, allCount);
        return moduleCountMap;
    }

    /**
     * 查找当前项目下模块每个节点对应的资源统计
     */
    public Map<String, Long> getModuleCountMap(SqlScenarioModuleRequest request, List<ModuleCountDTO> moduleCountDTOList) {
        List<BaseTreeNode> treeNodeList = this.getTreeOnlyIdsAndResourceCount(request, moduleCountDTOList);
        return super.getIdCountMapByBreadth(treeNodeList);
    }

    public List<BaseTreeNode> getTreeOnlyIdsAndResourceCount(SqlScenarioModuleRequest request, List<ModuleCountDTO> moduleCountDTOList) {
        //节点内容只有Id和parentId
        List<BaseTreeNode> fileModuleList = extSqlScenarioModuleMapper.selectIdAndParentIdByRequest(request);
        return super.buildTreeAndCountResource(fileModuleList, moduleCountDTOList, true, Translator.get(UNPLANNED_SCENARIO));
    }

    private void checkTestPlanRepeatCase(SqlScenarioModuleRequest request) {
        // TODO：
//        TestPlanConfig testPlanConfig = testPlanConfigMapper.selectByPrimaryKey(request.getTestPlanId());
//        if (testPlanConfig != null && BooleanUtils.isTrue(testPlanConfig.getRepeatCase())) {
//            //测试计划允许重复用例，意思就是统计不受测试计划影响。去掉这个条件，
//            request.setTestPlanId(null);
//        }
    }

    public String add(ModuleCreateRequest request, String operator) {
        SqlScenarioModule module = new SqlScenarioModule();
        module.setId(IDGenerator.nextStr());
        module.setName(request.getName());
        module.setParentId(request.getParentId());
        module.setProjectId(request.getProjectId());
        module.setCreateUser(operator);
        this.checkDataValidity(module);
        module.setCreateTime(System.currentTimeMillis());
        module.setUpdateTime(module.getCreateTime());
        module.setPos(this.getNextOrder(request.getParentId()));
        module.setUpdateUser(operator);
        sqlScenarioModuleMapper.insertSelective(module);
        // TODO：记录日志
//        sqlScenarioModuleLogService.saveAddLog(module, operator);
        return module.getId();
    }

    /**
     * 检查数据的合法性
     */
    private void checkDataValidity(SqlScenarioModule module) {
        SqlScenarioModuleExample example = new SqlScenarioModuleExample();
        if (!StringUtils.equals(module.getParentId(), ModuleConstants.ROOT_NODE_PARENT_ID)) {
            example.createCriteria().andIdEqualTo(module.getParentId())
                    .andProjectIdEqualTo(module.getProjectId());
            if (sqlScenarioModuleMapper.countByExample(example) == 0) {
                throw new MSException(Translator.get("parent.node.not_blank"));
            }
            example.clear();
        }
        example.createCriteria().andParentIdEqualTo(module.getParentId())
                .andNameEqualTo(module.getName()).andIdNotEqualTo(module.getId())
                .andProjectIdEqualTo(module.getProjectId());
        if (sqlScenarioModuleMapper.countByExample(example) > 0) {
            throw new MSException(Translator.get("node.name.repeat"));
        }
        example.clear();
    }

    public void update(ModuleUpdateRequest request, String userId) {
        SqlScenarioModule module = checkResourceExist(request.getId());
        SqlScenarioModule updateModule = new SqlScenarioModule();
        updateModule.setId(request.getId());
        updateModule.setName(request.getName());
        updateModule.setParentId(module.getParentId());
        updateModule.setProjectId(module.getProjectId());
        this.checkDataValidity(updateModule);
        updateModule.setUpdateTime(System.currentTimeMillis());
        updateModule.setUpdateUser(userId);
        sqlScenarioModuleMapper.updateByPrimaryKeySelective(updateModule);
        // TODO：记录日志
//        apiScenarioModuleLogService.saveUpdateLog(updateModule, userId);
    }

    public SqlScenarioModule checkResourceExist(String id) {
        SqlScenarioModule module = sqlScenarioModuleMapper.selectByPrimaryKey(id);
        if (module == null) {
            throw new MSException(Translator.get(MODULE_NO_EXIST));
        }
        return module;
    }

    public Long getNextOrder(String parentId) {
        Long maxPos = extSqlScenarioModuleMapper.getMaxPosByParentId(parentId);
        if (maxPos == null) {
            return LIMIT_POS;
        } else {
            return maxPos + LIMIT_POS;
        }
    }

    @Override
    public void updatePos(String id, long pos) {
        // TODO：
    }

    @Override
    public void refreshPos(String parentId) {
        // TODO：
    }
}
