package ru.practicum.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.error.exceptions.NotFoundException;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto addUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> findUsers(List<Long> ids, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<UserDto> ans = new ArrayList<>();
        if (ids == null || ids.isEmpty()) {
            List<User> users = userRepository.findAll(pageable).getContent();
            ans.addAll(users.stream()
                    .map(UserMapper::toUserDto).collect(Collectors.toList()));
        } else if (ids.size() == 1) {
            User user = userRepository.findById(ids.get(0)).orElse(null);
            if (user == null) {
                return ans;
            }
            ans.add(UserMapper.toUserDto(user));
        } else {
            List<User> users = userRepository.findByIdIn(ids, pageable).getContent();
            ans.addAll(users.stream()
                    .map(UserMapper::toUserDto).collect(Collectors.toList()));
        }
        return ans;
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("Not found"));
        userRepository.delete(user);
    }
}