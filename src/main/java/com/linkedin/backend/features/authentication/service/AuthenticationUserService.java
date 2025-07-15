package com.linkedin.backend.features.authentication.service;

import com.linkedin.backend.exception.AppException;
import com.linkedin.backend.exception.ErrorCode;
import com.linkedin.backend.features.authentication.dto.request.*;
import com.linkedin.backend.features.authentication.dto.response.ExchangeCodeForTokenResponse;
import com.linkedin.backend.features.authentication.mapper.UserMapper;
import com.linkedin.backend.features.authentication.model.User;
import com.linkedin.backend.features.authentication.repository.AuthenticationUserRepository;
import com.linkedin.backend.features.authentication.dto.response.AuthenticationUserResponseBody;
import com.linkedin.backend.features.authentication.repository.httpclient.OauthClient;
import com.linkedin.backend.features.networking.domain.CONNECTION_STATUS;
import com.linkedin.backend.features.networking.model.Connection;
import com.linkedin.backend.utils.EmailService;
import com.linkedin.backend.features.authentication.utils.Encoder;
import com.linkedin.backend.features.authentication.utils.JsonWebToken;
import com.linkedin.backend.features.authentication.utils.OneTimePasswordGenerator;
import io.jsonwebtoken.Claims;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationUserService {
    AuthenticationUserRepository authenticationUserRepository;
    Encoder encoder;
    JsonWebToken jsonWebToken;
    OneTimePasswordGenerator oneTimePasswordGenerator;
    int durationInMinutes = 10;
    EmailService emailService;
    UserMapper userMapper;
    EntityManager entityManager;
    @NonFinal
    @Value("${oauth.client.id}")
    String oauthClientId;
    @NonFinal
    @Value("${oauth.client.secret}")
    String oauthClientSecret;
    OauthClient oauthClient;


    public User getUser(String email) {
        return authenticationUserRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("User not found"));
    }


    public AuthenticationUserResponseBody register(AuthenticationUserRequestBody authenticationUserRequestBody) {
        if(authenticationUserRepository.findByEmail(authenticationUserRequestBody.getEmail()).isPresent()) {
            throw new AppException(ErrorCode.USER_ALREADY_EXISTS);
        }
        try {
            String emailVerificationCode = oneTimePasswordGenerator.generateOTP();

            User authenticationUser = authenticationUserRepository.save(
                    User.builder()
                            .email(authenticationUserRequestBody.getEmail())
                            .password(encoder.encode(authenticationUserRequestBody.getPassword()))
                            .emailVerificationTokenExpiryDate(new Date(Instant.now().plus(durationInMinutes, ChronoUnit.MINUTES).toEpochMilli()))
                            .emailVerificationToken(encoder.encode(emailVerificationCode))
                            .lastLogin(LocalDateTime.now())
                            .build()
            );

            emailService.sendEmail(SendEmailRequest.builder()
                    .to(authenticationUserRequestBody.getEmail())
                    .subject("WELCOME TO LINKEDIN CLONE BY RINVAN05")
                    .body(emailVerificationCode)
                    .build());


            return AuthenticationUserResponseBody.builder()
                    .token(jsonWebToken.generateToken(authenticationUser))
                    .message("User registered successfully")
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Cannot register user");
        }
    }

    public AuthenticationUserResponseBody login(AuthenticationUserRequestBody authenticationUserRequestBody) {
        User authenticationUser = authenticationUserRepository.findByEmail(authenticationUserRequestBody.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        authenticationUser.setLastLogin(LocalDateTime.now());
        authenticationUserRepository.save(authenticationUser);
        if (!encoder.matches(authenticationUserRequestBody.getPassword(), authenticationUser.getPassword())) {
            throw new AppException(ErrorCode.PASSWORD_MISMATCH);
        }
        return AuthenticationUserResponseBody.builder()
                .token(jsonWebToken.generateToken(authenticationUser))
                .message("User logged in successfully")
                .build();
    }

    public AuthenticationUserResponseBody googleLoginOrRegister(OauthLoginRequest request) {
        log.info("Google login or register request: {}", request);
        try{
            String redirectUri = "http://localhost:3000/auth/"+ request.getPage();
            ExchangeCodeForTokenResponse exchangeCodeForTokenResponse = oauthClient.exchangeCodeForToken(
                    ExchangeCodeForTokenRequest.builder()
                            .code(request.getCode())
                            .client_id(oauthClientId)
                            .client_secret(oauthClientSecret)
                            .redirect_uri(redirectUri)
                            .grant_type("authorization_code")
                            .build()
            );

            Claims claims = jsonWebToken.getClaimsFromGoogleOauthIdToken(exchangeCodeForTokenResponse.getId_token());

            String email = claims.get("email", String.class);
            User user = authenticationUserRepository.findByEmail(email).orElse(null);
            if (user == null) {
                user = User.builder()
                        .email(email)
                        .firstName(claims.get("given_name", String.class))
                        .lastName(claims.get("family_name", String.class))
                        .profilePicture(claims.get("picture", String.class))
                        .emailVerified(true)
                        .lastLogin(LocalDateTime.now())
                        .creationDate(LocalDateTime.now())
                        .password(encoder.encode(oneTimePasswordGenerator.generateOTP())) // Temporary password
                        .build();
                user = authenticationUserRepository.save(user);
            }else{
                user.setLastLogin(LocalDateTime.now());
                user.setProfilePicture(claims.get("picture", String.class));
                user.setFirstName(claims.get("given_name", String.class));
                user.setLastName(claims.get("family_name", String.class));
                user = authenticationUserRepository.save(user);
            }

            return AuthenticationUserResponseBody.builder()
                    .token(jsonWebToken.generateToken(user))
                    .build();
        }catch (Exception e) {
            throw new AppException("Error during Google login or registration: "+ e.getMessage());
        }
    }

    public void sendEmailVerifyToken(String email) {
        User user = authenticationUserRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        if (user.getEmailVerified() == null || !user.getEmailVerified()) {
            String emailVerificationCode = oneTimePasswordGenerator.generateOTP();
            user.setEmailVerificationToken(encoder.encode(emailVerificationCode));
            user.setEmailVerificationTokenExpiryDate(new Date(Instant.now().plus(durationInMinutes, ChronoUnit.MINUTES).toEpochMilli()));
            authenticationUserRepository.save(user);
            emailService.sendEmail(SendEmailRequest.builder()
                    .to(email)
                    .subject("EMAIL VERIFICATION")
                    .body(emailVerificationCode)
                    .build());
        } else {
            throw new AppException(ErrorCode.EMAIL_ALREADY_VERIFIED);
        }
    }

    public void validateEmailVerificationToken(String token, String email) {
        User user = authenticationUserRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        if (user.getEmailVerificationToken() != null && encoder.matches(token, user.getEmailVerificationToken())) {
            if (user.getEmailVerificationTokenExpiryDate().before(new Date())) {
                throw new AppException(ErrorCode.EMAIL_VERIFICATION_EXPIRED);
            }

            user.setEmailVerified(true);
            user.setEmailVerificationToken(null);
            user.setEmailVerificationTokenExpiryDate(null);
            authenticationUserRepository.save(user);
        } else {
            throw new AppException(ErrorCode.EMAIL_VERIFICATION_FAILED);
        }
    }

    public void sendPasswordResetToken(String email) {
        User user = authenticationUserRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        String passwordResetCode = oneTimePasswordGenerator.generateOTP();
        user.setPasswordResetToken(encoder.encode(passwordResetCode));
        user.setPasswordResetTokenExpiryDate(new Date(Instant.now().plus(durationInMinutes, ChronoUnit.MINUTES).toEpochMilli()));
        authenticationUserRepository.save(user);
        emailService.sendEmail(SendEmailRequest.builder()
                .to(email)
                .subject("PASSWORD RESET")
                .body(passwordResetCode)
                .build());
    }

    public void resetPassword(PasswordResetRequest request){
        User user = authenticationUserRepository.findByEmail(request.getEmail()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        if (user.getPasswordResetToken() != null && encoder.matches(request.getToken(), user.getPasswordResetToken())) {
            if (user.getPasswordResetTokenExpiryDate().before(new Date())) {
                throw new AppException(ErrorCode.PASSWORD_RESET_TOKEN_EXPIRED);
            }
            if(encoder.matches(request.getNewPassword(), user.getPassword())){
                throw new AppException(ErrorCode.NEW_PASSWORD_CAN_NOT_SAME_OLD_PASSWORD);
            }

            user.setPassword(encoder.encode(request.getNewPassword()));
            user.setPasswordResetToken(null);
            user.setPasswordResetTokenExpiryDate(null);
            authenticationUserRepository.save(user);
        } else {
            throw new AppException(ErrorCode.PASSWORD_RESET_FAILED);
        }
    }

    public User updateUserProfile(Long id, UpdateUserRequest updateUserRequest, User authenticatedUser) {
        if(id == null || !id.equals(authenticatedUser.getId())) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        User user = authenticationUserRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        userMapper.updateUserProfile(user, updateUserRequest);

        user.setProfileComplete(isProfileComplete(user));

        user = authenticationUserRepository.save(user);

        return user;
    }

    public User updateProfileAbout(User user, User authenticatedUser){
        authenticatedUser.setAbout(user.getAbout());
        return authenticationUserRepository.save(authenticatedUser);
    }

    private boolean isProfileComplete(User user) {
        return user.getLastName() != null && !user.getLastName().isEmpty()
               && user.getFirstName() != null && !user.getFirstName().isEmpty()
               && user.getCompany() != null && !user.getCompany().isEmpty()
               && user.getPosition() != null && !user.getPosition().isEmpty()
               && user.getLocation() != null && !user.getLocation().isEmpty();
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = entityManager.find(User.class, userId);
        if (user != null) {
            entityManager.createNativeQuery("DELETE FROM posts_likes where user_id= :userId")
                    .setParameter("userId", userId)
                    .executeUpdate();
            authenticationUserRepository.deleteById(userId);
        }


    }

    public List<User> getUserWithoutAuthenticated(User user) {
        return authenticationUserRepository.findAllByIdNot(user.getId());
    }

    public List<User>  getRecommendations(Long userId, int limit) {
        User user = getUserById(userId);
        Set<User> secondDegreeConnections = getSecondDegreeConnections(user);
        if (secondDegreeConnections.isEmpty()) {
            secondDegreeConnections = new HashSet<>(findAllByIdNot(user.getId()));
        }
        List<UserRecommendation> recommendations = new ArrayList<>();
        secondDegreeConnections.stream().forEach(
                secondDegreeConnection -> {
                    double score = calculateSimilarityScore(user, secondDegreeConnection);
                    score += countMutualConnections(user, secondDegreeConnection) * 5.0; // Add weight for mutual connections
                    recommendations.add(new UserRecommendation(secondDegreeConnection, score));
                }
        );
        return recommendations.stream()
                .sorted(Comparator.comparingDouble(UserRecommendation::score).reversed())
                .limit(limit)
                .map(UserRecommendation::user)
                .collect(Collectors.toList());
    }

    private double calculateSimilarityScore(User user1, User user2) {
        double score = 0.0;

        if (user1.getCompany() != null && user1.getCompany().equals(user2.getCompany())) {
            score += 3.0; // Same company
        }
        if (user1.getPosition() != null && user1.getPosition().equals(user2.getPosition())) {
            score += 2.5; // Same position
        }
        if (user1.getLocation() != null && user1.getLocation().equals(user2.getLocation())) {
            score += 2.0; // Same location
        }
        if (user1.getAbout() != null && user1.getAbout().equals(user2.getAbout())) {
            score += 1.0; // Similar about section
        }

        return score;
    }

    private int countMutualConnections(User user1, User user2) {
        Set<User> user1Connections = new HashSet<>();

        user1.getInitiatedConnections().stream()
                .filter(conn -> conn.getStatus().equals(CONNECTION_STATUS.ACCEPTED))
                .forEach(conn -> user1Connections.add(conn.getRecipient()));
        user1.getReceivedConnections().stream()
                .filter(conn -> conn.getStatus().equals(CONNECTION_STATUS.ACCEPTED))
                .forEach(conn -> user1Connections.add(conn.getAuthor()));

        Set<User> user2Connections = new HashSet<>();
        user2.getInitiatedConnections().stream()
                .filter(conn -> conn.getStatus().equals(CONNECTION_STATUS.ACCEPTED))
                .forEach(conn -> user2Connections.add(conn.getRecipient()));
        user2.getReceivedConnections().stream()
                .filter(conn -> conn.getStatus().equals(CONNECTION_STATUS.ACCEPTED))
                .forEach(conn -> user2Connections.add(conn.getAuthor()));

        user1Connections.retainAll(user2Connections);
        return user1Connections.size();
    }

    private record UserRecommendation(User user, double score) {
    }

    private Set<User> getSecondDegreeConnections(User user) {
        Set<User> directConnections = new HashSet<>();

        user.getInitiatedConnections().stream()
                .filter(conn -> conn.getStatus().equals(CONNECTION_STATUS.ACCEPTED))
                .forEach(conn -> directConnections.add(conn.getRecipient()));

        user.getReceivedConnections().stream()
                .filter(conn -> conn.getStatus().equals(CONNECTION_STATUS.ACCEPTED))
                .forEach(conn -> directConnections.add(conn.getAuthor()));

        Set<User> secondDegreeConnections = new HashSet<>();


        for (User directConnection : directConnections) {
            directConnection.getInitiatedConnections().stream()
                    .filter(conn -> conn.getStatus().equals(CONNECTION_STATUS.ACCEPTED))
                    .forEach(conn -> secondDegreeConnections.add(conn.getRecipient()));

            directConnection.getReceivedConnections().stream()
                    .filter(conn -> conn.getStatus().equals(CONNECTION_STATUS.ACCEPTED))
                    .forEach(conn -> secondDegreeConnections.add(conn.getAuthor()));
        }

        secondDegreeConnections.remove(user); // Remove the user themselves from the recommendations
        secondDegreeConnections.removeAll(directConnections); // Remove direct connections to avoid duplicates
        // Xóa những người đã gửi // connection request đến người dùng (pending connections)
        secondDegreeConnections.removeAll(user.getInitiatedConnections().stream()
                .filter(conn -> conn.getStatus() == CONNECTION_STATUS.PENDING)
                .map(Connection::getRecipient)
                .collect(Collectors.toSet()));
        secondDegreeConnections.removeAll(user.getReceivedConnections().stream()
                .filter(conn -> conn.getStatus() == CONNECTION_STATUS.PENDING)
                .map(Connection::getAuthor)
                .collect(Collectors.toSet()));


        return secondDegreeConnections;
    }

    public User getUserById(Long id){
        return authenticationUserRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }

    public List<User> findAllByIdNot(Long id) {
        return authenticationUserRepository.findAllByIdNot(id);
    }

    public void updateLastLogin(Long userId) {
        User user = authenticationUserRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        user.setLastLogin(LocalDateTime.now());
        authenticationUserRepository.save(user);
    }
}
