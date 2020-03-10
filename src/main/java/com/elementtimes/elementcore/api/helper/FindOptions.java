package com.elementtimes.elementcore.api.helper;

import com.elementtimes.elementcore.api.ECModElements;
import com.elementtimes.elementcore.api.utils.ReflectUtils;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.objectweb.asm.Type;

import java.lang.annotation.ElementType;
import java.lang.reflect.*;
import java.util.*;
import java.util.function.Supplier;

public class FindOptions {

    public static final FindOptions DEFAULT = new FindOptions();

    ElementType[] allowedTypes = new ElementType[] {ElementType.FIELD, ElementType.TYPE, ElementType.CONSTRUCTOR};

    Class<?>[] returnTypes = new Class<?>[] {Object.class};

    List<ImmutablePair<Class<?>[], Supplier<Object[]>>> parameterObjects = new ArrayList<>();

    Object o = null;

    public FindOptions withTypes(ElementType... types) {
        allowedTypes = types;
        return this;
    }

    public FindOptions withReturns(Class<?>... types) {
        returnTypes = types.length == 0 ? new Class<?>[] {Object.class} : types;
        return this;
    }

    public FindOptions addParameterObjects(Supplier<Object[]> parameters, Class<?>... types) {
        parameterObjects.add(ImmutablePair.of(types, parameters));
        return this;
    }

    public FindOptions bindObject(Object bind) {
        o = bind;
        return this;
    }

    public Optional<Object> get(ECModElements elements, ModFileScanData.AnnotationData data) {
        ElementType targetType = data.getTargetType();
        if (ArrayUtils.contains(allowedTypes, targetType)) {
            switch (targetType) {
                case TYPE: return getFromClass(elements, data);
                case CONSTRUCTOR: return getFromConstructor(elements, data);
                case METHOD: return getFromMethod(elements, data);
                case FIELD: return getFromField(elements, data);
                default: return Optional.empty();
            }
        }
        return Optional.empty();
    }

    private Optional<Object> getFromClass(ECModElements elements, ModFileScanData.AnnotationData data) {
        String className = data.getClassType().getClassName();
        Optional<Class<?>> classOpt = ObjHelper.findClass(elements, className);
        if (classOpt.isPresent()) {
            Class<?> aClass = classOpt.get();
            if (checkType(aClass)) {
                for (ImmutablePair<Class<?>[], Supplier<Object[]>> pair : parameterObjects) {
                    Class<?>[] parameterTypes = pair.left;
                    Object[] parameterObjects = pair.right.get();
                    try {
                        Constructor<?> c = aClass.getConstructor(parameterTypes);
                        c.setAccessible(true);
                        return Optional.of(c.newInstance(parameterObjects));
                    } catch (Exception ignored) { }
                }
            }
        }
        return Optional.empty();
    }

    private Optional<Object> getFromConstructor(ECModElements elements, ModFileScanData.AnnotationData data) {
        String className = data.getClassType().getClassName();
        Optional<Class<?>> classOpt = ObjHelper.findClass(elements, className);
        if (classOpt.isPresent()) {
            Class<?> aClass = classOpt.get();
            if (checkType(aClass)) {
                Class<?>[] parameterTypes = Arrays.stream(data.getClassType().getArgumentTypes())
                        .map(Type::getClassName)
                        .map(cn -> {
                            try { return Thread.currentThread().getContextClassLoader().loadClass(cn); }
                            catch (ClassNotFoundException e) { e.printStackTrace();return null; }
                        })
                        .toArray(Class[]::new);
                try {
                    Constructor<?> c = aClass.getConstructor(parameterTypes);
                    c.setAccessible(true);
                    return Optional.of(c.newInstance(parameterObjects));
                } catch (Exception ignored) { }
            }
        }
        return Optional.empty();
    }

    private Optional<Object> getFromMethod(ECModElements elements, ModFileScanData.AnnotationData data) {
        String memberName = data.getMemberName();
        memberName = memberName.substring(memberName.indexOf(")") + 1);
        String className = data.getClassType().getClassName();
        Optional<Class<?>> classOpt = ObjHelper.findClass(elements, className);
        if (classOpt.isPresent()) {
            Class<?> aClass = classOpt.get();
            for (ImmutablePair<Class<?>[], Supplier<Object[]>> pair : parameterObjects) {
                Class<?>[] parameterTypes = pair.left;
                Object[] parameterObjects = pair.right.get();
                try {
                    Method m;
                    try {
                        m = aClass.getDeclaredMethod(memberName, parameterTypes);
                    } catch (Exception e) {
                        m = aClass.getMethod(memberName, parameterTypes);
                    }
                    if (checkType(m.getReturnType())) {
                        m.setAccessible(true);
                        return Optional.of(m.invoke(o, parameterObjects));
                    }
                } catch (Exception ignored) { }
            }
        }
        return Optional.empty();
    }

    private Optional<Object> getFromField(ECModElements elements, ModFileScanData.AnnotationData data) {
        String memberName = data.getMemberName();
        String className = data.getClassType().getClassName();
        Optional<Class<?>> classOpt = ObjHelper.findClass(elements, className);
        if (classOpt.isPresent()) {
            Class<?> aClass = classOpt.get();
            Optional<Object> o = ReflectUtils.get(aClass, memberName, this.o, returnTypes.length == 1 ? returnTypes[0] : Object.class, elements);
            return o.filter(obj -> checkType(obj.getClass()));
        }
        return Optional.empty();
    }

    private boolean checkType(Class<?> type) {
        for (Class<?> returnType : returnTypes) {
            if (returnType.isAssignableFrom(type)) {
                return true;
            }
        }
        return false;
    }
}
