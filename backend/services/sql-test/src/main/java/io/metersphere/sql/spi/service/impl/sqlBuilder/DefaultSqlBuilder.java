package io.metersphere.sql.spi.service.impl.sqlBuilder;

import com.google.common.collect.Lists;
import io.metersphere.sql.spi.model.Table;
import io.metersphere.sql.spi.service.SqlBuilder;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.GroupByElement;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DefaultSqlBuilder implements SqlBuilder<Table> {

    @Override
    public String pageLimit(String sql, int offset, int pageNo, int pageSize) {
        return null;
    }
}