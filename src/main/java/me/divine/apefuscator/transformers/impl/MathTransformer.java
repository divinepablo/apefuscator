package me.divine.apefuscator.transformers.impl;

import com.google.gson.GsonBuilder;
import me.divine.apefuscator.Apefuscator;
import me.divine.apefuscator.transformers.Transformer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class MathTransformer extends Transformer {

    private Logger LOGGER = LogManager.getLogger(MathTransformer.class);

    public MathTransformer() {
        super("Operations", "Converts some math functions to their equivalent in java and converts some operations to bitwise operations."); // copilot wrote most of this sentence lmao
    }


    @Override
    public void transform(Apefuscator var1) {
        var1.getClasses().forEach(clazz -> {
            clazz.methods.forEach(method -> {
                method.instructions.forEach(instruction -> {
                    if (instruction instanceof IntInsnNode) {
                        IntInsnNode node = (IntInsnNode) instruction;
                        LOGGER.info("Found an IntInsnNode {} {}", node.getOpcode(), node.operand);
                    }
                });
            });
        });
    }
}
