package chatservice.adapters.api;


import chatservice.domain.ChatService;
import chatservice.domain.model.*;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.parser.Entity;
import java.util.List;


@RestController
@AllArgsConstructor
public class ChatServiceController {

    SimpMessagingTemplate simpMessagingTemplate;
    private final ChatService chatService;


    @GetMapping("/chats/{userID}")
    public ResponseEntity<List<ChatRoom>> getUserChats(@PathVariable Long userID,
                                                       @RequestParam(defaultValue = "0") int pageOffset, @RequestParam(defaultValue = "15") int pageSize){
        chatService.noticeUserActivity(userID);
        return new ResponseEntity<>(chatService.getUserChatRooms(userID, pageOffset, pageSize), HttpStatus.OK);

    }



    @PostMapping("/chats/createRoom")
    public ResponseEntity<ChatRoom> createChat(@RequestBody List<ChatUser> users){
       return new ResponseEntity<>(chatService.createChatRoom(users), HttpStatus.CREATED);

    }

    @PutMapping("/chats/{chatID}/readByUser/{userID}")
    public ResponseEntity<ChatMessage> readMessages(@PathVariable Long userID, @PathVariable Long chatID, @RequestBody ChatMessage lastChatMessage){

        lastChatMessage.setChatId(chatID);
        chatService.noticeUserActivity(userID);
        if(lastChatMessage.getMessageStatus().equals(MessageStatus.READ)){
            return new ResponseEntity<>(lastChatMessage, HttpStatus.OK);
        }

        List<ChatMessage> chatMessages = chatService.readMessagesAndGetUpdatedMessages(chatID);
        List<ChatUser> messageRecipients = chatService.getRecipientsOfMessage(lastChatMessage);
        ChatUser sender = chatService.getUserById(lastChatMessage.getSenderId()).orElseThrow();

        //maybe send notification only about recent (latest) message in chat?
        for(var messageRecipient : messageRecipients){
            simpMessagingTemplate.convertAndSendToUser(
                    messageRecipient.getId().toString(), "/queue/messages",
                    new ChatNotification(chatID, sender, chatMessages.get(0).getId(), NotificationType.USER_READ_CHAT_MESSAGE)
            );
        }

        return new ResponseEntity<>(chatMessages.get(0), HttpStatus.OK);
    }

    @MessageMapping("/chat")
    public void processMessages(@Payload ChatMessage chatMessage){

        chatService.noticeUserActivity(chatMessage.getSenderId());
        ChatMessage message = chatService.sendMessage(chatMessage);
        ChatUser sender = chatService.getUserById(message.getSenderId()).orElseThrow();
        List<ChatUser> chatRecipients = chatService.getRecipientsOfMessage(message);

        for(var recipientID : chatRecipients){
            simpMessagingTemplate.convertAndSendToUser(
                    recipientID.getId().toString(), "/queue/messages",
                    new ChatNotification(message.getChatId(), sender, message.getId(), NotificationType.NEW_MESSAGE_FROM)
            );
        }
    }

    @GetMapping("/users/{userID}")
    public ResponseEntity<ChatUser> getUserInfo(@PathVariable Long userID){
        return new ResponseEntity<>(chatService.getUserById(userID).orElseThrow(), HttpStatus.OK);
    }

    @GetMapping("/messages/{chatID}")
    public ResponseEntity<List<ChatMessage>> getChatMessages(@PathVariable Long chatID,
                                                             @RequestParam(defaultValue = "0") int pageOffset, @RequestParam(defaultValue = "15") int pageSize){
        return new ResponseEntity<>(chatService.getChatMessagesForChatRoom(chatID, pageOffset, pageSize), HttpStatus.OK);
    }

}
