package ex.org.project.reportservice.repositories;

import ex.org.project.reportservice.model.UserLogin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;

public interface UserLoginRepository extends JpaRepository<UserLogin, Integer> {

    List<UserLogin> findByUserId(Integer userId);
    long countByUserIdAndLoginAtGreaterThanAndLoginAtLessThan(Integer userId, OffsetDateTime startDate, OffsetDateTime endDate);
    @Query(value = "select distinct user_id from public.user_login where " +
            "login_at > :startDate and " +
            "login_at < :endDate", nativeQuery = true)
    List<Integer> findDistinctUserIdByLoginAtGreaterThanAndLoginAtLessThan(@Param("startDate") OffsetDateTime startDate, @Param("endDate") OffsetDateTime endDate);
}
