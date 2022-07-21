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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class NameTransformer extends Transformer {
    private final HashMap<String, String> mappings = new HashMap<>();
    private final Logger LOGGER = LogManager.getLogger();
    private int namingMode = ENTERPRISE;
    public static final int ENTERPRISE = 46;
    public static final int TROLL = 47;

    public NameTransformer() {
        super("Renamer", "great");
    }

    public NameTransformer(int namingMode) {
        this();
        this.namingMode = namingMode;
    }

    @Override
    public void transform(Apefuscator obfuscator) {
        obfuscator.getLogger().info("Renaming with mode {}", namingMode);
        obfuscator.getLogger().info("Generating mappings");
        obfuscator.getClasses().forEach(classNode -> {

            AtomicBoolean h = new AtomicBoolean(false);
            classNode.methods.forEach(methodNode -> {
                if (!h.get()) {
                    h.set(Objects.equals(methodNode.name, "main"));
                } else {
                    obfuscator.getLogger().info("Found main method in class {}", classNode.name);
                }
                if (canRename(classNode, methodNode, obfuscator)) {
                    String key = String.format("%s.%s%s", classNode.name, methodNode.name, methodNode.desc);
                    if (!ASMUtils.isMethodFromSuperclass(obfuscator.getClass(classNode.superName), methodNode)) {
                        mappings.put(key, getName(15));
                    }
                }
            });
            classNode.fields.forEach(fieldNode -> {
                String key = String.format("%s.%s%s", classNode.name, fieldNode.name, fieldNode.desc);
                if (mappings.containsKey(key)) return;
                if (fieldNode.access != Opcodes.ACC_STATIC) {
                    mappings.put(key, getName(30));
                }

            });
            if (!h.get()) {
                String name = (namingMode == 47 ? "tear/x/eviate/hypixel/bypass/extraordinaire/" : "enterprise/") + getName(ThreadLocalRandom.current().nextInt(40, 100));
                if (mappings.containsValue(name)) {
                    name += getName(ThreadLocalRandom.current().nextInt(40, 100));
                }
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

        for (Map.Entry<String, ClassNode> entry : obfuscator.allClasses().entrySet()) {
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
//                obfuscator.getLogger().info("{} class", copy.name);
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
                return mappings.containsKey(String.format("%s.%s%s", parent.name, methodNode.name, methodNode.desc));
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
        name.append(RandomStringUtils.randomNumeric(6));
        return name.toString();
    }


    public String[] getWords() {
        String[] words;
        switch (namingMode) {

            case TROLL:
                words = new String[]{
                        "Tear", "Eviate", "Charlie", "Walker",
                        "Tony", "Adams", "Eleven", "Don", "Lane",
                        "SuwonHighschool", "Asteroid", "SmokeX", "Autumn", "Summer", "Alan", "Real",
                        "Hypixel", "Fly", "LEAKED", "Public", "Rise", "Felix", "Gravity", "Raybo",
                        "Zajchu", "Eviratted", "Dortware", "Memeware", "Floydware", "Pandaware",
                        "SRC", "FREE", "Skidded", "Pasted", "HowManyBytesInRadiumPaste", "Ketamine", "Zane",
                        "HomoBus", "Devonshire", "Rd", "Hauppauge", "NY", "Jinthium", "Haram",
                        "Halal", "Allah", "Final", "Roy", "Hwang", "HomoBus", "Devonshire", "Rd",
                        "Hauppauge", "NY", "Jinthium", "Roy", "Hwang","BeanDog","PowredByNefariousIntent"
                        ,"YouJustGotHashTagEviRatted", "DiabloGatoSexo","IForgotHowToCode","IntentStoreRCE",
                        "IfYouAreReadingThisYouAreAlreadyRatted", "VincentDiablo","ImNotObfuscated","RatLoader",
                        "RobertLopresti","LoprestiWare"
                };
                break;
            case ENTERPRISE:
                words = new String[]{"Abstract", "Client", "Proxy", "Factory",
                        "Handler", "Singleton", "Builder", "Object", "Bean",
                        "MXBean", "NetworkHandler", "Instance", "Spring", "Entry",
                        "Typed", "Controller", "Applet", "Mapping", "Pipeline",
                        "Closeable", "Openable", "Main"};
                break;
            default:
                words = new String[]{"a", "b", "c"};
                break;
        }
        return words;
    }

}
