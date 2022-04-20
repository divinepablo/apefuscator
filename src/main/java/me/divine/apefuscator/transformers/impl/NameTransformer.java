package me.divine.apefuscator.transformers.impl;

import me.divine.apefuscator.Apefuscator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class NameTransformer extends me.divine.apefuscator.transformers.Transformer implements Opcodes {
    private final Logger LOGGER = LogManager.getLogger(NameTransformer.class);
    private Map<ClassNode, String[]> classMap = new HashMap<>();
    private Map<FieldNode, String[]> fieldMap = new HashMap<FieldNode, String[]>();
    private Map<MethodNode, String[]> methodMap = new HashMap<>();
    private Map<String, String> localVariableMap = new HashMap<>();
    char primaryChar = '\u0D9E';
    char secondaryChar = '\u0D9D';
    private int types = 0;
    public static int CLASSES = 1;
    public static int LOCALVARIABLES = 3;
    public static int METHODS = 6;
    public static int FIELDS = 12;

    public NameTransformer() {
        super("Name", "Renames classes, methods, and fields");
        types = LOCALVARIABLES + METHODS;
        if (types == CLASSES || types == LOCALVARIABLES + CLASSES || types == METHODS + CLASSES || types == LOCALVARIABLES + METHODS + CLASSES || types == FIELDS + CLASSES || types == LOCALVARIABLES + FIELDS + CLASSES || types == METHODS + FIELDS + CLASSES || types == LOCALVARIABLES + METHODS + FIELDS + CLASSES) {
            LOGGER.info("Classes aren't finished yet so they will be buggy");
        }
    }

    public NameTransformer(int types) {
        this();
        this.types = types;
        if (types == CLASSES || types == LOCALVARIABLES + CLASSES || types == METHODS + CLASSES || types == LOCALVARIABLES + METHODS + CLASSES) {
            LOGGER.info("Classes aren't finished yet so they will be buggy");
        }
    }

    public NameTransformer(char primaryChar, char secondaryChar) {
        this();
        this.primaryChar = primaryChar;
        this.secondaryChar = secondaryChar;
    }

    public NameTransformer(char primaryChar, char secondaryChar, int types) {
        this(types);
        this.primaryChar = primaryChar;
        this.secondaryChar = secondaryChar;
    }

    @Override
    public void transform(Apefuscator obfuscator) {
        obfuscator.getClasses().forEach(classNode -> {
            LOGGER.info("Transforming class: {}", classNode.name);
            ClassNode superClass = getSuperClass(obfuscator, classNode);

            if (!isMain(classNode)) {
                String newName = getName(classNode.name.length() + ThreadLocalRandom.current().nextInt(1, 10));
                renameClass(classNode, newName);
            }
            classNode.methods.forEach(methodNode -> {
                LOGGER.info("Found method: {}", methodNode.name);
                if (methodNode.localVariables != null) {
                    methodNode.localVariables.forEach(localVariableNode -> {
                        if (localVariableNode.name != null) {

                            String newName = getName(localVariableNode.name.length() + ThreadLocalRandom.current().nextInt(1, 10));

                            localVariableMap.put(localVariableNode.name, newName);
                            localVariableNode.name = newName;

                        }
                    });
                }

                if (methodNode.name.equals("<init>") || methodNode.name.equals("<clinit>") || methodNode.name.equals("main")) {
                    return;
                }

                if (superClass != null && superClass.methods.stream().anyMatch(superMethodNode -> superMethodNode.name.equals(methodNode.name))) {
                    String newName = superClass.name;
                    renameMethod(methodNode, newName);
                    return;
                }

                String newName = getName(methodNode.name.length() + ThreadLocalRandom.current().nextInt(1, 10));
                renameMethod(methodNode, newName);

            });
        });
//        LOGGER.info("Finished renaming. Fixing methods and fields.");
        LOGGER.info("Finished renaming. Fixing methods.");
        obfuscator.getClasses().forEach(this::fixMethods);
//        obfuscator.getClasses().forEach(classNode -> fixFields(obfuscator, classNode));
    }

    private void fixMethods(ClassNode classNode) {
        LOGGER.info("Fixing methods for class: {}", classNode.name);
        classNode.methods.forEach(methodNode -> {

            methodNode.instructions.forEach(instruction -> {
                if (instruction instanceof FieldInsnNode) {
                    FieldInsnNode fieldInsnNode = (FieldInsnNode) instruction;

                    classMap.values().forEach(mapping -> {
                        if (mapping.length == 2) {
                            if (mapping[0].equals(fieldInsnNode.owner)) {
                                LOGGER.info("Found field owner for obfuscated class with unobfuscated name: {}", fieldInsnNode.owner);
                                String newName = mapping[1];
                                LOGGER.info("Renaming field owner to: {}", newName);
                                fieldInsnNode.owner = newName;
                            }
                        }
                    });
                }
                if (instruction instanceof MethodInsnNode) {
                    MethodInsnNode methodInsnNode = (MethodInsnNode) instruction;
                    classMap.values().forEach(mapping -> {
                        if (mapping.length == 2) {
                            if (mapping[0].equals(methodInsnNode.owner)) {
                                LOGGER.info("Found method call for obfuscated class with unobfuscated name: {}", methodInsnNode.owner);
                                String newName = mapping[1];
                                LOGGER.info("Renaming method call to: {}", newName);
                                methodInsnNode.owner = newName;
                            }
                        }
                    });
                }
            });
        });
    }

    private void fixFields(Apefuscator obfuscator, ClassNode classNode) {
        LOGGER.info("Fixing fields for class: {}", classNode.name);
        classNode.fields.forEach(fieldNode -> {
            LOGGER.info("Found field {} with descriptor {} and attributes {}", fieldNode.name, fieldNode.desc, fieldNode.attrs);
        });
    }

    private void renameClass(ClassNode classNode, String newName) {
        classMap.put(classNode, new String[]{classNode.name, newName});
        LOGGER.info("Renamed class: {} to {}", classNode.name, newName);
        classNode.name = newName;
    }

    private void renameMethod(MethodNode methodNode, String newName) {
        methodMap.put(methodNode, new String[]{methodNode.name, newName});
        methodNode.name = newName;
    }

    private boolean isMain(ClassNode classNode) {
        return classNode.methods.stream().noneMatch(mn -> mn.name.equals("main"));
    }

    private ClassNode getSuperClass(Apefuscator obfuscator, ClassNode classNode) {
        if (classNode.superName != null && !classNode.superName.equals("java/lang/Object")) {
            return obfuscator.getClass(classNode.superName);
        }
        return null;
    }

    private String getName(int length) {
        StringBuilder name = new StringBuilder();
        for (int i = 0; i < length; i++) {
            name.append(ThreadLocalRandom.current().nextInt(1, 6) == 5 ? secondaryChar : primaryChar);
        }
        if (localVariableMap.containsValue(name.toString())) {
            return getName(name.length() + ThreadLocalRandom.current().nextInt(1, 5));
        } else {
            for (String[] mapping : methodMap.values()) {
                if (mapping[1] == name.toString()) {
                    return getName(name.length() + ThreadLocalRandom.current().nextInt(1, 5));
                }
            }
        }

        for (String[] mapping : classMap.values()) {
            if (Objects.equals(mapping[1], name.toString())) {
                return getName(name.length() + ThreadLocalRandom.current().nextInt(1, 5));
            }
        }
        return name.toString();
    }

    private void renameField(FieldNode fieldNode, String newName) {
        fieldMap.put(fieldNode, new String[]{fieldNode.name, newName});
        fieldNode.name = newName;
    }
}
