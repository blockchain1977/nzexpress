package utils;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Created by yplyf on 2015/3/18.
 */
public class SysUtil {

    /* use ssh. */
    public static boolean runSystemCommand(String systemCommand) {
        boolean bResult = true;

        String[] args = new String[] {"/bin/bash", "-c", systemCommand};

        try {
            Process proc = new ProcessBuilder(args).start();

            // Read the output

            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(proc.getInputStream()));

            String line = "";
            while((line = reader.readLine()) != null) {
                System.out.print(line + "\n");
            }

            proc.waitFor();
            if (proc.exitValue() != 0) {
                bResult = false;
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            bResult = false;
        } catch (InterruptedException e) {
            e.printStackTrace();
            bResult = false;
        }

        return bResult;
    }

    public   static   String StringFilter(String   str) throws PatternSyntaxException {
        String regEx="[{}\"']";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }

}
