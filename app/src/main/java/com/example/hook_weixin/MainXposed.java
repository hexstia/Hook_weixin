package com.example.hook_weixin;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.LocalServerSocket;
import android.net.LocalSocket;
import android.os.BaseBundle;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class MainXposed implements IXposedHookLoadPackage {

    //微信数据库包名称
    private static final String WECHAT_DATABASE_PACKAGE_NAME = "com.tencent.wcdb.database.SQLiteDatabase";
    //聊天精灵客户端包名称
    private static final String WECHATGENIUS_PACKAGE_NAME = "net.dalu2048.wechatgenius";
    //微信主进程名
    private static final String WECHAT_PROCESS_NAME = "com.tencent.mm";
    public static Object page ;
    public static XC_LoadPackage.LoadPackageParam loadPackageParam1;
    public static  int a = 0;
//    public static String username[] = {"qinhao1994","wxid_n2yfeiwy56m541"};
//    public static String user ="qinhao1994";
    class myThread extends Thread{

        @Override
        public void run() {
            super.run();
            LocalSocket ls = null;
            LocalServerSocket lss = null;
            try {
                 lss = new LocalServerSocket("weixin_socket");
            }catch (IOException e){
                XposedBridge.log("Exception0:"+e.getMessage());
            }
            while(true){
                try {
                    XposedBridge.log("正在等待 socket 连接。。。。");
                     ls = lss.accept();

                    XposedBridge.log(" socket 连接成功。。。。");
                    while (true){
                        byte[] bs = new byte[60];// 1:xxxxx
                        int len = ls.getInputStream().read(bs);

                        XposedBridge.log("正在等待 socket 读取数据。。。。");
                        if(len ==-1){
                            break;
                        }
                        String content = new String(bs,0,len);

                        XposedBridge.log("读取到的内容是。。。。"+content);
                        String[] ss = content.split(":");
                        if(ss[0].equals("1")){

                            XposedBridge.log("USERNAME :"+ss[1]);
                            createChatRoom(page,loadPackageParam1,ss[1]);
                        }else if(ss.equals("0")){

                        }

                    }
                }catch (IOException e){
                    XposedBridge.log("Exception:"+e.getMessage());


                }finally {
                    try {
                        ls.close();
                        ls = null;
                    }catch (IOException e){
                        XposedBridge.log("Exception 1:"+e.getMessage());
                    }
                }
            }
        }
    }
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
//        XposedBridge.log("进入handloadPackage：" + lpparam.processName);

        if (!lpparam.processName.equals(WECHAT_PROCESS_NAME)) {
            return;
        }
        XposedBridge.log("进入微信进程：" + lpparam.processName);
//        if(a ==0) {
//            new myThread().start();
//        a++;
//        }
        //调用 hook数据库插入。
  //HookDumpActivity(lpparam);
//  HookStartActivity(lpparam);
//        HookMessage(lpparam);
//        Hooka(lpparam);

          HookMainActivity(lpparam);
            HookimageSend(lpparam);
    }

    private void HookimageSend(final XC_LoadPackage.LoadPackageParam loadPackageParam) {

        Class<?> classDb = XposedHelpers.findClassIfExists("com.tencent.mm.ui.chatting.SendImgProxyUI", loadPackageParam.classLoader);

        if (classDb == null) {
            XposedBridge.log("com.tencent.mm.ui.chatting.SendImgProxyUI：未找到类" );
            return;
        }

        XposedBridge.log("进入com.tencent.mm.ui.chatting.SendImgProxyUI.a() static 函数：" );
        XposedHelpers.findAndHookMethod(classDb,"a",classDb,Intent.class,new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {}
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
               Intent intent = (Intent)param.args[1];
              boolean booleanExtra = intent.getBooleanExtra("CropImage_Compress_Img", true);
               XposedBridge.log("CropImage_Compress_Img： "+ booleanExtra);

             ArrayList<String> strlist =  intent.getStringArrayListExtra("CropImage_OutputPath_List");
             XposedBridge.log("CropImage_OutputPath_List： "+ strlist);
             int CropImage_limit_Img_Size =intent.getIntExtra("CropImage_limit_Img_Size", 26214400);
                XposedBridge.log("CropImage_limit_Img_Size： "+ CropImage_limit_Img_Size);
                List stringArrayListExtra = intent.getStringArrayListExtra("key_select_video_list");
                XposedBridge.log("key_select_video_list： "+ stringArrayListExtra);
                int intExtra = intent.getIntExtra("from_source", 0);
                int intExtra2 = intent.getIntExtra("CropImage_rotateCount", 0);
                XposedBridge.log("from_source： "+ intExtra);
                XposedBridge.log("CropImage_rotateCount： "+ intExtra2);
                String  GalleryUI_ToUser =  intent.getStringExtra("GalleryUI_ToUser");
                XposedBridge.log("GalleryUI_ToUser： "+ GalleryUI_ToUser);

            }
        });

    }
    private void HookMessage(final XC_LoadPackage.LoadPackageParam loadPackageParam) {

        Class<?> classDb = XposedHelpers.findClassIfExists("com.tencent.mm.modelmulti.h", loadPackageParam.classLoader);

        if (classDb == null) {
            XposedBridge.log("com.tencent.mm.modelmulti.h：未找到类" );
            return;
        }

        XposedBridge.log("进入com.tencent.mm.modelmulti.h 构造器：" );
        XposedHelpers.findAndHookConstructor(classDb,String.class, String.class, int.class, int.class, Object.class,new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {}
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                XposedBridge.log("===================================================");
                      String id = (String)param.args[0];
                      String content = (String)param.args[1];
                      Integer type = (Integer)param.args[2];
                      Integer other = (Integer)param.args[3];
                        XposedBridge.log(" id: "+id);
                        XposedBridge.log(" content: "+content);
                        XposedBridge.log(" type: "+type);
                        XposedBridge.log(" other: "+other);
                XposedBridge.log("===================================================");

            }
        });

    }
    private void Hooka(final XC_LoadPackage.LoadPackageParam loadPackageParam) {
        XposedBridge.log("---------------------------------------------------------------------");
        Class<?> classDb = XposedHelpers.findClassIfExists("com.tencent.mm.ah.p", loadPackageParam.classLoader);

        if (classDb == null) {
            XposedBridge.log("com.tencent.mm.ah.p：未找到类" );
            return;
        }
        Class<?> classI = XposedHelpers.findClassIfExists("com.tencent.mm.ah.p", loadPackageParam.classLoader);
        XposedBridge.log("进入com.tencent.mm.ah.p a()：" );

        XposedHelpers.findAndHookMethod(classDb,
                "a",classI,int.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {}
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                       Object obj = param.args[1];
                        XposedBridge.log("Integer  value :" +obj);
                    }
                });


    }
    private void HookMainActivity(final XC_LoadPackage.LoadPackageParam loadPackageParam) {
        XposedBridge.log("HookMainActivity：" );
        Class<?> classDb = XposedHelpers.findClassIfExists("com.tencent.mm.ui.LauncherUI", loadPackageParam.classLoader);

        if (classDb == null) {
            XposedBridge.log("com.tencent.mm.ui.LauncherUI：未找到类" );
            return;
        }

        XposedBridge.log("进入com.tencent.mm.ui.LauncherUI onResume：" );

        XposedHelpers.findAndHookMethod(classDb,
                "onResume",
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        XposedBridge.log("进入com.tencent.mm.ui.LauncherUI onResume： afterHookedMethod" );
                        Class<?> classDba = XposedHelpers.findClassIfExists("com.tencent.mm.ui.chatting.SendImgProxyUI", loadPackageParam.classLoader);

                        if (classDba == null) {
                            XposedBridge.log("com.tencent.mm.ui.chatting.SendImgProxyUI：未找到类" );
                            return;
                        }

                        Intent intent = new Intent();
                        intent.putExtra("CropImage_Compress_Img",true);
                        ArrayList<String> list = new ArrayList<String>();
                        XposedBridge.log("执行A" );
                        list.add("/storage/emulated/0/Pictures/Screenshots/Screenshot_20190117-111604.png");
                        intent.putStringArrayListExtra("CropImage_OutputPath_List",list);
                        intent.putExtra("CropImage_limit_Img_Size",26214400);
                        ArrayList<String> list1 = new ArrayList<String>();
                        XposedBridge.log("执行B" );
                        intent.putStringArrayListExtra("key_select_video_list",list1);
                        intent.putExtra("from_source",0);
                        intent.putExtra("CropImage_rotateCount",0);
                        intent.putExtra("GalleryUI_ToUser","wxid_n2yfeiwy56m541");
                        XposedBridge.log("执行C" );
                        Class<?>[] claz = {classDba,Intent.class};
                        XposedHelpers.callStaticMethod(classDba,"a",claz,null,intent);
                        XposedBridge.log("执行D" );

                    }
                });


    }
    private void HookDumpActivity(final XC_LoadPackage.LoadPackageParam loadPackageParam) {
        XposedBridge.log("进入HookDumpActivity：" );
        Class<?> classDb = XposedHelpers.findClassIfExists("com.tencent.mm.ui.contact.AddressUI$a", loadPackageParam.classLoader);

        if (classDb == null) {
            XposedBridge.log("com.tencent.mm.ui.contact.AddressUI$a：未找到类" );
            return;
        }

        XposedBridge.log("进入findAndHookMethod cxE()：" );

        XposedHelpers.findAndHookMethod(classDb,
                "cxE",
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        page =  param.thisObject;
                        loadPackageParam1 = loadPackageParam;
//                    createChatRoom(page,loadPackageParam);

                    }
                });


    }
    public static void createChatRoom(Object obj,final XC_LoadPackage.LoadPackageParam loadPackageParam,String username){
        XposedBridge.log("进入createChatRoom");
        Object context = XposedHelpers.callMethod(obj,"getApplicationContext");
        if(context == null){
            XposedBridge.log("方法调用不成功");
            return;
        }
        Class<?> ChattingUI_class =XposedHelpers.findClassIfExists("com.tencent.mm.ui.chatting.ChattingUI",loadPackageParam.classLoader);
        if (ChattingUI_class == null) {
            XposedBridge.log("com.tencent.mm.ui.chatting.ChattingUI：未找到类");
            return;
        }

        if(context instanceof Context){
            Context context1 = (Context) context;
            XposedBridge.log("类型转换");
            Intent i = new Intent(context1,ChattingUI_class);
//            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.putExtra("Chat_Mode",1);
            i.putExtra("Chat_User",username);
            context1.startActivity(i);
        }
    }
    private void HookStartActivity(final XC_LoadPackage.LoadPackageParam loadPackageParam) {
        Class<?> classDb = XposedHelpers.findClassIfExists("android.app.Activity", loadPackageParam.classLoader);

        if (classDb == null) {
            XposedBridge.log("android.app.Activity：未找到类" + WECHAT_DATABASE_PACKAGE_NAME);
            return;
        }



        XposedHelpers.findAndHookMethod(classDb,
                "startActivity",
                Intent.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//flags = 67108864
//className = com.tencent.mm.ui.chatting.ChattingUI
//Chat_Mode = 1 Chat_User = wxid_n2yfeiwy56m541
                        XposedBridge.log("Hook 之前" );
                        Intent intent = (Intent)param.args[0];
                        String action = intent.getAction();
                        int  flags =  intent.getFlags();
                        String className =  intent.getComponent().getClassName();
                        XposedBridge.log("className:" +className );
                        Bundle bundle  = intent.getExtras();
                        Field field = XposedHelpers.findFieldIfExists(Bundle.class,"mMap");
                        if(field ==null){
                            XposedBridge.log("field  ==null" );
                            field = XposedHelpers.findFieldIfExists(BaseBundle.class,"mMap");
                        }

                        field.setAccessible(true);

                        ArrayMap<String,Object> map = (ArrayMap<String,Object> )field.get(bundle);
                        XposedBridge.log("action:" +action );
                        XposedBridge.log("flags:" +flags );

                        XposedBridge.log("map:" +map );


                        XposedBridge.log("Hook 之前" );
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {



                    }
                });
    }

}
