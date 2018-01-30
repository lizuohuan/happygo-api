package com.magicbeans.happygo.config;

import com.magicbeans.happygo.entity.Member;
import com.magicbeans.happygo.service.IMemberService;
import com.magicbeans.module.jwt.JwtUser;
import com.magicbeans.module.jwt.auth.AbstractAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class JwtAuthService extends AbstractAuthService {

    @Autowired
    private IMemberService memberService;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        Member admin = memberService.find("name",s);
        if (admin == null) {
            throw new UsernameNotFoundException(String.format("No user found with username '%s'.", s));
        } else {
            JwtUser jwtUser = new JwtUser(admin.getId(),admin.getName(),admin.getPassword(),null,null,null);
            return jwtUser;
        }
    }
}
