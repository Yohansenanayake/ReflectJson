package com.yohan.reflectjson;

import com.yohan.reflectjson.annotations.JsonIgnore;
import com.yohan.reflectjson.annotations.JsonProperty;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JsonMapper {

    public String toJson(Object obj) {
        var fields = getFields(obj);
        var methods = getMethods(obj);

        var uniqueMembers = Stream.concat(fields.stream(), methods.stream())
                .filter(member -> !shouldIgnore(member))
                .collect(Collectors.toMap(
                        member -> getName(member),
                        member -> asJsonValue(getValue(member,obj)),
                        (a,b) -> a
                ));

        return uniqueMembers.entrySet().stream()
                .map(entry -> "\"%s\":%s" .formatted(entry.getKey(),entry.getValue()))
                .collect(Collectors.joining(",","{","}"));
    }

    private Object getValue(AccessibleObject member, Object obj) {
        try{
            member.setAccessible(true);
            return switch (member){
                case Field field -> field.get(obj);
                case Method method -> method.invoke(obj);
                default -> throw new IllegalStateException("Unexpected value: " + member);
            };
        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
            member.setAccessible(false);
        }
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

    private List<Field> getFields(Object obj) {
        var clazz = obj.getClass();
        var fields = clazz.getDeclaredFields();
        return Arrays.asList(fields);
    }

    private List<Method> getMethods(Object obj){
        var clazz = obj.getClass();
        var methods = clazz.getDeclaredMethods();

        return Arrays.stream(methods)
                .filter(method -> method.isAnnotationPresent(JsonProperty.class))
                .filter(this::isGetter)
                .toList();
    }

    private boolean isGetter(Method method){
        return method.getReturnType() != void.class && method.getParameterCount() == 0 ;
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

    private boolean shouldIgnore(AccessibleObject object) {
        return object.isAnnotationPresent(JsonIgnore.class);
    }

    private Object getName(Member member) {
        if (member instanceof AnnotatedElement ae &&  ae.isAnnotationPresent(JsonProperty.class)){
            var annotation = ae.getAnnotation(JsonProperty.class);
            return annotation.value();
        }
        return member.getName();
    }
}
