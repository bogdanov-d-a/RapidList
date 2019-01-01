package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

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

    public static void copyFile(String sourcePath, String targetPath) throws Exception {
        FileInputStream sourceStream = new FileInputStream(new File(sourcePath));
        try {
            OutputStream targetStream = new FileOutputStream(targetPath);
            try {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = sourceStream.read(buffer)) > 0) {
                    targetStream.write(buffer, 0, length);
                }
            } finally {
                targetStream.flush();
                targetStream.close();
            }
        } finally {
            sourceStream.close();
        }
    }
}
