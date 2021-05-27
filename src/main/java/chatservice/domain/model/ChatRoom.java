package chatservice.domain.model;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class ChatRoom {
    private Long id;
    private List<ChatUser> users;

    @NotNull
    private Date lastActivityDate;

    public ChatRoom(List<ChatUser> users, Date creationDate){
        this.users = users;
        this.lastActivityDate = creationDate;
    }

}
