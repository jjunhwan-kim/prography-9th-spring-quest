package com.prography.game.domain.user.service;

import com.prography.game.domain.user.entity.User;
import com.prography.game.domain.user.repository.UserQueryRepository;
import com.prography.game.domain.user.repository.UserRepository;
import com.prography.game.domain.user.request.UserListRequest;
import com.prography.game.domain.user.response.UserListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserQueryRepository userQueryRepository;

    @Transactional
    public void clearAndSaveUsers(List<User> users) {

        // TODO Clear All Data
        // userRoomRepository.deleteAll();
        // userRepository.deleteAll();
        // roomRepository.deleteAll();

        userRepository.saveAll(users);
    }

    @Transactional(readOnly = true)
    public UserListResponse getUserList(UserListRequest request) {

        Integer page = request.getPage();
        Integer size = request.getSize();

        Page<User> users = userQueryRepository.findAll(page, size);

        long totalElements = users.getTotalElements();
        int totalPages = users.getTotalPages();
        List<UserListResponse.User> userList = users.getContent().stream()
                .map(UserListResponse.User::of)
                .collect(Collectors.toList());

        return new UserListResponse(totalElements, totalPages, userList);
    }
}
