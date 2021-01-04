package com.example.demo.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.StandardPasswordEncoder;

import javax.sql.DataSource;

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
    @Autowired
    DataSource dataSource;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception{
        auth.jdbcAuthentication()
                .dataSource(dataSource)
        .usersByUsernameQuery(
                "select username,password, enabled from Users where username=?")
        .authoritiesByUsernameQuery(
                "select username,authority from UserAuthorities where username=?")
        .passwordEncoder(new StandardPasswordEncoder("53cr3t"));
    }
}
