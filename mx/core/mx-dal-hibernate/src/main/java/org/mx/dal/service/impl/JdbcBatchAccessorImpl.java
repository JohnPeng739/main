package org.mx.dal.service.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mx.StringUtils;
import org.mx.dal.entity.Base;
import org.mx.dal.error.UserInterfaceDalErrorException;
import org.mx.dal.service.JdbcBatchAccessor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.ReflectionUtils;

import javax.persistence.Column;
import javax.persistence.Table;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 描述： 基于Hibernate JdbcTemplate的批量数据处理实现类
 *
 * @author john peng
 * Date time 2019/5/25 7:41 PM
 */
public class JdbcBatchAccessorImpl implements JdbcBatchAccessor {
    private static final Log logger = LogFactory.getLog(JdbcBatchAccessorImpl.class);

    private JdbcTemplate jdbcTemplate;

    public JdbcBatchAccessorImpl(JdbcTemplate jdbcTemplate) {
        super();
        this.jdbcTemplate = jdbcTemplate;
    }

    private String getTableName(Class<?> entityClass) {
        Table table = entityClass.getAnnotation(Table.class);
        String name = null;
        if (table != null) {
            name = table.name();
        }
        if (StringUtils.isBlank(name)) {
            return entityClass.getName();
        }
        return name;
    }

    private Object getFieldValue(Object entity, String fieldName) {
        Field field = ReflectionUtils.findField(entity.getClass(), fieldName);
        field.setAccessible(true);
        return ReflectionUtils.getField(field, entity);
    }

    private List<String> prepareFieldName(Class<?> entityClass, List<String> fields) {
        List<String> tarFields = new ArrayList<>();
        fields.forEach(fieldName -> {
            Field field = ReflectionUtils.findField(entityClass, fieldName);
            Column column = field.getAnnotation(Column.class);
            if (column != null && !StringUtils.isBlank(column.name())) {
                tarFields.add(column.name());
                return;
            }
            tarFields.add(fieldName);
        });
        return tarFields;
    }

    private <T extends Base> int batchExecute(String sql, List<T> entities, List<String> fields) throws SQLException {
        int[] result = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                T entity = entities.get(i);
                for (int index = 0; index < fields.size(); index++) {
                    ps.setObject(index + 1, getFieldValue(entity, fields.get(index)));
                }
            }

            @Override
            public int getBatchSize() {
                return entities.size();
            }
        });
        int total = 0;
        for (int r : result) {
            total += r;
        }
        return total;
    }

    /**
     * {@inheritDoc}
     *
     * @see JdbcBatchAccessor#batchInsert(List, List)
     */
    @Override
    public <T extends Base> int batchInsert(final List<String> insertFields, List<T> entities) {
        if (insertFields == null || insertFields.isEmpty() || entities == null || entities.isEmpty()) {
            if (logger.isWarnEnabled()) {
                logger.warn("The insert fields or the entities is empty.");
            }
            return 0;
        }
        if (entities.size() > 3000) {
            if (logger.isWarnEnabled()) {
                logger.warn("The entities is bigger than 3000, it would lead to high transaction impact on database performance.");
            }
        }
        // 组装INSERT SQL
        Class<?> entityClass = entities.get(0).getClass();
        String table = getTableName(entityClass);
        List<String> tarInsertFields = prepareFieldName(entityClass, insertFields);
        String fields = StringUtils.merge(tarInsertFields, ",");
        String values = StringUtils.repeat(tarInsertFields.size() * 2 - 1, "?,");
        String insertSql = String.format("INSERT INTO %s(%s) VALUES(%s)", table, fields, values);
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Batch insert: %s.", insertSql));
        }
        Connection connection = null;
        try {
            connection = jdbcTemplate.getDataSource().getConnection();
            connection.setAutoCommit(false);
            int total = batchExecute(insertSql, entities, insertFields);
            connection.commit();
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Batch insert successfully, sql: %s, entities: %d, result: %d.", insertSql, entities.size(), total));
            }
            return total;
        } catch (SQLException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("Batch insert fail, sql: %s, entities: %d.", insertSql, entities.size()), ex);
            }
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException exRollback) {
                if (logger.isErrorEnabled()) {
                    logger.error("Batch insert transaction rollback fail.", exRollback);
                }
            }
            throw new UserInterfaceDalErrorException(UserInterfaceDalErrorException.DalErrors.DB_BATCH_INSERT_FAIL);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see JdbcBatchAccessor#batchUpdate(List, List, List)
     */
    @Override
    public <T extends Base> int batchUpdate(List<String> primaryKeyFields, List<String> updateFields, List<T> entities) {
        if (primaryKeyFields == null || primaryKeyFields.isEmpty() || updateFields == null || updateFields.isEmpty() || entities == null || entities.isEmpty()) {
            if (logger.isWarnEnabled()) {
                logger.warn("The primary key fields or the update fields or the entities is empty.");
            }
            return 0;
        }
        if (entities.size() > 3000) {
            if (logger.isWarnEnabled()) {
                logger.warn("The entities is bigger than 3000, it would lead to high transaction impact on database performance.");
            }
        }
        // 组装UPDATE SQL
        Class<?> entityClass = entities.get(0).getClass();
        String table = getTableName(entityClass);
        List<String> tarUpdateFields = prepareFieldName(entityClass, updateFields);
        List<String> tarPrimaryKeyFields = prepareFieldName(entityClass, primaryKeyFields);
        StringBuffer sbFields = new StringBuffer();
        updateFields.forEach(field -> {
            sbFields.append(field);
            sbFields.append("=?,");
        });
        String fields = sbFields.substring(0, sbFields.length() - 1);
        StringBuffer sbPrimary = new StringBuffer();
        tarPrimaryKeyFields.forEach(key -> {
            sbPrimary.append(key);
            sbPrimary.append("=?,");
        });
        String primary = sbPrimary.substring(0, sbPrimary.length() - 1);
        String updateSql = String.format("UPDATE %s SET %s WHERE %s", table, fields, primary);
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Batch update: %s.", updateSql));
        }
        // 使用原有的字段名获取数据
        List<String> mergFields = new ArrayList<>();
        mergFields.addAll(updateFields);
        mergFields.addAll(primaryKeyFields);
        Connection connection = null;
        try {
            connection = jdbcTemplate.getDataSource().getConnection();
            connection.setAutoCommit(false);
            int total = batchExecute(updateSql, entities, mergFields);
            connection.commit();
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Batch update successfully, sql: %s, entities: %d, total: %d.", updateSql, entities.size(), total));
            }
            return total;
        } catch (SQLException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("Batch update fail, sql: %s, entities: %d.", updateSql, entities.size()), ex);
            }
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException exRollback) {
                if (logger.isErrorEnabled()) {
                    logger.error("Batch update transaction rollback fail.", exRollback);
                }
            }
            throw new UserInterfaceDalErrorException(UserInterfaceDalErrorException.DalErrors.DB_BATCH_UPDATE_FAIL);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @see JdbcBatchAccessor#batchDelete(List, List)
     */
    @Override
    public <T extends Base> int batchDelete(List<String> primaryKeyFields, List<T> entities) {
        if (primaryKeyFields == null || primaryKeyFields.isEmpty() || entities == null || entities.isEmpty()) {
            if (logger.isWarnEnabled()) {
                logger.warn("The primary key fields or the entities is empty.");
            }
            return 0;
        }
        if (entities.size() > 3000) {
            if (logger.isWarnEnabled()) {
                logger.warn("The entities is bigger than 3000, it would lead to high transaction impact on database performance.");
            }
        }
        // 组装DELETE SQL
        Class<?> entityClass = entities.get(0).getClass();
        String table = getTableName(entityClass);
        List<String> tarPrimaryKeyFields = prepareFieldName(entityClass, primaryKeyFields);
        StringBuffer sb = new StringBuffer();
        tarPrimaryKeyFields.forEach(key -> {
            sb.append(key);
            sb.append("=?,");
        });
        String primary = sb.substring(0, sb.length() - 1);
        String deleteSql = String.format("DELETE FROM %s WHERE %s", table, primary);
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Batch delete: %s.", deleteSql));
        }
        Connection connection = null;
        try {
            connection = jdbcTemplate.getDataSource().getConnection();
            connection.setAutoCommit(false);
            int total = batchExecute(deleteSql, entities, primaryKeyFields);
            connection.commit();
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Batch delete successfully, sql: %s, entities: %d, total: %d.", deleteSql, entities.size(), total));
            }
            return total;
        } catch (SQLException ex) {
            if (logger.isErrorEnabled()) {
                logger.error(String.format("Batch delete fail, sql: %s, entities: %d.", deleteSql, entities.size()), ex);
            }
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException exRollback) {
                if (logger.isErrorEnabled()) {
                    logger.error("Batch delete transaction rollback fail.", exRollback);
                }
            }
            throw new UserInterfaceDalErrorException(UserInterfaceDalErrorException.DalErrors.DB_BATCH_DELETE_FAIL);
        }
    }
}
