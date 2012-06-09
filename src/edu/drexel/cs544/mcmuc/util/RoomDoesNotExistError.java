package edu.drexel.cs544.mcmuc.util;

@SuppressWarnings("serial")
public class RoomDoesNotExistError extends Exception {
    String roomName;

    public RoomDoesNotExistError(String roomName) {
        this.roomName = roomName;
    }

    @Override
    public String getMessage() {
        return "Room: \"" + this.roomName + "\" does not exist";
    }
}
