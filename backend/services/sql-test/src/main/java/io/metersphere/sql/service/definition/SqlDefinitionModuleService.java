package io.metersphere.sql.service.definition;

import com.github.pagehelper.PageHelper;
import io.metersphere.api.domain.*;
import io.metersphere.api.mapper.ApiDefinitionMapper;
import io.metersphere.api.mapper.ApiDefinitionModuleMapper;
import io.metersphere.plan.domain.TestPlanConfig;
import io.metersphere.plan.mapper.TestPlanConfigMapper;
import io.metersphere.project.dto.ModuleCountDTO;
import io.metersphere.project.dto.NodeSortDTO;
import io.metersphere.project.service.ModuleTreeService;
import io.metersphere.sdk.constants.ModuleConstants;
import io.metersphere.sdk.exception.MSException;
import io.metersphere.sdk.util.CommonBeanFactory;
import io.metersphere.sdk.util.Translator;
import io.metersphere.sql.domain.SqlDefinitionModule;
import io.metersphere.sql.domain.SqlDefinitionModuleExample;
import io.metersphere.sql.mapper.ExtSqlDefinitionModuleMapper;
import io.metersphere.sql.mapper.SqlDefinitionModuleMapper;
import io.metersphere.sql.pojo.dto.ModuleCreateRequest;
import io.metersphere.sql.pojo.dto.SqlTreeNode;
import io.metersphere.sql.pojo.dto.definition.SqlModuleRequest;
import io.metersphere.sql.service.debug.SqlDebugModuleService;
import io.metersphere.system.dto.sdk.BaseTreeNode;
import io.metersphere.system.uid.IDGenerator;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;


@Service
@Transactional(rollbackFor = Exception.class)
public class SqlDefinitionModuleService extends ModuleTreeService {
    private static final String UNPLANNED_API = "api_unplanned_request";

    @Resource
    private ExtSqlDefinitionModuleMapper extSqlDefinitionModuleMapper;

    @Resource
    private SqlDebugModuleService sqlDebugModuleService;

    @Resource
    private SqlDefinitionModuleMapper sqlDefinitionModuleMapper;

    public List<BaseTreeNode> getTree(SqlModuleRequest request, boolean deleted, boolean containRequest) {
        //接口的树结构是  模块：子模块+接口 接口为非delete状态的
        List<BaseTreeNode> fileModuleList = extSqlDefinitionModuleMapper.selectBaseByRequest(request);
        List<BaseTreeNode> baseTreeNodes = super.buildTreeAndCountResource(fileModuleList, true, Translator.get(UNPLANNED_API));
        if (!containRequest) {
            return baseTreeNodes;
        }
        SqlDefinitionService sqlDefinitionService = CommonBeanFactory.getBean(SqlDefinitionService.class);
        // TODO：在ms的接口测试中有如下两行代码，但是没看懂处理逻辑，猜测可能和木块覆盖率和接口覆盖率有些关联
        //  参考木块覆盖率，可以考虑在db测试中实现模块覆盖率的统计，比如DDL模块下有个array类型的字段，可能存在增删改 字段的逻辑，
        //  可以增加在模块树中三个模块（目录），这样就可以根据sql的数据实现模块覆盖率
//        request.setExcludeIds(sqlDefinitionService.getQueryExcludeIds(request.getFilter(), request.getProjectId(), request.getProtocols()));
//        request.setIncludeIds(sqlDefinitionService.getQueryIncludeIds(request.getFilter(), request.getProjectId(), request.getProtocols()));
        List<SqlTreeNode> sqlTreeNodeList = extSqlDefinitionModuleMapper.selectSqlDataByRequest(request, deleted);
        return sqlDebugModuleService.getBaseTreeNodes(sqlTreeNodeList, baseTreeNodes);
    }

    // TODO：
    @Override
    public void updatePos(String id, long pos) {

    }

    // TODO：
    @Override
    public void refreshPos(String parentId) {

    }

    public String add(ModuleCreateRequest request, String operator) {
        SqlDefinitionModule module = new SqlDefinitionModule();
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
        sqlDefinitionModuleMapper.insert(module);
        // TODO：记录日志
//        sqlDefinitionModuleLogService.saveAddLog(module, operator);
        return module.getId();
    }

    /**
     * 检查数据的合法性
     */
    private void checkDataValidity(SqlDefinitionModule module) {
        SqlDefinitionModuleExample example = new SqlDefinitionModuleExample();
        if (!StringUtils.equals(module.getParentId(), ModuleConstants.ROOT_NODE_PARENT_ID)) {
            //检查父ID是否存在  接口模块的逻辑是  同一个协议下的  同一个项目的同层级节点下不能有相同的名称
            example.createCriteria().andIdEqualTo(module.getParentId())
                    .andProjectIdEqualTo(module.getProjectId());
            if (sqlDefinitionModuleMapper.countByExample(example) == 0) {
                throw new MSException(Translator.get("parent.node.not_blank"));
            }
            example.clear();
        }
        example.createCriteria().andParentIdEqualTo(module.getParentId())
                .andNameEqualTo(module.getName()).andIdNotEqualTo(module.getId())
                .andProjectIdEqualTo(module.getProjectId());
        if (sqlDefinitionModuleMapper.countByExample(example) > 0) {
            throw new MSException(Translator.get("node.name.repeat"));
        }
        example.clear();
    }

    public Long getNextOrder(String parentId) {
        Long maxPos = extSqlDefinitionModuleMapper.getMaxPosByParentId(parentId);
        if (maxPos == null) {
            return LIMIT_POS;
        } else {
            return maxPos + LIMIT_POS;
        }
    }
}
