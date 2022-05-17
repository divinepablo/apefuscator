package me.divine.apefuscator.transformers.impl;

import me.divine.apefuscator.Apefuscator;
import me.divine.apefuscator.transformers.Transformer;

public class SourceFileTransformer extends Transformer {
    private boolean className = false;
    public SourceFileTransformer(boolean className) {
        super("SourceFile", "Change the name of the source file to class name (use with name transformer)");
        this.className = className;
    }

    @Override
    public void transform(Apefuscator obfuscator) {
        obfuscator.classes().forEach(classNode -> {
            if (className) {
                classNode.sourceFile = classNode.name + ".java";
            } else {
                classNode.sourceFile = "     " + ".java";
            }
            // literally that easy
        });
    }
}
