package com.xiya.accounts.comm;

import org.springframework.util.DigestUtils;

public class PasswordUtil {
    final static String md5key = "jiaotu";

    //生成加密口令
    public static String genPassword(String id, String pwd) {
        String str = pwd + md5key + id;
        str = DigestUtils.md5DigestAsHex(str.getBytes());
        return str;
    }

    //检验密码强度
    public static boolean iCheckPassword(String username, String password) {
        //数字
        final String REG_NUMBER = ".*\\d+.*";
        //小写字母
        final String REG_UPPERCASE = ".*[A-Z]+.*";
        //大写字母
        final String REG_LOWERCASE = ".*[a-z]+.*";
        //特殊符号(~!@#$%^&*()_+|<>,.?/:;'[]{}\)
        final String REG_SYMBOL = ".*[~!@#$%^&*()+=_|<>,.?/:;'\\[\\]{}\"]+.*";

        if (password == null) return false;
        if (password.length() < 8) return false;

        int i = 0;
        if (password.matches(REG_NUMBER)) i++;
        if (password.matches(REG_LOWERCASE))i++;
        if (password.matches(REG_UPPERCASE)) i++;
        if (password.matches(REG_SYMBOL)) i++;

        boolean contains = password.contains(username);
        return i >= 4 && !contains;
    }
}
