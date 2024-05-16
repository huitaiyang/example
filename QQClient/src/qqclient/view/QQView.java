package qqclient.view;
import qqclient.service.FileClientService;
import qqclient.service.UserClientService;

import java.util.Scanner;
//显示菜单栏
public class QQView {
    private boolean flag=true; //是否显示菜单
    private UserClientService userClientService = new UserClientService();
    private FileClientService fileClientService = new FileClientService();

    public void mainMenu(){
        Scanner sc = new Scanner(System.in);
        while(flag){
            System.out.println("======欢迎使用网络通讯系统======");
            System.out.println("\t\t 1.登录");
            System.out.println("\t\t 9.退出");
            System.out.print("请输入你的选择:");
            String choice = sc.next();
            switch(choice){
                case "1":
                    System.out.println("<<<登录操作>>>");

                    System.out.print("请输入账号: ");
                    String userId = sc.next();
                    System.out.print("请输入密码: ");
                    String passwd = sc.next();

                    //定义一个qqclient.service,用于实现用户验证和注册等功能
                    if(userClientService.checkUser(userId,passwd)){
                        System.out.println("~登录成功~");
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        while(flag){
                            System.out.println("\n=======网络通讯系统二级菜单(欢迎:"+userId+")======");
                            System.out.println("\t\t 1.显示在线用户列表");
                            System.out.println("\t\t 2.群发消息");
                            System.out.println("\t\t 3.私聊消息");
                            System.out.println("\t\t 4.发送文件");
                            System.out.println("\t\t 9.退出系统");
                            System.out.println("请输入你的选择:");
                            choice = sc.next();
                            String content;
                            switch(choice){
                                case "1":
                                    userClientService.onlineFriendList();
                                    break;
                                case "2":
                                    System.out.print("请输入群发消息:");
                                     content= sc.next();
                                    userClientService.sendMessageToAll(content);
                                    break;
                                case "3":
                                    boolean loop=true;
                                    System.out.print("请输入私聊对象:");
                                    String target = sc.next();
                                    System.out.println("开始聊天吧~(输入9退出私聊)");
                                    System.out.println("======对方:【"+target+"】======");
                                    while(loop){
                                        content = sc.next();
                                        if(content.equals("9")){
                                            loop=false;
                                        }else{
                                            System.out.println("你:"+content);
                                            userClientService.privateChat(target,content);
                                        }

                                    }

                                    break;
                                case "4":
                                    String getterId,src,dest;
                                    System.out.println("请输入发送对象:");
                                    getterId = sc.next();
                                    System.out.println("源文件路径:");
                                    src = sc.next();
                                    String[] arr = src.split("\\."); //  \\. -> .
                                    dest = arr[1];
                                    fileClientService.sendFileToOne(userId,getterId,src,dest);
                                    break;
                                case "5":
                                case "9":
                                    flag = false;
                                    userClientService.closeClient();
                                    System.exit(0); //结束进程
                                    break;
                                default:
                                    System.out.println("input error");
                            }
                        }

                    }else{
                        System.out.println("~登录失败~");
                    }
                    break;
                case "9":
                    System.out.println("<<<退出操作>>>");
                    flag = false;
                    break;
                default:
                    System.out.println("input error");
            }
        }
    }
}
