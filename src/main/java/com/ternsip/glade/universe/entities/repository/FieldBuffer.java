package com.ternsip.glade.universe.entities.repository;

import com.sun.xml.internal.ws.util.StringUtils;
import com.ternsip.glade.common.logic.Utils;
import com.ternsip.glade.network.ClientSide;
import com.ternsip.glade.network.ServerSide;
import com.ternsip.glade.universe.entities.base.Entity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.reflections.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Getter
public final class FieldBuffer {

    private final Map<Class, Set<Methods>> CLASS_TO_METHODS_SERVER_SIDE = new HashMap<>();
    private final Map<Class, Set<Methods>> CLASS_TO_METHODS_CLIENT_SIDE = new HashMap<>();

    private final boolean onServer;

    public final <T extends Entity> FieldValues pullChanges(T entity) {
        Set<Methods> methods = isOnServer() ? getServerMethods(entity.getClass()) : getClientMethods(entity.getClass());
        FieldValues newFieldValues = new FieldValues();
        methods.forEach(m -> {
            Object getterResult = Utils.cloneThroughJson(invokeSilently(m.getGetter(), entity));
            newFieldValues.put(m.getFieldName(), getterResult);
        });
        return newFieldValues;
    }

    public final <T extends Entity> void applyChanges(FieldValues fieldValues, T entity) {
        Set<Methods> methods = isOnServer() ? getClientMethods(entity.getClass()) : getServerMethods(entity.getClass());
        methods.forEach(m -> {
            if (fieldValues.containsKey(m.getFieldName())) {
                Object value = fieldValues.get(m.getFieldName());
                invokeSilently(m.getSetter(), entity, value);
            }
        });
    }

    private Set<Methods> getClientMethods(Class<? extends Entity> clazz) {
        return CLASS_TO_METHODS_CLIENT_SIDE.computeIfAbsent(clazz, key -> retrieveMethods(clazz, ClientSide.class));
    }

    private Set<Methods> getServerMethods(Class<? extends Entity> clazz) {
        return CLASS_TO_METHODS_SERVER_SIDE.computeIfAbsent(clazz, key -> retrieveMethods(clazz, ServerSide.class));
    }

    private Set<Methods> retrieveMethods(Class<? extends Entity> clazz, Class<? extends Annotation> annotationClass) {
        Map<String, Method> nameToMethod = ReflectionUtils.getAllMethods(clazz).stream()
                .collect(Collectors.toMap(Method::getName, e -> e, (a1, a2) -> a1.getDeclaringClass().isAssignableFrom(a2.getDeclaringClass()) ? a2 : a1));
        Set<Method> setters = nameToMethod.values().stream()
                .filter(method -> checkIfMethodNetworking(method, clazz, annotationClass))
                .collect(Collectors.toSet());
        return setters.stream()
                .map(setter -> new Methods(nameToMethod, setter))
                .collect(Collectors.toSet());
    }

    private boolean checkIfMethodNetworking(Method method, Class<? extends Entity> clazz, Class<? extends Annotation> annotationClass) {
        if (!method.getName().startsWith("set")) {
            return false;
        }
        if (method.isAnnotationPresent(annotationClass)) {
            return true;
        }
        String fieldName = StringUtils.decapitalize(method.getName().replaceAll("^set", ""));
        try {
            Field field = Utils.findFieldInHierarchy(fieldName, clazz);
            return field.isAnnotationPresent(annotationClass);
        } catch (Exception e) {
            return false;
        }
    }

    @SneakyThrows
    private Object invokeSilently(Method method, Object object, Object... args) {
        return method.invoke(object, args);
    }

    @RequiredArgsConstructor
    @Getter
    private static class Methods {

        private final String fieldName;
        private final Method setter;
        private final Method getter;

        private Methods(Map<String, Method> nameToMethod, Method setter) {
            String setterName = setter.getName();
            String getterNameGet = setterName.replaceAll("^set", "get");
            String getterNameIs = setterName.replaceAll("^set", "is");
            String getterName = nameToMethod.containsKey(getterNameGet) ? getterNameGet : getterNameIs;
            String fieldName = StringUtils.decapitalize(setterName.replaceAll("^set", ""));
            Method getter = nameToMethod.get(getterName);
            if (getter == null) {
                throw new IllegalArgumentException(String.format("Getter does not exists for setter %s!", setterName));
            }
            if (setter.getParameterCount() != 1) {
                throw new IllegalArgumentException(String.format("Wrong number of parameters %s for setter %s", Arrays.toString(setter.getParameterTypes()), setterName));
            }
            if (getter.getParameterCount() != 0) {
                throw new IllegalArgumentException(String.format("Wrong number of parameters %s for getter %s", Arrays.toString(getter.getParameterTypes()), getterName));
            }
            if (getter.getReturnType() != setter.getParameterTypes()[0]) {
                String errMsg = String.format(
                        "Getter %s and setter %s have different types - %s and %s",
                        getterName, setterName, getter.getReturnType(), setter.getParameterTypes()[0]
                );
                throw new IllegalArgumentException(errMsg);
            }
            setter.setAccessible(true);
            getter.setAccessible(true);

            this.fieldName = fieldName;
            this.setter = setter;
            this.getter = getter;
        }

    }

}
