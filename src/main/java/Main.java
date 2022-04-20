import me.divine.apefuscator.Apefuscator;
import me.divine.apefuscator.transformers.impl.NameTransformer;

import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        System.out.println("Apefuscator v1.0");
        Apefuscator.builder()
                .input(Path.of("test", "irc.jar"))
                .output(Path.of("test", "Aped-irc.jar"))
//                .addTransformer(new MathTransformer())
                .ignored("com", "kotlin", "org")
                .addTransformer(new NameTransformer('難', '手'))
                .build()
                .start();  // start the apefuscator

    }
}