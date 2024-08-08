/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.san.voidlauncher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Usuario
 */
public class ProgramProperties {
    private static Properties properties;
    private static final String propFilePath = VoidLauncher.LAUNCHER_FOLDER + "/config.properties";
    
    public static void init() {
        properties = new Properties();
        File propFile = new File(propFilePath);
        try {
            propFile.createNewFile();
            properties.load(new FileInputStream(propFilePath));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ProgramProperties.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ProgramProperties.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void setProp(String key, String value) {
        properties.setProperty(key, value);
    }
    
    public static String getProp(String key) {
        return properties.getProperty(key);
    }
    
    public static void save() {
        try {
            properties.store(new FileOutputStream(propFilePath), null);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ProgramProperties.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ProgramProperties.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
