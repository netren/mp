package com.gtphoto.mp;

import org.json.JSONException;
import org.json.JSONObject;

public class ErrorCode {
    public int code;
    String message;

    @Override
    public boolean equals(Object o) {
        return super.equals(o) || (ErrorCode.class.isInstance(o) && ((ErrorCode) o).code == this.code);
    }

    public int rawValue() {
        return code;
    }
    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    ErrorCode(JSONObject jsonObject) {
        this.code = jsonObject.optInt("Code");
        this.message = jsonObject.optString("Message");
    }

    public boolean isSucceeded() {
        return this.code == 1;
    }

    public String toString() {
        return this.message;
    }

    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        try {
            json.put("Code", code);
            json.put("Message", message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    static public boolean isSucceeded(int code) {
        return code == 1;
    }

    static public ErrorCode Success = new ErrorCode(1, "Success");
    static public ErrorCode Failure = new ErrorCode(0, "General Failure");

    static public ErrorCode FailToConnect = new ErrorCode(-1, "FailToConnect");
    static public ErrorCode MethodError = new ErrorCode(-1000, "Method Error");
    static public ErrorCode IllegalArguments = new ErrorCode(-1001, "Illegal Arguments");
    static public ErrorCode SessionError = new ErrorCode(-1002, "Session Error");

    static public ErrorCode ExceedVerificationCodeLimit = new ErrorCode(-10001, "Exceed Verification Code Limit");
    static public ErrorCode ExceedPhoneLimit = new ErrorCode(-10002, "Exceed Phone Limit");
    static public ErrorCode VerificationCodeError = new ErrorCode(-10003, "Verification Code Error");
    static public ErrorCode NotInFamily = new ErrorCode(-10004, "Not In Family");
    static public ErrorCode FailToCreateEvent = new ErrorCode(-10005, "Fail To Create Event");
    static public ErrorCode EventNotFound = new ErrorCode(-10006, "Event Not Found");
    static public ErrorCode FailToFillEvent = new ErrorCode(-10007, "Fail To Fill Event");
    static public ErrorCode ImageNotFound = new ErrorCode(-10008, "Image Not Found");
    static public ErrorCode FailToLeaveAMessage = new ErrorCode(-10009, "Fail To Leave a Message");
    static public ErrorCode MessageNotFound = new ErrorCode(-10010, "Message Not Found");
    static public ErrorCode FailToCreateNotification = new ErrorCode(-10011, "Fail To Create Notification");
    static public ErrorCode UserNotFound = new ErrorCode(-10012, "User Not Found");
    static public ErrorCode BoundTag = new ErrorCode(-10013, "Tag has been bound");
    static public ErrorCode FamilyNotFound = new ErrorCode(-10014, "Family not found");
    static public ErrorCode ExceedBytesLimit = new ErrorCode(-10015, "ExceedBytesLimit");

    static public ErrorCode CanNoFindFile = new ErrorCode(-20004, "can not find File");
    static public ErrorCode CanNotCovertFile = new ErrorCode(-20005, "can not convert file");

    static String LS(String v) {
        return v;
    }


    static public String getString(int code) {

            if (code == Success.code)
                return LS("成功");
            if (code == Failure.code)
                return LS("网络错误");
            if (code == FailToConnect.code)
                return LS("网络连接失败");

            if (code == MethodError.code)
                return LS("没有调用方法");

            if (code == IllegalArguments.code)
                return LS("错误参数"); //Illegal Arguments

            if (code == ExceedVerificationCodeLimit.code)
                return LS("验证码长度错误"); //Exceed Verification Code Limit
            if (code == ExceedPhoneLimit.code)
                return LS("号码格式错误"); //Exceed Phone Limit
            if (code == VerificationCodeError.code)
                return LS("验证码错误"); //Verification Code Error
            if (code == NotInFamily.code)
                return LS("不在家庭"); //Not In Family
            if (code == FailToCreateEvent.code)
                return LS("创建事件失败"); //Fail To Create Event
            if (code == EventNotFound.code)
                return LS("消息已经删除"); //Event Not Found
            if (code == FailToFillEvent.code)
                return LS("添加照片失败"); //Fail To Fill Event
            if (code == ImageNotFound.code)
                return LS("照片不存在"); //Image Not Found
            if (code == FailToLeaveAMessage.code)
                return LS("留言失败"); //Fail To Leave a Message
            if (code == MessageNotFound.code)
                return LS("留言不存在"); //Message Not Found
            if (code == FailToCreateNotification.code)
                return LS("创建通知失败"); //Fail To Create Notification
            if (code == UserNotFound.code)
                return LS("用户找不到"); //User Not Found
            if (code == BoundTag.code)
                return LS("标签不存在");
            //Tag has been bound
            if (code == FamilyNotFound.code)
                return LS("家庭不存在");
            if (code == ExceedBytesLimit.code)
                return LS("空间已满了");

            if (code == CanNoFindFile.code)
                return LS("找不到文件");
            if (code == CanNotCovertFile.code)
                return LS("不能转换文件");

        return LS("未知错误");
    }
}
