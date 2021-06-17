package net.foxtam.foxclicker;

import net.foxtam.foxclicker.exceptions.WrongGlobalLoggerStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

public class GlobalLogger {
    public static final Path LOG_DIRECTORY = Path.of("logs");
    private static final Map<String, Logger> loggers = new HashMap<>();
    private static final Deque<String> methodsStack = new ArrayDeque<>();
    private static boolean inExceptionState = false;

    static {
        if (Files.exists(LOG_DIRECTORY)) {
            deleteRecursively(LOG_DIRECTORY);
        }
    }

    public static <T extends Exception> T exception(T e) {
        inExceptionState = true;
        getCurrentLogger().error(getIndent() + e);
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
        return "    ".repeat(methodsStack.size());
    }

    private static String getFullClassName() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        StackTraceElement element = stackTrace[4];
        return element.getClassName();
    }

    public static void enter(Object... args) {
        String argString = getArgString(args);
        String classWithMethodName = getClassMethodName();
        String indent = getIndent();
        methodsStack.addLast(classWithMethodName);
        getCurrentLogger().trace(indent + "> {}{}", classWithMethodName, argString);
    }

    public static void trace(String msg) {
        getCurrentLogger().trace(getIndent() + msg);
    }
    
    private static String getArgString(Object[] args) {
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
        alignStack(actualClassMethodName);
        checkMethodStack(actualClassMethodName);
        getCurrentLogger().trace(getIndent() + "< {} | {}", actualClassMethodName, result);
        return result;
    }

    private static void alignStack(String actualClassMethodName) {
        if (inExceptionState) {
            while (!methodsStack.getLast().equals(actualClassMethodName)) {
                methodsStack.removeLast();
            }
            inExceptionState = false;
        }
    }

    private static void checkMethodStack(String actualClassMethodName) {
        String expectedClassMethodName = methodsStack.removeLast();
        if (!expectedClassMethodName.equals(actualClassMethodName)) {
            String msg = "actualClassMethodName = " + actualClassMethodName + ", expectedClassMethodName = " + expectedClassMethodName;
            throw new WrongGlobalLoggerStack(msg);
        }
    }

    public static void exit() {
        String actualClassMethodName = getClassMethodName();
        alignStack(actualClassMethodName);
        checkMethodStack(actualClassMethodName);
        getCurrentLogger().trace(getIndent() + "< {}", actualClassMethodName);
    }

    public static void deleteRecursively(Path directory) {
        try (Stream<Path> walk = Files.walk(directory)) {
            //noinspection ResultOfMethodCallIgnored
            walk.sorted(Comparator.reverseOrder())
                  .map(Path::toFile)
                  .forEach(File::delete);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
