/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Campbell Suter
 */
public class IOSys {

    private IOSys() {
    }

    public static IOSys getInstance() {
        return IOSysHolder.INSTANCE;
    }

    private static class IOSysHolder {

        private static final IOSys INSTANCE = new IOSys();
    }

    public void saveToFile(String fname, String data) {
        try {
            File fi = new File("/sdcard/" + fname);
            if (!fi.getParentFile().exists()) {
                fi.getParentFile().mkdirs();
            }
            BufferedWriter writer = new BufferedWriter(new FileWriter(fi));
            writer.append(data);
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(IOSys.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String readLineFromFile(String fname) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("/sdcard/" + fname));
            String out = reader.readLine();
            reader.close();
            return out;
        } catch (FileNotFoundException ex) {
            return null;
        } catch (IOException ex) {
            Logger.getLogger(IOSys.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
