package com.prography.game.domain.user.response;

import lombok.Data;

import java.util.List;

@Data
public class UserInitResponse {

    private String status;
    private Integer code;
    private Integer total;
    private List<User> data;

    @Data
    public static class User {

        private Long id;
        private String uuid;
        private String firstname;
        private String lastname;
        private String username;
        private String password;
        private String email;
        private String ip;
        private String macAddress;
        private String website;
        private String image;
    }
}
