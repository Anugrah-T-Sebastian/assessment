package com.inrix.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ZoneLocationReferenceTest {

    @Test
    void testSetAndGetSide() {
        // Arrange
        Zone.LocationReference locationReference = new Zone.LocationReference();
        String side = "left";

        // Act
        locationReference.setSide(side);

        // Assert
        assertEquals(side, locationReference.getSide());
    }
}
