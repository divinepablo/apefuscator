package me.divine.apefuscator.transformers.impl;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import me.divine.apefuscator.Apefuscator;
import me.divine.apefuscator.utils.ASMUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
// TODO: Make this a lot cleaner
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
    public static int ALL = CLASSES + LOCALVARIABLES + METHODS + FIELDS;
    private boolean save = false;

    public NameTransformer() {
        super("Name", "Renames classes, methods, and fields");
        types = LOCALVARIABLES + METHODS;
        if (isClasses()) {
            LOGGER.warn("Classes aren't finished yet so they will be buggy");
        }
    }

    public NameTransformer(int types) {
        this();
        this.types = types;
        if (isClasses()) {
            LOGGER.warn("Classes aren't finished yet so they will be buggy");
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

    public NameTransformer(boolean save) {
        this();
        this.save = save;
    }

    public NameTransformer(boolean save, int types) {
        this(types);
        this.save = save;
    }

    public NameTransformer(char primaryChar, char secondaryChar, boolean save) {
        this(primaryChar, secondaryChar);
        this.save = save;
    }

    public NameTransformer(char primaryChar, char secondaryChar, int types, boolean save) {
        this(primaryChar, secondaryChar, types);
        this.save = save;
    }

    @Override
    public void transform(Apefuscator obfuscator) {
        obfuscator.getClassesOriginal().forEach(classNode -> {
            ClassNode superClass = getSuperClass(obfuscator, classNode);

            if (!isMain(classNode) && isClasses()) {
                String newName = getName(classNode.name.length() + ThreadLocalRandom.current().nextInt(1, 10));
                renameClass(classNode, newName);
            }
            classNode.methods.forEach(methodNode -> {
                if (isLocalVariables())
                    if (methodNode.localVariables != null) {
                        methodNode.localVariables.forEach(localVariableNode -> {

                            String newName = getName(localVariableNode.name.length() + ThreadLocalRandom.current().nextInt(1, 10));
                            renameLocalVariable(localVariableNode, newName);
                        });
                    }

                if (isMethods()) {
                    if (methodNode.name.equals("<init>") || methodNode.name.equals("<clinit>") || methodNode.name.equals("main")) {
                        return;
                    }
                    if (isMethodFromSuperclass(superClass, methodNode)) {
                        for (MethodNode superMethodNode : superClass.methods) {
                            if (superMethodNode.name.equals(methodNode.name)) {
                                String newName = superMethodNode.name;
                                renameMethod(methodNode, newName);
                                return;
                            }
                        }
                    } else {
                        String newName = getName(methodNode.name.length() + ThreadLocalRandom.current().nextInt(1, 10));
                        renameMethod(methodNode, newName);
                    }
                }

            });
            if (isFields()) {
                classNode.fields.forEach(fieldNode -> {
                    String newName = getName(fieldNode.name.length() + ThreadLocalRandom.current().nextInt(1, 10));
                    renameField(fieldNode, newName);
                });
            }
        });
        LOGGER.info("Renaming done");
        if (save) {
            saveMappings();
        }

//        LOGGER.info("Finished renaming. Fixing methods and fields.");
        if (isClasses()) {
            LOGGER.info("Fixing methods.");
            obfuscator.getClassesOriginal().forEach(this::fixMethods);
            LOGGER.info("Fixing fields.");
            obfuscator.getClassesOriginal().forEach(this::fixFields);
        }
    }

    private void renameLocalVariable(LocalVariableNode localVariableNode, String newName) {
        localVariableMap.put(localVariableNode.name, newName);
        localVariableNode.name = newName;
    }

    private boolean isMethodFromSuperclass(ClassNode superClass, MethodNode methodNode) {
        return superClass != null && superClass.methods.stream().anyMatch(superMethodNode -> superMethodNode.name.equals(methodNode.name));
    }

    private void saveMappings() {
        LOGGER.info("Saving mappings to file");
        try {
            File file = new File("mappings.txt");
            File anotherFile = new File("mappings2.txt");
            File jsonFile = new File("mappings.json");
            if (!file.exists()) {
                file.createNewFile();
            }
            if (!anotherFile.exists()) {
                anotherFile.createNewFile();
            }
            if (!jsonFile.exists()) {
                jsonFile.createNewFile();
            }
            FileWriter fileWriter = new FileWriter(file);
            FileWriter fileWriter2 = new FileWriter(anotherFile);
            FileWriter fileWriter3 = new FileWriter(jsonFile);

            String s1 = "";
            String s2 = "";
            JsonObject jsonObject = new JsonObject();
            JsonArray localVariables = new JsonArray();
            s1 += "Local variables: \n";
            s2 += "Local variables: \n";
            for (Map.Entry<String, String> mapping : localVariableMap.entrySet()) {
                s1 += mapping.getKey() + " -> " + mapping.getValue() + "\n";
                s2 += mapping.getValue() + ":" + mapping.getKey() + "\n";
                JsonObject json = new JsonObject();
                json.addProperty("before", mapping.getKey());
                json.addProperty("after", mapping.getValue());
                localVariables.add(json);
            }
            jsonObject.add("localVariables", localVariables);
            JsonArray fields = new JsonArray();
            s1 += "Fields: \n";
            s2 += "Fields: \n";
            for (String[] mapping : fieldMap.values()) {
                s1 += mapping[0] + " -> " + mapping[1] + "\n";
                s2 += mapping[1] + ":" + mapping[0] + "\n";
                JsonObject json = new JsonObject();
                json.addProperty("before", mapping[0]);
                json.addProperty("after", mapping[1]);
                fields.add(json);
            }
            jsonObject.add("fields", fields);
            JsonArray methods = new JsonArray();
            s1 += "Methods: \n";
            s2 += "Methods: \n";
            for (String[] mapping : methodMap.values()) {
                s1 += mapping[0] + " -> " + mapping[1] + "\n";
                s2 += mapping[1] + ":" + mapping[0] + "\n";
                JsonObject json = new JsonObject();
                json.addProperty("before", mapping[0]);
                json.addProperty("after", mapping[1]);
                methods.add(json);
            }
            jsonObject.add("methods", methods);
            JsonArray classes = new JsonArray();
            s1 += "Classes: \n";
            s2 += "Classes: \n";
            for (String[] mapping : classMap.values()) {
                s1 += mapping[0] + " -> " + mapping[1] + "\n";
                s2 += mapping[1] + ":" + mapping[0] + "\n";
                JsonObject json = new JsonObject();
                json.addProperty("before", mapping[0]);
                json.addProperty("after", mapping[1]);
                classes.add(json);
            }
            jsonObject.add("classes", classes);
            fileWriter.write(s1);
            fileWriter2.write(s2);
            fileWriter3.write(new GsonBuilder().setPrettyPrinting().create().toJson(jsonObject));
            fileWriter.close();
            fileWriter2.close();
            fileWriter3.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        LOGGER.info("Finished saving the mapping");
    }

    private boolean isClasses() {
        return types == CLASSES || types == LOCALVARIABLES + CLASSES || types == METHODS + CLASSES || types == LOCALVARIABLES + METHODS + CLASSES || types == ALL || types == CLASSES + FIELDS;
    }

    private boolean isLocalVariables() {
        return types == LOCALVARIABLES || types == LOCALVARIABLES + CLASSES || types == LOCALVARIABLES + METHODS || types == LOCALVARIABLES + METHODS + CLASSES || types == LOCALVARIABLES + FIELDS || types == LOCALVARIABLES + METHODS + FIELDS || types == LOCALVARIABLES + FIELDS + CLASSES || types == ALL;
    }

    private boolean isMethods() {
        return types == METHODS || types == METHODS + CLASSES || types == METHODS + FIELDS || types == METHODS + FIELDS + CLASSES || types == ALL || types == METHODS + LOCALVARIABLES || types == METHODS + LOCALVARIABLES + CLASSES || types == METHODS + LOCALVARIABLES + FIELDS;
    }

    private boolean isFields() {
        return types == FIELDS || types == ALL || types == (FIELDS + CLASSES) || types == (FIELDS + METHODS) || types == (FIELDS + METHODS + CLASSES) || types == (FIELDS + LOCALVARIABLES) || types == (FIELDS + LOCALVARIABLES + CLASSES) || types == (FIELDS + LOCALVARIABLES + METHODS);
    }

    private void fixMethods(ClassNode classNode) {
        classNode.methods.forEach(methodNode -> {

            methodNode.instructions.forEach(instruction -> {
                if (instruction instanceof FieldInsnNode) {
                    FieldInsnNode fieldInsnNode = (FieldInsnNode) instruction;
                    String descClass = fieldInsnNode.desc.replaceFirst("L", "").replaceFirst(";", "");
                    classMap.values().forEach(mapping -> {
                        if (mapping.length == 2) {
                            if (mapping[0].equals(descClass)) {
                                LOGGER.info("Found field owner for obfuscated class with unobfuscated name: {}", descClass);
//                                String newName = mapping[1];
//                        LOGGER.info("Renaming field owner to: {}", newName);
                                fieldInsnNode.desc = Type.getDescriptor(Object.class); // idgaf anymore
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
//                                String newName = mapping[1];
//                                LOGGER.info("Renaming method call to: {}", newName);
                                methodInsnNode.owner = Type.getType(Object.class).getClassName();
                            }
                        }
                    });
                }
            });
        });
    }

    private void fixFields(ClassNode classNode) {
        classNode.fields.forEach(fieldNode -> {
            String descClass = ASMUtils.getType(fieldNode.desc).getClassName();
            classMap.values().forEach(mapping -> {
                if (mapping.length == 2) {
                    if (mapping[0].equals(descClass)) {
                        LOGGER.info("Found field owner for obfuscated class with unobfuscated name: {}", descClass);
                        String newName = mapping[1];
//                        LOGGER.info("Renaming field owner to: {}", newName);
                        LOGGER.info("Replacing with object");
                        fieldNode.desc = Type.getDescriptor(Object.class); // idgaf anymore
                    }
                }
            });
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
        return classNode.methods.stream().anyMatch(mn -> mn.name.equals("main") && ASMUtils.getArgumentTypes(mn.desc)[0].getSort() == Type.ARRAY);
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
