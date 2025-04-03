package io.metersphere.sql.domain;

import java.util.ArrayList;
import java.util.List;

public class SqlScenarioRecordExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public SqlScenarioRecordExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andSqlScenarioReportIdIsNull() {
            addCriterion("sql_scenario_report_id is null");
            return (Criteria) this;
        }

        public Criteria andSqlScenarioReportIdIsNotNull() {
            addCriterion("sql_scenario_report_id is not null");
            return (Criteria) this;
        }

        public Criteria andSqlScenarioReportIdEqualTo(String value) {
            addCriterion("sql_scenario_report_id =", value, "sqlScenarioReportId");
            return (Criteria) this;
        }

        public Criteria andSqlScenarioReportIdNotEqualTo(String value) {
            addCriterion("sql_scenario_report_id <>", value, "sqlScenarioReportId");
            return (Criteria) this;
        }

        public Criteria andSqlScenarioReportIdGreaterThan(String value) {
            addCriterion("sql_scenario_report_id >", value, "sqlScenarioReportId");
            return (Criteria) this;
        }

        public Criteria andSqlScenarioReportIdGreaterThanOrEqualTo(String value) {
            addCriterion("sql_scenario_report_id >=", value, "sqlScenarioReportId");
            return (Criteria) this;
        }

        public Criteria andSqlScenarioReportIdLessThan(String value) {
            addCriterion("sql_scenario_report_id <", value, "sqlScenarioReportId");
            return (Criteria) this;
        }

        public Criteria andSqlScenarioReportIdLessThanOrEqualTo(String value) {
            addCriterion("sql_scenario_report_id <=", value, "sqlScenarioReportId");
            return (Criteria) this;
        }

        public Criteria andSqlScenarioReportIdLike(String value) {
            addCriterion("sql_scenario_report_id like", value, "sqlScenarioReportId");
            return (Criteria) this;
        }

        public Criteria andSqlScenarioReportIdNotLike(String value) {
            addCriterion("sql_scenario_report_id not like", value, "sqlScenarioReportId");
            return (Criteria) this;
        }

        public Criteria andSqlScenarioReportIdIn(List<String> values) {
            addCriterion("sql_scenario_report_id in", values, "sqlScenarioReportId");
            return (Criteria) this;
        }

        public Criteria andSqlScenarioReportIdNotIn(List<String> values) {
            addCriterion("sql_scenario_report_id not in", values, "sqlScenarioReportId");
            return (Criteria) this;
        }

        public Criteria andSqlScenarioReportIdBetween(String value1, String value2) {
            addCriterion("sql_scenario_report_id between", value1, value2, "sqlScenarioReportId");
            return (Criteria) this;
        }

        public Criteria andSqlScenarioReportIdNotBetween(String value1, String value2) {
            addCriterion("sql_scenario_report_id not between", value1, value2, "sqlScenarioReportId");
            return (Criteria) this;
        }

        public Criteria andSqlScenarioIdIsNull() {
            addCriterion("sql_scenario_id is null");
            return (Criteria) this;
        }

        public Criteria andSqlScenarioIdIsNotNull() {
            addCriterion("sql_scenario_id is not null");
            return (Criteria) this;
        }

        public Criteria andSqlScenarioIdEqualTo(String value) {
            addCriterion("sql_scenario_id =", value, "sqlScenarioId");
            return (Criteria) this;
        }

        public Criteria andSqlScenarioIdNotEqualTo(String value) {
            addCriterion("sql_scenario_id <>", value, "sqlScenarioId");
            return (Criteria) this;
        }

        public Criteria andSqlScenarioIdGreaterThan(String value) {
            addCriterion("sql_scenario_id >", value, "sqlScenarioId");
            return (Criteria) this;
        }

        public Criteria andSqlScenarioIdGreaterThanOrEqualTo(String value) {
            addCriterion("sql_scenario_id >=", value, "sqlScenarioId");
            return (Criteria) this;
        }

        public Criteria andSqlScenarioIdLessThan(String value) {
            addCriterion("sql_scenario_id <", value, "sqlScenarioId");
            return (Criteria) this;
        }

        public Criteria andSqlScenarioIdLessThanOrEqualTo(String value) {
            addCriterion("sql_scenario_id <=", value, "sqlScenarioId");
            return (Criteria) this;
        }

        public Criteria andSqlScenarioIdLike(String value) {
            addCriterion("sql_scenario_id like", value, "sqlScenarioId");
            return (Criteria) this;
        }

        public Criteria andSqlScenarioIdNotLike(String value) {
            addCriterion("sql_scenario_id not like", value, "sqlScenarioId");
            return (Criteria) this;
        }

        public Criteria andSqlScenarioIdIn(List<String> values) {
            addCriterion("sql_scenario_id in", values, "sqlScenarioId");
            return (Criteria) this;
        }

        public Criteria andSqlScenarioIdNotIn(List<String> values) {
            addCriterion("sql_scenario_id not in", values, "sqlScenarioId");
            return (Criteria) this;
        }

        public Criteria andSqlScenarioIdBetween(String value1, String value2) {
            addCriterion("sql_scenario_id between", value1, value2, "sqlScenarioId");
            return (Criteria) this;
        }

        public Criteria andSqlScenarioIdNotBetween(String value1, String value2) {
            addCriterion("sql_scenario_id not between", value1, value2, "sqlScenarioId");
            return (Criteria) this;
        }
    }

    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}