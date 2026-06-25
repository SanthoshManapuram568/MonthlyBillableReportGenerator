//package com.srm.billable.startup;
//
//import java.io.IOException;
//import java.nio.file.Paths;
//
//public class StartupManager {
//
//    private static final String REGISTRY_KEY =
//            "HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Run";
//
//    private static final String APP_NAME = "BillableReportHelper";
//
//    public static void registerIfNeeded() {
//
//        try {
//
//            if (isAlreadyRegistered()) {
//                return;
//            }
//
//            String exePath = getExecutablePath();
//
//            Process process = Runtime.getRuntime().exec(new String[]{
//                    "reg",
//                    "add",
//                    REGISTRY_KEY,
//                    "/v",
//                    APP_NAME,
//                    "/t",
//                    "REG_SZ",
//                    "/d",
//                    "\"" + exePath + "\"",
//                    "/f"
//            });
//
//            process.waitFor();
//
//            System.out.println("Startup registration completed.");
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private static boolean isAlreadyRegistered() throws IOException, InterruptedException {
//
//        Process process = Runtime.getRuntime().exec(new String[]{
//                "reg",
//                "query",
//                REGISTRY_KEY,
//                "/v",
//                APP_NAME
//        });
//
//        int exitCode = process.waitFor();
//
//        return exitCode == 0;
//    }
//
//    /**
//     * Returns current executable location.
//     * Works after packaging.
//     */
//    private static String getExecutablePath() {
//
//        try {
//
//            return Paths.get(
//                            StartupManager.class
//                                    .getProtectionDomain()
//                                    .getCodeSource()
//                                    .getLocation()
//                                    .toURI())
//                    .toString()
//                    .replace("file:/", "")
//                    .replace("/", "\\");
//
//        } catch (Exception e) {
//
//            throw new RuntimeException(e);
//
//        }
//
//    }
//
//}