package qqserver.service;

import qq.common.Message;
import qq.common.MessageType;
import qq.common.User;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

//QQ服务端
public class QQServer {
    private ServerSocket serverSocket = null;

    //验证用户是否有效
    public boolean checkUser(String userId,String passwd){
        User user = CurrentUserDB.validUsers.get(userId);
        if(user==null){
            return false;
        }
        return user.getPasswd().equals(passwd);
    }

    //初始化用户信息
    public QQServer() {
        try {
            //9999端口监听
            serverSocket = new ServerSocket(9999);
            //启动推送新闻线程
            new sendNewsToAllService().start();
            //启动发送离线消息线程
            new sendOfflineMessage().start();
            while(true){
                System.out.println("服务端开始监听......");
                Socket socket = serverSocket.accept();
                //获取客户端发送来的用户信息，进行验证
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                User u = (User)ois.readObject();
                //回复
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                Message message = new Message();
                if(checkUser(u.getUserId(), u.getPasswd())){ //登录成功
                    System.out.println("账号为"+u.getUserId()+" 密码为"+u.getPasswd()+"的用户 验证成功");
                    message.setMesType(MessageType.MESSAGE_LOGIN_SUCCEED);
                    oos.writeObject(message);
                    //创建一个服务端线程与客户端保持联系，该线程需持有socket对象 -> ServerConnectClientThread
                    ServerConnectClientThread serverConnectClientThread = new ServerConnectClientThread(socket, u.getUserId());
                    //启动线程 !!!
                    serverConnectClientThread.start();
                    //创建一个和客户端通讯的线程管理集合
                    ManageServerConnectClientThread.addServerConnectClientThread(u.getUserId(),serverConnectClientThread);

                }else{ //登录失败
                    message.setMesType(MessageType.MESSAGE_LOGIN_FAIL);
                    System.out.println("账号为"+u.getUserId()+" 密码为"+u.getPasswd()+"的用户 验证失败");
                    oos.writeObject(message);
                    //关闭连接
                    socket.close();
                }

            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }finally{
            //如果服务器退出，应关闭ServerSocket
            try {
                serverSocket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
