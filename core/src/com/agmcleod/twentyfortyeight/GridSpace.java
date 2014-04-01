package com.agmcleod.twentyfortyeight;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by aaronmcleod on 2014-03-30.
 */
public class GridSpace {
    private Vector2 pos;
    public boolean empty;
    private Tile tile;

    public GridSpace(Vector2 pos) {
        empty = true;
        this.pos = pos;
    }

    public Vector2 getPos() {
        return pos;
    }

    public Tile getTile() {
        return tile;
    }

    public void setTile(Tile tile) {
        this.tile = tile;
    }
}