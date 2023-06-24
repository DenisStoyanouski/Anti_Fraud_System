package antifraud.persistence;

import antifraud.businesslayer.User;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/*
How can we connect a database instead of a Map? There are a lot of ways to do it. In our case, we can remove
the Map with user data and connect the findUserByUsername and save methods of UserRepository with a database.
To learn how to work with databases, see our topics on Spring Data.
*/

@Component
public class UserRepository {
    final private Map<String, User> users = new ConcurrentHashMap<>();

    public User findUserByUsername(String username) {
        return users.get(username);
    }

    public void save(User user) {
        users.put(user.getUsername(), user);
    }

    public List<User> findAllUsers() {
        return users.values().stream().toList();
    }



}
