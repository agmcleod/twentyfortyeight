package com.agmcleod.twentyfortyeight;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenAccessor;

/**
 * Created by aaronmcleod on 2014-04-01.
 */
public class TileAccessor implements TweenAccessor<Tile> {
    static final int POSITION_X = 0;
    static final int POSITION_Y = 1;

    @Override
    public int getValues(Tile target, int tweenType, float[] returnValues) {
        switch (tweenType) {
            case POSITION_X: returnValues[0] = target.getPos().x; return 1;
            case POSITION_Y: returnValues[0] = target.getPos().y; return 1;
            default: assert false; return -1;
        }
    }

    @Override
    public void setValues(Tile target, int tweenType, float[] newValues) {
        switch (tweenType) {
            case POSITION_X: target.getPos().x = newValues[0]; break;
            case POSITION_Y: target.getPos().y = newValues[0]; break;
            default: assert false; break;
        }
    }

}
