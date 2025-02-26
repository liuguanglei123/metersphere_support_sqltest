package io.metersphere.sql.service.debug;

import io.metersphere.project.dto.ModuleCountDTO;
import io.metersphere.project.dto.NodeSortDTO;
import io.metersphere.project.service.ModuleTreeService;
import io.metersphere.sdk.constants.ModuleConstants;
import io.metersphere.sdk.exception.MSException;
import io.metersphere.sdk.util.Translator;
import io.metersphere.sql.domain.*;
import io.metersphere.sql.mapper.ExtSqlDebugModuleMapper;
import io.metersphere.sql.mapper.SqlDebugBlobMapper;
import io.metersphere.sql.mapper.SqlDebugMapper;
import io.metersphere.sql.mapper.SqlDebugModuleMapper;
import io.metersphere.sql.pojo.dto.ModuleCreateRequest;
import io.metersphere.sql.pojo.dto.ModuleUpdateRequest;
import io.metersphere.sql.pojo.dto.SqlDebugRequest;
import io.metersphere.sql.pojo.dto.SqlTreeNode;
import io.metersphere.system.dto.sdk.BaseTreeNode;

import io.metersphere.system.dto.sdk.request.NodeMoveRequest;
import io.metersphere.system.uid.IDGenerator;
import jakarta.annotation.Resource;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;


import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SqlDebugModuleService extends ModuleTreeService {

    private static final String UNPLANNED = "sql_debug_module.unplanned_request";
    private static final String DEBUG_MODULE_COUNT_ALL = "all";
    private static final String MODULE_NO_EXIST = "sql_module.not.exist";

    @Resource
    private ExtSqlDebugModuleMapper extSqlDebugModuleMapper;

    @Resource
    private SqlDebugModuleMapper sqlDebugModuleMapper;

    @Resource
    private SqlDebugModuleLogService sqlDebugModuleLogService;

    @Resource
    private SqlDebugMapper sqlDebugMapper;

    @Resource
    private SqlDebugBlobMapper sqlDebugBlobMapper;


    public List<BaseTreeNode> getTree(String userId) {
        List<BaseTreeNode> fileModuleList = extSqlDebugModuleMapper.selectBaseByUser(userId);
        List<BaseTreeNode> baseTreeNodes = super.buildTreeAndCountResource(fileModuleList, true, Translator.get(UNPLANNED));
        List<SqlTreeNode> sqlTreeNodeList = extSqlDebugModuleMapper.selectSqlDebugByUser(userId);
        return getBaseTreeNodes(sqlTreeNodeList, baseTreeNodes);

    }

    public List<BaseTreeNode> getBaseTreeNodes(List<SqlTreeNode> sqlTreeNodeList, List<BaseTreeNode> baseTreeNodes) {
        if (CollectionUtils.isEmpty(sqlTreeNodeList)) {
            return baseTreeNodes;
        }
        List<BaseTreeNode> nodeList = sqlTreeNodeList.stream().map(sqlTreeNode -> {
            BaseTreeNode baseTreeNode = new BaseTreeNode();
            baseTreeNode.setId(sqlTreeNode.getId());
            baseTreeNode.setName(sqlTreeNode.getName());
            baseTreeNode.setParentId(sqlTreeNode.getParentId());
            baseTreeNode.setType(sqlTreeNode.getType());
            return baseTreeNode;
        }).toList();
        //sqlTreeNodeList使用stream实现将parentId分组生成map
        Map<String, List<BaseTreeNode>> sqlTreeNodeMap = nodeList.stream().collect(Collectors.groupingBy(BaseTreeNode::getParentId));
        //遍历baseTreeNodes，将sqlTreeNodeMap中的id相等的数据添加到baseTreeNodes中
        return generateTree(baseTreeNodes, sqlTreeNodeMap);
    }

    //生成树结构
    private List<BaseTreeNode> generateTree(List<BaseTreeNode> baseTreeNodes, Map<String, List<BaseTreeNode>> sqlTreeNodeMap) {
        baseTreeNodes.forEach(baseTreeNode -> {
            if (sqlTreeNodeMap.containsKey(baseTreeNode.getId())) {
                baseTreeNode.getChildren().addAll(sqlTreeNodeMap.get(baseTreeNode.getId()));
            }
            if (CollectionUtils.isNotEmpty(baseTreeNode.getChildren())) {
                generateTree(baseTreeNode.getChildren(), sqlTreeNodeMap);
            }
        });
        return baseTreeNodes;
    }

    public Map<String, Long> moduleCount(SqlDebugRequest request, String operator) {
        //查出每个模块节点下的资源数量。 不需要按照模块进行筛选
        request.setModuleIds(null);
        List<ModuleCountDTO> moduleCountDTOList = extSqlDebugModuleMapper.countModuleIdByKeyword(request, operator);
        long allCount = getAllCount(moduleCountDTOList);
        Map<String, Long> moduleCountMap = getModuleCountMap(operator, moduleCountDTOList);
        moduleCountMap.put(DEBUG_MODULE_COUNT_ALL, allCount);
        return moduleCountMap;
    }

    /**
     * 查找当前项目下模块每个节点对应的资源统计
     */
    public Map<String, Long> getModuleCountMap(String userId, List<ModuleCountDTO> moduleCountDTOList) {
        //构建模块树，并计算每个节点下的所有数量（包含子节点）
        List<BaseTreeNode> treeNodeList = this.getTreeOnlyIdsAndResourceCount(userId, moduleCountDTOList);
        return super.getIdCountMapByBreadth(treeNodeList);
    }

    public List<BaseTreeNode> getTreeOnlyIdsAndResourceCount(String userId, List<ModuleCountDTO> moduleCountDTOList) {
        //节点内容只有Id和parentId
        List<BaseTreeNode> fileModuleList = extSqlDebugModuleMapper.selectIdAndParentIdByUserId(userId);
        return super.buildTreeAndCountResource(fileModuleList, moduleCountDTOList, true, Translator.get(UNPLANNED));
    }

    public String add(ModuleCreateRequest request, String operator) {
        SqlDebugModule sqlDebugModule = new SqlDebugModule();
        sqlDebugModule.setId(IDGenerator.nextStr());
        sqlDebugModule.setName(request.getName());
        sqlDebugModule.setParentId(request.getParentId());
        sqlDebugModule.setProjectId(request.getProjectId());
        sqlDebugModule.setCreateUser(operator);
        this.checkDataValidity(sqlDebugModule);
        sqlDebugModule.setCreateTime(System.currentTimeMillis());
        sqlDebugModule.setUpdateTime(sqlDebugModule.getCreateTime());
        sqlDebugModule.setPos(this.countPos(request.getParentId()));
        sqlDebugModule.setUpdateUser(operator);
        sqlDebugModuleMapper.insert(sqlDebugModule);
        //记录日志
        sqlDebugModuleLogService.saveAddLog(sqlDebugModule, operator);
        return sqlDebugModule.getId();
    }

    /**
     * 检查数据的合法性
     */
    private void checkDataValidity(SqlDebugModule sqlDebugModule) {
        SqlDebugModuleExample example = new SqlDebugModuleExample();
        if (!StringUtils.equals(sqlDebugModule.getParentId(), ModuleConstants.ROOT_NODE_PARENT_ID)) {
            //检查父ID是否存在  调试模块的逻辑是  同一个用户下的同级模块不能重名  每个协议是不同的模块
            example.createCriteria().andIdEqualTo(sqlDebugModule.getParentId())
                    .andCreateUserEqualTo(sqlDebugModule.getCreateUser());
            if (sqlDebugModuleMapper.countByExample(example) == 0) {
                throw new MSException(Translator.get("parent.node.not_blank"));
            }
            example.clear();
        }
        example.createCriteria().andParentIdEqualTo(sqlDebugModule.getParentId())
                .andNameEqualTo(sqlDebugModule.getName())
                .andIdNotEqualTo(sqlDebugModule.getId())
                .andCreateUserEqualTo(sqlDebugModule.getCreateUser());
        if (sqlDebugModuleMapper.countByExample(example) > 0) {
            throw new MSException(Translator.get("node.name.repeat"));
        }
        example.clear();
    }

    @Override
    public void updatePos(String id, long pos) {

    }

    @Override
    public void refreshPos(String parentId) {

    }

    private Long countPos(String parentId) {
        Long maxPos = extSqlDebugModuleMapper.getMaxPosByParentId(parentId);
        if (maxPos == null) {
            return LIMIT_POS;
        } else {
            return maxPos + LIMIT_POS;
        }
    }

    public void update(ModuleUpdateRequest request, String userId, String projectId) {
        SqlDebugModule module = checkModuleExist(request.getId());
        SqlDebugModule updateModule = new SqlDebugModule();
        updateModule.setId(request.getId());
        updateModule.setName(request.getName());
        updateModule.setParentId(module.getParentId());
        updateModule.setCreateUser(module.getCreateUser());
        this.checkDataValidity(updateModule);
        updateModule.setUpdateTime(System.currentTimeMillis());
        updateModule.setUpdateUser(userId);
        updateModule.setProjectId(projectId);
        sqlDebugModuleMapper.updateByPrimaryKeySelective(updateModule);
        //记录日志
        sqlDebugModuleLogService.saveUpdateLog(updateModule, projectId, userId);
    }

    public SqlDebugModule checkModuleExist(String moduleId) {
        SqlDebugModule module = sqlDebugModuleMapper.selectByPrimaryKey(moduleId);
        if (module == null) {
            throw new MSException(Translator.get(MODULE_NO_EXIST));
        }
        return module;
    }

    public void deleteModule(String deleteId, String currentUser) {
        SqlDebugModule deleteModule = checkModuleExist(deleteId);
        if (deleteModule != null) {
            this.deleteModule(Collections.singletonList(deleteId), currentUser, deleteModule.getProjectId());
        }
    }

    public void deleteModule(List<String> deleteIds, String currentUser, String projectId) {
        if (CollectionUtils.isEmpty(deleteIds)) {
            return;
        }
        List<BaseTreeNode> baseTreeNodes = extSqlDebugModuleMapper.selectBaseNodeByIds(deleteIds);
        extSqlDebugModuleMapper.deleteByIds(deleteIds);
        sqlDebugModuleLogService.saveDeleteModuleLog(baseTreeNodes, currentUser, projectId);
        //删除模块下的所有接口
        SqlDebugExample example = new SqlDebugExample();
        example.createCriteria().andModuleIdIn(deleteIds);
        List<SqlDebug> sqlDebugs = sqlDebugMapper.selectByExample(example);
        if (CollectionUtils.isNotEmpty(sqlDebugs)) {
            List<String> sqlDebugIds = sqlDebugs.stream().map(SqlDebug::getId).toList();
            sqlDebugMapper.deleteByExample(example);
            SqlDebugBlobExample blobExample = new SqlDebugBlobExample();
            blobExample.createCriteria().andIdIn(sqlDebugIds);
            sqlDebugBlobMapper.deleteByExample(blobExample);
            // TODO：暂不清楚module和上传文件的关系，下面几行内容先注释，待确认
            //删除文件关联关系
//            String sqlDebugDir = DefaultRepositoryDir.getSqlDebugDir(projectId, StringUtils.EMPTY);
//            sqlFileResourceService.deleteByResourceIds(sqlDebugDir, sqlDebugDir, projectId, currentUser, OperationLogModule.SQL_TEST_DEBUG_MANAGEMENT_DEBUG);

            sqlDebugModuleLogService.saveDeleteDataLog(sqlDebugs, currentUser, projectId);
        }

        List<String> childrenIds = extSqlDebugModuleMapper.selectChildrenIdsByParentIds(deleteIds);
        if (CollectionUtils.isNotEmpty(childrenIds)) {
            deleteModule(childrenIds, currentUser, projectId);
        }
    }

    public void moveNode(NodeMoveRequest request, String currentUser) {
        NodeSortDTO nodeSortDTO = super.getNodeSortDTO(request,
                extSqlDebugModuleMapper::selectBaseModuleById,
                extSqlDebugModuleMapper::selectModuleByParentIdAndPosOperator);

        SqlDebugModuleExample example = new SqlDebugModuleExample();
        example.createCriteria().andParentIdEqualTo(nodeSortDTO.getParent().getId()).andIdEqualTo(request.getDragNodeId());
        //节点换到了别的节点下,要先更新parent节点.
        if (sqlDebugModuleMapper.countByExample(example) == 0) {
            SqlDebugModule fileModule = new SqlDebugModule();
            SqlDebugModule currentModule = sqlDebugModuleMapper.selectByPrimaryKey(request.getDragNodeId());
            currentModule.setParentId(nodeSortDTO.getParent().getId());
            checkDataValidity(currentModule);
            fileModule.setId(request.getDragNodeId());
            fileModule.setParentId(nodeSortDTO.getParent().getId());
            sqlDebugModuleMapper.updateByPrimaryKeySelective(fileModule);
        }

        super.sort(nodeSortDTO);
        //记录日志
        sqlDebugModuleLogService.saveMoveLog(nodeSortDTO, currentUser);
    }
}
