package hack.polyus.goldenrush.auth.service;

import hack.polyus.goldenrush.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepo extends JpaRepository<User, Long> {
    User getByEmail(String email);

    @Modifying(clearAutomatically = true)
    @Query("update User u set u.currentRefreshTokenHash = :token where u.id = :id")
    void replaceRefreshToken(@Param(value = "id") Long id, @Param(value = "token") String token);

}
