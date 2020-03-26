package me.davidml16.aparkour.utils;

import me.davidml16.aparkour.data.WalkableBlock;

import java.util.List;

public class WalkableBlocksUtil {

    public static boolean noContainsWalkable(List<WalkableBlock> walkableBlocks, int id, byte data) {
        for(WalkableBlock walkableBlock : walkableBlocks) {
            if(id == 43 && data == walkableBlock.getData() && walkableBlock.getId() == 44) return false;
            if(walkableBlock.isDirectional()) {
                if(walkableBlock.getId() == id) return false;
            } else {
                if(walkableBlock.getId() == id && walkableBlock.getData() == data) return false;
            }
        }
        return true;
    }

    public static WalkableBlock getWalkableBlock(List<WalkableBlock> walkableBlocks, int id, byte data) {
        for(WalkableBlock walkableBlock : walkableBlocks) {
            if(walkableBlock.getId() == id && walkableBlock.getData() == data) return walkableBlock;
        }
        return null;
    }

}
