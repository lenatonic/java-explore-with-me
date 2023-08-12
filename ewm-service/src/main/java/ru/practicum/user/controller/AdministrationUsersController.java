package ru.practicum.user.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.user.dto.UserDto;
import ru.practicum.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@Validated
@RequestMapping("/admin")
public class AdministrationUsersController {
    private final UserService userService;

    @Autowired
    public AdministrationUsersController(UserService userService) {
        this.userService = userService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/users")
    public UserDto addUser(@RequestBody @Valid UserDto userDto) {
        UserDto addedUser = userService.addUser(userDto);
        log.debug("Админ зарегестрировал нового пользователя: " + addedUser.toString());
        return addedUser;
    }

    @GetMapping("/users")
    public List<UserDto> findUsers(@RequestParam(required = false) List<Long> ids,
                                   @RequestParam (defaultValue = "0") int from,
                                   @RequestParam(defaultValue = "10") int size) {
        List<UserDto> ans = userService.findUsers(ids, from, size);
        log.debug("Админ получает ответ на запрос списка пользователей.");
        return ans;
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/users/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}