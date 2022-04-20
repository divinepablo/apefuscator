package me.divine.apefuscator.transformers;

import me.divine.apefuscator.Apefuscator;

public abstract class Transformer {
    public abstract void transform(Apefuscator obfuscator);
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
