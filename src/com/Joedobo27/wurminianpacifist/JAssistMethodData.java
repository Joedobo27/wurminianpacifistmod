package com.Joedobo27.wurminianpacifist;

import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.bytecode.AccessFlag;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.CodeIterator;
import javassist.bytecode.MethodInfo;

import java.util.Arrays;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("unused")
class JAssistMethodData {

    private JAssistClassData parentClass;
    private MethodInfo methodInfo;
    private CodeAttribute codeAttribute;
    private CodeIterator codeIterator;
    private CtMethod ctMethod;
    private CtConstructor ctConstructor;
    private static Logger logger = Logger.getLogger(JAssistMethodData.class.getName());

    JAssistMethodData(JAssistClassData jAssistClassData, String descriptor, String methodName) throws NullPointerException {
        parentClass = jAssistClassData;
        methodInfo = Arrays.stream(jAssistClassData.getClassFile().getMethods().toArray())
                .map((Object value) -> (MethodInfo) value)
                .filter((MethodInfo value) -> Objects.equals(value.getDescriptor(), descriptor))
                .filter((MethodInfo value) -> Objects.equals(value.getName(), methodName))
                .findFirst()
                .orElse(null);
        if (methodInfo == null)
            throw new NullPointerException();
        codeAttribute = methodInfo.getCodeAttribute();
        codeIterator = codeAttribute.iterator();
        ctMethod = setCtMethod(descriptor, methodName);
        ctConstructor = setCtConstructor(descriptor, methodName);
    }

    private CtMethod setCtMethod(String descriptor, String methodName) {
        CtMethod toReturn;
        boolean isPrivate = (methodInfo.getAccessFlags() & (AccessFlag.PRIVATE)) != 0;
        if (isPrivate){
            Arrays.stream(parentClass.getCtClass().getDeclaredMethods())
                    .forEach(value -> logger.log(Level.INFO, "method "+ parentClass.getCtClass().getSimpleName() + " : "
                            + value.getName() + " " + value.getSignature()));
            toReturn = Arrays.stream(parentClass.getCtClass().getDeclaredMethods())
                    .filter((CtMethod value) -> Objects.equals(value.getSignature(), descriptor))
                    .filter((CtMethod value) -> Objects.equals(value.getName(), methodName))
                    .findFirst()
                    .orElse(null);
        }
        else {
            toReturn = Arrays.stream(parentClass.getCtClass().getMethods())
                    .filter((CtMethod value) -> Objects.equals(value.getSignature(), descriptor))
                    .filter((CtMethod value) -> Objects.equals(value.getName(), methodName))
                    .findFirst()
                    .orElse(null);
        }
        return toReturn;
    }

    private CtConstructor setCtConstructor(String descriptor, String methodName) {
        CtConstructor toReturn;
        boolean isPrivate = (methodInfo.getAccessFlags() & (AccessFlag.PRIVATE)) != 0;
        if (isPrivate){
            toReturn = Arrays.stream(parentClass.getCtClass().getDeclaredConstructors())
                    .filter((CtConstructor value) -> Objects.equals(value.getSignature(), descriptor))
                    .filter((CtConstructor value) -> Objects.equals(value.getName(), methodName))
                    .findFirst()
                    .orElse(null);
        }
        else {
            toReturn = Arrays.stream(parentClass.getCtClass().getConstructors())
                    .filter((CtConstructor value) -> Objects.equals(value.getSignature(), descriptor))
                    .filter((CtConstructor value) -> Objects.equals(value.getName(), methodName))
                    .findFirst()
                    .orElse(null);
        }
        return toReturn;
    }

    MethodInfo getMethodInfo() {
        return methodInfo;
    }

    CodeAttribute getCodeAttribute() {
        return codeAttribute;
    }

    CodeIterator getCodeIterator() {
        return codeIterator;
    }

    JAssistClassData getParentClass() {
        return parentClass;
    }

    CtMethod getCtMethod() {
        return ctMethod;
    }

    CtConstructor getCtConstructor() {return ctConstructor;}

    /**
     * This method is used to check if the JVM code matches a hash. It's primary purpose is to detected changes in WU
     * vanilla code. When Javassist is used to replace or insert there is nothing that informs a mod author
     * the code should be reviewed.
     *
     * @param ci type CodeIterator
     * @return a hash value.
     */
    static int byteCodeHashCheck(CodeIterator ci) {
        int length = ci.getCodeLength();
        int[] code = new int[length];
        for (int i=0;i<length;i++){
            code[i] = ci.byteAt(i);
        }
        return Arrays.hashCode(code);
    }

}
