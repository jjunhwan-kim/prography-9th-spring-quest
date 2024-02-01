package com.prography.game.domain.user.service;

import com.prography.game.domain.user.entity.User;
import com.prography.game.domain.user.request.UserInitRequest;
import com.prography.game.domain.user.response.UserInitResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserInitService {

    private static final String USERS_URL = "https://fakerapi.it/api/v1/users";
    private final RestTemplate restTemplate;
    private final UserService userService;

    public void initializeUsers(UserInitRequest request) {

        // Request users
        UserInitResponse response = requestInitUsers(request);

        // Convert
        List<User> users = sortAndConvertUsers(response);

        // save users
        userService.clearAndSaveUsers(users);
    }

    public UserInitResponse requestInitUsers(UserInitRequest request) {

        Integer seed = request.getSeed();
        Integer quantity = request.getQuantity();

        URI uri = UriComponentsBuilder.fromHttpUrl(USERS_URL)
                .queryParam("_seed", seed)
                .queryParam("_quantity", quantity)
                .queryParam("_locale", "ko_KR")
                .build()
                .toUri();

        return restTemplate.getForObject(uri, UserInitResponse.class);
    }

    public List<User> sortAndConvertUsers(UserInitResponse response) {
        return response.getData().stream()
                .sorted(Comparator.comparing(UserInitResponse.User::getId))
                .map(user -> new User(user.getId(), user.getUsername(), user.getEmail()))
                .collect(Collectors.toList());
    }
}
