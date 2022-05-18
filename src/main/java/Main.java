import me.divine.apefuscator.Apefuscator;
import me.divine.apefuscator.transformers.impl.*;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        System.out.println("Loading Apefuscator v1.0");
        Apefuscator.builder()
//                .input(Path.of("test", "input", "elloWorld.jar"))
                .input(Path.of("test", "irc.jar"))
                .output(Path.of("test", "Aped-HelloWorld.jar"))
//                .addTransformer(new MathTransformer())
                .ignored("com", "kotlin", "org")
//                .addTransformer(new TestTransformer())
//                .addTransformer(new FlowTransformer())
//                .addTransformer(new StringEncryptionTransformer())
                .addTransformer(new InvokeDynamicTransformer())
//                .addTransformer(new LabelRenamerTransformer())
                .addTransformer(new BetterNameTransformer())
                .build()
                .start();  // start the apefuscator

    }
}