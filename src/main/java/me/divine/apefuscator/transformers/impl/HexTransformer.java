package me.divine.apefuscator.transformers.impl;

import me.divine.apefuscator.Apefuscator;
import me.divine.apefuscator.transformers.Transformer;
import me.divine.apefuscator.utils.ASMUtils;
import org.objectweb.asm.Opcodes;

public class HexTransformer extends Transformer {
    public HexTransformer() {
        super("Hex", "Converts all numbers to hexadecimal");
    }


    @Override
    public void transform(Apefuscator var1) {
        // TODO: nothing at all
        // im not doing this rn
    }

}
