package com.agmcleod.twentyfortyeight;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.MathUtils;

/**
 * Created by aaronmcleod on 2014-03-30.
 */
public class Tile {

    static final int SIZE = 140;

    private int value;
    private Game game;
    private Vector2 pos;
    private Texture texture;
    private Vector2 textureOffset;

    public Tile(Vector2 pos, Texture texture, Game game) {
        this.pos = pos;
        textureOffset = new Vector2();
        setValue(MathUtils.random(0, 1));
        this.texture = texture;
        this.game = game;
    }

    public Vector2 getPos() {
        return this.pos;
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, this.pos.x, this.pos.y, (int) this.textureOffset.x, (int) this.textureOffset.y, SIZE, SIZE);
    }

    public void setValue(int value) {
        this.value = value;
        if(value > 5) {
            textureOffset.y = SIZE;
            textureOffset.x = (value - 6) * SIZE;
        }
        else {
            textureOffset.y = 0;
            textureOffset.x = value * SIZE;
        }
    }

    public void setXByColumn(int c, TweenManager tweenManager) {
        int x = c * SIZE + ((c + 1) * Game.TILE_SPACING);
        Tween.to(this, TileAccessor.POSITION_X, 0.1f).target(x).start(tweenManager).setCallback(game.getMoveCallback());
    }

    public void setYByRow(int r, TweenManager tweenManager) {
        int y = r * SIZE + ((r + 1) * Game.TILE_SPACING) + Game.yOffset;
        Tween.to(this, TileAccessor.POSITION_Y, 0.1f).target(y).start(tweenManager).setCallback(game.getMoveCallback());
    }

    public void update(float delta) {

    }
}
