package qqclient.service;

import qq.common.Message;
import qq.common.MessageType;

import java.io.*;

//实现文件传输功能
public class FileClientService {
    public void sendFileToOne(String senderId,String getterId,String src,String dest){ //向个人发送文件
        Message message = new Message();
        message.setMesType(MessageType.MESSAGE_FILE_MES);
        message.setSender(senderId);
        message.setGetter(getterId);
        message.setSrc(src);
        message.setDest(dest);

        //需要将文件读取
        FileInputStream fileInputStream = null;
        File file = new File(src);
        if(!file.exists()){
            System.out.println("文件不存在");
            return;
        }
        byte[] fileBytes = new byte[(int) file.length()];
        try {
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(fileBytes); //将src文件读到字节数组
        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally{
            if(fileInputStream != null){
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        message.setFileBytes(fileBytes);

        System.out.println("\n"+senderId+"向"+getterId+"发送文件");
        try {
            ObjectOutputStream oos = new ObjectOutputStream(ManageClientThreads.getManageClientThread(senderId).getSocket().getOutputStream());
            oos.writeObject(message);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
