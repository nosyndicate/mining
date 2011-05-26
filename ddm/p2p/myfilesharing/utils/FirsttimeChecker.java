package ddm.p2p.myfilesharing.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

import javax.swing.JFileChooser;
import javax.swing.JTextArea;


public class FirsttimeChecker extends JFileChooser{
    
    private static boolean firstTime = true;
    public JTextArea log;
    public static String SharedPath="unknown";
    
    public FirsttimeChecker(JTextArea txt){
        this.log = txt;
    }
    
    public void searchForConfigFile(){
        File configFile = new File("config.properties");
        if(configFile.exists()&& configFile.isFile()){
            readingConfigFile(configFile);            
        }
        else{
           createConfigFile();//建立配置文件
        }    
    }
    
    public void readingConfigFile(File file){
    	String path=null;   
        PropertiesUtil cf = new PropertiesUtil("config.properties");
        path = cf.getValue("sharedpath");
        log.append("[+]共享文件夹是: " + path + "\n");
        File temp = new File(path);
        if(temp.exists()){
        	SharedPath = temp.getAbsolutePath();       
        }else{
            createShareFolder(temp);
        }          
    }
    
    public void createShareFolder(File pathname){
        if(pathname.mkdir()){
            log.append("[+]成功建立共享文件夹.\n");
            SharedPath = pathname.getAbsolutePath();
        }
        
    }
    
    public void createConfigFile(){
        File sysconfig = new File("config.properties");
        //Default path
        String Path="C:\\DDM";
        
        log.append("**** 请选择共享文件夹 ****\n");
        
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int retVal =chooser.showOpenDialog(this);
        if(retVal == JFileChooser.APPROVE_OPTION){
           log.append("[+]共享路径是：" + chooser.getSelectedFile().getAbsolutePath()+"\n");
           Path = chooser.getSelectedFile().getAbsolutePath();
        }        
        SharedPath = Path;
        try {            
            boolean success = sysconfig.createNewFile();
            if(success){
                log.append("[+]config.properties 创建成功\n");
                log.append("[+]写数据到配置文件中...\n");
                PropertiesUtil rc = new PropertiesUtil();
                rc.setValue("sharedpath", Path);
                rc.setValue("username", AddressUtil.getHostName());
                rc.saveFile("config.properties", "sysconfig");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        readingConfigFile(sysconfig);
    }
    
    public String getSharedPath(){
        return SharedPath;        
    }
    
    public boolean isFirstTime() //Search for initialization file, if not found assumes that it is
    {                           // the first time that program is being executed and will create 
                                //Initialization File
        File configFile = new File("config.properties");
        if(configFile.exists() && configFile.isFile()){
            firstTime = false;
        }
        else{
            firstTime =true;
        }
        return firstTime;
    }
}
