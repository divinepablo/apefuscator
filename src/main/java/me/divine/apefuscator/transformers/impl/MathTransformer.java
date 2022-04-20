package me.divine.apefuscator.transformers.impl;

import com.google.gson.GsonBuilder;
import me.divine.apefuscator.Apefuscator;
import me.divine.apefuscator.transformers.Transformer;
import org.objectweb.asm.tree.MethodNode;

public class MathTransformer extends Transformer {
    public MathTransformer() {
        super("Operations", "Converts some math functions to their equivalent in java and converts some operations to bitwise operations."); // copilot wrote most of this sentence lmao
    }


    @Override
    public void transform(Apefuscator var1) {

    }
}
