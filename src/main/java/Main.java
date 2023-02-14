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
                .input(Path.of("test", "Pasta Mafia.jar"))
                .output(Path.of("test", "Aped-Pasta.jar"))
//                .input(Path.of("test", "irc.jar"))
//                .output(Path.of("test", "Aped-IRC.jar"))
//                .input(Path.of("test", "input", "elloWorld.jar"))
//                .output(Path.of("test", "Aped-HelloWorld.jar"))
                .ignored("best", "javassist", "org", "net/minecraft/server/management", "ru", "javassist", "shadersmod", "optifine", "best", "kotlin", "com", "joptsimple")

//                .addTransformer(new FlowTransformer())
//                .addTransformer(new StringEncryptionTransformer())
                .addTransformer(new NameTransformer(NameTransformer.ALPHABET))
                .addTransformer(new SourceFileTransformer(true))
                .build()
                .start();  // start the apefuscator

    }

    public void monk(Apefuscator apefuscator) {
        System.out.println(apefuscator);
    }
}