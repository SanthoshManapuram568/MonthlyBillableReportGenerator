package com.srm.billable.outlook;

import java.net.URISyntaxException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Locates the JACOB native DLL and configures jacob.dll.path before COM usage.
 */
public final class JacobNativeLoader {

    private static final String JACOB_VERSION = "1.21";
    private static String resolvedDllPath;

    private JacobNativeLoader() {
    }

    public static String expectedDllName() {
        String arch = is64Bit() ? "x64" : "x86";
        return "jacob-" + JACOB_VERSION + "-" + arch + ".dll";
    }

    public static void configureJacobDllPath() {
        String configured = System.getProperty("jacob.dll.path");
        if (configured != null && !configured.isBlank()) {
            resolvedDllPath = configured;
            return;
        }

        List<Path> candidates = buildCandidatePaths();
        for (Path candidate : candidates) {
            if (candidate != null && Files.exists(candidate)) {
                String absolute = candidate.toAbsolutePath().toString();
                System.setProperty("jacob.dll.path", absolute);
                resolvedDllPath = absolute;
                return;
            }
        }
    }

    public static String resolvedDllPath() {
        return resolvedDllPath;
    }

    public static String searchDiagnostics() {
        List<Path> candidates = buildCandidatePaths();
        StringBuilder sb = new StringBuilder();
        for (Path candidate : candidates) {
            if (candidate == null) {
                continue;
            }
            sb.append(candidate.toAbsolutePath())
              .append(" => ")
              .append(Files.exists(candidate) ? "FOUND" : "MISSING")
              .append("; ");
        }
        return sb.toString();
    }

    private static List<Path> buildCandidatePaths() {
        String preferred = expectedDllName();
        String arch = is64Bit() ? "x64" : "x86";

        String[] names = new String[]{
                preferred,
                "jacob.dll",
                "jacob-" + JACOB_VERSION + "-" + arch + ".dll",
                "jacob-" + JACOB_VERSION + (is64Bit() ? "-x86.dll" : "-x64.dll")
        };

        Set<Path> candidateSet = new LinkedHashSet<>();

        Path cwd = Paths.get(System.getProperty("user.dir"));
        Path appDir = resolveAppDirectory();

        for (String name : names) {
            candidateSet.add(cwd.resolve(name));
            candidateSet.add(cwd.resolve("target").resolve(name));
            if (appDir != null) {
                candidateSet.add(appDir.resolve(name));
                candidateSet.add(appDir.resolve("target").resolve(name));
            }
        }

        // Also inspect classpath entries for sibling DLLs.
        String classPath = System.getProperty("java.class.path", "");
        if (!classPath.isBlank()) {
            String[] entries = classPath.split(System.getProperty("path.separator"));
            for (String entry : entries) {
                try {
                    Path p = Paths.get(entry);
                    Path dir = Files.isDirectory(p) ? p : p.getParent();
                    if (dir == null) {
                        continue;
                    }
                    for (String name : names) {
                        candidateSet.add(dir.resolve(name));
                    }
                } catch (Exception ignored) {
                    // Ignore malformed classpath entries.
                }
            }
        }

        return new ArrayList<>(candidateSet);
    }

    private static boolean is64Bit() {
        String arch = System.getProperty("os.arch", "").toLowerCase();
        return arch.contains("64");
    }

    private static Path resolveAppDirectory() {
        try {
            Path codeSource = Paths.get(JacobNativeLoader.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            if (Files.isDirectory(codeSource)) {
                return codeSource;
            }
            return codeSource.getParent();
        } catch (URISyntaxException | NullPointerException ex) {
            return null;
        }
    }
}

