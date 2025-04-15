package com.ruyiruyi.rylibrary.utils;

import com.netease.nimlib.sdk.avsignalling.builder.InviteParamBuilder;
import com.netease.nimlib.sdk.avsignalling.event.InvitedEvent;
import com.ruyiruyi.rylibrary.model.IMGridUsers;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CommonData {
    public static double lat = 0;
    public static double lng = 0;
    public static String icNumber;

    //大华乐橙
    public static String SECRET = "2e4c74dd0786457eaee3d8d1f7a961";
    public static String APPID = "lccb9f39c3fa3343a2";
    public static String daHuaTokenStr = "";
    public static String daHuaId = "";
    public static JSONObject device;
    public static String subAccount;
    public static String subOpenid;
    public static String subToken;
    public static String subId;
    public static JSONObject deviceSub;

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
    public static List<InviteParamBuilder> inviteOtherList = new ArrayList<>();
    public static boolean ringing = false;
}
