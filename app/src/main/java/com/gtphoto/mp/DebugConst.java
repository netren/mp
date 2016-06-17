package com.gtphoto.mp;

/**
 * Created by kennymac on 15/10/2.
 */
//这个目录用来放一些debug版下边的配置.用来测试
public class DebugConst {

    static public boolean isRelease = BuildConfig.DEBUG == false;
    static public boolean showEventTitleId = isRelease ? false : false;
    static public boolean useSameEventLayout = isRelease ? false : false;
    static public boolean startLoadEventOnLine = isRelease ? false : false; //true方便测试,不加载本地数据库
    static public boolean signInTest = isRelease ? false : false; //true方便测试,不加载本地数据库
    static public int verifiCodeTime = isRelease ? 30 : 4;
    static public boolean createFamilyTest = isRelease ? false : false;
    static public boolean debugInvite = isRelease ? false : true;
    static public int linkerIndex = isRelease ? 0 : 0;
    static public boolean showNetIndex = isRelease ? false : true;
    static public int selectMovieDuration = isRelease ? 60 : 60;
}
