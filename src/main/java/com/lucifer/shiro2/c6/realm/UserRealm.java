package com.lucifer.shiro2.c6.realm;

import com.lucifer.shiro2.c6.service.UserService;
import com.lucifer.shiro2.c6.service.UserServiceImpl;
import com.lucifer.shiro2.c6.entity.User;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;

/**
 1、UserRealm父类AuthorizingRealm将获取Subject相关信息分成两步：
 获取身份验证信息（doGetAuthenticationInfo）及授权信息（doGetAuthorizationInfo）；

 3、doGetAuthorizationInfo获取授权信息：PrincipalCollection是一个身份集合，因为我们现在就一个Realm，所以直接调用getPrimaryPrincipal得到之前传入的用户名即可；然后根据用户名调用UserService接口获取角色及权限信息。
 */
public class UserRealm extends AuthorizingRealm {

    private UserService userService = new UserServiceImpl();

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        String username = (String)principals.getPrimaryPrincipal();

        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        authorizationInfo.setRoles(userService.findRoles(username));
        authorizationInfo.setStringPermissions(userService.findPermissions(username));

        return authorizationInfo;
    }
    /**
     2、doGetAuthenticationInfo获取身份验证相关信息：首先根据传入的用户名获取User信息；
     然后如果user为空，那么抛出没找到帐号异常UnknownAccountException；
     如果user找到但锁定了抛出锁定异常LockedAccountException；


     最后生成AuthenticationInfo信息，交给间接父类AuthenticatingRealm使用CredentialsMatcher进行判断密码是否匹配，
     如果不匹配将抛出密码错误异常IncorrectCredentialsException；

     另外如果密码重试此处太多将抛出超出重试次数异常ExcessiveAttemptsException；
     在组装SimpleAuthenticationInfo信息时，需要传入：
     身份信息（用户名）、凭据（密文密码）、盐（username+salt），

     CredentialsMatcher使用盐加密传入的明文密码和此处的密文密码进行匹配。
     * */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {

        String username = (String)token.getPrincipal();

        User user = userService.findByUsername(username);

        if(user == null) {
            throw new UnknownAccountException();//没找到帐号
        }

        if(Boolean.TRUE.equals(user.getLocked())) {
            throw new LockedAccountException(); //帐号锁定
        }

        //交给AuthenticatingRealm使用CredentialsMatcher进行密码匹配，如果觉得人家的不好可以自定义实现
        //这个就是shiro自己提供的验证方式   参数为四个：身份/凭证/盐/realmname  内部会使用盐来做hash加密匹配
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
                user.getUsername(), //用户名
                user.getPassword(), //密码
                ByteSource.Util.bytes(user.getCredentialsSalt()),//salt=username+salt
                getName()  //realm name
        );
        return authenticationInfo;
    }
}
