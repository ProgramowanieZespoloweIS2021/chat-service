package chatservice.domain;

import chatservice.domain.model.ChatMessage;
import chatservice.domain.model.ChatRoom;
import chatservice.domain.model.ChatUser;
import chatservice.domain.model.MessageStatus;
import chatservice.domain.ports.ChatRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final int UPDATING_MESSAGES_INTERVAL = 10;
    private final int EMERGENCY_PAGES_STOP = 10;

    public ChatMessage sendMessage(ChatMessage chatMessage){

        chatMessage.setMessageStatus(MessageStatus.SENT);
        ChatRoom chatRoom = chatRepository.getChatRoom(chatMessage.getChatId()).orElseThrow();
        chatRoom.setLastActivityDate(new Date());
        chatRepository.addOrUpdateChatRoom(chatRoom);
        chatMessage.setRecipientsIds(new ArrayList<>());
        List<Long> chatRecipientsIds = new ArrayList<>();

        for(var user : chatRoom.getUsers()){
            if(!user.getId().equals(chatMessage.getSenderId())){
                chatRecipientsIds.add(user.getId());
            }
        }

        chatMessage.setRecipientsIds(chatRecipientsIds);
        return chatRepository.addOrUpdateMessage(chatMessage);
    }

    //maybe later xd
//    public ChatMessage deleteMessage(ChatMessage chatMessage){
//        chatMessage.setContent(null);
//        chatMessage.setMessageStatus(MessageStatus.DELETED);
//        chatRepository.addOrUpdateMessage(chatMessage);
//        return chatMessage;
//    }

    public ChatRoom createChatRoom(List<ChatUser> users){

        users.forEach(chatRepository::addOrUpdateUser);
        ChatRoom chatRoom = new ChatRoom(users, new Date());
        return chatRepository.addOrUpdateChatRoom(chatRoom);
    }

    public Optional<ChatUser> getUserById(Long id){
        return chatRepository.getUser(id);
    }

    public List<ChatUser> getRecipientsOfMessage(ChatMessage chatMessage){
        ChatRoom chatRoom = chatRepository.getChatRoom(chatMessage.getChatId()).orElseThrow();
        List<ChatUser> recipients = new ArrayList<>();
        for(var chatUser : chatRoom.getUsers()){
            if(!chatUser.getId().equals(chatMessage.getSenderId())){
                recipients.add(chatUser);
            }
        }
        return recipients;
    }

    public List<ChatMessage> readMessagesAndGetUpdatedMessages(Long chatID) {

        List<ChatMessage> chatMessages = new ArrayList<>();
        boolean allMessagesUpdated = false;
        for (int page = 0; page < EMERGENCY_PAGES_STOP; ++page) {
            for (var chatMessage : chatRepository.getLatestMessagesForChatRoom(chatID, page, UPDATING_MESSAGES_INTERVAL)) {
                if (chatMessage.getMessageStatus().equals(MessageStatus.SENT)) {
                    chatMessage.setMessageStatus(MessageStatus.READ);
                    chatMessages.add(chatMessage);
                    chatRepository.addOrUpdateMessage(chatMessage);
                } else {
                    allMessagesUpdated = true;
                }
            }
            if (allMessagesUpdated) {
                break;
            }
        }

        return chatMessages;
    }

    public void noticeUserActivity(Long userID){
        ChatUser user = chatRepository.getUser(userID).orElseThrow();
        user.setLastActivity(new Date());
        chatRepository.addOrUpdateUser(user);
    }

    //how to move pagination logic here? (in efficient way?)
    public List<ChatRoom> getUserChatRooms(Long userID, int pageOffset, int pageSize){
        return chatRepository.getLatestUserChatRooms(userID, pageOffset, pageSize);
    }
    //and here
    public List<ChatMessage> getChatMessagesForChatRoom(Long chatRoomID, int pageOffset, int pageSize){
        return chatRepository.getLatestMessagesForChatRoom(chatRoomID, pageOffset, pageSize);
    }





}
