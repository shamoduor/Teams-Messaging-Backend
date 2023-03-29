package com.shamine.teamsmessagingbackend.websockets;

import com.shamine.teamsmessagingbackend.repositories.*;
import com.shamine.teamsmessagingbackend.services.GroupMessageDeliveryService;
import com.shamine.teamsmessagingbackend.services.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfiguration implements WebSocketConfigurer
{
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatGroupRepository chatGroupRepository;

    @Autowired
    private ChatGroupMemberRepository chatGroupMemberRepository;

    @Autowired
    private MessagePrivateRepository messagePrivateRepository;

    @Autowired
    private MessageGroupRepository messageGroupRepository;

    @Autowired
    private GroupMessageDeliveryRepository messageDeliveryRepository;

    @Autowired
    private GroupMessageDeliveryService groupMessageDeliveryService;

    @Autowired
    private MailService mailService;


    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry)
    {
        webSocketHandlerRegistry.addHandler(new WebSocketHandler(userRepository,
                        chatGroupRepository,
                        chatGroupMemberRepository,
                        messagePrivateRepository,
                        messageGroupRepository,
                        messageDeliveryRepository,
                        groupMessageDeliveryService,
                        mailService),
                "/websocket");
    }
}
