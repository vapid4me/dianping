package com.hmdp.utils;

import com.hmdp.model.vo.UserVO;

public class UserHolder {
    private static final ThreadLocal<UserVO> tl = new ThreadLocal<>();

    public static void saveUser(UserVO user){
        tl.set(user);
    }

    public static UserVO  getUser(){
        return tl.get();
    }

    public static void removeUser(){
        tl.remove();
    }
}
