package chatservice.adapters.persistence.orm;


import chatservice.domain.model.ChatMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface ChatMessagesRepository extends PagingAndSortingRepository<ChatMessageOrm, Long> {
    List<ChatMessageOrm> findAllByChatRoom(ChatRoomOrm chatRoomOrm, Pageable pageable);
}
