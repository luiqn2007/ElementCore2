package com.elementtimes.elementcore.api.annotation.result;

import net.minecraftforge.forgespi.language.ModFileScanData;
import org.objectweb.asm.Type;

import java.lang.annotation.ElementType;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class ObjFindResult {

    private ElementType type;
    private String className;
    private String[] arguments;
    private String returnType;
    private String memberName;
    private Optional<Object> result;

    public ObjFindResult(ModFileScanData.AnnotationData data, Optional<Object> result) {
        this.type = data.getTargetType();
        Type type = data.getClassType();
        this.className = type.getClassName();
        Type[] types = type.getArgumentTypes();
        this.arguments = (types == null || types.length == 0)
                ? new String[0]
                : Arrays.stream(types).map(Type::getClassName).toArray(String[]::new);
        this.returnType = type.getReturnType().getClassName();
        this.memberName = data.getMemberName();
        this.result = result;
    }

    public boolean match(ModFileScanData.AnnotationData data) {
        if (type != data.getTargetType()) {
            return false;
        }
        Type type = data.getClassType();
        if (!Objects.equals(className, type.getClassName())) {
            return false;
        }
        Type[] argumentTypes = type.getArgumentTypes();
        int length = argumentTypes == null ? 0 : argumentTypes.length;
        if (length != arguments.length) {
            return false;
        }
        if (length > 0) {
            for (int i = 0; i < length; i++) {
                if (!Objects.equals(argumentTypes[i].getClassName(), arguments[i])) {
                    return false;
                }
            }
        }
        if (!Objects.equals(returnType, type.getReturnType().getClassName())) {
            return false;
        }
        return Objects.equals(memberName, data.getMemberName());
    }

    public Optional<Object> getResult() {
        return result;
    }
}
