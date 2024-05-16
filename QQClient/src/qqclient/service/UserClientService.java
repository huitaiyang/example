package qqclient.service;
import qq.common.Message;
import qq.common.MessageType;
import qq.common.User;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

//该类完成用户登录验证和用户注册等功能
public class UserClientService {
    private User u=new User(); //创建User对象，全局
    private boolean flag=false;

    private Message message= new Message();;
    public boolean checkUser(String userId,String passwd){
        u.setUserId(userId);
        u.setPasswd(passwd);
        //设置发送人
        message.setSender(u.getUserId());
        //将接收到的User对象发送给服务器验证
        try {
            //将User对象发送到服务器验证
            Socket socket = new Socket(InetAddress.getByName("127.0.0.1"),9999);
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(u); // 发送User对象

            //读取服务器返回消息
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            Message ms = (Message)ois.readObject();
            //解读ms
            if(ms.getMesType().equals(MessageType.MESSAGE_LOGIN_SUCCEED)){ //验证成功
                System.out.println("验证成功");
                //创建一个和服务器保持通讯的线程 -> 创建一个类 ClientConnectServerThread
                ClientConnectServerThread clientConnectServerThread = new ClientConnectServerThread(socket);
                //启动客户端线程
                clientConnectServerThread.start();
                //为方便对客户端线程的管理，将线程存入集合中 ->ManageClientConnectServerThread
                ManageClientThreads.addClientThread(userId,clientConnectServerThread);
                flag=true;
            }else{ //验证失败
                System.out.println("验证失败");
                socket.close();
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return flag;
    }

    public void onlineFriendList(){ //显示在线好友
        message.setMesType(MessageType.MESSAGE_GET_ONLINE_FRIEND);
        try {
            ObjectOutputStream oos = new ObjectOutputStream(ManageClientThreads.getManageClientThread(u.getUserId())
                    .getSocket().getOutputStream());
            oos.writeObject(message);
            System.out.println("请求服务端显示在线好友信息");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void closeClient(){ //客户端正常退出
        message.setMesType(MessageType.MESSAGE_CLIENT_EXIT);
        message.setSender(u.getUserId());
        try {
            ObjectOutputStream oos = new ObjectOutputStream(ManageClientThreads.getManageClientThread(u.getUserId())
                    .getSocket().getOutputStream());
            oos.writeObject(message);
            System.out.println("请求服务端关闭连接");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void privateChat(String getter,String content){ //实现私聊功能
        message.setGetter(getter);
        message.setContent(content);
        message.setMesType(MessageType.MESSAGE_COMM_MES);
        try {
            ObjectOutputStream oos = new ObjectOutputStream(ManageClientThreads.getManageClientThread(u.getUserId())
                    .getSocket().getOutputStream());
            oos.writeObject(message);
            oos.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public void sendMessageToAll(String content){ //实现群发功能
        message.setContent(content);
        message.setMesType(MessageType.MESSAGE_TO_ALL_MES);
        try {
            ObjectOutputStream oos = new ObjectOutputStream(ManageClientThreads.getManageClientThread(u.getUserId())
                    .getSocket().getOutputStream());
            oos.writeObject(message);
            oos.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
