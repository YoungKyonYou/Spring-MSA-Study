package com.example.userservice.security;


import com.example.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.servlet.Filter;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity

public class WebSecurity extends WebSecurityConfigurerAdapter {
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserService userService;
    private final Environment env;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
//        http.authorizeRequests().antMatchers("/users/**").permitAll();
        http.authorizeRequests().antMatchers("/actuator/**").permitAll();
        http.authorizeRequests().antMatchers("/**")
                //user-service에 접속하면 localhost:port 이렇게 되면서 오류가 뜨는데
                //localhost말고 아이피로 바꿔줘야함!!! 안 그러면 오류 남!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            .hasIpAddress("127.0.0.1") //<- 내 아이피임 나중에 http://172.30.1.46:11861/welcome 이런식으로 접속하기
                //아래 필터를 통과 시킨 데이터에 한에서만 권한을 부여하고 작업을 진행하겠다는 것
            .and().addFilter(getAuthenticationFilter());

        http.headers().frameOptions().disable();
    }

    private AuthenticationFilter getAuthenticationFilter() throws Exception{
        AuthenticationFilter authenticationFilter=new AuthenticationFilter(authenticationManager(), env, userService);
//        authenticationFilter.setAuthenticationManager(authenticationManager());

        return authenticationFilter;
    }

    //select pwd from users where email=?
    //db_pwd(encrypted)==input_pwd(encrypted)
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //여기서는 인증을 한다 그리고 위의 configure는 권한에 대한 것이다. 인증이 되야 권한이 허가가 될 것이다]

        //select하는 부분은 이 userDetailsService가 해줄 것이다.
        //그리고 passwordEncoder를 통해서 input_pwd된 것을 encrpyted해준다.
        auth.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder);

    }
}
