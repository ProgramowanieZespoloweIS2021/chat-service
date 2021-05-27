package chatservice.adapters.persistence.orm;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatUserRepository extends JpaRepository<ChatUserOrm, Long> {
    Optional<ChatUserOrm> findByExternalId(Long id);
}
