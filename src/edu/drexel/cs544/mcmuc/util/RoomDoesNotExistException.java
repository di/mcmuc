package edu.drexel.cs544.mcmuc.util;

/**
 * RoomDoesNotExistException is returned whenever a room is requested that does not exist
 */
@SuppressWarnings("serial")
public class RoomDoesNotExistException extends Exception {
    String roomName;

    public RoomDoesNotExistException(String roomName) {
        this.roomName = roomName;
    }

    /**
     * Give a message for the Exception
     */
    @Override
    public String getMessage() {
        return "Room: \"" + this.roomName + "\" does not exist";
    }
}
