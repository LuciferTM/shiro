package com.lucifer.shiro2.c5;

import com.lucifer.shiro2.BaseTest;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.converters.AbstractConverter;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.realm.jdbc.JdbcRealm;
import org.junit.Test;

/**
 * Shiro提供了PasswordService及CredentialsMatcher用于提供加密密码及验证密码服务。
 * */

public class PasswordTest extends BaseTest {

    @Test
    public void testPasswordServiceWithMyRealm() {
        login("classpath:c5/shiro-passwordservice.ini", "wu", "123");
    }

    @Test
    public void testPasswordServiceWithJdbcRealm() {
        login("classpath:c5/shiro-jdbc-passwordservice.ini", "wu", "123");
    }

    /**
     * Shiro提供了CredentialsMatcher的散列实现HashedCredentialsMatcher，和之前的PasswordMatcher不同的是，
     * 它只用于密码验证，且可以提供自己的盐，而不是随机生成盐，且生成密码散列值的算法需要自己写，因为能提供自己的盐。
     *
     * 如果要写用户模块，需要在新增用户/重置密码时使用如上算法保存密码，
     * 将生成的密码及salt2存入数据库（因为我们的散列算法是：md5(md5(密码+username+salt2))）
     * */
    @Test
    public void testGeneratePassword() {
        String algorithmName = "md5";
        String username = "liu";
        String password = "123";
        String salt1 = username;
        String salt2 = new SecureRandomNumberGenerator().nextBytes().toHex();
        int hashIterations = 2;

        SimpleHash hash = new SimpleHash(algorithmName, password, salt1 + salt2, hashIterations);
        String encodedPassword = hash.toHex();
        System.out.println(salt2);
        System.out.println(encodedPassword);
    }

    @Test
    public void testHashedCredentialsMatcherWithMyRealm2() {
        //使用testGeneratePassword生成的散列密码
        login("classpath:c5/shiro-hashedCredentialsMatcher.ini", "liu", "123");
    }

    @Test
    public void testHashedCredentialsMatcherWithJdbcRealm() {
        /**
         * 注意Shiro默认使用了apache commons BeanUtils，默认是不进行Enum类型转型的，此时需要自己注册一个Enum转换器
         * */
        BeanUtilsBean.getInstance().getConvertUtils().register(new EnumConverter(), JdbcRealm.SaltStyle.class);

        //使用testGeneratePassword生成的散列密码
        login("classpath:c5/shiro-jdbc-hashedCredentialsMatcher.ini", "liu", "123");
    }


    private class EnumConverter extends AbstractConverter {
        @Override
        protected String convertToString(final Object value) throws Throwable {
            return ((Enum) value).name();
        }
        @Override
        protected Object convertToType(final Class type, final Object value) throws Throwable {
            return Enum.valueOf(type, value.toString());
        }

        @Override
        protected Class getDefaultType() {
            return null;
        }

    }

    @Test(expected = ExcessiveAttemptsException.class)
    public void testRetryLimitHashedCredentialsMatcherWithMyRealm() {
        for(int i = 1; i <= 5; i++) {
            try {
                login("classpath:c5/shiro-retryLimitHashedCredentialsMatcher.ini", "liu", "234");
            } catch (Exception e) {/*ignore*/}
        }
        login("classpath:c5/shiro-retryLimitHashedCredentialsMatcher.ini", "liu", "234");
    }
}
