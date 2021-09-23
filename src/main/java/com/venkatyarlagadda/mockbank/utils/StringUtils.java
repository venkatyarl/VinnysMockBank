package com.venkatyarlagadda.mockbank.utils;

import com.venkatyarlagadda.mockbank.exceptions.InvalidUUIDException;

import java.util.UUID;

/**
 * @author Venkat Yarlagadda
 * @version V1
 */
public class StringUtils {

    public static UUID convertStringToUUID(final String uuidString) throws InvalidUUIDException {
        try {
            return UUID.fromString(uuidString);
        } catch (IllegalArgumentException e) {
            throw new InvalidUUIDException("Invalid UUID");
        }
    }

}