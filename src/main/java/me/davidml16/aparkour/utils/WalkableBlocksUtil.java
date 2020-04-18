package me.davidml16.aparkour.utils;

import me.davidml16.aparkour.data.WalkableBlock;

import java.util.List;

public class WalkableBlocksUtil {

    public static boolean noContainsWalkable(List<WalkableBlock> walkableBlocks, int id, byte data) {
        for(WalkableBlock walkableBlock : walkableBlocks) {
            if(id == 43 && data == walkableBlock.getData() && walkableBlock.getId() == 44) return false;
            if(id == 125 && data == walkableBlock.getData() && walkableBlock.getId() == 126) return false;
            if(walkableBlock.isDirectional() && walkableBlock.getId() == id) return false;
            if(walkableBlock.getId() == id && walkableBlock.getData() == data) return false;
            if(walkableBlock.isSkull() && WalkableBlock.skullIDs.contains(id)) return false;
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
