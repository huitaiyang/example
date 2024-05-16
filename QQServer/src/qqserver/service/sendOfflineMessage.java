package qqserver.service;

import qq.common.Message;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

public class sendOfflineMessage extends Thread {
    @Override
    public void run() {
        while(true){
            //System.out.println("接收离线消息......");
            Set<String> MessageSet=ManageServerConnectClientThread.offline.keySet();
            Set<String> OnlineUserset = ManageServerConnectClientThread.hm.keySet();
            for (String onlineUserId : OnlineUserset) {
                for (String getter : MessageSet) {
                    if(getter.equals(onlineUserId)) {
                        ArrayList<Message> list = ManageServerConnectClientThread.offline.get(getter);
                        for (int i = 0; i < list.size(); i++) {
                            try {
                                ObjectOutputStream oos = new ObjectOutputStream(ManageServerConnectClientThread.getServerConnectClientThread(getter).getSocket().getOutputStream());
                                oos.writeObject(list.get(i));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        ManageServerConnectClientThread.offline.remove(getter);
                    }
                }
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
