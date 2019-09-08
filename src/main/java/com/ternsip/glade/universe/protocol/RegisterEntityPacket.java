package com.ternsip.glade.universe.protocol;

import com.ternsip.glade.network.Connection;
import com.ternsip.glade.network.Packet;
import com.ternsip.glade.universe.entities.base.Entity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.reflections.ReflectionUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Getter
public class RegisterEntityPacket implements Packet {

    private static final Field ENTITY_UUID_FIELD = getEntityUUIDField();
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
                .collect(Collectors.toMap(Field::getName, field -> getFieldValueSilently(field, entity)));
    }

    @Override
    public void apply(Connection connection) {
        getUniverse().getEntityClientRepository().register(constructNewEntity());
    }

    @SneakyThrows
    private Entity constructNewEntity() {
        Entity entity = clazz.getDeclaredConstructor().newInstance();
        ENTITY_UUID_FIELD.set(entity, getUuid());
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

    @SneakyThrows
    private static Field getEntityUUIDField() {
        Field uuidField = Entity.class.getDeclaredField("uuid");
        uuidField.setAccessible(true);
        return uuidField;
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
                .filter(field -> Serializable.class.isAssignableFrom(field.getType()))
                .peek(field -> field.setAccessible(true))
                .collect(Collectors.toSet());
    }

}
