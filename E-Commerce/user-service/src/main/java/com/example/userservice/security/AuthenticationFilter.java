package com.example.userservice.security;

import com.example.userservice.dto.UserDto;
import com.example.userservice.service.UserService;
import com.example.userservice.vo.RequestLogin;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

@Slf4j

public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final Environment env;
    private final UserService userService;

    public AuthenticationFilter(AuthenticationManager authenticationManager, Environment env, UserService userService) {
        super(authenticationManager);
        this.env = env;
        this.userService = userService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        try{
            //HTTP Reqeust 중 message-body로 넘어온 parameter 확인을 위해서는 getInputStream()이나 getReader()를 사용함
            //ObjectMapper()은 다양한 용도로 쓰이는데 여기서는 'Convert "JSON" to "Java Object" 할 때 쓰이는 것이다.
            RequestLogin creds=new ObjectMapper().readValue(request.getInputStream(), RequestLogin.class);


            //UsernamePasswordAuthenticationToken 안에는 아이디, 비번, 그리고 어떤 권한을 가질 것인지 전달
            //그리고 manager에다가 넘기면 인증을 해주는 것이다.
            return getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(creds.getEmail(), creds.getPassword(),new ArrayList<>()));


        }catch(IOException e){
            throw new RuntimeException(e);
        }

    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        String userName=((User)authResult.getPrincipal()).getUsername();
        UserDto userDetails=userService.getUserDetailsByEmail(userName);

        System.out.println("토큰:"+env.getProperty("token.secret"));
        String token=Jwts.builder()
                .setSubject(userDetails.getUserId())
                //현재 시간+ yml 파일에 있는 유효 시간를 가져오는데 이건 문자열이니까 Long으로 파싱해줘야함
                .setExpiration(new Date(System.currentTimeMillis()+
                        Long.parseLong(env.getProperty("token.expiration_time"))))
                .signWith(SignatureAlgorithm.HS512, env.getProperty("token.secret"))
                .compact();

        response.addHeader("token",token);
        response.addHeader("userID", userDetails.getUserId());
    }
}
