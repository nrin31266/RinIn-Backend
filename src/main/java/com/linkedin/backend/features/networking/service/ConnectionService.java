package com.linkedin.backend.features.networking.service;

import com.linkedin.backend.features.authentication.model.User;
import com.linkedin.backend.features.networking.domain.CONNECTION_STATUS;
import com.linkedin.backend.features.networking.model.Connection;

import java.util.List;

public interface ConnectionService {
    List<Connection> getUserConnections(User user, CONNECTION_STATUS status);
    Connection sendConnectionRequest(User author, Long Id);
    Connection acceptConnectionRequest(User recipient, Long connectionId);
    Connection rejectOrCancelConnection(User user,Long connectionId);
    Connection markConnectionAsSeen(User recipient, Long connectionId);


}
