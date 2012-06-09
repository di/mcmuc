package edu.drexel.cs544.mcmuc.util;

/**
 * RoomDoesNotExistError is returned whenever a room is requested that does not exist
 */
@SuppressWarnings("serial")
public class RoomDoesNotExistError extends Exception {
    String roomName;

    public RoomDoesNotExistError(String roomName) {
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
