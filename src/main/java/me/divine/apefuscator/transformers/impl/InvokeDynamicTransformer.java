package me.divine.apefuscator.transformers.impl;

import me.divine.apefuscator.Apefuscator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Handle;
import org.objectweb.asm.tree.InvokeDynamicInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;

public class InvokeDynamicTransformer extends me.divine.apefuscator.transformers.Transformer {
    Logger LOGGER = LogManager.getLogger(InvokeDynamicTransformer.class);

    public InvokeDynamicTransformer() {
        super("InvokeDynamic", "Fucks up method calls");
    }

    @Override
    public void transform(Apefuscator obfuscator) {
//        ArrayList<InvokeDynamicInsnNode> test = new ArrayList<>();
        obfuscator.classes().forEach(classNode -> {
            classNode.methods.forEach(methodNode -> {
                methodNode.instructions.forEach(instruction -> {
                    if (instruction instanceof MethodInsnNode) {
                        Handle bootstrapMethod = new Handle(H_INVOKESTATIC, "Bootstrap",
                                "bootstrapMethod",
                                "(Ljava/lang/invoke/MethodHandles$Lookup;"
                                        + "Ljava/lang/String;Ljava/lang/invoke/MethodType;)"
                                        + "Ljava/lang/invoke/CallSite;");
                        InvokeDynamicInsnNode invokeDynamic = new InvokeDynamicInsnNode(((MethodInsnNode) instruction).name, ((MethodInsnNode) instruction).desc, bootstrapMethod);
                        methodNode.instructions.insertBefore(instruction, invokeDynamic);
                        methodNode.instructions.remove(instruction);
                    }

                });
            });
        });
    }
}
