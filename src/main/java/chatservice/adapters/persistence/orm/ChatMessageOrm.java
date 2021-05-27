package chatservice.adapters.persistence.orm;

import chatservice.domain.model.ChatMessage;
import chatservice.domain.model.ChatUser;
import chatservice.domain.model.MessageStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class ChatMessageOrm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;
    private Date timestamp;
    private MessageStatus messageStatus;

    @OneToOne
    private ChatRoomOrm chatRoom;

    @ManyToOne
    private ChatUserOrm sender;

    @ManyToMany(targetEntity = ChatUserOrm.class, fetch = FetchType.EAGER)
    private Set<ChatUserOrm> recipients;

}
