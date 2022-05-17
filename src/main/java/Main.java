import me.divine.apefuscator.Apefuscator;
import me.divine.apefuscator.transformers.impl.*;

import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        System.out.println("Loading Apefuscator v1.0");
        Apefuscator.builder()
                .input(Path.of("test", "irc.jar"))
                .output(Path.of("test", "Aped-IRC.jar"))
                .ignored("com", "kotlin", "org", "net/minecraft/client/main")
//                .addTransformer(new BetterNameTransformer("難", "手"))
//                .addTransformer(new BetterNameTransformer("aaaaa", "bbbbb"))
                .addTransformer(new TestTransformer())
                .addTransformer(new SourceFileTransformer(true))
                .build()
                .start();  // start the apefuscator

    }
}