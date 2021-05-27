package chatservice.domain.model;

import lombok.*;
import java.util.Date;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessage {
    private Long id;
    private Long chatId;
    private String content;
    private Date timestamp;
    private MessageStatus messageStatus;
    private Long senderId;
    private List<Long> recipientsIds;
}
