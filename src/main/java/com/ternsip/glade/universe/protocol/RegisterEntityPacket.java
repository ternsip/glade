package com.ternsip.glade.universe.protocol;

import com.ternsip.glade.common.logic.Utils;
import com.ternsip.glade.network.Connection;
import com.ternsip.glade.network.NetworkSide;
import com.ternsip.glade.network.Packet;
import com.ternsip.glade.universe.entities.base.Entity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.reflections.ReflectionUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Getter
public class RegisterEntityPacket extends Packet {

    private static final Map<Class<? extends Entity>, Set<Field>> CLASS_TO_SERIALIZABLE_FIELDS = new HashMap<>();
    private final Class<? extends Entity> clazz;
    private final UUID uuid;
    private final Map<String, Object> initialValues;

    public RegisterEntityPacket(Entity entity) {
        this.clazz = entity.getClass();
        this.uuid = entity.getUuid();
        this.initialValues = CLASS_TO_SERIALIZABLE_FIELDS
                .computeIfAbsent(entity.getClass(), k -> findAllSerializableFields(entity.getClass()))
                .stream()
                .collect(HashMap::new, (m, field) -> m.put(field.getName(), Utils.cloneThroughJson(getFieldValueSilently(field, entity))), HashMap::putAll);
        this.initialValues.put("networkSide", NetworkSide.CLIENT);
    }

    @SneakyThrows
    private static Object getFieldValueSilently(Field field, Object object) {
        return field.get(object);
    }

    @SneakyThrows
    private static void setFieldValueSilently(Field field, Object object, Object value) {
        field.set(object, value);
    }

    private static Set<Field> findAllSerializableFields(Class<? extends Entity> clazz) {
        return ReflectionUtils.getAllFields(clazz).stream()
                .filter(field -> Serializable.class.isAssignableFrom(field.getType()) && !Modifier.isTransient(field.getModifiers()))
                .peek(field -> field.setAccessible(true))
                .collect(Collectors.toSet());
    }

    @Override
    public void apply(Connection connection) {
        constructNewEntity().register();
    }

    @SneakyThrows
    private Entity constructNewEntity() {
        Entity entity = clazz.getDeclaredConstructor().newInstance();
        if (!getInitialValues().isEmpty()) {
            CLASS_TO_SERIALIZABLE_FIELDS
                    .computeIfAbsent(clazz, k -> findAllSerializableFields(clazz))
                    .forEach(field -> {
                        Object value = getInitialValues().get(field.getName());
                        setFieldValueSilently(field, entity, value);
                    });
        }
        return entity;
    }

}
