package me.divine.apefuscator.transformers.impl;

import me.divine.apefuscator.Apefuscator;
import me.divine.apefuscator.transformers.Transformer;
import me.divine.apefuscator.utils.ASMUtils;
import me.divine.apefuscator.utils.MemberRemapper;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.Remapper;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class NameTransformer extends Transformer {
    private final HashMap<String, String> mappings = new HashMap<>();
    private final HashMap<ClassNode, List<ClassNode>> classesThing = new HashMap<>();
    private final Logger LOGGER = LogManager.getLogger();
    private int namingMode = ENTERPRISE;
    public static final int ENTERPRISE = 46;
    public static final int TROLL = 47;
    public static final int ALPHABET = 48;

    public NameTransformer() {
        super("Renamer", "great");
    }

    public NameTransformer(int namingMode) {
        this();
        this.namingMode = namingMode;
    }

    @Override
    public void transform(Apefuscator obfuscator) {
        obfuscator.getLogger().info("Finding all parents and subclasses");
        obfuscator.getClasses().forEach(classNode -> {
                    if (classNode.superName != null) {
                        if (obfuscator.getClass(classNode.superName) != null) {
                            ClassNode superClass = obfuscator.getClass(classNode.superName);
                            if (classesThing.containsKey(superClass)) {
                                classesThing.get(superClass).add(classNode);
                            } else {
                                ArrayList<ClassNode> list = new ArrayList<>();
                                list.add(classNode);
                                classesThing.put(superClass, list);
                            }
                        }
                    }
        });
        obfuscator.getLogger().info("Renaming with mode {}", namingMode);
        obfuscator.getLogger().info("Generating mappings");
        obfuscator.getClasses().forEach(classNode -> {
            if (classNode.superName != null) {
                if (obfuscator.getClass(classNode.superName) != null) {
                    ClassNode superClass = obfuscator.getClass(classNode.superName);
                    if (classesThing.containsKey(superClass)) {
                        classesThing.get(superClass).add(classNode);
                    } else {
                        ArrayList<ClassNode> list = new ArrayList<>();
                        list.add(classNode);
                        classesThing.put(superClass, list);
                    }
                }
            }
            AtomicBoolean h = new AtomicBoolean(false);
            classNode.methods.forEach(methodNode -> {
                if (!ASMUtils.isAccess(methodNode.access, Opcodes.ACC_PRIVATE)) {
                    methodNode.access &= ~Opcodes.ACC_PRIVATE;
                    methodNode.access &= ~Opcodes.ACC_PROTECTED;
                    methodNode.access |= Opcodes.ACC_PUBLIC;
                }
                if (!h.get()) {
                    h.set(Objects.equals(methodNode.name, "main"));
                } else {
                    obfuscator.getLogger().info("Found main method in class {}", classNode.name);
                }
                if (methodNode.localVariables != null) {
                    methodNode.localVariables.forEach(local -> {
                        local.name = getName(5);
                    });
                }

                if (canRename(classNode, methodNode, obfuscator)) {
                    String key = String.format("%s.%s%s", classNode.name, methodNode.name, methodNode.desc);
                    String name = getName(3);
                    if (mappings.containsKey(key)) {
                        name += getName(3);
                    }
                    if (classesThing.containsKey(classNode)) {
                        for (ClassNode subClass : classesThing.get(classNode)) {
                            String key2 = String.format("%s.%s%s", subClass.name, methodNode.name, methodNode.desc);
                            mappings.put(key2, name);
                        }
                    }
                    // probably not the prettiest but it works
                    if (classNode.superName != null) {
                        ClassNode superClass = obfuscator.getClass(classNode.superName);
                        if (superClass != null && classesThing.containsKey(superClass)) {
                            String key2 = String.format("%s.%s%s", superClass.name, methodNode.name, methodNode.desc);
                            if (mappings.containsKey(key2)) {
                                mappings.put(key, mappings.get(key2));
//                                LOGGER.info("hi {}->{}", key, mappings.get(key));
                            }
                        } else {
                            mappings.put(key, name);
                        }
                    } else {
                        mappings.put(key, name);
                    }
                }
            });
            classNode.fields.forEach(fieldNode -> {
                if (!ASMUtils.isAccess(fieldNode.access, Opcodes.ACC_PRIVATE)) {
                    fieldNode.access &= ~Opcodes.ACC_PRIVATE;
                    fieldNode.access &= ~Opcodes.ACC_PROTECTED;
                    fieldNode.access |= Opcodes.ACC_PUBLIC;
                }
                String key = String.format("%s.%s.%s", classNode.name, fieldNode.name, fieldNode.desc);
                String name = getName(3);
                if (mappings.containsKey(key)) {
                    name += getName(3);
                }
                if (classesThing.containsKey(classNode)) {
                    for (ClassNode subClass : classesThing.get(classNode)) {
                        String key2 = String.format("%s.%s.%s", subClass.name, fieldNode.name, fieldNode.desc);
                        mappings.put(key2, name);
                    }
                }
                if (classNode.superName != null) {
                    ClassNode superClass = obfuscator.getClass(classNode.superName);
                    if (superClass != null && classesThing.containsKey(superClass)) {
                        String key2 = String.format("%s.%s.%s", superClass.name, fieldNode.name, fieldNode.desc);
                        if (mappings.containsKey(key2)) {
                            mappings.put(key, mappings.get(key2));

                        }
                        return;
                    }

                }
                mappings.put(key, name);


            });


            classNode.access &= ~Opcodes.ACC_PRIVATE;
            classNode.access &= ~Opcodes.ACC_PROTECTED;
            classNode.access &= ~Opcodes.ACC_STATIC;
            classNode.access |= Opcodes.ACC_PUBLIC;

            if (!h.get()) {
                String name = "";
                switch (namingMode) {
                    case TROLL:
                        name = "tear/x/eviate/hypixel/bypass/extraordinaire/";
                        break;
                    case ENTERPRISE:
                        name = "enterprise/";
                        break;
                    case ALPHABET:
                        name = "abc/";
                        break;
                    default:
                        name = "hi/";
                        break;

                }

                name += getName(1);
//                String name = (namingMode == 47 ? "tear/x/eviate/hypixel/bypass/extraordinaire/" : "enterprise/") + getName(ThreadLocalRandom.current().nextInt(40, 100));
                while (mappings.containsValue(name)) {
                    name += getName(1);
                }

                if (classNode.name.contains("PlayerProfileCache"))
                    return;
                mappings.put(classNode.name, name);
            }
        });

//        obfuscator.getLogger().info("mappings {}", mappings);
        obfuscator.getLogger().info("Applying mappings");
        applyMappings(obfuscator);
        obfuscator.getLogger().info("Saving mappings");
        saveMappings();
    }

    private void applyMappings(Apefuscator obfuscator) {
        Remapper remapper = new MemberRemapper(mappings);
        for (Map.Entry<String, ClassNode> entry : obfuscator.getClasses2().entrySet()) {
            if (!entry.getKey().contains("net")) return; // hardcoding minecraft shid
            try {
                ClassNode b = entry.getValue();

                if (!obfuscator.getIgnoredList().contains(entry.getValue())) {
                    obfuscator.getClassMap().remove(entry.getKey());
                }

                ClassNode copy = new ClassNode();
                b.accept(new ClassRemapper(copy, remapper));
                for (int i = 0; i < copy.methods.size(); i++) {
                    b.methods.set(i, copy.methods.get(i));
                }
                for (int i = 0; i < copy.fields.size(); i++) {
                    b.fields.set(i, copy.fields.get(i));
                }
                if (!obfuscator.getIgnoredList().contains(entry.getValue()))
                    obfuscator.getClassMap().put(copy.name, copy);


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void saveMappings() {
        try {
            File file = new File("mappings.txt");
            if (!file.exists()) {
                file.createNewFile();
            }
            StringBuilder black = new StringBuilder();
            mappings.forEach((k, v) -> {
                black.append(k + " -> " + v + "\n");
            });
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(black.toString());
            fileWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private boolean canRename(ClassNode parent, MethodNode methodNode, Apefuscator obfuscator) {
        if (!Objects.equals(methodNode.name, "main") && !methodNode.name.startsWith("<") && !ASMUtils.isAccess(methodNode.access, Opcodes.ACC_NATIVE)) {
            if (parent.outerClass == null || obfuscator.getClassNotIfIgnored(parent.outerClass) == null) {
                return !mappings.containsKey(String.format("%s.%s%s", parent.name, methodNode.name, methodNode.desc));
            }

        }
        return false;
    }

    private String getName(int length) {
        StringBuilder name = new StringBuilder();
        String[] array = getWords();
//        String[] array = { "Tear", "Eviate", "Charlie", "Walker",
//                "Tony", "Adams", "Eleven", "Don", "Lane"
//        };
        for (int i = 0; i < length; i++) {
            name.append(array[ThreadLocalRandom.current().nextInt(array.length)]);
        }
//        name.append(RandomStringUtils.randomNumeric(6));
        return name.toString();
    }


    public String[] getWords() {
        String[] words;
        switch (namingMode) {
            case TROLL:
                words = new String[]{
                        "Tear", "Eviate", "Charlie", "Walker",
                        "Tony", "Adams", "Eleven", "Don", "Lane",
                        "Suwon", "Highschool", "Asteroid", "SmokeX", "Autumn", "Summer", "Alan", "Real",
                        "Hypixel", "Fly", "LEAKED", "Public", "Rise", "Felix", "Gravity", "Raybo", "Brett",
                        "Zajchu", "Eviratted", "Dortware", "Memeware", "Floydware", "Pandaware",
                        "SRC", "FREE", "Skidded", "Pasted", "HowManyBytesInRadiumPaste", "Ketamine", "Zane",
                        "HomoBus", "Devonshire", "Rd", "Hauppauge", "NY", "Jinthium", "Haram",
                        "Halal", "Allah", "Final", "Roy", "Hwang", "HomoBus", "Devonshire", "Rd",
                        "Hauppauge", "NY", "Jinthium", "Roy", "Hwang","Beandog","Powered","By","Nefarious","Intent",
                        "Diablo","IntentStore", "RCE", "Vincent","ImNotObfuscated","Rat", "Loader",
                        "Robert", "Lopresti", "Ware"
                };
                break;
            default:
            case ENTERPRISE:
                words = new String[]{"Abstract", "Client", "Proxy", "Factory",
                        "Handler", "Singleton", "Builder", "Object", "Bean",
                        "MXBean", "NetworkHandler", "Instance", "Spring", "Entry",
                        "Typed", "Controller", "Applet", "Mapping", "Pipeline",
                        "Closeable", "Openable", "Main"};
                break;
            case ALPHABET:
                words = new String[] {
//                        "A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z",
                        "a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"
                };
                break;
        }
        return words;
    }

}
