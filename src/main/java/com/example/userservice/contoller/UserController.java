package com.example.userservice.contoller;

import com.example.userservice.dto.UserDto;
import com.example.userservice.jpa.UserEntity;
import com.example.userservice.service.UserService;
import com.example.userservice.vo.RequestUser;
import com.example.userservice.vo.ResponseUser;
import java.util.ArrayList;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user-service")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;
  private final Environment env;

  @GetMapping("/heath_check")
  public String status() {
    return String.format("It's Working in User Service on Port %s.",
        env.getProperty("local.server.port"));
  }

  @PostMapping("/users")
  public ResponseEntity<ResponseUser> createUser(@RequestBody @Valid RequestUser requestUser) {
    ModelMapper mapper = new ModelMapper();
    mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    UserDto userDto = mapper.map(requestUser, UserDto.class);
    userService.createdUser(userDto);

    ResponseUser responseUser = mapper.map(userDto, ResponseUser.class);
    return ResponseEntity.status(HttpStatus.CREATED).body(responseUser);
  }

  @GetMapping("users")
  public ResponseEntity<List<ResponseUser>> getUsers() {
    Iterable<UserEntity> users = userService.getUserByAll();
    List<ResponseUser> result = new ArrayList<>();
    users.forEach(user-> result.add(new ModelMapper().map(user, ResponseUser.class)));
    return ResponseEntity.status(HttpStatus.OK).body(result);
  }

  @GetMapping("users/{userId}")
  public ResponseEntity<ResponseUser> getUser(@PathVariable String userId) {
    UserDto userDto = userService.getUserByUserId(userId);
    ResponseUser responseUser = new ModelMapper().map(userDto, ResponseUser.class);

    return ResponseEntity.status(HttpStatus.OK).body(responseUser);
  }

}
