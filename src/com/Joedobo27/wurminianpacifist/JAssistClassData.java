package com.Joedobo27.wurminianpacifist;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;

@SuppressWarnings("unused")
class JAssistClassData {

    private CtClass ctClass;
    private ClassFile classFile;
    private ConstPool constPool;
    private String classPath;

    JAssistClassData(String _classPath, ClassPool classPool) throws NotFoundException {
        ctClass = classPool.get(_classPath);
        classFile = ctClass.getClassFile();
        constPool = classFile.getConstPool();
        classPath = _classPath;
    }

    CtClass getCtClass() {
        return ctClass;
    }

    ClassFile getClassFile() {
        return classFile;
    }

    ConstPool getConstPool() {
        return constPool;
    }

    String getClassPath() { return classPath;}

    void constantPoolPrint(String destinationPath) {
        Path printPath = Paths.get(destinationPath);
        PrintWriter out = null;
        try {
            out = new PrintWriter(printPath.toFile());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        constPool.print(out);
        //noinspection ConstantConditions
        out.close();
    }
}
