package app.user;

import lombok.Value;

import java.util.List;

@Value // All fields are private and final. Getters (but not setters) are generated (https://projectlombok.org/features/Value.html)
public class User {
    String username;
    String salt;
    String hashedPassword;
    UserType type;
    List<String> emails;
}
