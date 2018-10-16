package utils;

public class Utils {
    public static String ParseLine(String str) {
        StringBuilder result = new StringBuilder();
        boolean parsingCmd = false;

        for (int i = 0; i < str.length(); ++i) {
            char c = str.charAt(i);

            if (parsingCmd) {
                if (c == '#') {
                    result.append('#');
                    parsingCmd = false;
                } else if (c == 'n') {
                    result.append('\n');
                    parsingCmd = false;
                } else {
                    result.append("error");
                    parsingCmd = false;
                }
            } else {
                if (c == '#') {
                    parsingCmd = true;
                } else {
                    result.append(c);
                }
            }
        }

        return result.toString();
    }
}
