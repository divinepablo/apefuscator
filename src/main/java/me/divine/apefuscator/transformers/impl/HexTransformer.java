package me.divine.apefuscator.transformers.impl;

import me.divine.apefuscator.Apefuscator;
import me.divine.apefuscator.transformers.Transformer;
import me.divine.apefuscator.utils.ASMUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;

import java.lang.reflect.Method;

public class HexTransformer extends Transformer {
    public HexTransformer() {
        super("Hex", "Converts all numbers to hexadecimal");
    }

    Logger LOGGER = LogManager.getLogger(HexTransformer.class);

    @Override
    public void transform(Apefuscator obfuscator) {

    }

    private String getHex(int i) {
        return Integer.toHexString(i);
    }

}
