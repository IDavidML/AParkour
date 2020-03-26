package me.davidml16.aparkour.data;

import java.util.Arrays;
import java.util.List;

public class WalkableBlock {

    private static List<Integer> directionablesIDs = Arrays.asList(
            17, 23, 26, 29, 33, 44, 53, 54, 61, 62, 64, 65, 67, 71, 86, 91, 96, 108, 109,
            114, 123, 124, 128, 130, 134, 135, 136, 145, 146, 154, 156, 158, 162, 163, 164,
            167, 180, 182, 183, 184, 185, 186, 187, 193, 194, 195, 196, 197, 198, 203, 216,
            218, 219, 220, 221, 222, 223, 224, 225, 226, 227, 228, 229, 230, 231, 232, 233,
            234, 235, 236, 237, 238, 239, 240, 241, 242, 243, 244, 245, 246, 247, 248, 249, 250
    );

    private int id;
    private byte data;

    private boolean directional;

    public WalkableBlock(int id, byte data) {
        this.id = id;
        this.data = data;
        this.directional = directionablesIDs.contains(id);
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

}
