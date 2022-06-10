package me.divine.apefuscator.transformers.impl;

import me.divine.apefuscator.Apefuscator;
import me.divine.apefuscator.transformers.Transformer;
import me.divine.apefuscator.utils.ASMUtils;
import me.divine.apefuscator.utils.MemberRemapper;
import org.apache.commons.lang3.RandomStringUtils;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.Remapper;
import org.objectweb.asm.tree.ClassNode;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;

public class NameTransformer extends Transformer {
    private final HashMap<String, String> mappings = new HashMap<>();

    public NameTransformer() {
        super("Renamer", "great");
    }

    @Override
    public void transform(Apefuscator obfuscator) {

        obfuscator.getClasses().forEach(classNode -> {
            AtomicBoolean h = new AtomicBoolean(false);
            classNode.methods.forEach(methodNode -> {
                if (!h.get()) {
                    h.set(Objects.equals(methodNode.name, "main"));
                } else {
                    obfuscator.getLogger().info("Found main method in class {}", classNode.name);
                }
                if (!Objects.equals(methodNode.name, "main") && !methodNode.name.startsWith("<") && methodNode.access != Opcodes.ACC_NATIVE) {

                    String key = String.format("%s.%s%s", classNode.name, methodNode.name, methodNode.desc);
                    if (mappings.containsKey(key)) return;
                    if (methodNode.access != Opcodes.ACC_STATIC) {
                        if (!ASMUtils.isMethodFromSuperclass(obfuscator.getClass(classNode.superName), methodNode)) {
                            mappings.put(key, getName(ThreadLocalRandom.current().nextInt(3, 29)));
                        }
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
                mappings.put(classNode.name, getName(15));
            }

        });
//        obfuscator.getLogger().info("mappings {}", mappings);
        Remapper remapper = new MemberRemapper(mappings);
        for (Map.Entry<String, ClassNode> entry : obfuscator.classes().entrySet()) {
            if (!obfuscator.getIgnoredList().contains(entry.getValue())) {
                String a = entry.getKey();
                ClassNode b = entry.getValue();
                obfuscator.getClassMap().remove(a);
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
            }
        }
    }

    private String getName(int length) {
        StringBuilder name = new StringBuilder();
        String[] array = { "Abstract", "Client", "Proxy", "Factory",
                "Handler", "Singleton", "Builder", "Object", "Bean",
                "MXBean", "NetworkHandler", "Instance", "Spring", "Entry",
                "Typed", "Controller", "Applet", "Mapping", "Pipeline",
                "Closeable", "Openable", "Main"
        };
//        String[] array = { "Tear", "Eviate", "Charlie", "Walker",
//                "Tony", "Adams", "Eleven", "Don", "Lane"
//        };
        for (int i = 0; i < length; i++) {
            name.append(array[ThreadLocalRandom.current().nextInt(array.length)]);
        }
        name.append(RandomStringUtils.randomNumeric(6));
        return name.toString();
    }
}
