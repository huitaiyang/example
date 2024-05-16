package qqclient.service;

import java.util.HashMap;

//关系客户端线程
public class ManageClientThreads {
    private static HashMap<String,ClientConnectServerThread> hm=new HashMap<>();

    public static void addClientThread(String userId, ClientConnectServerThread c){
        hm.put(userId,c);
    }

    public static ClientConnectServerThread getManageClientThread(String userId){
        return hm.get(userId);
    }

}
