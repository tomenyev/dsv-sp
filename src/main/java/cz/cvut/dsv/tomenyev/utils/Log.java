package cz.cvut.dsv.tomenyev.utils;

import cz.cvut.dsv.tomenyev.Main;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Log {

    public enum To {
        FILE, CONSOLE, BOTH
    }

    /**
     * SINGLETON
     */
    private static Log instance;

    private FileOutputStream log;

    private String path;

    private Log(String path) {
        try {
            this.path = path;
            this.log = new FileOutputStream(path, false);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    public void print(To to, String text) {
        String start ="[" + new SimpleDateFormat("mm:ss.SSS").format(new Date()) + "] ";
        String end = ".";
        switch (to) {
            case FILE:
                text = start + text + end;
                text += System.lineSeparator();
                try {
                    log.write(text.getBytes());
                } catch (IOException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            case CONSOLE:
                System.out.println(start + text + end);
                break;
            case BOTH:
                text = start + text + end;
                text += System.lineSeparator();
                try {
                    log.write(text.getBytes());
                } catch (IOException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.out.print(text);
                break;
        }
    }

    public void close() {
        try {
            log.close();
        } catch (IOException ignored) {
        }
    }

    @SuppressWarnings("UnusedReturnValue")
    public static Log setInstance(String path) {
        instance = new Log(path);
        return instance;
    }

    public static Log getInstance() {
        if(instance == null)
            instance = new Log("");
        return instance;
    }

    /**
     * print content from log file to the console.
     */
    public void printLog() {
        try {
            Files.readAllLines(Paths.get(path))
                    .forEach(System.out::println);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}



