package qqclient.service;

import qq.common.Message;
import qq.common.MessageType;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.Scanner;

//创建一个和服务器保持通讯的线程
public class ClientConnectServerThread extends Thread{
    private Socket socket;

    public ClientConnectServerThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        while(true){
            try {
                //接收服务端消息
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                Message message = (Message)ois.readObject(); //向下转型
                if(message.getMesType().equals(MessageType.MESSAGE_RET_ONLINE_FRIEND)){ //返回的在线好友列表
                    String[] onlineUsers = message.getContent().split(" ");
                    System.out.println("\n======当前在线用户列表======");
                    for (int i = 0; i < onlineUsers.length; i++) {
                        System.out.println("用户: "+onlineUsers[i]);
                    }
                }else if(message.getMesType().equals(MessageType.MESSAGE_COMM_MES)){ //来自私聊消息
                    System.out.println(message.getSender()+":"+message.getContent());
                }
                else if(message.getMesType().equals(MessageType.MESSAGE_TO_ALL_MES)){
                    System.out.println("\n"+message.getSender()+"发送群发消息:"+message.getContent());
                }else if(message.getMesType().equals(MessageType.MESSAGE_FILE_MES)){
                    System.out.print("\n你收到了"+message.getSender()+"发来的文件,是否接收(y/n?):");
                    Scanner sc = new Scanner(System.in);
                    String choice=sc.next();
                    if(choice.equals("y")){
                        System.out.println("接收路径(默认路径)");
                        String type = message.getDest();
                        String dest="D:\\user\\javaProject\\QQClient\\src\\"+message.getGetter()+"的电脑\\"+"test."+type;
                        message.setDest(dest);
                        System.out.println("收到"+message.getGetter()+"发来的文件,存到了"+message.getDest());
                        byte[] fileBytes = message.getFileBytes();
                        FileOutputStream fileOutputStream = new FileOutputStream(message.getDest());
                        fileOutputStream.write(fileBytes);
                    }else{
                        System.out.println("拒绝接收文件");
                    }

                }
                else{
                    System.out.println("其他信息");
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public Socket getSocket() {
        return socket;
    }
}
