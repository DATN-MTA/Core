package edu.mta.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class testPwdEncoder {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "123";
        System.out.println(encoder.encode(password));
    }
}
