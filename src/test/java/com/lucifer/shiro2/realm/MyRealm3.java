package com.lucifer.shiro2.realm;

import org.apache.shiro.authc.*;
import org.apache.shiro.realm.Realm;

/**
 * Created by lucifer on 21/12/2017.
 */
public class MyRealm3 implements Realm{
    @Override
    public String getName() {
        return "myrealm3";
    }

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof UsernamePasswordToken;
    }

    @Override
    public AuthenticationInfo getAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {

        String username = (String)token.getPrincipal();
        String password = new String((char[])token.getCredentials());
        if(!"zhang".equals(username)) {
            throw new UnknownAccountException();
        }
        if(!"123".equals(password)) {
            throw new IncorrectCredentialsException();
        }
        //与reaml1，2比较，返回的认证信息的区别
        return new SimpleAuthenticationInfo(username + "@163.com", password, getName());
    }
}
