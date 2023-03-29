package com.shamine.teamsmessagingbackend.websockets;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shamine.teamsmessagingbackend.entities.*;
import com.shamine.teamsmessagingbackend.models.requests.*;
import com.shamine.teamsmessagingbackend.repositories.*;
import com.shamine.teamsmessagingbackend.services.ChatGroupService;
import com.shamine.teamsmessagingbackend.services.GroupMessageDeliveryService;
import com.shamine.teamsmessagingbackend.services.MailService;
import com.shamine.teamsmessagingbackend.services.UserService;
import com.shamine.teamsmessagingbackend.utils.Utils;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WebSocketHandler extends AbstractWebSocketHandler {

    final private UserRepository userRepository;
    final private ChatGroupRepository chatGroupRepository;
    final private ChatGroupMemberRepository groupMemberRepository;
    final private MessagePrivateRepository messagePrivateRepository;
    final private MessageGroupRepository messageGroupRepository;
    final private GroupMessageDeliveryRepository messageDeliveryRepository;
    final private GroupMessageDeliveryService groupMessageDeliveryService;
    final private List<WebSocketSession> sessions;
    final private MailService mailService;

    public WebSocketHandler(UserRepository userRepository,
                            ChatGroupRepository chatGroupRepository,
                            ChatGroupMemberRepository groupMemberRepository,
                            MessagePrivateRepository messagePrivateRepository,
                            MessageGroupRepository messageGroupRepository,
                            GroupMessageDeliveryRepository messageDeliveryRepository,
                            GroupMessageDeliveryService groupMessageDeliveryService,
                            MailService mailService) {
        this.userRepository = userRepository;
        this.chatGroupRepository = chatGroupRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.messagePrivateRepository = messagePrivateRepository;
        this.messageGroupRepository = messageGroupRepository;
        this.messageDeliveryRepository = messageDeliveryRepository;
        this.groupMessageDeliveryService = groupMessageDeliveryService;
        this.sessions = new ArrayList<>();
        this.mailService = mailService;
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            if (session.getPrincipal() != null && userRepository != null) {
                User loggedInUser = userRepository.findByEmail(session.getPrincipal().getName());
                String msg = message.getPayload();
                Gson gson = new Gson();
                Type empMapType = new TypeToken<GenericMessageRequest>() {
                }.getType();

                GenericMessageRequest messageRequest = gson.fromJson(msg, empMapType);

                if (messageRequest.getMessagePrivate() != null) {
                    System.out.println("MM: mpmpmp");
                    MessagePrivateRequest privateRequest = messageRequest.getMessagePrivate();
                    handleMessagePrivate(privateRequest, loggedInUser);
                } else if (messageRequest.getMessageGroup() != null) {
                    MessageGroupRequest msgGrpRequest = messageRequest.getMessageGroup();
                    ChatGroup chatGroup = chatGroupRepository.findByChatGroupIdAndAvailable
                            (msgGrpRequest.getChatGroupId(), true);

                    MessageGroup messageGroup = new MessageGroup();
                    boolean updated = false, toCreate = false;

                    if (msgGrpRequest.getMessageId() > 0) {
                        messageGroup = messageGroupRepository.findByMessageId(msgGrpRequest.getMessageId());

                        if (messageGroup != null && messageRequest.getGroupMessageDelivery() != null) {
                            GroupMessageDeliveryRequest deliveryRequest = messageRequest.getGroupMessageDelivery();
                            GroupMessageDelivery delivery = messageDeliveryRepository.
                                    findByDeliveryId(deliveryRequest.getDeliveryId());

                            if (delivery != null) {
                                boolean save = false;

                                if ((delivery.getReceivedOn() == null || delivery.getReceivedOn().getTime() == 0)
                                        && deliveryRequest.getReceivedOn() > 0) {
                                    delivery.setReceivedOn(new Date(deliveryRequest.getReceivedOn()));
                                    save = true;
                                }

                                if ((delivery.getReadOn() == null || delivery.getReadOn().getTime() == 0)
                                        && deliveryRequest.getReadOn() != null && deliveryRequest.getReadOn() > 0) {
                                    delivery.setReadOn(new Date(deliveryRequest.getReadOn()));
                                    save = true;
                                }

                                if (save) {
                                    messageDeliveryRepository.save(delivery);
                                    updated = true;
                                }
                            }
                        }
                    } else {
                        messageGroup.setContent(msgGrpRequest.getContent());
                        messageGroup.setSender(loggedInUser);
                        messageGroup.setCreatedOn(new Date(msgGrpRequest.getCreatedOn()));
                        messageGroup.setChatGroup(chatGroup);
                        toCreate = true;
                    }

                    MessageGroup s = null;
                    if (toCreate) {
                        s = messageGroupRepository.save(messageGroup);
                        if (s.getMessageId() > 0) {
                            List<ChatGroupMember> members = groupMemberRepository.
                                    findAllByChatGroupAndAvailable(s.getChatGroup(), true);

                            if (members != null && !members.isEmpty()) {
                                List<GroupMessageDelivery> groupMessageDeliveries = new ArrayList<>();
                                for (ChatGroupMember m : members) {
                                    if (m.getUser().getUserId() != loggedInUser.getUserId()) {
                                        GroupMessageDelivery d = new GroupMessageDelivery();
                                        d.setMessage(s);
                                        d.setRecipient(m);
                                        d.setReceivedOn(null);
                                        d.setReadOn(null);

                                        groupMessageDeliveries.add(d);
                                    }
                                }
                                messageDeliveryRepository.saveAll(groupMessageDeliveries);
                            }
                        }

                    } else if (updated) {
                        s = messageGroup;
                    }

                    if (s != null && s.getMessageId() > 0) {
                        List<ChatGroupMember> chatGroupMembers = groupMemberRepository.
                                findAllByChatGroupAndAvailable(s.getChatGroup(), true);
                        sendMessageGroup(s, chatGroupMembers);
                    }
                } else if (messageRequest.getGroupMessageDelivery() != null) {
                    handleGroupMessageDelivery(messageRequest.getGroupMessageDelivery());
                } else if (messageRequest.getSyncRequest() != null) {
                    SyncRequest syncRequest = messageRequest.getSyncRequest();
                    if (syncRequest.getDeliveriesToSync() != null && !syncRequest.getDeliveriesToSync().isEmpty()) {
                        for (GroupMessageDeliveryRequest d : syncRequest.getDeliveriesToSync()) {
                            handleGroupMessageDelivery(d);
                        }
                    }

                    if (syncRequest.getPrivateMessagesToSync() != null && !syncRequest.getPrivateMessagesToSync().isEmpty()) {
                        for (MessagePrivateRequest p : syncRequest.getPrivateMessagesToSync()) {
                            handleMessagePrivate(p, loggedInUser);
                        }
                    }

                    long lastSyncTimestamp = messageRequest.getSyncRequest().getLastSyncTimestamp();
                    lastSyncTimestamp = lastSyncTimestamp >= 0 ? lastSyncTimestamp : 0;
                    syncMessages(lastSyncTimestamp, session);

                    GenericMessageRequest r9 = new GenericMessageRequest();
                    SyncRequest sr9 = new SyncRequest();
                    sr9.setLastSyncTimestamp(new Date().getTime());
                    r9.setSyncRequest(sr9);
                    session.sendMessage(new TextMessage(gson.toJson(r9)));
                }
            } else {
                System.out.println("User details not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        if (!sessions.contains(session)) {
            sessions.add(session);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        sessions.remove(session);
    }

    private void syncMessages(long lastUpdateTimestamp, WebSocketSession currentSession) {
        try {
            if (!sessions.isEmpty() && currentSession.getPrincipal() != null && userRepository != null) {
                User loggedInUser = userRepository.findByEmail(currentSession.getPrincipal().getName());

                //Private
                List<MessagePrivate> messagesPrivate = messagePrivateRepository.findAllByRecipientAndCreatedOnGreaterThanOrderByCreatedOnAsc(loggedInUser, new Date(lastUpdateTimestamp));

                if (messagesPrivate != null && !messagesPrivate.isEmpty()) {
                    for (MessagePrivate mPrivate : messagesPrivate) {
                        sendMessagePrivate(mPrivate);
                    }
                }

                //group
                //getting groups where user is member
                List<ChatGroup> userGroups = chatGroupRepository.findAllChatGroupsByUserId(loggedInUser.getUserId());

                for (ChatGroup cg : userGroups) {// for each chat group
                    //find member based on the loggedinuser, current chatgroup and availability
                    ChatGroupMember groupMember = groupMemberRepository.findByUserAndChatGroupAndAvailable(loggedInUser, cg, true);
                    if (groupMember != null) {
                        //get pending message deliveries
                        List<GroupMessageDelivery> pendingMessageDeliveries = messageDeliveryRepository.findAllByUserIdToSync(groupMember.getUser().getUserId(), new Date(lastUpdateTimestamp));
                        List<ChatGroupMember> chatGroupMembers = groupMemberRepository.findAllByChatGroupAndAvailable(cg, true);

                        if (pendingMessageDeliveries != null && chatGroupMembers != null) {
                            //for each pending delivery, get the message in the pending delivery and send it
                            for (GroupMessageDelivery delivery : pendingMessageDeliveries) {
                                //for each pending delivery, get the message and send it
                                sendMessageGroup(delivery.getMessage(), chatGroupMembers);
                            }
                        }
                    }
                }
            } else {
                System.out.println("User details not found"); //todo: how to sort such and error
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Sync fail");
        }
    }

    private void handleMessagePrivate(MessagePrivateRequest privateRequest, User loggedInUser) {
        MessagePrivate messagePrivate = new MessagePrivate();

        if (privateRequest.getMessageId() > 0) {
            messagePrivate = messagePrivateRepository.findByMessageId(privateRequest.getMessageId());

            if (privateRequest.getReceivedOn() != null && privateRequest.getReceivedOn() > 0) {
                messagePrivate.setReceivedOn(new Date(privateRequest.getReceivedOn()));
            }

            if (privateRequest.getReadOn() != null && privateRequest.getReadOn() > 0) {
                messagePrivate.setReadOn(new Date(privateRequest.getReadOn()));
            }
        } else {
            messagePrivate.setCreatedOn(new Date(privateRequest.getCreatedOn()));
            messagePrivate.setContent(privateRequest.getContent());
            messagePrivate.setSender(loggedInUser);
            messagePrivate.setRecipient(userRepository.findByUserId(privateRequest.getRecipientId()));
            messagePrivate.setAvailableForSender(true);
            messagePrivate.setAvailableForRecipient(true);
        }

        MessagePrivate s = messagePrivateRepository.save(messagePrivate);

        if (s.getMessageId() > 0) {
            sendMessagePrivate(s);
        }
    }

    private void handleGroupMessageDelivery(GroupMessageDeliveryRequest deliveryRequest) {
        GroupMessageDelivery groupMessageDelivery = messageDeliveryRepository.findByDeliveryId(deliveryRequest.getDeliveryId());
        if (groupMessageDelivery != null) {
            boolean updateDelivery = false;
            if ((groupMessageDelivery.getReceivedOn() == null || groupMessageDelivery.getReceivedOn().getTime() == 0) && deliveryRequest.getReceivedOn() > 0) {
                updateDelivery = true;
                groupMessageDelivery.setReceivedOn(new Date(deliveryRequest.getReceivedOn()));
            }
            if ((groupMessageDelivery.getReadOn() == null || groupMessageDelivery.getReadOn().getTime() == 0) && deliveryRequest.getReadOn() != null && deliveryRequest.getReadOn() > 0) {
                updateDelivery = true;
                groupMessageDelivery.setReadOn(new Date(deliveryRequest.getReadOn()));
            }

            if (updateDelivery) {
                GroupMessageDelivery gd = messageDeliveryRepository.save(groupMessageDelivery);
                if (gd.getDeliveryId() > 0) {
                    sendGroupMessageDelivery(gd);
                }
            }
        }
    }

    private void sendMessagePrivate(MessagePrivate mp) {
        try {
            Gson gson = new Gson();
            MessagePrivateRequest privateRequest = new MessagePrivateRequest();
            privateRequest.setRecipientId(mp.getRecipient().getUserId());
            privateRequest.setAvailableForRecipient(mp.isAvailableForRecipient());
            privateRequest.setMessageId(mp.getMessageId());
            privateRequest.setContent(mp.getContent());
            privateRequest.setCreatedOn(mp.getCreatedOn().getTime());
            privateRequest.setSenderId(mp.getSender().getUserId());
            if (mp.getReceivedOn() != null && mp.getReceivedOn().getTime() > 0) {
                privateRequest.setReceivedOn(new Date().getTime());
            }
            if (mp.getReadOn() != null && mp.getReadOn().getTime() > 0) {
                privateRequest.setReadOn(new Date().getTime());
            }

            GenericMessageRequest messageRequest = new GenericMessageRequest();
            messageRequest.setMessagePrivate(privateRequest);
            messageRequest.setContact(UserService.userToContactHashMap(mp.getSender()));
            for (WebSocketSession se : sessions) {
                //send message to sender and receiver
                if (se.getPrincipal() != null &&
                        (se.getPrincipal().getName().equals(mp.getSender().getEmail()) ||
                                se.getPrincipal().getName().equals(mp.getRecipient().getEmail()))) {
                    se.sendMessage(new TextMessage(gson.toJson(messageRequest)));

                    try {
                        if (se.getPrincipal().getName().equals(mp.getRecipient().getEmail())) {
                            User fcmRecipient = userRepository.findByEmail(se.getPrincipal().getName());
                            mailService.sendFCMMessage(fcmRecipient, fcmRecipient.getName(), privateRequest.getContent(),
                                    String.valueOf(privateRequest.getCreatedOn()), String.valueOf(privateRequest.getSenderId()), String.valueOf(privateRequest.getRecipientId()));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendMessageGroup(MessageGroup mg, List<ChatGroupMember> chatGroupMembers) {
        try {
            Gson gson = new Gson();
            MessageGroupRequest groupRequest = new MessageGroupRequest();
            groupRequest.setChatGroupId(mg.getChatGroup().getChatGroupId());
            groupRequest.setSenderName(mg.getSender().getName());
            groupRequest.setMessageId(mg.getMessageId());
            groupRequest.setContent(mg.getContent());
            groupRequest.setCreatedOn(mg.getCreatedOn().getTime());
            groupRequest.setSenderId(mg.getSender().getUserId());

            GenericMessageRequest senderMessageRequest = new GenericMessageRequest();
            GenericMessageRequest recipientMessageRequest = new GenericMessageRequest();
            senderMessageRequest.setMessageGroup(groupRequest);
            recipientMessageRequest.setMessageGroup(groupRequest);

            List<GroupMessageDelivery> deliveries = messageDeliveryRepository.findAllByMessage(mg);

            for (WebSocketSession se : sessions) {
                //send message to sender and recipients
                if (se.getPrincipal() != null) {
                    for (ChatGroupMember u : chatGroupMembers) {
                        if (u.getUser().getEmail().equals(se.getPrincipal().getName())) {
                            if (mg.getSender().getUserId() == u.getUser().getUserId() && deliveries != null && !deliveries.isEmpty()) {
                                List<GroupMessageDeliveryRequest> deliveryRequests = groupMessageDeliveryService.groupMessageDeliveryListToRequest(deliveries);
                                senderMessageRequest.setGroupMessageDeliveries(deliveryRequests);
                                se.sendMessage(new TextMessage(gson.toJson(senderMessageRequest)));
                            } else {
                                List<GroupMessageDelivery> d1 = new ArrayList<>();
                                if (deliveries != null && !deliveries.isEmpty()) {
                                    for (GroupMessageDelivery d : deliveries) {
                                        if (d.getRecipient().getMemberId() == u.getMemberId()) {
                                            d1.add(d);
                                            break;
                                        }
                                    }
                                    if (!d1.isEmpty()) {
                                        recipientMessageRequest.setGroupMessageDeliveries(groupMessageDeliveryService.groupMessageDeliveryListToRequest(d1));
                                        recipientMessageRequest.setChatGroup(ChatGroupService.chatGroupToHashMap(mg.getChatGroup(), u.getUser()));
                                        recipientMessageRequest.setChatGroupMembers(ChatGroupService.chatGroupMemberListToHashMap(chatGroupMembers));
                                    }
                                }
                                se.sendMessage(new TextMessage(gson.toJson(recipientMessageRequest)));

                                try {
                                    if (mg.getSender().getUserId() != u.getUser().getUserId()) {
                                        User fcmRecipient = userRepository.findByEmail(se.getPrincipal().getName());
                                        mailService.sendFCMMessage(fcmRecipient, mg.getChatGroup().getTitle(), mg.getContent(), String.valueOf(mg.getCreatedOn()),
                                                String.valueOf(mg.getSender().getUserId()), String.valueOf(mg.getChatGroup().getChatGroupId()));
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendGroupMessageDelivery(GroupMessageDelivery delivery) {
        try {
            Gson gson = new Gson();
            GroupMessageDeliveryRequest request = groupMessageDeliveryService.groupMessageDeliveryToRequest(delivery);
            GenericMessageRequest messageRequest = new GenericMessageRequest();
            messageRequest.setGroupMessageDelivery(request);
            for (WebSocketSession s : sessions) {
                if (s.getPrincipal() != null &&
                        !Utils.stringNullOrEmpty(s.getPrincipal().getName()) &&
                        (s.getPrincipal().getName().equals(delivery.getMessage().getSender().getEmail()) ||
                                s.getPrincipal().getName().equals(delivery.getRecipient().getUser().getEmail()))) {
                    s.sendMessage(new TextMessage(gson.toJson(messageRequest)));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}