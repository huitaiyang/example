package qqserver.service;

import qq.common.Message;
import qq.common.MessageType;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.Scanner;

//服务器发送新闻给在线用户
public class sendNewsToAllService extends Thread{
    @Override
    public void run() {
        while(true){
            Scanner sc = new Scanner(System.in);
            System.out.print("输入新闻内容[exit->退出]:");
            String news = sc.next();
            if(news.equals("exit")){
                return ;
            }
            Message message = new Message();
            message.setSender("服务器");
            message.setMesType(MessageType.MESSAGE_COMM_MES);
            message.setContent(news);
            message.setSendTime(new Date().toString());

            Iterator<String> iterator = ManageServerConnectClientThread.hm.keySet().iterator();
            while(iterator.hasNext()){
                String userId = iterator.next();
                try {
                    ObjectOutputStream oos = new ObjectOutputStream(ManageServerConnectClientThread.getServerConnectClientThread
                            (userId).getSocket().getOutputStream());
                    oos.writeObject(message);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            System.out.println("\n服务器发送了一条内容为:"+news+" 的新闻" );
        }
    }
}
