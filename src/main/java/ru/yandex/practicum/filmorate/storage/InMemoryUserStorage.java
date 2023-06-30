package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private long id = 0;
    public final Map<Long, User> users = new HashMap<>();
    public Map<Long, Set<Long>> userFriendsIds = new HashMap<>();

    @Override
    public Optional<User> save(Optional<User> user) {
        user.get().setId(++id);
        users.put(user.get().getId(), user.get());
        return user;
    }

    @Override
    public Optional<User> update(Optional<User> user) {
        checkId(user.get());
        users.replace(user.get().getId(), user.get());
        return user;
    }

    @Override
    public void delete(Optional<User> user) {
        users.remove(user.get().getId());
    }

    @Override
    public Optional<User> getById(long userId) {
        User user = users.get(userId);
        if (user == null) {
            throw new NotFoundException("Пользователя с таким id не существует.");
        }
        return Optional.of(user);
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void addFriend(Optional<User> user, Optional<User> friend) {
        Set<Long> userFriendIds = userFriendsIds.computeIfAbsent(user.get().getId(), id -> new HashSet<>());
        userFriendIds.add(friend.get().getId());

        Set<Long> friendFriendIds = userFriendsIds.computeIfAbsent(friend.get().getId(), id -> new HashSet<>());
        friendFriendIds.add(user.get().getId());
    }

    @Override
    public void deleteFriend(Optional<User> user, Optional<User> friend) {
        Set<Long> userFriendIds = userFriendsIds.computeIfAbsent(user.get().getId(), id -> new HashSet<>());
        userFriendIds.remove(friend.get().getId());

        Set<Long> friendFriendIds = userFriendsIds.computeIfAbsent(friend.get().getId(), id -> new HashSet<>());
        friendFriendIds.remove(user.get().getId());
    }

    @Override
    public List<User> getAllCommonFriends(Optional<User> user, Optional<User> friend) {
        List<User> userFriendIds = getFriends(user);
        List<User> friendFriendIds = getFriends(friend);

        List<User> commonFriends = new ArrayList<>();
        for (User userFriendId : userFriendIds) {
            for (User friendFriendId : friendFriendIds) {
                if (userFriendId.equals(friendFriendId)) {
                    commonFriends.add(userFriendId);
                }
            }
        }
        return commonFriends;
    }

    @Override
    public List<User> getFriends(Optional<User> user) {
        Set<Long> userFriends = userFriendsIds.get(user.get().getId());
        List<User> friends = new ArrayList<>();
        if (userFriends == null) {
            return friends;
        }
        for (Long friendId : userFriends) {
            friends.add(users.get(friendId));
        }
        return friends;
    }

    public void checkId(User user) {
        for (Long id : users.keySet()) {
            if (id == user.getId()) {
                return;
            }
        }
        throw new NotFoundException("Пользователя с таким id не существует.");
    }
}
