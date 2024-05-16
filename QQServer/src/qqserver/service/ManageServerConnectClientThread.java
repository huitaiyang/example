package qqserver.service;

import qq.common.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

//通过集合管理服务端与客户端保持连接的线程
public class ManageServerConnectClientThread {
    //存放在线用户与服务端线程
    public static ConcurrentHashMap<String,ServerConnectClientThread> hm = new ConcurrentHashMap<>();

    //存放离线消息
    public static  ConcurrentHashMap<String, ArrayList<Message>> offline = new ConcurrentHashMap<>();

    public static void addServerConnectClientThread(String userId,ServerConnectClientThread serverConnectClientThread){
        hm.put(userId,serverConnectClientThread);
    }
    public static ServerConnectClientThread getServerConnectClientThread(String userId){ //通过userId获取线程
        return hm.get(userId);
    }
    public static String getOnlineUserList(){ //获取在线用户
        String onlineUserList="";
        Iterator<String> iterator = ManageServerConnectClientThread.hm.keySet().iterator();
        while(iterator.hasNext()){
            onlineUserList+=iterator.next()+" ";
        }
        return onlineUserList;
    }
}
