package reader;

import annotation.CsvMapping;
import annotation.CsvRow;
import exceptions.CsvReadingException;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public class ObjectCsvReader<T> extends CsvReader {

    private final Class<T> TYPE_REFERENCE;
    private List<String> header;
    private final boolean firstRowIsHeader;

    /**
     * Constructor
     *
     * @param READER    InputStream object of text to read
     * @param delimiter delimiting character
     * @param enclosure text enclosure, could be null
     * @param escape    escape char, could be null
     */
    public ObjectCsvReader(InputStream READER, Character delimiter, Character enclosure, Character escape, boolean firstRowHeader, Class<T> tClass) throws CsvReadingException, IOException {
        super(READER, delimiter, enclosure, escape);
        this.firstRowIsHeader = firstRowHeader;
        if (firstRowHeader) {
            this.header = readRow();
        }
        this.TYPE_REFERENCE = tClass;
        validateType(TYPE_REFERENCE);
    }

    public T readNext() throws CsvReadingException, IOException {
        List<String> line = readRow();

        if (firstRowIsHeader && line.size() != header.size()) {
            throw new IllegalStateException("Header and line has different number of elements, error on line: " + rowsProcessed);
        }

        Map<String, String> fields = populateFields(line);
        return mapEntities(fields, TYPE_REFERENCE);
    }

    private Map<String, String> populateFields(List<String> line) {
        Map<String, String> fields = new HashMap<>();
        if (firstRowIsHeader) {
            for (int i = 0; i < line.size(); i++) {
                fields.put(header.get(i), line.get(i));
            }
        } else {
            for (int i = 0; i < line.size(); i++) {
                fields.put(String.valueOf(i), line.get(i));
            }
        }
        return fields;
    }

    private static <T> T mapEntities(Map<String, String> map, Class<T> tClass) {
        try {
            T obj = tClass.getDeclaredConstructor().newInstance();
            for (Field field : tClass.getDeclaredFields()) {
                if (field.isAnnotationPresent(CsvMapping.class)) {
                    field.setAccessible(true);
                    CsvMapping annotation = field.getAnnotation(CsvMapping.class);
                    if (!annotation.name().isBlank() && annotation.ordinal() != -1) {
                        throw new IllegalStateException("Name %s and ordinal %d can't be set at the same time".formatted(
                                annotation.name(), annotation.ordinal()
                        ));
                    }
                    String key = !annotation.name().isBlank() ? annotation.name() : String.valueOf(annotation.ordinal());
                    String value = map.get(key);

                    if (value == null) throw new IllegalArgumentException(
                            "Key %s - %d not found".formatted(annotation.name(), annotation.ordinal())
                    );

                    if (annotation.collection()) {
                        processCollection(value, field, annotation, obj);
                    } else {
                        field.set(obj, convert(value, field.getType()));
                    }
                }
            }
            return obj;
        } catch (InvocationTargetException |
                 InstantiationException |
                 IllegalAccessException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private static <T> void processCollection(String s, Field field, CsvMapping annotation, T obj) throws IllegalAccessException {
        String[] split = s.split(annotation.separator());
        Type genericType = field.getGenericType();
        if (genericType instanceof ParameterizedType parameterizedType) {
            Type[] typeArgs = parameterizedType.getActualTypeArguments();
            if (typeArgs.length == 1) {
                field.set(obj, mapToCollection(field, typeArgs, split));
            }
        } else {
            throw new IllegalArgumentException("Field %s is not a collection %s".formatted(field.getName(), field.getType()));
        }
    }

    private static Collection<Object> mapToCollection(Field field, Type[] typeArgs, String[] split) {
        Class<?> elementType = (Class<?>) typeArgs[0];
        Collection<Object> coll;
        if (List.class.isAssignableFrom(field.getType())) {
            coll = new ArrayList<>();
        } else if (Set.class.isAssignableFrom(field.getType())) {
            coll = new HashSet<>();
        } else {
            throw new IllegalArgumentException("Unsupported collection type: " + field.getType());
        }
        for (String part : split) {
            Object value = convert(part, elementType);
            coll.add(value);
        }
        return coll;
    }

    private static Object convert(String s, Class<?> type) {
        s = s.trim();
        if (type == String.class) return s;
        if (type == Integer.class || type == int.class) return Integer.parseInt(s);
        if (type == Boolean.class || type == boolean.class) return Boolean.parseBoolean(s);
        if (type == Long.class || type == long.class) return Long.parseLong(s);
        if (type == Double.class || type == double.class) return Double.parseDouble(s);
        if (type.isEnum()) {
            @SuppressWarnings("unchecked")
            Class<? extends Enum> enumClass = (Class<? extends Enum>) type;
            try {
                return Enum.valueOf(enumClass, s);
            } catch (IllegalArgumentException ex) {
                for (Object constant : enumClass.getEnumConstants()) {
                    if (((Enum<?>) constant).name().equalsIgnoreCase(s)) {
                        return constant;
                    }
                }
                throw ex;
            }
        }
        // fallback
        return s;
    }

    private void validateType(Class<T> clazz) {
        if (!clazz.isAnnotationPresent(CsvRow.class)) {
            throw new IllegalArgumentException("Class " + clazz.getName() + " must be annotated with @CsvRow");
        }

        for (Field field : clazz.getDeclaredFields()) {
            if (!field.isAnnotationPresent(CsvMapping.class)) {
                throw new IllegalArgumentException("Field " + field.getName() + " is missing @Mapping");
            }
        }
    }
}
