package com.smsrz.url_shortener.Model;

public record CreateUserCmd(String email,String password,String name,Role role) {
}
