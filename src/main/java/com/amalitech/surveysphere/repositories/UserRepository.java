package com.amalitech.surveysphere.repositories;

import com.amalitech.surveysphere.models.User;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

  Optional<User> findByEmail(String email);

  Page<User> findAllByDeletedAndRoleNot(boolean deleted, String role, Pageable pageable);

  long countUserByEnabled(boolean enabled);

  List<User> findByCreatedDateBetween(Date startDate, Date endDate);

  List<User> findByEnabledAndCreatedDateBetween(boolean enabled, Date start, Date end);

  long countByEnabled(boolean enabled);

  int countByRole(String role);
}
