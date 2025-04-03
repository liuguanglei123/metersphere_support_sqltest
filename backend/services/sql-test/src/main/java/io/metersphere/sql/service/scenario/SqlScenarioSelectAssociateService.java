package io.metersphere.sql.service.scenario;

import io.metersphere.sdk.dto.AssociateCaseDTO;
import io.metersphere.sql.domain.SqlDefinition;
import io.metersphere.sql.mapper.ExtSqlDefinitionMapper;
import io.metersphere.sql.pojo.dto.scenario.SqlScenarioSelectAssociateDTO;
import io.metersphere.sql.pojo.dto.scenario.SqlScenarioStepDTO;
import io.metersphere.system.dto.ModuleSelectDTO;
import jakarta.annotation.Resource;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;
import io.metersphere.sql.constant.SqlScenarioStepType;
import java.util.*;

@Service
public class SqlScenarioSelectAssociateService {

    @Resource
    private ExtSqlDefinitionMapper extSqlDefinitionMapper;

    public static final String MODULE_ALL = "all";


    public List<SqlScenarioStepDTO> getSelectDto(Map<String, SqlScenarioSelectAssociateDTO> request) {
        List<SqlScenarioStepDTO> steps = new ArrayList<>();
        for (String key : request.keySet()) {
            switch (key) {
                case "SCENARIO":
                    List<SqlScenarioStepDTO> sqlScenarioStepDTOs = handleSqlScenarioData(request.get(key));
                    steps.addAll(sqlScenarioStepDTOs);
                    break;
                default:
                    List<SqlScenarioStepDTO> sqlStepDTOs = handleSqlData(request.get(key));
                    steps.addAll(sqlStepDTOs);
                    break;
            }
        }
        return steps;
    }

    private List<SqlScenarioStepDTO> handleSqlScenarioData(SqlScenarioSelectAssociateDTO request) {
        // TODO：
        return new ArrayList<>();
    }

    private List<SqlScenarioStepDTO> handleSqlData(SqlScenarioSelectAssociateDTO request) {
        List<SqlScenarioStepDTO> steps = new ArrayList<>();
        boolean selectAllModule = request.isSelectAllModule();
        Map<String, ModuleSelectDTO> moduleMaps = request.getModuleMaps();
        moduleMaps.remove(MODULE_ALL);
        if (selectAllModule) {
            // 选择了全部模块
            List<SqlDefinition> sqlDefinitionList = extSqlDefinitionMapper.selectAllSql(request.getProjectId());
            getSqlSteps(request, sqlDefinitionList, steps);
        } else {
            AssociateCaseDTO dto = getCaseIds(moduleMaps);
            List<SqlDefinition> sqlDefinitionList = new ArrayList<>();
            //获取全选的模块数据
            if (CollectionUtils.isNotEmpty(dto.getModuleIds())) {
                sqlDefinitionList = extSqlDefinitionMapper.getListBySelectModules(request.getProjectId(), dto.getModuleIds());
            }

            if (CollectionUtils.isNotEmpty(dto.getSelectIds())) {
                CollectionUtils.removeAll(dto.getSelectIds(), sqlDefinitionList.stream().map(SqlDefinition::getId).toList());
                //获取选中的ids数据
                List<SqlDefinition> selectIdList = extSqlDefinitionMapper.getListBySelectIds(request.getProjectId(), dto.getSelectIds());
                sqlDefinitionList.addAll(selectIdList);
            }
            if (CollectionUtils.isNotEmpty(dto.getExcludeIds())) {
                //排除的ids
                List<String> excludeIds = dto.getExcludeIds();
                sqlDefinitionList = sqlDefinitionList.stream().filter(item -> !excludeIds.contains(item.getId())).toList();
            }
            if (CollectionUtils.isNotEmpty(sqlDefinitionList)) {
                List<SqlDefinition> list = sqlDefinitionList.stream().sorted(Comparator.comparing(SqlDefinition::getPos)).toList();
                getSqlSteps(request, list, steps);
            }

        }
        return steps;
    }


    private static void getSqlSteps(SqlScenarioSelectAssociateDTO request, List<SqlDefinition> sqlDefinitionList, List<SqlScenarioStepDTO> steps) {
        sqlDefinitionList.forEach(item -> {
            SqlScenarioStepDTO step = new SqlScenarioStepDTO();
            LinkedHashMap<String, Object> config = new LinkedHashMap<>();
            config.put("enable", true);
            config.put("id", "");
            config.put("name", "");
            step.setConfig(config);
            step.setStepType(SqlScenarioStepType.SQL.name());
            step.setName(item.getName());
            step.setResourceId(item.getId());
            step.setRefType(request.getRefType());
            step.setProjectId(item.getProjectId());
            step.setOriginProjectId(item.getProjectId());
//            step.setResourceNum(item.getNum().toString());
            step.setVersionId(item.getVersionId());
            steps.add(step);
        });
    }

    protected AssociateCaseDTO getCaseIds(Map<String, ModuleSelectDTO> moduleMaps) {
        // 排除的ids
        List<String> excludeIds = moduleMaps.values().stream()
                .flatMap(moduleSelectDTO -> moduleSelectDTO.getExcludeIds().stream())
                .toList();
        // 选中的ids
        List<String> selectIds = moduleMaps.values().stream()
                .filter(moduleSelectDTO -> BooleanUtils.isFalse(moduleSelectDTO.isSelectAll()) && org.apache.commons.collections4.CollectionUtils.isNotEmpty(moduleSelectDTO.getSelectIds()))
                .flatMap(moduleSelectDTO -> moduleSelectDTO.getSelectIds().stream())
                .toList();
        // 全选的模块
        List<String> moduleIds = moduleMaps.entrySet().stream()
                .filter(entry -> BooleanUtils.isTrue(entry.getValue().isSelectAll()))
                .map(Map.Entry::getKey)
                .toList();

        return new AssociateCaseDTO(excludeIds, selectIds, moduleIds);
    }


}