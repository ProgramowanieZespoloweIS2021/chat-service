package chatservice.adapters.persistence.orm;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.lang.NonNull;
import org.springframework.lang.NonNullApi;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends PagingAndSortingRepository<ChatRoomOrm, Long> {
//    List<ChatRoomOrm> findAllByUsersExternalId(Long userID, Pageable pageable);
    List<ChatRoomOrm> findAllByUsers(ChatUserOrm user, Pageable pageable);
}
