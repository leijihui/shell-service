package org.shelltest.service.services;

import ch.ethz.ssh2.SCPClient;
import org.shelltest.service.exception.MyException;
import org.shelltest.service.utils.Constant;
import org.shelltest.service.utils.ShellRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;

@Service
public class UploadService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Value("${config.shellpath}")
    String BASE_PATH;

    /**
     * 上传脚本至指定服务器的home目录下.
     * @param shellRunner 建立了与远程桌面的连接
     * @param scriptName 要上传的脚本名称
     * @param localPath 待上传脚本在本地的位置（jar内路径）
     * @return 脚本是否上传成功（可能没有内存空间无法写入）
     * */
    public boolean uploadScript (ShellRunner shellRunner,String scriptName, String localPath) throws MyException {
        //拼接完整的脚本目录
        String shell = localPath==null?BASE_PATH +"/"+ scriptName:BASE_PATH +"/"+ localPath +"/"+ scriptName;
        logger.debug("上传shell脚本: "+ shell);
        ClassPathResource resource = new ClassPathResource(shell);
        StringBuilder stringBuilder = new StringBuilder();
        try {
            if (resource == null || resource.getInputStream() == null) {
                throw new MyException(Constant.ResultCode.INTERNAL_ERROR,"Script cannot be found:"+shell);
            }
            BufferedReader bufferedReader =
                    new BufferedReader(new InputStreamReader(resource.getInputStream()));
            String sbReadLine;
            while ((sbReadLine=bufferedReader.readLine())!=null){
                stringBuilder.append(sbReadLine+" \n");
            }
            File scriptFile = new File(shell);
            if (!scriptFile.exists()) {
                new File(scriptFile.getParent()).mkdirs();
                scriptFile.createNewFile();
            }
            FileOutputStream writer = new FileOutputStream(scriptFile,false);
            writer.write(stringBuilder.toString().getBytes("utf-8"));
            writer.close();
        } catch (IOException e){
            throw new MyException(Constant.ResultCode.INTERNAL_ERROR,"读取/写入脚本错误："+e.getMessage());
        }
        uploadFile(shellRunner, shell, "");
        logger.info("脚本上传完成");
        return true;
    }

    public void uploadFiles (ShellRunner shellRunner, String localPath, String remotePath, String suffix) throws MyException {
        //SCPClient
        String[] uploadNameList = null;
        try {
            File filePath = new File(localPath);
            if (filePath.canRead() && filePath.isDirectory()) {
                File[] fileList = filePath.listFiles();
                LinkedList<String> targetFiles = new LinkedList<>();
                for (int i = 0; i < fileList.length; i++) {
                    if (fileList[i].isFile() && fileList[i].toString().endsWith(suffix)) {
                        targetFiles.add(fileList[i].getPath());
                    }
                }
                if (targetFiles.size() == 0) {
                    throw new MyException(Constant.ResultCode.NOT_FOUND, "本地目录下未发现可用的文件:"+localPath+suffix);
                }
                uploadNameList = new String[targetFiles.size()];
                targetFiles.toArray(uploadNameList);
            } else {
                throw new MyException(Constant.ResultCode.NOT_FOUND, "不可操作的本地目录:"+localPath);
            }
            SCPClient scpClient = shellRunner.getConn().createSCPClient();
            scpClient.put(uploadNameList,remotePath);
            logger.info("文件上传完成");
        } catch (IOException e) {
            throw new MyException(Constant.ResultCode.INTERNAL_ERROR, "无法创建scp连接:"+e.getMessage());
        }
    }
    public void uploadFile (ShellRunner shellRunner, String localFileWithPath, String remotePath) throws MyException {
        try {
            File fileName = new File(localFileWithPath);
            if (!fileName.exists() || !fileName.isFile() || !fileName.canRead()) {
                throw new FileNotFoundException("找不到指定文件: "+fileName);
            }
            SCPClient scpClient = shellRunner.getConn().createSCPClient();
            scpClient.put(fileName.getAbsolutePath(),remotePath);
        } catch (IOException e) {
            throw new MyException(Constant.ResultCode.LOGIN_FAILED, "scp上传失败:"+e.getMessage());
        }
    }
}