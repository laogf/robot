package com.robot.common.utils.ftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.log4j.Logger;
  
/**
 * 
 * FTPClient 上传下载工具
 *
 * @Author 	Laogf
 * @Version 	1.0 
 * @Data 	2018年5月24日
 */
public class FtpUtil {  
	private static Logger logger = Logger.getLogger(FtpUtil.class);
	
    private FTPClient ftpClient;  
    public static final int BINARY_FILE_TYPE = FTP.BINARY_FILE_TYPE;  
    public static final int ASCII_FILE_TYPE = FTP.ASCII_FILE_TYPE;  
      
    /**
     * 利用FtpConfig进行服务器连接
     * @param ftpConfig 参数配置Bean类
     * @throws SocketException
     * @throws IOException
     */
    public void connectServer(FtpConfig ftpConfig) throws SocketException,  
            IOException {  
        String server = ftpConfig.getServer();  
        int port = ftpConfig.getPort();  
        String user = ftpConfig.getUsername();  
        String password = ftpConfig.getPassword();  
        String location = ftpConfig.getLocation();  
        connectServer(server, port, user, password, location);  
    }  
      
    /**
     * 使用详细信息进行服务器连接
     * @param server：服务器地址名称
     * @param port：端口号
     * @param user：用户名
     * @param password：用户密码
     * @param path：转移到FTP服务器目录 
     * @throws SocketException
     * @throws IOException
     */
    public void connectServer(String server, int port, String user,  
            String password, String path) throws SocketException, IOException {  
        ftpClient = new FTPClient();  
        ftpClient.connect(server, port);  
        logger.info("Connected to " + server + ".");  
        //连接成功后的回应码
        logger.info(ftpClient.getReplyCode());  
        ftpClient.login(user, password);  
        if (path!=null&&path.length() != 0) {  
            ftpClient.changeWorkingDirectory(path);  
        }  
    	ftpClient.setBufferSize(1024);//设置上传缓存大小
    	ftpClient.setControlEncoding("UTF-8");//设置编码
    	ftpClient.setFileType(BINARY_FILE_TYPE);//设置文件类型
    }  
    
    /**
     * 设置传输文件类型:FTP.BINARY_FILE_TYPE | FTP.ASCII_FILE_TYPE  
     * 二进制文件或文本文件
     * @param fileType
     * @throws IOException
     */
    public void setFileType(int fileType) throws IOException {  
        ftpClient.setFileType(fileType);  
    }  
  
    /**
     * 关闭连接
     * @throws IOException
     */
    public void closeServer() throws IOException {  
        if (ftpClient!=null&&ftpClient.isConnected()) {  
        	ftpClient.logout();//退出FTP服务器 
            ftpClient.disconnect();//关闭FTP连接 
        }  
    }
    
    /**
     * 转移到FTP服务器工作目录
     * @param path
     * @return
     * @throws IOException
     */
    public boolean changeDirectory(String path) throws IOException {  
        return ftpClient.changeWorkingDirectory(path);  
    }  
    
    /**
     * 在服务器上创建目录
     * @param pathName
     * @return
     * @throws IOException
     */
    public boolean createDirectory(String pathName) throws IOException {  
        return ftpClient.makeDirectory(pathName);  
    }  
    
    /**
     * 在服务器上删除目录
     * @param path
     * @return
     * @throws IOException
     */
    public boolean removeDirectory(String path) throws IOException {  
        return ftpClient.removeDirectory(path);  
    }  
      
    /**
     * 删除所有文件和目录
     * @param path
     * @param isAll true:删除所有文件和目录
     * @return
     * @throws IOException
     */
    public boolean removeDirectory(String path, boolean isAll) throws IOException {  
          
        if (!isAll) {  
            return removeDirectory(path);  
        }  
  
        FTPFile[] ftpFileArr = ftpClient.listFiles(path);  
        if (ftpFileArr == null || ftpFileArr.length == 0) {  
            return removeDirectory(path);  
        }  
        //   
        for (FTPFile ftpFile : ftpFileArr) {  
            String name = ftpFile.getName();  
            if (ftpFile.isDirectory()) {  
            	logger.info("* [sD]Delete subPath ["+path + "/" + name+"]");               
                removeDirectory(path + "/" + name, true);  
            } else if (ftpFile.isFile()) {  
            	logger.info("* [sF]Delete file ["+path + "/" + name+"]");                          
                deleteFile(path + "/" + name);  
            } else if (ftpFile.isSymbolicLink()) {  
  
            } else if (ftpFile.isUnknown()) {  
  
            }  
        }  
        return ftpClient.removeDirectory(path);  
    }  
    
    /**
     * 检查目录在服务器上是否存在 true：存在  false：不存在
     * @param path
     * @return
     * @throws IOException
     */
    public boolean existDirectory(String path) throws IOException {  
        boolean flag = false;  
        FTPFile[] ftpFileArr = ftpClient.listFiles(path);  
        for (FTPFile ftpFile : ftpFileArr) {  
            if (ftpFile.isDirectory()  
                    && ftpFile.getName().equalsIgnoreCase(path)) {  
                flag = true;  
                break;  
            }  
        }  
        return flag;  
    }  
    public boolean isExistsFile(String fileName) {
		try {
			FTPFile[] file = ftpClient.listFiles(fileName);
			return file.length > 0 ? true : false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
   }
    /**
     * 得到文件列表,listFiles返回包含目录和文件，它返回的是一个FTPFile数组
     * listNames()：只包含目录的字符串数组
     * String[] fileNameArr = ftpClient.listNames(path); 
     * @param path:服务器上的文件目录:/DF4
     */
    public List<String> getFileList(String path) throws IOException {  
        FTPFile[] ftpFiles= ftpClient.listFiles(path);  
        List<String> retList = new ArrayList<String>();  
        if (ftpFiles == null || ftpFiles.length == 0) {  
            return retList;  
        }  
        for (FTPFile ftpFile : ftpFiles) {  
            if (ftpFile.isFile()) {  
                retList.add(ftpFile.getName());  
            }  
        }  
        return retList;  
    }  
    
    public List<String> getFileList() throws IOException {  
    	ftpClient.changeWorkingDirectory("/");
        FTPFile[] ftpFiles= ftpClient.listFiles();  
        List<String> retList = new ArrayList<String>();  
        if (ftpFiles == null || ftpFiles.length == 0) {  
            return retList;  
        }  
        for (FTPFile ftpFile : ftpFiles) {  
            if (ftpFile.isFile()) {  
                retList.add(ftpFile.getName());  
                System.out.println(ftpFile.getName());
            }  
        }  
        return retList;  
    } 
    
    public ArrayList<String> arFiles;
    /** 
     * 递归遍历出目录下面所有文件 
     * @param pathName 需要遍历的目录，必须以"/"开始和结束 
     * @throws IOException 
     */  
    public void List(String pathName) throws IOException{ 
        if(pathName.startsWith("/")&&pathName.endsWith("/")){  
            String directory = pathName;  
            //更换目录到当前目录  
            this.ftpClient.changeWorkingDirectory(directory);  
            FTPFile[] files = this.ftpClient.mlistDir();  
            for(FTPFile file:files){  
                if(file.isFile()){  
                    arFiles.add(directory+file.getName());  
                }else if(file.isDirectory()){  
                    List(directory+file.getName()+"/");  
                }  
            }  
        }  
    }  
      
    /** 
     * 递归遍历目录下面指定的文件名 
     * @param pathName 需要遍历的目录，必须以"/"开始和结束 
     * @param ext 文件的扩展名 
     * @throws IOException  
     */  
    public void List(String pathName,String ext) throws IOException{  
        if(pathName.startsWith("/")&&pathName.endsWith("/")){  
            String directory = pathName;  
            //更换目录到当前目录  
            this.ftpClient.changeWorkingDirectory(directory);  
            FTPFile[] files = this.ftpClient.listFiles();  
            for(FTPFile file:files){  
                if(file.isFile()){  
                    if(file.getName().endsWith(ext)){  
                        arFiles.add(directory+file.getName());  
                    }  
                }else if(file.isDirectory()){  
                    List(directory+file.getName()+"/",ext);  
                }  
            }  
        }  
    }  
    
    
  
    /**
     * 删除服务器上的文件
     * @param pathName
     * @return
     * @throws IOException
     */
    public boolean deleteFile(String pathName) throws IOException {  
        return ftpClient.deleteFile(pathName);  
    }  
  
    /**
     * 上传文件到ftp服务器
     * 在进行上传和下载文件的时候，设置文件的类型最好是：
     * ftpUtil.setFileType(FtpUtil.BINARY_FILE_TYPE)
     * localFilePath:本地文件路径和名称
     * remoteFileName:服务器文件名称
     */
    public boolean uploadFile(String localFilePath, String remoteFileName)  
            throws IOException {  
        boolean flag = false;  
        InputStream iStream = null;  
        try {  
            iStream = new FileInputStream(localFilePath);  
            //我们可以使用BufferedInputStream进行封装
            //BufferedInputStream bis=new BufferedInputStream(iStream);
            //flag = ftpClient.storeFile(remoteFileName, bis); 
            flag = ftpClient.storeFile(remoteFileName, iStream);  
        } catch (IOException e) {  
            flag = false;  
            return flag;  
        } finally {  
            if (iStream != null) {  
                iStream.close();  
            }  
        }  
        return flag;  
    }  
  
    /**
     * 上传文件到ftp服务器，上传新的文件名称和原名称一样
     * @param fileName：文件名称
     * @return
     * @throws IOException
     */
    public boolean uploadFile(String fileName) throws IOException {  
        return uploadFile(fileName, fileName);  
    }  
  
    /**
     * 上传文件到ftp服务器
     * @param iStream 输入流
     * @param newName 新文件名称
     * @return
     * @throws IOException
     */
    public boolean uploadFile(InputStream iStream, String newName)  
            throws IOException {  
        boolean flag = false;  
        try {  
            flag = ftpClient.storeFile(newName, iStream);  
        } catch (IOException e) {  
            flag = false;  
            return flag;  
        } finally {  
            if (iStream != null) {  
                iStream.close();  
            }  
        }  
        return flag;  
    }  
  
    /**
     * 从ftp服务器上下载文件到本地
     * @param remoteFileName：ftp服务器上文件名称
     * @param localFileName：本地文件名称
     * @return
     * @throws IOException
     */
    public boolean download(String remoteFileName, String localFileName)  
            throws IOException {  
        boolean flag = false;  
        File outfile = new File(localFileName);  
		OutputStream oStream = null; 
        
        try {  
           oStream = new FileOutputStream(outfile);  
           // 我们可以使用BufferedOutputStream进行封装
         	//BufferedOutputStream bos=new BufferedOutputStream(oStream);
         	ftpClient.enterLocalPassiveMode();
         	//flag = ftpClient.retrieveFile(new String(remoteFileName.getBytes("UTF-8"),"iso-8859-1"), bos); 
            flag = ftpClient.retrieveFile(new String(remoteFileName.getBytes("UTF-8"),"iso-8859-1"), oStream);  
        } catch (IOException e) {  
            flag = false;  
            return flag;  
        } finally {  
            oStream.close();  
        }  
        return flag;  
    }  
      
    /**
     * 从ftp服务器上下载文件到本地
     * @param sourceFileName：服务器资源文件名称
     * @return InputStream 输入流
     * @throws IOException
     */
    public InputStream downFile(String sourceFileName) throws IOException {  
        return ftpClient.retrieveFileStream(sourceFileName);  
    }  
    
}
