package com.ruyiruyi.rylibrary.utils;

import com.baidu.trace.LBSTraceClient;
import com.netease.nimlib.sdk.avsignalling.builder.InviteParamBuilder;
import com.netease.nimlib.sdk.avsignalling.event.InvitedEvent;
import com.ruyiruyi.rylibrary.model.IMGridUsers;

import java.util.ArrayList;
import java.util.List;

public class CommonData {
    public static double lat = 0;
    public static double lng = 0;
    public static double lat_an = 0;
    public static double lng_an = 0;
    public static double lat_84 = 0;
    public static double lng_84 = 0;
    public static long time = 0;
    public static String icNumber;
    public static String turnId;
    public static String aqsToken;
    public static int offset = 0 ;
    public static double lastLat = 0;
    public static double lastLng = 0;
    public static boolean hasSign = false;
    public static boolean hasSensor = false;//是否有传感器
    public static boolean hasMove = true;//是否有移动
    public static double sensorValue = 0;//传感器参数
    public static boolean hasGet = false;
    public static int walkDistance = 0;


    //网易云信
    public static String wyyAccId;
    public static String wyyToken;
    public static String audioRoomName;
    public static String audioRoomId;
    public static String xdChannelName;
    public static String xdChannelId;
    public static boolean isCalling;
    public static String testId;
    public static List<IMGridUsers.Users> personList = new ArrayList<>();
    public static int personListSize = 0;
    public static InvitedEvent invitedEvent;
    public static InviteParamBuilder inviteOtherParam;
    public static String inviteOtherId;
    public static List<InviteParamBuilder> inviteOtherList = new ArrayList<>();
    public static boolean ringing = false;

    //百度鹰眼轨迹
    public static LBSTraceClient mTraceClient;
}
