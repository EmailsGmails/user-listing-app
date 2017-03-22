package lv.emils.dev.simpleapp.content;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Emils on 18.03.2017.
 */

public class UserContent {

    public static  String USERS_LINK = "http://jsonplaceholder.typicode.com/users/";

    public static List<UserListUser> USERS = new ArrayList<>();
    public static Map<String, UserListUser> USER_MAP = new HashMap<>();

    public static void addUser(UserListUser user) {
        USERS.add(user);
        USER_MAP.put(user.getId(), user);
    }

    public static class UserListUser {
        private String id;
        private String userName;
        private String email;

        public UserListUser(String id) {
            this.id = id;
        }

        @Override
        public String toString() {
            return "Username: " + userName + "\nE-mail: " + email;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

    }

    public static class User {
        private String id;
        private String username;
        private String name;
        private String zipcode;
        private String companyName;

        public User(String id) {
            this.id = id;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("\nId: " + id);
            builder.append("\nName: " + name);
            builder.append("\nZip code: " + zipcode);
            builder.append("\nCompany name: " + companyName);
            return builder.toString();
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getZipcode() {
            return zipcode;
        }

        public void setZipcode(String zipcode) {
            this.zipcode = zipcode;
        }

        public String getCompanyName() {
            return companyName;
        }

        public void setCompanyName(String companyName) {
            this.companyName = companyName;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }
}
