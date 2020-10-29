package cz.cvut.dsv.tomenyev.utils;

import cz.cvut.dsv.tomenyev.Main;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Log {

    private FileOutputStream log;

    /**
     * Creates new logger.
     * @param name name of the log
     */
    public Log(String name) {
        try {
            log = new FileOutputStream(name, false);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Log.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /**
     *  Writes message to log output.
     * @param text string to be written to log
     */
    public void write(String text) {
        text += System.lineSeparator();
        try {
            log.write(text.getBytes());
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Ends logging and closes output.
     */
    public void close() {
        try {
            log.close();
        } catch (IOException ex) {
            Logger.getLogger(Log.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}

