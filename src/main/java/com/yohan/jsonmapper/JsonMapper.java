package com.yohan.jsonmapper;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.Collectors;

public class JsonMapper {

    public String toJson(Object obj) {
        var clazz = obj.getClass();
        var fields = clazz.getDeclaredFields();

        return Arrays.stream(fields)
                .map(field -> {
                        var name = field.getName();
                        var value = asJsonValue(getFieldValue(field, obj));
                        return "\"%s\":%s" .formatted(name,value);

                }).collect(Collectors.joining(",","{","}"));
    }

    private Object getFieldValue(Field field, Object obj){
        try {
            field.setAccessible(true);
            return field.get(obj);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }finally {
            field.setAccessible(false);
        }
    }

    private String asJsonValue(Object value) {
        if(isPrimitive(value)){
            return String.valueOf(value);
        }
        //characters and string have quotes
        return '"'+ String.valueOf(value)+'"';
    }

    private boolean isPrimitive(Object value) {
        return value instanceof Boolean || value instanceof Number ;
    }
}
