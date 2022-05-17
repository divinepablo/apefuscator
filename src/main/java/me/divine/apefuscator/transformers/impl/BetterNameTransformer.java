package me.divine.apefuscator.transformers.impl;

import me.divine.apefuscator.Apefuscator;
import me.divine.apefuscator.transformers.Transformer;
import me.divine.apefuscator.utils.ASMUtils;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.Remapper;
import org.objectweb.asm.commons.SimpleRemapper;
import org.objectweb.asm.tree.ClassNode;

import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

public class BetterNameTransformer extends Transformer {
    private String primary = "ඞ";
    private String secondary = "ඝ";;
    private HashMap<String, String> mappings = new HashMap<>();
    public BetterNameTransformer(String primary, String secondary) {
        this();
        this.primary = primary;
        this.secondary = secondary;
    }

    public BetterNameTransformer() {
        super("ClassRemapper", "XD");
    }

    @Override
    public void transform(Apefuscator obfuscator) {
        obfuscator.classes().forEach(classNode -> {
            classNode.methods.forEach(methodNode -> {
                if (methodNode.name != "main" && methodNode.access != Opcodes.ACC_NATIVE) {
                    String key = String.format("%s.%s.%s", classNode.name, methodNode.name, methodNode.desc);
                    if (mappings.containsKey(key)) return;
                    if (methodNode.access != Opcodes.ACC_STATIC) {
                        if (!ASMUtils.isMethodFromSuperclass(obfuscator.getClass(classNode.superName), methodNode)) {
                            mappings.put(key, getName(30));
                        }
                    }
                }
            });
            classNode.fields.forEach(fieldNode -> {
                if (fieldNode.name != "main" && fieldNode.access != Opcodes.ACC_NATIVE) {
                    String key = String.format("%s.%s.%s", classNode.name, fieldNode.name, fieldNode.desc);
                    if (mappings.containsKey(key)) return;
                    if (fieldNode.access != Opcodes.ACC_STATIC) {
                            mappings.put(key, getName(30));

                    }
                }
            });

        });

        obfuscator.getLogger().info("mappings {}", mappings);
        Remapper remapper = new SimpleRemapper(mappings);
        obfuscator.classes().forEach(classNode -> {
            ClassNode copy = new ClassNode();
            classNode.accept(new ClassRemapper(copy, remapper));
            for (int i = 0; i < copy.methods.size(); i++) {
                classNode.methods.set(i, copy.methods.get(i));
            }
            for (int i = 0; i < copy.fields.size(); i++) {
                classNode.fields.set(i, copy.fields.get(i));
            }

            obfuscator.getClasses().remove(classNode.name);
            obfuscator.getClasses().put(copy.name, copy);
        });
    }
    private String getName(int length) {
        StringBuilder name = new StringBuilder();
        for (int i = 0; i < length; i++) {
            name.append(ThreadLocalRandom.current().nextInt(1, 6) == 5 ? secondary : primary);
        }
        return name.toString();
    }
}
