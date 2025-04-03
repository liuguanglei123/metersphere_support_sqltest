package io.metersphere.sql.pojo.model;

import io.metersphere.sql.pojo.vo.ExecuteResultVO;
import io.metersphere.sql.spi.model.Header;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.*;

/**
 * Results of the
 *
 * @author Jiaju Zhuang
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DiffExecuteResult extends ExecuteResult {

    private String reportId;

    private String stepId;

    // 差异的表头，比如预期比实际多一列，map内容可能是{"more"：["col1","col2"]},反之少了则为less
    private Map<String,String> diffHeaders;

    // 合并后的数据，如果部分数据存在差异，那么会被合并到一起，合并后的内容是：预期值：xxx 实际值：yyy，如果预期和实际一致，则以responseDefinition为准显示即可
    private List<List<String>> mergeDataList;

    // 差异的数据行id，用于前端标记
    private List<Integer> diffDataRowIds;

    public static Map.Entry<Boolean, Map<String, String>> diffheader(ExecuteResult A, ExecuteResult B) {
        return Map.entry(true,null);
// TODO：
//        Set<Header> setA = new HashSet<>(A.getHeaderList());
//        Set<Header> setB = new HashSet<>(B.getHeaderList());

//        if (setA.equals(setB)) {
//            return new AbstractMap.SimpleEntry<>(true, null);
//        } else {
//            if (setA.size() > setB.size()) {
//                List<Header> extraHeaders = new ArrayList<>(setA);
//                extraHeaders.removeIf(setB::contains);
//                String extraHeadersStr = String.join(",", extraHeaders);
//                return new AbstractMap.SimpleEntry<>(false, new HashMap<String, String>() {{
//                    put("more", extraHeadersStr);
//                }});
//            } else {
//                List<String> missingHeaders = new ArrayList<>(setB);
//                missingHeaders.removeIf(setA::contains);
//                String missingHeadersStr = String.join(",", missingHeaders);
//                return new AbstractMap.SimpleEntry<>(false, new HashMap<String, String>() {{
//                    put("less", missingHeadersStr);
//                }});
//            }
//        }
    }

    public static Triple<Boolean, List<Integer>, List<List<String>>> diffdata(ExecuteResult A, ExecuteResult B) {
        Map<String, List<String>> mapA = new HashMap<>();
        Map<String, List<String>> mapB = new HashMap<>();

        for (List<String> row : A.getDataList()) {
            mapA.put(row.get(0), row);
        }
        for (List<String> row : B.getDataList()) {
            mapB.put(row.get(0), row);
        }

        List<Integer> diffIndices = new ArrayList<>();
        List<List<String>> modifiedA = new ArrayList<>();

        for (int i = 0; i < A.getDataList().size(); i++) {
            List<String> rowA = A.getDataList().get(i);
            String key = rowA.get(0);
            List<String> rowB = mapB.get(key);

            if (rowB == null) {
                diffIndices.add(i);
                modifiedA.add(markDifference(rowA, null));
            } else {
                List<String> markedRow = new ArrayList<>(rowA);
                for (int j = 1; j < rowA.size(); j++) {
                    if (!rowA.get(j).equals(rowB.get(j))) {
                        markedRow.set(j, rowA.get(j) + " vs " + rowB.get(j));
                        diffIndices.add(i);
                        break;
                    }
                }
                modifiedA.add(markedRow);
            }
        }

        boolean isConsistent = diffIndices.isEmpty();
        return Triple.of(isConsistent, diffIndices, modifiedA);
    }

    private static List<String> markDifference(List<String> rowA, List<String> rowB) {
        List<String> markedRow = new ArrayList<>(rowA);
        if (rowB == null) {
            for (int j = 1; j < markedRow.size(); j++) {
                markedRow.set(j, markedRow.get(j) + " vs null");
            }
        } else {
            for (int j = 1; j < markedRow.size(); j++) {
                if (j < rowB.size()) {
                    markedRow.set(j, markedRow.get(j) + " vs " + rowB.get(j));
                } else {
                    markedRow.set(j, markedRow.get(j) + " vs null");
                }
            }
        }
        return markedRow;
    }

    public static DiffExecuteResult diffOther(ExecuteResultVO expectResult, ExecuteResult actualResult){
        DiffExecuteResult result = new DiffExecuteResult();
        BeanUtils.copyProperties(actualResult, result);

        return result;
    }

}
