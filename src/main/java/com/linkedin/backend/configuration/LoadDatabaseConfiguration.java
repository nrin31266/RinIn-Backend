package com.linkedin.backend.configuration;

import com.linkedin.backend.features.authentication.model.User;
import com.linkedin.backend.features.authentication.repository.AuthenticationUserRepository;
import com.linkedin.backend.features.authentication.utils.Encoder;
import com.linkedin.backend.features.feed.model.Post;
import com.linkedin.backend.features.feed.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashSet;
import java.util.List;
import java.util.Random;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class LoadDatabaseConfiguration {
    private final Encoder encoder;

    @Bean
    CommandLineRunner initDatabase(AuthenticationUserRepository authenticationUserRepository, PostRepository postRepository) {
        return args -> {
            if (authenticationUserRepository.findByEmail("rinin1@yopmail.com").isEmpty()) {
                log.warn("Admin created, please change password!");
//
//                //
//
                List<User> users = createUsers(authenticationUserRepository);
//                createPosts(postRepository, users);
            }
        };
    }

    private List<User> createUsers(AuthenticationUserRepository authenticationUserRepository) {
        List<User> users = List.of(
                createUser("rinin1@yopmail.com", "123", "RinIn", "1", "Data Scientist", "FPT", "New Delhi, IN",
                        null),
                createUser("rinin2@yopmail.com", "123", "RinIn", "2", "Web Dev", "FPT", "New Delhi, IN",
                        null),
                createUser("rinin3@yopmail.com", "123", "RinIn", "3", "Mobile Dev", "FPT", "New Delhi, IN",
                        null),
                createUser("rinin4@yopmail.com", "123", "RinIn", "4", "AI Engineer", "FPT", "New Delhi, IN",
                        null),
                createUser("rinin5@yopmail.com", "123", "RinIn", "5", "DevOps Engineer", "FPT", "New Delhi, IN",
                        null),
                createUser("rinin6@yopmail.com", "123", "RinIn", "6", "Cloud Engineer", "FPT", "New Delhi, IN",
                        null),
                createUser("rinin7@yopmail.com", "123", "RinIn", "7", "Cybersecurity Specialist", "FPT", "New Delhi, IN",
                        null),
                createUser("rinin8@yopmail.com", "123", "RinIn", "8", "Blockchain Developer", "FPT", "New Delhi, IN",
                        null),
                createUser("rinin9@yopmail.com", "123", "RinIn", "9", "Full Stack Developer", "FPT", "New Delhi, IN",
                        null),
                createUser("rinin10@yopmail.com", "123", "RinIn", "10", "Software Architect", "FPT", "New Delhi, IN",
                        null),
                createUser("john.doe@yopmail.com", "123", "John", "Doe", "Software Engineer", "Docker Inc.", "San Francisco, CA",
                        "https://images.unsplash.com/photo-1633332755192-727a05c4013d?q=80&w=3560&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"),
                createUser("anne.claire@yopmail.com", "123", "Anne", "Claire", "HR Manager", "eToro", "Paris, Fr",
                        "https://images.unsplash.com/photo-1494790108377-be9c29b29330?q=80&w=3687&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"),
                createUser("arnauld.manner@yopmail.com", "123", "Arnauld", "Manner", "Product Manager", "Arc", "Dakar, SN",
                        "https://images.unsplash.com/photo-1640960543409-dbe56ccc30e2?q=80&w=2725&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"),
                createUser("moussa.diop@yopmail.com", "123", "Moussa", "Diop", "Software Engineer", "Orange", "Bordeaux, FR",
                        "https://images.unsplash.com/photo-1586297135537-94bc9ba060aa?q=80&w=3560&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"),
                createUser("awa.diop@yopmail.com", "123", "Awa", "Diop", "Data Scientist", "Zoho", "New Delhi, IN",
                        "https://images.unsplash.com/photo-1640951613773-54706e06851d?q=80&w=2967&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D")

        );
        return authenticationUserRepository.saveAll(users);
    }

    private User createUser(String email, String password, String firstName, String lastName, String position,
                            String company, String location, String profilePicture) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(encoder.encode(password));
        user.setEmailVerified(true);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPosition(position);
        user.setCompany(company);
        user.setLocation(location);
        user.setProfilePicture(profilePicture);
        return user;
    }

    private void createPosts(PostRepository postRepository, List<User> users) {
        Random random = new Random();
        for (int j = 1; j <= 10; j++) {
            Post post = new Post();
            post.setContent("Lorem ipsum dolor sit amet, consectetur adipiscing elit.");
            post.setAuthor(users.get(random.nextInt(users.size())));
            post.setLikes(generateLikes(users, j, random));

            if (j == 1) {
                post.setPicture("https://images.unsplash.com/photo-1731176497854-f9ea4dd52eb6?q=80&w=3432&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D");
            }
            postRepository.save(post);
        }
    }

    private HashSet<User> generateLikes(List<User> users, int postNumber, Random random) {
        HashSet<User> likes = new HashSet<>();
        if (postNumber == 1) {
            while (likes.size() < 3) {
                likes.add(users.get(random.nextInt(users.size())));
            }
        } else {
            int likesCount = switch (postNumber % 5) {
                case 0 -> 3;
                case 2, 3 -> 2;
                default -> 1;
            };
            for (int i = 0; i < likesCount; i++) {
                likes.add(users.get(random.nextInt(users.size())));
            }
        }
        return likes;
    }
}
