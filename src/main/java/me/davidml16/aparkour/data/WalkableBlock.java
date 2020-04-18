package me.davidml16.aparkour.data;

import java.util.Arrays;
import java.util.List;

public class WalkableBlock {

    public static final List<Integer> directionablesIDs = Arrays.asList(
            17, 23, 26, 27, 28, 29, 33, 44, 50, 53, 54, 55, 61, 62, 64, 64, 65, 66, 67, 69,
            71, 75, 76, 77, 86, 91, 93, 94, 96, 106, 107, 108, 109, 111, 114, 123, 124, 126,
            127, 128, 130, 131, 132, 134, 135, 136, 143, 144, 145, 146, 149, 150, 154, 155,
            156, 157, 158, 162, 163, 164, 165, 167, 170, 176, 180, 182, 183, 184, 185, 186,
            187, 193, 194, 195, 196, 197, 198, 202, 203, 205, 216, 218, 219, 220, 221, 222,
            223, 224, 225, 226, 227, 228, 229, 230, 231, 232, 233, 234, 235, 236, 237, 238,
            239, 240, 241, 242, 243, 244, 245, 246, 247, 248, 249, 250
    );

    public static final List<Integer> skullIDs = Arrays.asList(
            397, 144
    );

    private int id;
    private byte data;

    private boolean directional;
    private boolean skull;

    public WalkableBlock(int id, byte data) {
        this.id = id;
        this.data = data;
        this.directional = directionablesIDs.contains(id);
        this.skull = skullIDs.contains(id);
    }

    public int getId() {
        return id;
    }

    public byte getData() {
        return data;
    }

    public boolean isDirectional() {
        return directional;
    }

    public boolean isSkull() { return skull; }

    @Override
    public String toString() {
        return "WalkableBlock{" +
                "id=" + id +
                ", data=" + data +
                ", directional=" + directional +
                '}';
    }

}
