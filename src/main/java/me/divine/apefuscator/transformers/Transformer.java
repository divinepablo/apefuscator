package me.divine.apefuscator.transformers;

import me.divine.apefuscator.Apefuscator;
import me.divine.apefuscator.transformers.impl.ObjectiferTransformer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Opcodes;

public abstract class Transformer implements Opcodes {
    public abstract void transform(Apefuscator obfuscator);
    protected Logger LOGGER = LogManager.getLogger(Transformer.class);
    private final String name;
    private final String description;

    public Transformer(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "Transformer{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
