package antifraud.persistence;

import antifraud.businesslayer.User;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.util.Optional;

/*
How can we connect a database instead of a Map? There are a lot of ways to do it. In our case, we can remove
the Map with user data and connect the findUserByUsername and save methods of UserRepository with a database.
To learn how to work with databases, see our topics on Spring Data.
*/

public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Iterable<User> findAll();

    boolean existsByUsername(String username);

    @Transactional
    void deleteByUsername(String username);

}
