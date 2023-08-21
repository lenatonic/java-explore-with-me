package ru.practicum.user.service;

import ru.practicum.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto addUser(UserDto userDto);

    List<UserDto> findUsers(List<Long> idis, int from, int size);

    void deleteUser(Long id);
}