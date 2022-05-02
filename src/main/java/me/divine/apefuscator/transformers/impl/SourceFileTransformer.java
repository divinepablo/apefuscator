package me.divine.apefuscator.transformers.impl;

import me.divine.apefuscator.Apefuscator;
import me.divine.apefuscator.transformers.Transformer;

public class SourceFileTransformer extends Transformer {
    public SourceFileTransformer() {
        super("SourceFile", "Change the name of the source file to class name (use with name transformer)");
    }

    @Override
    public void transform(Apefuscator obfuscator) {
        obfuscator.getClasses().forEach(classNode -> {
            classNode.sourceFile = "     " + ".java";
            // literally that easy
        });
    }
}
