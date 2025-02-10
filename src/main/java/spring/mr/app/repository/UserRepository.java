package spring.mr.app.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spring.mr.app.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Méthodes personnalisées si besoin (ex: findByEmail)
    User findByEmail(String email);
}
