package cz.cvut.dsv.tomenyev.utils;

import cz.cvut.dsv.tomenyev.Main;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Log {

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

    public void print(String text) {
            text += System.lineSeparator();
            try {
                log.write(text.getBytes());
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
    }

    public void close() {
        try {
            log.close();
        } catch (IOException ex) {
            ex.printStackTrace();
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

    public void printLog() {
        try {
            Files.readAllLines(Paths.get(path))
                    .forEach(System.out::println);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

