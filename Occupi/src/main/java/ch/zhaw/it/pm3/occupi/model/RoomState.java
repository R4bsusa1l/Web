package ch.zhaw.it.pm3.occupi.model;

/**
 * Enumeration representing the current state of a room.
 */
public enum RoomState {
    /** Room is free and available */
    FREE,

    /** Room is currently occupied */
    OCCUPIED,

    /** Room has an active event */
    EVENT,

    /** Room is reserved */
    RESERVED,

    /** Room is fully occupied */
    FULL
}
