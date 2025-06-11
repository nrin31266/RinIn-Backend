package com.linkedin.backend.features.networking.controller;

import com.linkedin.backend.dto.ApiResponse;
import com.linkedin.backend.features.authentication.model.User;
import com.linkedin.backend.features.networking.domain.CONNECTION_STATUS;
import com.linkedin.backend.features.networking.model.Connection;
import com.linkedin.backend.features.networking.service.ConnectionService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/networking")
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ConnectionController {
    ConnectionService connectionService;

    @GetMapping("/connections")
    public ApiResponse<List<Connection>> getUserConnections(@RequestAttribute("authenticatedUser") User user, @RequestParam(value = "status", defaultValue = "ACCEPTED") CONNECTION_STATUS status) {
        List<Connection> connections = connectionService.getUserConnections(user, status);
        return ApiResponse.<List<Connection>>builder()
                .data(connections)
                .message("Fetched user connections successfully")
                .build();
    }
    @PostMapping("/connections")
    public ApiResponse<Connection> sendConnectionRequest(@RequestAttribute("authenticatedUser") User author, @RequestParam("recipientId") Long recipientId) {
        Connection connection = connectionService.sendConnectionRequest(author, recipientId);
        return ApiResponse.<Connection>builder()
                .data(connection)
                .message("Connection request sent successfully")
                .build();
    }
    @PutMapping("/connections/{connectionId}/accept")
    public ApiResponse<Connection> acceptConnectionRequest(@RequestAttribute("authenticatedUser") User recipient, @PathVariable Long connectionId) {
        Connection connection = connectionService.acceptConnectionRequest(recipient, connectionId);
        return ApiResponse.<Connection>builder()
                .data(connection)
                .message("Connection request accepted successfully")
                .build();
    }
    @PutMapping("/connections/{connectionId}/reject")
    public ApiResponse<Connection> rejectOrCancelConnection(@RequestAttribute("authenticatedUser") User user, @PathVariable Long connectionId) {
        Connection connection = connectionService.rejectOrCancelConnection(user, connectionId);
        return ApiResponse.<Connection>builder()
                .data(connection)
                .message("Connection request rejected or cancelled successfully")
                .build();
    }
    @PutMapping("/connections/{connectionId}/seen")
    public ApiResponse<Connection> markConnectionAsSeen(@RequestAttribute("authenticatedUser") User recipient, @PathVariable Long connectionId) {
        Connection connection = connectionService.markConnectionAsSeen(recipient, connectionId);
        return ApiResponse.<Connection>builder()
                .data(connection)
                .message("Connection marked as seen successfully")
                .build();
    }
    @GetMapping("/connections/suggestions")
    public ApiResponse<List<User>> getConnectionSuggestions(@RequestAttribute("authenticatedUser") User user) {
        List<User> suggestions = connectionService.getConnectionSuggestions(user);
        return ApiResponse.<List<User>>builder()
                .data(suggestions)
                .message("Fetched connection suggestions successfully")
                .build();
    }



}
