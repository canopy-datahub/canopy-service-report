package ex.org.project.reportservice.util;

import java.util.Set;

public class HarmonizationCalculatorUtil {

    public static String normalizeCapitalizedProgramName(String programName) {
        return switch (programName) {
            case "RAD" -> "rad";
            case "UP" -> "UP";
            case "TECH" -> "Tech";
            case "DHT" -> "DHT";
            default -> "INVALID PROGRAM NAME";
        };
    }

    public static String concatenateSet(Set<String> set, String delimiter) {
        if (set == null || set.isEmpty()) {
            return null;
        }
        return String.join(delimiter, set);
    }
}
