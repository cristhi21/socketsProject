package org.poli.ingsoftware.model;

import java.net.Socket;

public record User(String name, Socket socket) {

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", socket=" + socket +
                '}';
    }
}
