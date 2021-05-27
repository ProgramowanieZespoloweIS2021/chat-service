package chatservice.domain.ports;

import chatservice.domain.model.ChatMessage;
import chatservice.domain.model.ChatRoom;
import chatservice.domain.model.ChatUser;

import java.util.List;
import java.util.Optional;

public interface ChatRepository {

    ChatMessage addOrUpdateMessage(ChatMessage chatMessage);
    ChatRoom addOrUpdateChatRoom(ChatRoom chatRoom);
    Optional<ChatRoom> getChatRoom(Long id);
    Optional<ChatUser> getUser(Long id);
    ChatUser addOrUpdateUser(ChatUser user);
    List<ChatRoom> getLatestUserChatRooms(Long chatUserID, int pageOffset, int pageSize);
    List<ChatMessage> getLatestMessagesForChatRoom(Long chatRoomID, int pageOffset, int pageSize);
}
