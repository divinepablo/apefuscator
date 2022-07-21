package me.divine.apefuscator;

import me.divine.apefuscator.transformers.Transformer;
import me.divine.apefuscator.utils.ClassUtil;
import me.divine.apefuscator.utils.FileUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.tree.ClassNode;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

// took some code from a deobfuscator as i dont know how to do this shit :skull:
public class Apefuscator {

    private static final Logger LOGGER = LogManager.getLogger();

    private final Map<String, ClassNode> classes = new ConcurrentHashMap<>();
    private final Map<String, ClassNode> originalClasses = new ConcurrentHashMap<>();
    private final Map<String, byte[]> files = new ConcurrentHashMap<>();
    private final Path input;
    private final Path output;
    private final ArrayList<Transformer> transformers = new ArrayList<>();
    private final CopyOnWriteArrayList<String> ignoredStrings = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<ClassNode> ignoredList = new CopyOnWriteArrayList<>();
    private final int readerMode;
    private final int writerMode;

    public CopyOnWriteArrayList<String> getIgnoredStrings() {
        return ignoredStrings;
    }

    public CopyOnWriteArrayList<ClassNode> getIgnoredList() {
        return ignoredList;
    }

    private Apefuscator(ApefuscatorBuilder builder) throws FileNotFoundException {
        if (!builder.input.toFile().exists())
            throw new FileNotFoundException(builder.input.toString());

        if (builder.output.toFile().exists())
            LOGGER.warn("Output file already exist, data will be overwritten");

        this.input = builder.input;
        this.output = builder.output;
        this.transformers.addAll(builder.transformers);
        this.ignoredStrings.addAll(builder.ignored);
        this.readerMode = builder.read;
        this.writerMode = builder.writer;

        System.out.println();
    }

    public static ApefuscatorBuilder builder() {
        return new ApefuscatorBuilder();
    }

    private void load() {
        LOGGER.info("Loading input file: {}", input);

        FileUtil.loadFilesFromZip(input.toString()).forEach((name, data) -> {
            try {
                if (ClassUtil.isClass(name, data)) {
                    ClassNode classNode = ClassUtil.loadClass(data, readerMode);

                    classes.put(classNode.name, classNode);
                    originalClasses.put(classNode.name, ClassUtil.copy(classNode)); //yes

                    for (String ignoredString : ignoredStrings) {
                        if (classNode.name.equals(ignoredString) || classNode.name.startsWith(ignoredString)) {
                            ignoredList.add(classNode);
                            ignoredStrings.add(classNode.name);
                        }
                    }


                } else {
                    files.put(name, data);
                }
            } catch (Exception e) {
                LOGGER.error("Could not load class: {}, adding as file", name);
                LOGGER.debug("Error", e);
                files.put(name, data);

                e.printStackTrace();
            }
        });
        LOGGER.info("Loaded input file: {}\n", input);
        LOGGER.info("Ignoring {} classes", ignoredList.size());
    }

    private void save() {
        LOGGER.info("Saving output file: {}", output);

        try (ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(output.toFile()))) {
            zipOutputStream.setLevel(9);

            classes.forEach((ignored, classNode) -> {
                try {
                    byte[] data = ClassUtil.classToBytes(classNode, writerMode);
                    zipOutputStream.putNextEntry(new ZipEntry(classNode.name + ".class"));
                    zipOutputStream.write(data);
                } catch (Exception e) {
                    LOGGER.error("Error when saving a class", e);
                    e.printStackTrace();
                    try {
                        byte[] data = ClassUtil.classToBytes(originalClasses.get(classNode.name), writerMode);

                        zipOutputStream.putNextEntry(new ZipEntry(classNode.name + ".class"));
                        zipOutputStream.write(data);
                    } catch (Exception e2) {
                        LOGGER.error("Error", e2);

                        e2.printStackTrace();
                    }
                }

                originalClasses.remove(classNode.name);
                classes.remove(ignored);
            });

            files.forEach((name, data) -> {
                try {
                    zipOutputStream.putNextEntry(new ZipEntry(name));
                    zipOutputStream.write(data);
                } catch (Exception e) {
                    LOGGER.error("Could not save file: {}", name);
                    LOGGER.debug("Error", e);

                    e.printStackTrace();
                }

                files.remove(name);
            });
        } catch (Exception e) {
            LOGGER.error("Could not save file: {}", output);
            LOGGER.debug("Error", e);

            e.printStackTrace();

        }

        LOGGER.info("Saved output file: {}\n", output);


    }

    public void start() {
        LOGGER.info("Starting Apefuscator");
        long beforebeforebefore = System.currentTimeMillis();
        load();
        transform();
        save();
        long diff = System.currentTimeMillis() - beforebeforebefore;
        LOGGER.info("Finished in {} milliseconds, ({} hours | {} minutes | {} seconds", diff, TimeUnit.MILLISECONDS.toHours(diff), TimeUnit.MILLISECONDS.toMinutes(diff), TimeUnit.MILLISECONDS.toSeconds(diff));
    }

    private void transform() {
        LOGGER.info("Transforming classes");
        long beforebefore = System.currentTimeMillis();
        for (Transformer transformer : transformers) {
            long before = System.currentTimeMillis();
            LOGGER.info("Using {} transformer", transformer.getName());
            transformer.transform(this);
            LOGGER.info("Finished in {} milliseconds", System.currentTimeMillis() - before);
        }
        LOGGER.info("Finished transforming classes in {} milliseconds", System.currentTimeMillis() - beforebefore);
    }

    public Map<String, ClassNode> classes() {
        Map<String, ClassNode> classes = new ConcurrentHashMap<>(this.classes);
        classes.keySet().forEach(key -> {
            if (ignoredStrings.contains(key)) {
                classes.remove(key);
            }
        });
        return classes;
    }    public Map<String, ClassNode> allClasses() {
        Map<String, ClassNode> classes = new ConcurrentHashMap<>(this.classes);
//        classes.keySet().forEach(key -> {
//            if (ignoredStrings.contains(key)) {
//                classes.remove(key);
//            }
//        });
        return classes;
    }


    public Collection<ClassNode> getClasses() {
        Collection<ClassNode> classes = new ArrayList<>(this.classes.values());
        classes.removeIf(ignoredList::contains);
        return classes;
    }

    public ClassNode getClass(String name) {
        return classes.values().stream().filter(classNode -> classNode.name.equals(name)).findFirst().orElse(null);
    }




    public Map<String, ClassNode> getOriginalClasses() {
        return originalClasses;
    }
    public Map<String, ClassNode> getClassMap() {
        return classes;
    }

    public Map<String, byte[]> getFiles() {
        return files;
    }

    public Path getInput() {
        return input;
    }

    public Path getOutput() {
        return output;
    }

    public ArrayList<Transformer> getTransformers() {
        return transformers;
    }

    public Logger getLogger() {
        return LOGGER;
    }

    public static class ApefuscatorBuilder {
        private Path input = Path.of("input.jar");
        private Path output = Path.of("output.jar");
        private final ArrayList<Transformer> transformers = new ArrayList<>();
        private final ArrayList<String> ignored = new ArrayList<>();
        private int read;
        private int writer;

        public ApefuscatorBuilder input(Path input) {
            this.input = input;
            return this;
        }

        public ApefuscatorBuilder output(Path output) {
            this.output = output;
            return this;
        }

        public ApefuscatorBuilder writerFlag(int writer) {
            this.writer = writer;
            return this;
        }
        public ApefuscatorBuilder readerFlag(int read) {
            this.read = read;
            return this;
        }

        public ApefuscatorBuilder addTransformer(Transformer transformer) {
            LOGGER.info("Adding transformer: {}", transformer.getName());
            transformers.add(transformer);
            return this;
        }

        public ApefuscatorBuilder transformers(Transformer... transformers) {
            System.out.println("Adding transformers: " + Arrays.toString(transformers));
            Collections.addAll(this.transformers, transformers);
            return this;
        }

        public ApefuscatorBuilder ignored(String... ignored) {

            Collections.addAll(this.ignored, ignored);
            return this;
        }


        public Apefuscator build() {
            Apefuscator builder = null;
            try {
                builder = new Apefuscator(this);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return builder;
        }

        public Path getInput() {
            return input;
        }

        public Path getOutput() {
            return output;
        }

        public ArrayList<Transformer> getTransformers() {
            return transformers;
        }
    }
}


