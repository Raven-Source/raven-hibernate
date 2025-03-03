package org.raven.hibernate.sql;//package org.raven.hibernate.sql;
//
//import org.hibernate.MappingException;
//import org.hibernate.dialect.Dialect;
//import org.hibernate.type.LiteralType;
//
//import java.util.Iterator;
//import java.util.LinkedHashMap;
//import java.util.Map;
//
//public class BatchInsert {
//
//    protected String tableName;
//    protected String comment;
//
//    protected Map<String, String> columns = new LinkedHashMap<>();
//    protected int rows = 1;
//
//    private Dialect dialect;
//
//    protected Dialect getDialect() {
//        return dialect;
//    }
//
//    public BatchInsert setComment(String comment) {
//        this.comment = comment;
//        return this;
//    }
//
//    public BatchInsert addColumn(String columnName) {
//        return addColumn(columnName, "?");
//    }
//
//    public BatchInsert addColumns(String[] columnNames) {
//        for (String columnName : columnNames) {
//            addColumn(columnName);
//        }
//        return this;
//    }
//
//    public BatchInsert addColumns(String[] columnNames, boolean[] insertable) {
//        for (int i = 0; i < columnNames.length; i++) {
//            if (insertable[i]) {
//                addColumn(columnNames[i]);
//            }
//        }
//        return this;
//    }
//
//    public BatchInsert addColumns(String[] columnNames, boolean[] insertable, String[] valueExpressions) {
//        for (int i = 0; i < columnNames.length; i++) {
//            if (insertable[i]) {
//                addColumn(columnNames[i], valueExpressions[i]);
//            }
//        }
//        return this;
//    }
//
//    public BatchInsert addColumn(String columnName, String valueExpression) {
//        columns.put(columnName, valueExpression);
//        return this;
//    }
//
//    public BatchInsert addColumn(String columnName, Object value, LiteralType type) throws Exception {
//        return addColumn(columnName, type.objectToSQLString(value, dialect));
//    }
//
//    public BatchInsert addIdentityColumn(String columnName) {
//        String value = dialect.getIdentityColumnSupport().getIdentityInsertString();
//        if (value != null) {
//            addColumn(columnName, value);
//        }
//        return this;
//    }
//
//    public BatchInsert setTableName(String tableName) {
//        this.tableName = tableName;
//        return this;
//    }
//
//    protected int getRows() {
//        return rows;
//    }
//
//    public BatchInsert setRows(int rows) {
//        this.rows = rows;
//        return this;
//    }
//
//    public String toStatementString() {
//        StringBuilder buf = new StringBuilder(columns.size() * 15 + tableName.length() + 10);
//        if (comment != null) {
//            buf.append("/* ").append(Dialect.escapeComment(comment)).append(" */ ");
//        }
//        buf.append("insert into ")
//                .append(tableName);
//        if (columns.size() == 0) {
//            if (dialect.supportsNoColumnsInsert()) {
//                buf.append(' ').append(dialect.getNoColumnsInsertString());
//            } else {
//                throw new MappingException(
//                        String.format(
//                                "The INSERT statement for table [%s] contains no column, and this is not supported by [%s]",
//                                tableName,
//                                dialect
//                        )
//                );
//            }
//        } else {
//            buf.append(" (");
//            Iterator<String> iter = columns.keySet().iterator();
//            while (iter.hasNext()) {
//                buf.append(iter.next());
//                if (iter.hasNext()) {
//                    buf.append(", ");
//                }
//            }
//            buf.append(") values ");
//            for (int r = 0; r < rows; r++) {
//                if (r == 0) {
//                    buf.append("(");
//                } else {
//                    buf.append(",(");
//                }
//                iter = columns.values().iterator();
//                while (iter.hasNext()) {
//                    buf.append(iter.next());
//                    if (iter.hasNext()) {
//                        buf.append(", ");
//                    }
//                }
//                buf.append(')');
//            }
//        }
//        return buf.toString();
//    }
//
//}
