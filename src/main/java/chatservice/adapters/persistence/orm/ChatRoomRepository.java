package chatservice.adapters.persistence.orm;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.lang.NonNullApi;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface ChatRoomRepository extends PagingAndSortingRepository<ChatRoomOrm, Long> {
    List<ChatRoomOrm> findAllByUsers(ChatUserOrm user, Pageable pageable);
    @Query("SELECT chatRoom FROM ChatRoomOrm chatRoom LEFT JOIN chatRoom.users users WHERE users IN :users GROUP BY chatRoom HAVING COUNT(users) = :usersSize")
    Optional<ChatRoomOrm> findByUsersExternalIdIn(@Param("users")List<ChatUserOrm> users, @Param("usersSize") long usersSize);
}
