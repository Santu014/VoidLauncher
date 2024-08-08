/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.san.voidlauncher.launching;

import com.mojang.authlib.Agent;
import com.mojang.authlib.UserAuthentication;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import java.net.Proxy;


/**
 *
 * @author Usuario
 */
public class MinecraftAuth {
    public static void getTokenAndUUID(String username, String password) {
        YggdrasilAuthenticationService authService = new YggdrasilAuthenticationService(Proxy.NO_PROXY, "");
        UserAuthentication auth = authService.createUserAuthentication(Agent.MINECRAFT);
        
        auth.setUsername(username);
        auth.setPassword(password);
        
        try {
            auth.logIn();
            System.out.println("Login successful!");
            System.out.println("Access Token: " + auth.getAuthenticatedToken());
            System.out.println("UUID: " + auth.getSelectedProfile().getId());
            System.out.println("Username: " + auth.getSelectedProfile().getName());
        } catch (AuthenticationException e) {
        }
    }
}
