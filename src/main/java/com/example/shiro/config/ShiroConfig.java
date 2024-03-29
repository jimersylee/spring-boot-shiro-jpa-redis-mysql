package com.example.shiro.config;

import com.google.common.collect.Maps;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;


@Configuration
public class ShiroConfig {


    /**
     * 自定义Realm
     * @return
     */
    @Bean(name = "customRealm")
    public CustomRealm customRealm() {
        CustomRealm customRealm = new CustomRealm();
        customRealm.setCredentialsMatcher(hashedCredentialsMatcher()); // 设置认证加密算法
        customRealm.setCachingEnabled(true);
        return customRealm;
    }

    /**
     * 定义Security manager
     * @param customRealm
     * @return
     */
    @Bean(name = "securityManager")
    public DefaultWebSecurityManager defaultWebSecurityManager(CustomRealm customRealm) {
        DefaultWebSecurityManager  securityManager = new DefaultWebSecurityManager ();
        securityManager.setRealm(customRealm);
        securityManager.setSessionManager(customerWebSessionManager()); // 可不指定，Shiro会用默认Session manager
        securityManager.setCacheManager(redisCacheManagers());  //可不指定，Shiro会用默认CacheManager
//        securityManager.setSessionManager(defaultWebSessionManager());
        return securityManager;
    }

    /**
     * 定义session管理器
     * @return
     */
    @Bean(name = "sessionManager")
    public DefaultWebSessionManager defaultWebSessionManager(){
        DefaultWebSessionManager defaultWebSessionManager = new DefaultWebSessionManager();
        defaultWebSessionManager.setSessionDAO(redisSessionDao());
        return defaultWebSessionManager;
    }

    /**
     * Shiro生命周期管理
     * @return
     */
    @Bean(name = "lifecycleBeanPostProcessor")
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    /**
     * 定义Shiro filter，Shiro核心内容
     * @param securityManager
     * @return
     */
    @Bean(name = "shiroFilter")
    public ShiroFilterFactoryBean shiroFilterFactoryBean(DefaultWebSecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);

        Map<String, String> filterChainMap = Maps.newHashMap();
        filterChainMap.put("/user/register","anon");
        filterChainMap.put("/user/login", "anon");
        filterChainMap.put("/user", "anon");
        filterChainMap.put("/user/logout", "anon");
        filterChainMap.put("/**", "authc");

        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainMap);
        return shiroFilterFactoryBean;
    }

    @Bean
    public static DefaultAdvisorAutoProxyCreator getDefaultAdvisorAutoProxyCreator(){
        DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator=new DefaultAdvisorAutoProxyCreator();
        /**
         * setUsePrefix(false)用于解决一个奇怪的bug。在引入spring aop的情况下。
         * 在@Controller注解的类的方法中加入@RequiresRole等shiro注解，会导致该方法无法映射请求，导致返回404。
         * 加入这项配置能解决这个bug
         */
        defaultAdvisorAutoProxyCreator.setUsePrefix(true);
        return defaultAdvisorAutoProxyCreator;
    }

    /**
     * 定义加密匹配算法
     * @return
     */
    @Bean(name="credentialsMatcher")
    public HashedCredentialsMatcher hashedCredentialsMatcher(){
        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();

        hashedCredentialsMatcher.setHashAlgorithmName("md5");//散列算法:这里使用MD5算法;
        hashedCredentialsMatcher.setHashIterations(1);//散列的次数，比如散列两次，相当于 md5(md5(""));
        //storedCredentialsHexEncoded默认是true，此时用的是密码加密用的是Hex编码；false时用Base64编码
        hashedCredentialsMatcher.setStoredCredentialsHexEncoded(true);

        return hashedCredentialsMatcher;
    }

    /**
     * 自定义RedisSessionDao用来管理Session在Redis中的CRUD
     * @return
     */
    @Bean(name = "redisSessionDao")
    public RedisSessionDao redisSessionDao(){
        return new RedisSessionDao();
    }

    /**
     * 自定义SessionManager,应用自定义SessionDao
     * @return
     */
    @Bean(name = "customerSessionManager")
    public CustomerWebSessionManager customerWebSessionManager(){
        CustomerWebSessionManager customerWebSessionManager = new CustomerWebSessionManager();
        customerWebSessionManager.setSessionDAO(redisSessionDao());
        return customerWebSessionManager;
    }

    /**
     * 定义Redis缓存管理器
     * @return
     */
    @Bean(name = "redisCacheManagers")
    public RedisCacheManager redisCacheManagers(){
        return new RedisCacheManager();
    }


}
