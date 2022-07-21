import me.divine.apefuscator.Apefuscator;
import me.divine.apefuscator.transformers.impl.*;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        System.out.println("Loading Apefuscator v1.0");
        Apefuscator.builder()
                .readerFlag(ClassReader.SKIP_FRAMES)
                .writerFlag(0)
                .input(Path.of("test", "Sleek.jar"))
                .output(Path.of("test", "Aped-Sleek.jar"))
                .ignored("com", "kotlin", "org", "fr", "net/minecraft/client/main", "javassist", "shadersmod", "de", "javax", "paulscode", "joptsimple", "net/minecraft/server/management")
//
//                .addTransformer(new NameTransformer(NameTransformer.TROLL))
                .addTransformer(new WrapperMethodTransformer())
                .addTransformer(new WrapperMethodTransformer())

                .build()
                .start();  // start the apefuscator

    }

    public void monk(Apefuscator apefuscator) {
        System.out.println(apefuscator);
    }
}