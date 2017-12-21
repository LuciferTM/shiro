package com.lucifer.shiro2.c3;

import org.apache.shiro.authz.UnauthorizedException;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

/**
 * <p>Title:</p>
 * <p>Description: </p>
 * <p>Company:</p>
 *
 * @author Lucifer
 * @date 2017/12/21
 */


/**
 * 基于角色的访问控制（即隐式角色），这种方式的缺点就是如果很多地方进行了角色判断，但
 * 是有一天不需要了那么就需要修改相应代码把所有相关的地方进行删除；这就是粗粒度造成的问题。
 * */

/**
 * 隐式角色：即直接通过角色来验证用户有没有操作权限，如在应用中CTO、技术总监、开发工程师可以使用打印机，
 * 假设某天不允许开发工程师使用打印机，此时需要从应用中删除相应代码；
 * 再如在应用中CTO、技术总监可以查看用户、查看权限；突然有一天不允许技术总监查看用户、查看权限了，
 * 需要在相关代码中把技术总监角色从判断逻辑中删除掉；即粒度是以角色为单位进行访问控制的，粒度较粗；如果进行修改可能造成多处代码修改。

    显示角色：在程序中通过权限控制谁能访问某个资源，角色聚合一组权限集合；这样假设哪个角色不能访问某个资源，
    只需要从角色代表的权限集合中移除即可；无须修改多处代码；即粒度是以资源/实例为单位的；粒度较细。
 * */

public class RoleTest extends BaseTest {
    @Test
    public void testHasRole() {
        /**
         * Shiro提供了hasRole/hasRole用于判断用户是否拥有某个角色/某些权限；
         * 但是没有提供如hashAnyRole用于判断是否有某些权限中的某一个。
         * */
        login("classpath:c3/shiro-role.ini", "zhang", "123");
        //判断拥有角色：role1
        Assert.assertTrue(subject().hasRole("role1"));
        //判断拥有角色：role1 and role2
        Assert.assertTrue(subject().hasAllRoles(Arrays.asList("role1", "role2")));
        //判断拥有角色：role1 and role2 and !role3
        boolean[] result = subject().hasRoles(Arrays.asList("role1", "role2", "role3"));
        Assert.assertEquals(true, result[0]);
        Assert.assertEquals(true, result[1]);
        Assert.assertEquals(false, result[2]);
    }

    @Test(expected = UnauthorizedException.class)
    public void testCheckRole() {
        /**
         * Shiro提供的checkRole/checkRoles和hasRole/hasAllRoles不同的地方是它在判断为假的情况下
         * 会抛出UnauthorizedException异常。
         * */
        login("classpath:c3/shiro-role.ini", "zhang", "123");
        //断言拥有角色：role1
        subject().checkRole("role1");
        //断言拥有角色：role1 and role3 失败抛出异常
        subject().checkRoles("role1", "role3");
    }
}
