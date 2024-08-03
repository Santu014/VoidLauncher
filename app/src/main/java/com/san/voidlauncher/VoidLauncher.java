/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.san.voidlauncher;

import com.san.voidlauncher.frames.InitFrame;
import com.formdev.flatlaf.FlatDarkLaf;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author Usuario
 */
public class VoidLauncher {
    public static final String LAUNCHER_FOLDER = "C:/Users/" + System.getProperty("user.name") + "/.sl";
    public static final String MANIFEST_URL = "https://drive.google.com/uc?export=download&id=11iOi-NV23m8f189GZaiaNCfG3iByNQCF";
    
    
    //public static final String city_craft_download_url = "https://drive.google.com/uc?export=download&id=16hhBlFGo-5kKfydF0Qhm8WAC3OaxwHbh";
    public static final String city_craft_download_url = "https://drive.usercontent.google.com/u/0/uc?id=16hhBlFGo-5kKfydF0Qhm8WAC3OaxwHbh&export=download";

    public static void main(String[] args) {
        try {
            DriveTest.main(null);
        } catch (IOException | GeneralSecurityException ex) {
            Logger.getLogger(VoidLauncher.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            LookAndFeel laf = new FlatDarkLaf();
            UIManager.setLookAndFeel(laf);
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(VoidLauncher.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        File lFolder = new File(LAUNCHER_FOLDER);
        lFolder.mkdirs();
        
        File modpacksFolder = new File(LAUNCHER_FOLDER + "/modpacks");
        modpacksFolder.mkdirs();
        
        InitFrame initFrame = new InitFrame();
        initFrame.setLocationRelativeTo(null);
        initFrame.setVisible(true);
        
        try {
            URL manifesturl = new URL(MANIFEST_URL);
            HttpURLConnection httpConnection = (HttpURLConnection) manifesturl.openConnection();
            long completeFileSize = httpConnection.getContentLength();
            
            BufferedInputStream in = new BufferedInputStream(httpConnection.getInputStream());
            FileOutputStream fos = new FileOutputStream(LAUNCHER_FOLDER + "/vlmanifest.json");
            byte[] byteBuffer = new byte[1024];
            int bytesRead;
            long downloadedFileSize = 0;
            while ((bytesRead = in.read(byteBuffer, 0, 1024)) != -1) {
                downloadedFileSize += bytesRead;
                int progress = (int) ((((double) downloadedFileSize) / ((double) completeFileSize)) * 100d);
                initFrame.setProgressValue(progress);
                fos.write(byteBuffer, 0, bytesRead);
            }
            
        } catch(IOException e) {
        }
        
        initFrame.setVisible(false);
        initFrame.dispose();
        
        MainWindow mainWindow = new MainWindow();
        mainWindow.setLocationRelativeTo(null);
        mainWindow.setVisible(true);
    }
}
