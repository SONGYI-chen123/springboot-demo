package com.example.demo.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    //基于内存的用户存储
//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception{
//        auth.inMemoryAuthentication()
//                .withUser("buzz")
//                .password("{noop}infinity")
//                .authorities("ROLE_USER")
//                .and()
//                .withUser("woody")
//                .password("{noop}bullseye")
//                .authorities("ROLE_USER");
//    }

    //基于JDBC的用户存储
//    @Autowired
//    DataSource dataSource;
//
//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception{
//        auth.jdbcAuthentication()
//                .dataSource(dataSource)
//        .usersByUsernameQuery(
//                "select username,password, enabled from Users where username=?")
//        .authoritiesByUsernameQuery(
//                "select username,authority from UserAuthorities where username=?")
//        .passwordEncoder(new StandardPasswordEncoder("53cr3t"));
//    }

    //LDAP作为后端的用户存储
//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.ldapAuthentication()
//                .userSearchBase("ou=people")
//                .userSearchFilter("(uid={0})")
//                .groupSearchBase("ou=groups")
//                .groupSearchFilter("member={0}")
//                .passwordCompare()
//                .passwordAttribute("passcode")
//                .passwordEncoder(new StandardPasswordEncoder("53cr3t"))
//                .and()
//                .contextSource()
//                .root("dc=tacocloud,dc=com")
//                .ldif("classpath:users.ldif");
//    }

    //自定义用户认证
    @Qualifier("userRepositoryUserDetailService")//指示我们要注入哪个bean
    @Autowired
    private UserDetailsService userDetailsService;

    @Bean//声明PasswordEncoder bean 对于encoder的任何调用都会被拦截
    public PasswordEncoder encoder(){
        return new StandardPasswordEncoder("53cr3t");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
        .passwordEncoder(encoder());
    }

    //保护请求，保护一些请求只能被认证用户请求
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/design","/orders")
                .hasRole("ROLE_USER")//具备"ROLE_USER"权限的用户才能访问
                .antMatchers("/","/**")
                .permitAll();//允许所以用户访问
        // 声明在前面的安全规则比声明在后面的规则有更高的优先级，交换上面两条安全规则，则对"/design""/orders"的声明不生效
    }
}
