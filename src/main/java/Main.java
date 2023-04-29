import me.divine.apefuscator.Apefuscator;
import me.divine.apefuscator.transformers.impl.*;
import org.objectweb.asm.ClassWriter;

import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        System.out.println("Loading Apefuscator v1.0");
        Apefuscator.builder()
                .readerFlag(0)
                .writerFlag(0)
//                .input(Path.of("input", "Test2.jar"))
//                .output(Path.of("output", "Aped-Test2.jar"))
//                .input(Path.of("input", "Test.jar"))
//                .output(Path.of("output", "Aped-Test.jar"))
                .input(Path.of("input", "irc.jar"))
                .output(Path.of("output", "Aped-IRC.jar"))
                .ignored("best", "javassist", "org", "net/minecraft/server/management", "ru", "javassist", "shadersmod", "optifine", "best", "kotlin", "com", "joptsimple")
                .addTransformer(new JarFillerTransformer(1000))
//                .addTransformer(new NameTransformer(NameTransformer.ENTERPRISE))
//                .addTransformer(new SourceFileTransformer(true))
//                .addTransformer(new StringEncryptionTransformer())
                .addTransformer(new ObjectiferTransformer())
                .build()
                .start();  // start the apefuscator

    }
}