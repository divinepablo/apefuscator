import me.divine.apefuscator.Apefuscator;
import me.divine.apefuscator.transformers.impl.*;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        System.out.println("Loading Apefuscator v1.0");
        Apefuscator.builder()
                .input(Path.of("test", "input","elloWorld.jar"))
                .output(Path.of("test", "Aped-HelloWorld.jar"))
                .ignored("com", "kotlin", "org", "net/minecraft/client/main")
                .writerFlag(ClassWriter.COMPUTE_MAXS)
                .readerFlag(ClassReader.SKIP_FRAMES)
//                .addTransformer(new BetterNameTransformer("難", "手"))
//                .addTransformer(new BetterNameTransformer("aaaaa", "bbbbb"))
                .addTransformer(new MathTransformer())
                .addTransformer(new TestTransformer())
                .addTransformer(new SourceFileTransformer(true))
                .build()
                .start();  // start the apefuscator

    }
}