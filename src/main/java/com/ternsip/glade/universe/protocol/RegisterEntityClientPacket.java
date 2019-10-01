package com.ternsip.glade.universe.protocol;

import com.ternsip.glade.network.ClientPacket;
import com.ternsip.glade.network.Connection;
import com.ternsip.glade.universe.entities.base.EntityClient;
import com.ternsip.glade.universe.entities.base.EntityServer;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.reflections.ReflectionUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Slf4j
public class RegisterEntityClientPacket extends ClientPacket {

    private static final Map<Class<? extends EntityClient>, Set<Field>> CLASS_TO_SERIALIZABLE_FIELDS = new HashMap<>();

    private final Class<? extends EntityClient> clazz;
    private final Map<String, Object> initialValues;

    public RegisterEntityClientPacket(EntityServer entityServer, Connection connection) {
        EntityClient entity = entityServer.getEntityClient(connection);
        this.clazz = entity.getClass();
        this.initialValues = CLASS_TO_SERIALIZABLE_FIELDS
                .computeIfAbsent(entity.getClass(), k -> findAllSerializableFields(entity.getClass()))
                .stream()
                .collect(HashMap::new, (m, field) -> m.put(field.getName(), getFieldValueSilently(field, entity)), HashMap::putAll);
        this.initialValues.put("uuid", entityServer.getUuid());
    }

    @SneakyThrows
    private static Object getFieldValueSilently(Field field, Object object) {
        return field.get(object);
    }

    @SneakyThrows
    private static void setFieldValueSilently(Field field, Object object, Object value) {
        field.set(object, value);
    }

    private static Set<Field> findAllSerializableFields(Class<? extends EntityClient> clazz) {
        return ReflectionUtils.getAllFields(clazz).stream()
                .filter(field -> Serializable.class.isAssignableFrom(field.getType()) && !Modifier.isTransient(field.getModifiers()))
                .peek(field -> field.setAccessible(true))
                .collect(Collectors.toSet());
    }

    @Override
    public void apply(Connection connection) {
        EntityClient entity = constructNewEntity();
        if (getUniverseClient().getEntityClientRepository().isEntityExists(entity.getUuid())) {
            throw new IllegalArgumentException(String.format("Entity already exists %s", entity.getUuid()));
        }
        entity.register();
    }

    @SneakyThrows
    private EntityClient constructNewEntity() {
        EntityClient entity = getClazz().getDeclaredConstructor().newInstance();
        if (!getInitialValues().isEmpty()) {
            CLASS_TO_SERIALIZABLE_FIELDS
                    .computeIfAbsent(getClazz(), k -> findAllSerializableFields(getClazz()))
                    .forEach(field -> {
                        Object value = getInitialValues().get(field.getName());
                        setFieldValueSilently(field, entity, value);
                    });
        }
        return entity;
    }

}
