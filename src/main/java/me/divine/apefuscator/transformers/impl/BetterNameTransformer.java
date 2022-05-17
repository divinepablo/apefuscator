package me.divine.apefuscator.transformers.impl;

import me.divine.apefuscator.Apefuscator;
import me.divine.apefuscator.transformers.Transformer;
import me.divine.apefuscator.utils.ASMUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.Remapper;
import org.objectweb.asm.commons.SimpleRemapper;
import org.objectweb.asm.tree.ClassNode;

import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class BetterNameTransformer extends Transformer {
    private final HashMap<String, String> mappings = new HashMap<>();

    public BetterNameTransformer() {
        super("ClassRemapper", "XD");
    }

    @Override
    public void transform(Apefuscator obfuscator) {
//        obfuscator.classes().forEach(classNode -> {
//            classNode.methods.forEach(methodNode -> {
//                if (!Objects.equals(methodNode.name, "main") && !classNode.name.startsWith("<") && methodNode.access != Opcodes.ACC_NATIVE) {
//                    String key = String.format("%s.%s.%s", classNode.name, methodNode.name, methodNode.desc);
//                    if (mappings.containsKey(key)) return;
//                    if (methodNode.access != Opcodes.ACC_STATIC) {
//                        if (!ASMUtils.isMethodFromSuperclass(obfuscator.getClass(classNode.superName), methodNode)) {
//                            mappings.put(key, getName(ThreadLocalRandom.current().nextInt(3, 9)));
//                        }
//                    }
//                }
//            });
//            classNode.fields.forEach(fieldNode -> {
//                if (!Objects.equals(fieldNode.name, "main") && fieldNode.access != Opcodes.ACC_NATIVE) {
//                    String key = String.format("%s.%s.%s", classNode.name, fieldNode.name, fieldNode.desc);
//                    if (mappings.containsKey(key)) return;
//                    if (fieldNode.access != Opcodes.ACC_STATIC) {
//                        mappings.put(key, getName(30));
//
//                    }
//                }
//            });
//
//        });
        obfuscator.getLogger().info("mappings {}", mappings);
        Remapper remapper = new Remapper() {
            @Override
            public String map(String internalName) {
                return BetterNameTransformer.this.getName(13);
            }
        };
        obfuscator.classes().forEach((a, b) -> {
            obfuscator.getClasses().remove(b);
            ClassNode copy = new ClassNode();
            b.accept(new ClassRemapper(copy, remapper));
            for (int i = 0; i < copy.methods.size(); i++) {
                b.methods.set(i, copy.methods.get(i));
            }
            for (int i = 0; i < copy.fields.size(); i++) {
                b.fields.set(i, copy.fields.get(i));
            }
            obfuscator.classes().put(copy.name, copy);
        });
    }

    private String getName(int length) {
        StringBuilder name = new StringBuilder();
        String[] array = { "Abstract", "Client", "Proxy", "Factory",
                "Handler", "Singleton", "Builder", "Object", "Bean",
                "MXBean", "NetworkHandler", "Instance", "Spring", "Entry",
                "Typed", "Controller", "Applet", "Mapping", "Pipeline",
                "Closeable", "Openable", "Main"
        };
        for (int i = 0; i < length; i++) {
            name.append(array[ThreadLocalRandom.current().nextInt(array.length)]);
        }
        name.append(RandomStringUtils.randomNumeric(6));
        return name.toString();
    }
}
