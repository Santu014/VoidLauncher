/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.san.voidlauncher;

import com.san.voidlauncher.frames.InitFrame;
import com.formdev.flatlaf.FlatDarkLaf;
import com.san.voidlauncher.frames.DownloadFrame;
import com.san.voidlauncher.frames.ModpackPanel;
import com.san.voidlauncher.frames.UnzipFrame;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import org.json.JSONArray;
import org.json.JSONObject;
import sk.tomsik68.mclauncher.impl.common.Platform;

/**
 *
 * @author Usuario
 */
public class VoidLauncher {
    public static final String LAUNCHER_FOLDER = "C:/Users/" + System.getProperty("user.name") + "/.sl";
    public static final String MODPACKS_FOLDER = LAUNCHER_FOLDER + "/modpacks";
    
    public static final String MANIFEST_URL = "https://raw.githubusercontent.com/Santu014/VoidLauncher/master/app-info/vlmanifest.json";
    
    public static Map<String, String> modpacksUrls = new HashMap<>();
    
    public static MainWindow mainWindow;
    public static String selectedModpack;
    
    public static void setSelectedModpack(String val) {
        selectedModpack = val;
        mainWindow.setModpack(val);
    }
    
    static {
        modpacksUrls.put("city-craft", "https://github.com/Santu014/VoidLauncher/raw/master/app-info/city-craft.zip");
    }
    
    public static void updateModpacksFrame(File launcherManifestFile) {
        mainWindow.updateModpackFrame();
        String launcherManifest = null;
        try {
            byte[] bytes = Files.readAllBytes(launcherManifestFile.toPath());
            launcherManifest = new String(bytes, Charset.defaultCharset());
        } catch (IOException ex) {
            Logger.getLogger(VoidLauncher.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        JSONObject lmjson = new JSONObject(launcherManifest);
        JSONArray lmModpacks = lmjson.getJSONArray("modpacks");
        for (int i = 0; i < lmModpacks.length(); i++) {
            JSONObject lmModpack = lmModpacks.getJSONObject(i);
            boolean isInstalled;
            String modpackPath = MODPACKS_FOLDER + "/" + lmModpack.getString("id");
            File modpackInstalled = new File(modpackPath + "/.installed");
            isInstalled = modpackInstalled.exists();
            ModpackPanel modpackPanel = new ModpackPanel(lmModpack.getString("id"), lmModpack.getString("name"), lmModpack.getString("version"), isInstalled);
            File modpackFolder = new File(modpackPath);
            modpackFolder.mkdir();
            mainWindow.addModpackPanel(modpackPanel);
        }
    }
    
    public static void downloadModpack(String modpackId) throws MalformedURLException, IOException, InterruptedException, ZipException {
        mainWindow.setModpackFrameVisibility(false);
        
        DownloadFrame downloadFrame = new DownloadFrame(modpackId);
        downloadFrame.setLocationRelativeTo(null);
        downloadFrame.setVisible(true);
        
        String link = modpacksUrls.get(modpackId);
        if (link == null)
            return;
        URL modpackUrl = new URL(link);
        HttpURLConnection httpConnection = (HttpURLConnection) modpackUrl.openConnection();
        long completeFileSize = httpConnection.getContentLength();
        BufferedInputStream in = new BufferedInputStream(httpConnection.getInputStream());
        FileOutputStream fos = new FileOutputStream(MODPACKS_FOLDER + "/" + modpackId + ".zip");
        byte[] byteBuffer = new byte[1024];
        int bytesRead;
        long downloadedFileSize = 0;
        while ((bytesRead = in.read(byteBuffer, 0, 1024)) != -1) {
            downloadedFileSize += bytesRead;
            /*int progress = (int) ((((double) downloadedFileSize) / ((double) completeFileSize)) * 100d);
            downloadFrame.setProgressValue(progress);*/
            fos.write(byteBuffer, 0, bytesRead);
        }
        fos.close();
        in.close();
        
        downloadFrame.setVisible(false);
        downloadFrame.dispose();
        
        UnzipFrame unzipFrame = new UnzipFrame(modpackId);
        unzipFrame.setLocationRelativeTo(null);
        unzipFrame.setVisible(true);
        
        //unzip(modpackZipFile.getPath(), MODPACKS_FOLDER + "/" + modpackId);
        ZipFile zip = new ZipFile(MODPACKS_FOLDER + "/" + modpackId + ".zip");
        zip.extractAll(MODPACKS_FOLDER);
        
        unzipFrame.setVisible(false);
        unzipFrame.dispose();
        
        File zipFile = new File(MODPACKS_FOLDER + "/" + modpackId + ".zip");
        zipFile.delete();
        
        File installedFile = new File(MODPACKS_FOLDER + "/" + modpackId + "/.installed");
        installedFile.createNewFile();
        
        updateModpacksFrame(new File(LAUNCHER_FOLDER + "/vlmanifest.json"));
    }

    public static void main(String[] args) throws Exception {
        ProgramProperties.init();
        
        try {
            LookAndFeel laf = new FlatDarkLaf();
            UIManager.setLookAndFeel(laf);
        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(VoidLauncher.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        File lFolder = new File(LAUNCHER_FOLDER);
        lFolder.mkdirs();
        
        File modpacksFolder = new File(MODPACKS_FOLDER);
        modpacksFolder.mkdirs();
        
        /*ZipFile zip = new ZipFile(MODPACKS_FOLDER + "/city-craft.zip");
        ZipParameters defaultZipParameters = new ZipParameters();
        defaultZipParameters.setCompressionLevel(9);
        zip.addFolder(MODPACKS_FOLDER + "/city-craft", defaultZipParameters); //*/
        
        InitFrame initFrame = new InitFrame();
        initFrame.setLocationRelativeTo(null);
        initFrame.setVisible(true);
        
        File launcherManifestFile = new File(LAUNCHER_FOLDER + "/vlmanifest.json");
        if (launcherManifestFile.exists()) {
            launcherManifestFile.delete();
        }
        
        try {
            launcherManifestFile.createNewFile();
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
            e.printStackTrace();
            System.exit(0);
        }
        
        String launcherManifest = null;
        try {
            byte[] bytes = Files.readAllBytes(launcherManifestFile.toPath());
            launcherManifest = new String(bytes, Charset.defaultCharset());
        } catch (IOException ex) {
            Logger.getLogger(VoidLauncher.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        System.out.println();
        
        mainWindow = new MainWindow();
        mainWindow.setLocationRelativeTo(null);
        
        JSONObject lmjson = new JSONObject(launcherManifest);
        JSONArray lmModpacks = lmjson.getJSONArray("modpacks");
        for (int i = 0; i < lmModpacks.length(); i++) {
            JSONObject lmModpack = lmModpacks.getJSONObject(i);
            boolean isInstalled;
            String modpackPath = MODPACKS_FOLDER + "/" + lmModpack.getString("id");
            File modpackInstalled = new File(modpackPath + "/.installed");
            isInstalled = modpackInstalled.exists();
            ModpackPanel modpackPanel = new ModpackPanel(lmModpack.getString("id"), lmModpack.getString("name"), lmModpack.getString("version"), isInstalled);
            File modpackFolder = new File(modpackPath);
            modpackFolder.mkdir();
            mainWindow.addModpackPanel(modpackPanel);
        }
        
        initFrame.setVisible(false);
        initFrame.dispose();
        
        setSelectedModpack(ProgramProperties.getProp("lastSelectedModpack"));
        
        mainWindow.setVisible(true);
        
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            ProgramProperties.setProp("lastSelectedModpack", selectedModpack);
            ProgramProperties.save();
        }));
    }
}
