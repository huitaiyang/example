package qqserver.service;

import qq.common.Message;
import qq.common.MessageType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class ServerConnectClientThread extends Thread{
    private Socket socket;
    private String userId;
    private Message message = new Message();

    public ServerConnectClientThread(Socket socket,String userId) {
        this.socket = socket;
        this.userId = userId;
    }

    public Socket getSocket(){
        return socket;
    }

    @Override
    public void run() {
        while(true){
            try {
                System.out.println("服务端与客户端保持通信......");
                //接收客户端发送的消息
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                Message ms = (Message) ois.readObject();
                if(ms.getMesType().equals(MessageType.MESSAGE_GET_ONLINE_FRIEND)){ //返回在线用户列表
                    System.out.println(ms.getSender()+"要在线用户列表信息");
                    String onlineUsers = ManageServerConnectClientThread.getOnlineUserList();
                    message =new Message();
                    message.setMesType(MessageType.MESSAGE_RET_ONLINE_FRIEND);
                    message.setContent(onlineUsers);
                    message.setGetter(ms.getSender());

                    //返回给客户端
                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(message);

                }else if(ms.getMesType().equals(MessageType.MESSAGE_CLIENT_EXIT)){ //断开与客户端线程连接
                    System.out.println(ms.getSender()+"退出连接");
                    ManageServerConnectClientThread.hm.remove(userId);
                    socket.close();
                    break;
                }else if(ms.getMesType().equals(MessageType.MESSAGE_COMM_MES)){ //私聊
                    System.out.println(ms.getSender()+" 要私聊 "+ms.getGetter());
                    //判断接收消息人是否在线
                    ServerConnectClientThread thread = ManageServerConnectClientThread.getServerConnectClientThread(ms.getGetter());
                    if(thread==null){
                        System.out.println(ms.getGetter()+"处于离线状态");
                        if(ManageServerConnectClientThread.offline.get(ms.getGetter())==null){ //离线用户的第一条消息则创建一个list
                            ArrayList<Message> list = new ArrayList<>();
                            list.add(ms);
                            ManageServerConnectClientThread.offline.put(ms.getGetter(),list);
                            System.out.println(ms.getGetter()+"存储离线信息->1");
                        }else{ //否则加入
                            ArrayList<Message> list=ManageServerConnectClientThread.offline.get(ms.getGetter());
                            list.add(ms);
                            System.out.println(ms.getGetter()+"存储离线信息->"+list.size());
                        }
                        continue;
                    }
                    ObjectOutputStream oos = new ObjectOutputStream(thread.getSocket().getOutputStream());
                    oos.writeObject(ms);
                }else if(ms.getMesType().equals(MessageType.MESSAGE_TO_ALL_MES)){ //群发消息
                    Iterator<String> iterator = CurrentUserDB.validUsers.keySet().iterator();
                    System.out.println(ms.getSender()+" 要 群发消息 ");
                    while(iterator.hasNext()){
                        String getter=iterator.next();
                        System.out.println(getter);
                        if(!userId.equals(getter)){
                            ServerConnectClientThread thread = ManageServerConnectClientThread.getServerConnectClientThread(getter);
                            //判断接收消息人是否在线
                            if(thread==null){
                                System.out.println(getter+"处于离线状态");
                                if(ManageServerConnectClientThread.offline.get(getter)==null){ //离线用户的第一条消息则创建一个list
                                    ArrayList<Message> list = new ArrayList<>();
                                    list.add(ms);
                                    ManageServerConnectClientThread.offline.put(getter,list);
                                    System.out.println(getter+"存储离线信息->1");
                                }else{ //否则加入
                                    ArrayList<Message> list=ManageServerConnectClientThread.offline.get(getter);
                                    list.add(ms);
                                    System.out.println(getter+"存储离线信息->"+list.size());
                                }
                                continue;
                            }

                            ObjectOutputStream oos = new ObjectOutputStream(thread.getSocket().getOutputStream());
                            oos.writeObject(ms);
                        }
                    }
                }else if(ms.getMesType().equals(MessageType.MESSAGE_FILE_MES)){ //单发文件
                    System.out.println(ms.getSender()+" 要发送文件给"+ms.getGetter());
                    ServerConnectClientThread thread = ManageServerConnectClientThread.getServerConnectClientThread(ms.getGetter());
                    if(thread==null){
                        System.out.println(ms.getGetter()+"处于离线状态");
                        if(ManageServerConnectClientThread.offline.get(ms.getGetter())==null){ //离线用户的第一条消息则创建一个list
                            ArrayList<Message> list = new ArrayList<>();
                            list.add(ms);
                            ManageServerConnectClientThread.offline.put(ms.getGetter(),list);
                            System.out.println(ms.getGetter()+"存储离线信息->1");
                        }else{ //否则加入
                            ArrayList<Message> list=ManageServerConnectClientThread.offline.get(ms.getGetter());
                            list.add(ms);
                            System.out.println(ms.getGetter()+"存储离线信息->"+list.size());
                        }
                        continue;
                    }
                    ObjectOutputStream oos = new ObjectOutputStream(thread.getSocket().getOutputStream());
                    oos.writeObject(ms);
                }
                else{
                    System.out.println("其他行为");
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
