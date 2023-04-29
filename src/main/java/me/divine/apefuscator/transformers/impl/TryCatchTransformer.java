package me.divine.apefuscator.transformers.impl;

import me.divine.apefuscator.Apefuscator;
import me.divine.apefuscator.transformers.Transformer;

public class TryCatchTransformer extends Transformer {
    private final int TRY_CATCHES = 5;
    public TryCatchTransformer() {
        super("TryCatch", "Adds nested try catch");
    }

    @Override
    public void transform(Apefuscator obfuscator) {

    }

}
