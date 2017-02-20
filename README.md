Data utilities
---

This libraries offers data types representing some common logic.

Usages
---

For example, `UserRepository` interface is given with signature like bellow
and you are going to implement user login feature in `UserService`.

```java
interface UserRepository {
  @NotNull
  Optional<User> findUserByEmail(@NotNull String email);
}
```

```java
interface UserService {
  @NotNull
  UserName login(@NotNull String email, @NotNull String password);
}
```

In the normal case this service will be implemented as follows.

```java
class UserServiceImpl {
  private final UserRepository repository;
  private final HashService hashService;

  @Override
  @NotNull
  public Username login(@NotNull String email, @NotNull String password) {
    final Optional<User> user = repository.findUserByEmail(email);
    if (!user.isPresent()) {
      throw new UserNotFoundException("Invalid email and password.");
    }
    final User u = user.get();
    if (hashService.validateHash(u.getPasswordHash(), password)) {
      return u.getName();
    } else {
      throw new UserNotFoundException("Invalid email and password.");
    }
  }
}
```

With this library you can implement this method with method chain.

```java
class UserServiceImpl {
  private final UserRepository repository;
  private final HashService hashService;

  @Override
  @NotNull
  public Username login(@NotNull String email, @NotNull String password) {
    return repository.findUserByEmail(email)
        .map(Tuple.mkTuple(User::getPasswordHash)) // User -> Tuple<User, PasswordHash>
        .filter(Tuple.conditionTuple(hash -> hashService.validateHash(hash, password)))
        .map(Tuple::getLeft) // Tuple<User, String> -> User
        .orElseThrow(() -> new UserNotFoundException("Invalid email and password."));
  }
}
```


