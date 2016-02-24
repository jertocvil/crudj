package net.jertocvil.easydb;

import net.jertocvil.easydb.annotatioins.HideField;
import net.jertocvil.easydb.annotatioins.IdName;
import net.jertocvil.easydb.annotatioins.TableName;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DBTable<T> {

    private static HashMap<Class, String> SQL_TYPES;

    @HideField // Hidden, will be added manually
    public int id;

    static {
        SQL_TYPES = new HashMap<Class, String>();
        SQL_TYPES.put(String.class, "TEXT");
        SQL_TYPES.put(int.class, "INTEGER");
        SQL_TYPES.put(float.class, "FLOAT");
    }

    public void inspect() {
        for (Field field : this.getClass().getFields()) {
            String log = String.format("Field %s type %s", field.getName(), field.getType().getName());
            System.out.println(log);
            System.out.println("Is string: " + field.getType().equals(String.class));
            System.out.println();
        }
    }

    public void createTable() {
// TODO refactor

        StringBuilder fieldsSql = new StringBuilder();

        for (Field field : this.getClass().getFields()) {

            if(SQL_TYPES.get(field.getType()) == null || DBTable.class.isAssignableFrom(field.getType())) continue;

            fieldsSql.append(String.format("%s %s, ", field.getName(), SQL_TYPES.get(field.getType())));
        }

        fieldsSql.delete(fieldsSql.length() - 2, fieldsSql.length());

        String query = String.format("CREATE TABLE IF NOT EXISTS %s ( %s )", this.getTableName(), fieldsSql.toString());

        System.out.println(query);

    }

    public void insert() {

        StringBuilder fieldNames = new StringBuilder(), fieldValues = new StringBuilder();

        for (Field field : this.getClass().getFields()) {

            if(getSqlType(field) == null) continue;

            fieldNames.append(field.getName());
            fieldNames.append(", ");

            fieldValues.append(getValueAsString(field));
            fieldValues.append(", ");

        }

        fieldNames.delete(fieldNames.length() - 2, fieldNames.length());
        fieldValues.delete(fieldValues.length() - 2, fieldValues.length());


        String query = String.format("INSERT INTO %s ( %s ) VALUES ( %s )", this.getTableName(), fieldNames, fieldValues);

        System.out.println(query);

    }

    public String getTableName() {

        TableName tableAnnotation = this.getClass().getAnnotation(TableName.class);

        if(tableAnnotation != null) {
            return tableAnnotation.value();
        } else {
            return this.getClass().getSimpleName() + 's';
        }

    }

    public String getIdName(boolean prefixTable) {

        IdName idAnnotation = this.getClass().getAnnotation(IdName.class);

        String idName = prefixTable ? getTableName() + '.' : "";

        if(idAnnotation != null) {
            idName += idAnnotation.value();
        } else {
            idName += "id";
        }

        return idName;

    }

    public T select(int id) {

        List<String> columnNames = getColumnNames(true, true, true); // Get column names without ids

//        columnNames.add(getIdName(true)); // add id
//
//        for (Field field : getClass().getFields()) {
//            if(DBTable.class.isAssignableFrom(field.getType())) {
//
//                try {
//                    DBTable dbTable = (DBTable) field.get(this);
//
//                    columnNames.addAll(dbTable.getColumnNames(true));
//                    columnNames.add(dbTable.getIdName(true));
//
////                    System.out.println(dbTable.getColumnNames(true));
//
//                } catch (IllegalAccessException e) {
//                    return null;
//                }
//            }
//        }

        String query = String.format("SELECT %s FROM %s WHERE %s.id = %d", String.join(", ", columnNames), getTableName(), getTableName(), id);
        System.out.println(query);
        return null;
    }

    private List<String> getColumnNames(boolean prefixTable, boolean followKeys, boolean addId) {

        ArrayList<String> columns = new ArrayList<String>();
        String tableName = getTableName();

        if(addId)
            columns.add(getIdName(prefixTable));

        for (Field field : this.getClass().getFields()) {

            if(getSqlType(field) == null || field.getAnnotation(HideField.class) != null) continue;

            if(prefixTable) {
                columns.add(String.format("%s.%s", tableName, field.getName()));
            } else {
                columns.add(String.format("%s", field.getName()));
            }

            if(followKeys && DBTable.class.isAssignableFrom(field.getType())) {

                try {
                    DBTable dbTable = (DBTable) field.get(this);
                    columns.addAll(dbTable.getColumnNames(true, true, true));

                } catch (IllegalAccessException e) {
                    return null;
                }
            }

        }

        return columns;
    }

    public PendingQuery<T> select() {
        return null;
    }

    private String getValueAsString(Field field) {

        try {
            if(field.getType().equals(String.class)) {
                return '"' + field.get(this).toString() + '"';
            } else {
                return field.get(this).toString();
            }
        } catch (IllegalAccessException e) {
            return null;
        }

    }

    private static String getSqlType(Field field) {
        try {

            // Omit if it is hidden
            HideField hide = field.getAnnotation(HideField.class);

            if(hide != null) return null;

            if(DBTable.class.isAssignableFrom(field.getType())) {
                // The object refers to another table, store the id
                return "INTEGER";
            } else {
                return SQL_TYPES.get(field.getType());
            }

        } catch (NullPointerException e) {
            return null; // The field probably isn't a DBTable
        }
    }

}
