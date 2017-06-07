package com.bergerkiller.bukkit.tc.controller.components;

import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.rails.logic.RailLogic;
import com.bergerkiller.bukkit.tc.rails.logic.RailLogicGround;
import com.bergerkiller.bukkit.tc.rails.logic.RailLogicVertical;
import com.bergerkiller.bukkit.tc.rails.type.RailType;
import com.bergerkiller.bukkit.tc.utils.TrackIterator;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

/**
 * Stores rail information of a Minecart Member
 */
public class RailTrackerMember extends RailTracker {
    private final MinecartMember<?> owner;
    private RailInfo lastRail, rail;
    private RailLogic lastRailLogic, railLogic;
    private boolean railLogicSnapshotted = false;

    public RailTrackerMember(MinecartMember<?> owner) {
        this.owner = owner;
        this.lastRail = this.rail = new RailInfo(null, RailType.NONE, BlockFace.SELF);
        this.lastRailLogic = this.railLogic = RailLogicGround.INSTANCE;
    }

    /**
     * Refreshes the basic information with the information from the owner
     */
    public void onAttached() {
        this.lastRail = this.rail = findInfo(this.owner);
        this.lastRailLogic = this.railLogic = null;
        this.railLogicSnapshotted = false;
    }

    /**
     * Obtains a new track iterator iterating the tracks from this point towards the direction
     * the Minecart is moving.
     *
     * @return forward track iterator
     */
    public TrackIterator getTrackIterator() {
        return new TrackIterator(this.rail.railsBlock, this.owner.getDirectionTo());
    }

    /**
     * Gets the rail type of the current tick
     *
     * @return current rail type
     */
    public RailType getRailType() {
        return this.rail.railsType;
    }

    /**
     * Gets the rail type from the previous tick
     *
     * @return previous rail type
     */
    public RailType getLastRailType() {
        return this.lastRail.railsType;
    }

    /**
     * Gets the block of the current tick
     *
     * @return current block
     */
    public Block getBlock() {
        return this.rail.railsBlock;
    }

    /**
     * Gets the position of the rail of the current tick
     * 
     * @return current rail position
     */
    public IntVector3 getBlockPos() {
        return this.rail.railsPos;
    }

    /**
     * Gets the block from the previous tick
     *
     * @return previous block
     */
    public Block getLastBlock() {
        return this.lastRail.railsBlock;
    }

    /**
     * Gets the rail logic of the current tick
     *
     * @return current rail logic
     */
    public RailLogic getRailLogic() {
        if (railLogicSnapshotted) {
            return this.railLogic;
        } else {
            return this.rail.railsType.getLogic(this.owner, this.rail.railsBlock);
        }
    }

    /**
     * Gets the rail logic from the previous tick
     *
     * @return previous rail logic
     */
    public RailLogic getLastLogic() {
        return lastRailLogic;
    }

    /**
     * Checks whether the current rails block has changed
     *
     * @return True if the block changed, False if not
     */
    public boolean hasBlockChanged() {
        Block a = lastRail.railsBlock;
        Block b = rail.railsBlock;
        return a.getX() != b.getX() || a.getY() != b.getY() || a.getZ() != b.getZ();
    }

    /**
     * Stops using the Rail Logic snapshot for the next run
     */
    public void setLiveRailLogic() {
        this.railLogicSnapshotted = false;
    }

    /**
     * Creates a snapshot of the Rail Logic for the entire next run
     */
    public void snapshotRailLogic() {
        this.railLogic = this.rail.railsType.getLogic(this.owner, this.rail.railsBlock);
        if (this.railLogic instanceof RailLogicVertical) {
            this.rail = new RailInfo(this.rail.railsBlock, RailType.VERTICAL, this.rail.direction);
        }
        this.railLogicSnapshotted = true;
    }

    public void refresh(RailInfo newInfo) {
        // Store the last rail information
        this.lastRail = this.rail;
        this.lastRailLogic = this.railLogic;

        // Gather rail information
        owner.vertToSlope = false;

        // Refresh
        this.rail = newInfo;
        this.railLogic = null;
        this.railLogicSnapshotted = false;
    }

}