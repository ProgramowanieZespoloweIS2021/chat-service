package chatservice.adapters.persistence;

import chatservice.adapters.persistence.orm.*;
import chatservice.domain.model.ChatMessage;
import chatservice.domain.model.ChatRoom;
import chatservice.domain.model.ChatUser;
import chatservice.domain.ports.ChatRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.*;


//Will be refactored

@Component
@AllArgsConstructor
public class ChatRepositoryJpa implements ChatRepository {

    private final ChatMessagesRepository chatMessagesRepository;
    private final ChatUserRepository chatUserRepository;
    private final ChatRoomRepository chatRoomRepository;



    @Override
    public Optional<ChatUser> getUser(Long id) {
        var ormUser = chatUserRepository.findByExternalId(id).orElseThrow();
        return Optional.of(new ChatUser(ormUser.getExternalId(), ormUser.getNickname(), ormUser.getLastActivity()));
    }


    @Override
    public ChatMessage addOrUpdateMessage(ChatMessage chatMessage) {

        ChatUserOrm sender = chatUserRepository.findByExternalId(chatMessage.getSenderId()).orElseThrow();
        Set<ChatUserOrm> recipients = new HashSet<>();

        if(null != chatMessage.getId()){
            recipients = chatMessagesRepository.findById(chatMessage.getId()).orElseThrow().getRecipients();
        } else if (null != chatMessage.getRecipientsIds()) {
            for(var chatUserExternalId : chatMessage.getRecipientsIds()){
                recipients.add(chatUserRepository.findByExternalId(chatUserExternalId).orElseThrow());
            }
        }

        ChatRoomOrm chatRoomOrm = chatRoomRepository.findById(chatMessage.getChatId()).orElseThrow();

        ChatMessageOrm chatMessageOrm = ChatMessageOrm.builder()
                .id(chatMessage.getId())
                .content(chatMessage.getContent())
                .timestamp(chatMessage.getTimestamp())
                .messageStatus(chatMessage.getMessageStatus())
                .sender(sender)
                .recipients(recipients)
                .chatRoom(chatRoomOrm)
                .build();
        chatMessageOrm = chatMessagesRepository.save(chatMessageOrm);
        chatMessage.setId(chatMessageOrm.getId());
        return chatMessage;
    }

    @Override
    public ChatRoom addOrUpdateChatRoom(ChatRoom chatRoom) {


        Set<ChatUserOrm> chatUsers = new HashSet<>();
        for(var chatUser : chatRoom.getUsers()){
            chatUsers.add(chatUserRepository.findByExternalId(chatUser.getId()).orElseThrow());
        }

        var existingChatRoom = chatRoomRepository.findByUsersExternalIdIn(new ArrayList<>(chatUsers), chatUsers.size());
        existingChatRoom.ifPresent(chatRoomOrm -> chatRoom.setId(chatRoomOrm.getId()));


        ChatRoomOrm updatedChatRoomOrm = ChatRoomOrm.builder()
                .id(chatRoom.getId())
                .users(chatUsers)
                .lastActivity(chatRoom.getLastActivityDate())
                .build();

        updatedChatRoomOrm = chatRoomRepository.save(updatedChatRoomOrm);
        chatRoom.setId(updatedChatRoomOrm.getId());
        return chatRoom;
    }


    @Override
    public Optional<ChatRoom> getChatRoom(Long id) {
        ChatRoomOrm chatRoomOrm = chatRoomRepository.findById(id).orElseThrow();
        List<ChatUser> chatUsers = new ArrayList<>();
        chatRoomOrm.getUsers().forEach(user -> chatUsers.add(
                ChatUser.builder()
                    .id(user.getExternalId())
                    .nickname(user.getNickname())
                    .lastActivity(user.getLastActivity())
                    .build()));

        return Optional.of(ChatRoom.builder().id(chatRoomOrm.getId()).users(chatUsers).lastActivityDate(chatRoomOrm.getLastActivity()).build());
    }

    @Override
    public ChatUser addOrUpdateUser(ChatUser user) {


        ChatUserOrm updatedChatUserOrm = ChatUserOrm.builder()
                .externalId(user.getId())
                .nickname(user.getNickname())
                .lastActivity(user.getLastActivity())
                .build();

        chatUserRepository.findByExternalId(user.getId()).ifPresent(userOrm -> updatedChatUserOrm.setId(userOrm.getId()));
        chatUserRepository.save(updatedChatUserOrm);
        return user;
    }

    //logic should be moved to chatService, but how with pagination? do it later
    @Override
    public List<ChatRoom> getLatestUserChatRooms(Long chatUserID, int pageOffset, int pageSize) {

        ChatUserOrm chatUserOrm = chatUserRepository.findByExternalId(chatUserID).orElseThrow();
        Pageable pageable = PageRequest.of(pageOffset, pageSize, Sort.by("lastActivity"));
        List<ChatRoomOrm> chatRoomsOrm = chatRoomRepository.findAllByUsers(chatUserOrm, pageable);

        List<ChatRoom> chatRooms = new ArrayList<>();
        for(var chatRoomOrm : chatRoomsOrm){
            List<ChatUser> chatRoomUsers = new ArrayList<>();
            chatRoomOrm.getUsers().forEach(
                    chatUser -> chatRoomUsers.add(
                            ChatUser.builder()
                                    .id(chatUser.getExternalId())
                                    .nickname(chatUser.getNickname())
                                    .lastActivity(chatUser.getLastActivity())
                                    .build()));
            chatRooms.add(ChatRoom.builder().users(chatRoomUsers).id(chatRoomOrm.getId()).lastActivityDate(chatRoomOrm.getLastActivity()).build());
        }
        return chatRooms;
    }

    @Override
    public List<ChatMessage> getLatestMessagesForChatRoom(Long chatRoomID, int pageOffset, int pageSize) {

        ChatRoomOrm chatRoomOrm = chatRoomRepository.findById(chatRoomID).orElseThrow();
        Pageable pageable = PageRequest.of(pageOffset, pageSize, Sort.by("timestamp"));
        List<ChatMessageOrm> chatMessagesOrm = chatMessagesRepository.findAllByChatRoom(chatRoomOrm, pageable);
        List<ChatMessage> chatMessages = new ArrayList<>();
        chatMessagesOrm.forEach(chatMessage -> chatMessages.add(ChatMessage.builder()
                .id(chatMessage.getId())
                .chatId(chatMessage.getChatRoom().getId())
                .senderId(chatMessage.getSender().getExternalId())
                .messageStatus(chatMessage.getMessageStatus())
                .timestamp(chatMessage.getTimestamp())
                .content(chatMessage.getContent())
                .build())); //skip recipients

        return chatMessages;
    }
}
