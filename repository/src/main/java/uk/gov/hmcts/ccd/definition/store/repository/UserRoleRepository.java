package uk.gov.hmcts.ccd.definition.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hmcts.ccd.definition.store.repository.entity.UserRoleEntity;

import java.util.List;
import java.util.Optional;

public interface UserRoleRepository extends JpaRepository<UserRoleEntity, Integer> {

    Optional<UserRoleEntity> findTopByRole(String role);

    List<UserRoleEntity> findByRoleIn(List<String> role);
}
