package net.foxtam.foxclicker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class GlobalLogger {
    public static final Path LOG_DIRECTORY = Path.of("logs");
    private static final Map<String, Logger> loggers = new HashMap<>();
    private static final String INDENT = "    ";

    static {
        if (Files.exists(LOG_DIRECTORY)) {
            deleteRecursively(LOG_DIRECTORY);
        }
    }

    public static <T extends Exception> T exception(T e) {
        getCurrentLogger().error(INDENT + getIndent() + "~ " + e);
        return e;
    }

    private static Logger getCurrentLogger() {
        String fullClassName = getFullClassName();
        Logger logger = loggers.get(fullClassName);
        if (logger == null) {
            logger = LoggerFactory.getLogger(fullClassName);
            loggers.put(fullClassName, logger);
        }
        return logger;
    }

    private static String getIndent() {
        return INDENT.repeat(Thread.currentThread().getStackTrace().length - 4);
    }

    private static String getFullClassName() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        return stackTrace[4].getClassName();
    }

    public static void enter(Object... args) {
        String argString = argsToString(args);
        String classWithMethodName = getClassMethodName();
        String indent = getIndent();
        getCurrentLogger().trace(indent + "> {}{}", classWithMethodName, argString);
    }

    private static String argsToString(Object[] args) {
        StringBuilder builder = new StringBuilder();
        for (Object arg : args) {
            builder.append(" | ").append(arg);
        }
        return builder.toString();
    }

    private static String getClassMethodName() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        StackTraceElement element = stackTrace[3];
        String[] split = element.getClassName().split("\\.");
        return split[split.length - 1] + "." + element.getMethodName();
    }

    public static <T> T exit(T result) {
        String actualClassMethodName = getClassMethodName();
        getCurrentLogger().trace(getIndent() + "< {} | {}", actualClassMethodName, result);
        return result;
    }

    public static void exit() {
        String actualClassMethodName = getClassMethodName();
        getCurrentLogger().trace(getIndent() + "< {}", actualClassMethodName);
    }

    public static void deleteRecursively(Path directory) {
        try (Stream<Path> walk = Files.walk(directory)) {
            //noinspection ResultOfMethodCallIgnored
            walk.sorted(Comparator.reverseOrder())
                  .map(Path::toFile)
                  .forEach(File::delete);
        } catch (IOException e) {
//            Bot.showErrorMessage("Can't delete: " + directory);
//            throw new RuntimeException(e);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            e.printStackTrace(new PrintWriter(outputStream, true, StandardCharsets.UTF_8));
            trace(outputStream.toString(StandardCharsets.UTF_8));
        }
    }

    public static <T> T trace(T obj) {
        getCurrentLogger().trace(INDENT + getIndent() + obj.toString());
        return obj;
    }

    public static void trace(Exception e) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        e.printStackTrace(new PrintWriter(outputStream, true, StandardCharsets.UTF_8));
        trace(outputStream.toString(StandardCharsets.UTF_8));
    }
}
