package net.jertocvil.easydb;

import net.jertocvil.easydb.annotatioins.TableName;

import java.lang.reflect.Field;
import java.util.HashMap;

public class DBTable<T> {

    private static HashMap<Class, String> SQL_TYPES;

    static {
        SQL_TYPES = new HashMap<Class, String>();
        SQL_TYPES.put(String.class, "TEXT");
        SQL_TYPES.put(int.class, "INTEGER");
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


        StringBuilder fieldsSql = new StringBuilder();

        for (Field field : this.getClass().getFields()) {
            fieldsSql.append(String.format("%s %s, ", field.getName(), SQL_TYPES.get(field.getType())));
        }

        fieldsSql.delete(fieldsSql.length() - 2, fieldsSql.length());

        String query = String.format("CREATE TABLE IF NOT EXISTS %s ( %s )", this.getTableName(), fieldsSql.toString());

        System.out.println(query);

    }

    public void insert() {


        StringBuilder fieldNames = new StringBuilder(), fieldValues = new StringBuilder();

        for (Field field : this.getClass().getFields()) {

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

    public PendingQuery<T> select(int id) {
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




}
