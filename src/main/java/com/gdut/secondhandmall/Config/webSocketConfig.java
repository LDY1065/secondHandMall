package com.gdut.secondhandmall.Config;


import org.springframework.context.annotation.Bean;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

public class webSocketConfig {
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }


    @Bean
    public myEndpointConfigure newMyEndpointConfigure (){
        return new myEndpointConfigure();
    }
}
