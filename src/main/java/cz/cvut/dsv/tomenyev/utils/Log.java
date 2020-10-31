package cz.cvut.dsv.tomenyev.utils;

import cz.cvut.dsv.tomenyev.Main;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Log {

    private static Log instance;

    private FileOutputStream log;

    private Log(String name) {
        try {
            log = new FileOutputStream(name, false);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Log.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void print(String to, String text) {
        switch (to) {
            case "FILE":
                text += System.lineSeparator();
                try {
                    log.write(text.getBytes());
                } catch (IOException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
            case "CONSOLE":
                System.out.print(text);
        }
    }

    public void print(String text) {
            text += System.lineSeparator();
            System.out.print(text);
            try {
                log.write(text.getBytes());
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.print(text);
    }

    public void close() {
        try {
            log.close();
        } catch (IOException ex) {
            Logger.getLogger(Log.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static Log getInstance(String path) {
        if(instance == null)
            instance = new Log(path);
        return instance;
    }

    public static Log getInstance() {
        if(instance == null)
            instance = new Log("");
        return instance;
    }
}

