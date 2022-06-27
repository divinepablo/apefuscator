import me.divine.apefuscator.Apefuscator;
import me.divine.apefuscator.transformers.impl.*;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        System.out.println("Loading Apefuscator v1.0");
        Apefuscator.builder()
                .readerFlag(ClassReader.EXPAND_FRAMES)
                .writerFlag(ClassWriter.COMPUTE_MAXS)
//                .input(Path.of("test", "input", "elloWorld.jar"))
                .input(Path.of("test", "Sleek.jar"))
                .output(Path.of("test", "Aped-Sleek.jar"))
//                .addTransformer(new MathTransformer())
                .ignored("com", "kotlin", "org", "fr", "net/minecraft/client/main", "javassist", "shadersmod", "de", "javax")
//                .addTransformer(new ObjectiferTransformer())
//                .addTransformer(new DortCodeObfuscation())
                .addTransformer(new NameTransformer(NameTransformer.TROLL))
                .addTransformer(new SourceFileTransformer(true))

//                .addTransformer(new WrapperMethodTransformer())
                .build()
                .start();  // start the apefuscator

    }

    public void monk(Apefuscator apefuscator) {
        System.out.println(apefuscator);
    }
}