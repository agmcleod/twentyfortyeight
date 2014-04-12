package com.agmcleod.twentyfortyeight;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Game extends ApplicationAdapter implements InputProcessor {
    static final int TILE_SPACING = 16;

    static int yOffset;
    private SpriteBatch batch;
    private GridSpace[][] gridSpaces;
    private int moveAmount = 0;
    private boolean moving;
    private TweenCallback moveCallback;
    private Texture numbersTexture;
    private ShapeRenderer shapeRenderer;

    private TweenManager tweenManager;

    public void assignTile() {
        int c = MathUtils.random(0, 3);
        int r = MathUtils.random(0, 3);
        GridSpace gs = gridSpaces[c][r];
        if(!gs.empty) {
            assignTile();
        }
        else {
            gs.setTile(new Tile(new Vector2(gs.getPos().x, gs.getPos().y), numbersTexture, this));
            gs.empty = false;
        }
    }

    public boolean canMoveAtAll() {
        int emptyCount = 0;
        for(int c = 0; c < gridSpaces.length; c++) {
            for(int r = 0; r < gridSpaces[c].length; r++) {
                if(gridSpaces[c][r].empty) {
                    emptyCount++;
                }
            }
        }

        return emptyCount > 0;
    }

    @Override
    public void create () {
        batch = new SpriteBatch();
        gridSpaces = new GridSpace[4][4];
        moving = false;
        tweenManager = new TweenManager();
        Tween.registerAccessor(Tile.class, new TileAccessor());
        yOffset = Gdx.graphics.getHeight() - (Tile.SIZE * 4 + TILE_SPACING * 5);
        for(int c = 0; c < 4; c++) {
            for(int r = 0; r < 4; r++) {
                GridSpace gs = new GridSpace(
                    new Vector2(
                        Tile.SIZE * c + (TILE_SPACING * c) + TILE_SPACING,
                        Tile.SIZE * r + yOffset + (TILE_SPACING * r) + TILE_SPACING
                    )
                );
                gridSpaces[c][r] = gs;
            }
        }

        Gdx.input.setInputProcessor(this);

        numbersTexture = new Texture(Gdx.files.internal("numbers.png"));
        shapeRenderer = new ShapeRenderer();
        assignTile();
        assignTile();

        moveCallback = new TweenCallback() {
            @Override
            public void onEvent(int type, BaseTween<?> source) {
                moveAmount--;
                if(moveAmount <= 0) {
                    moving = false;
                    assignTile();
                }
            }
        };
    }

    @Override
    public void dispose() {
        batch.dispose();
        numbersTexture.dispose();
        shapeRenderer.dispose();
    }

    public TweenCallback getMoveCallback() {
        return moveCallback;
    }

    @Override
    public boolean keyDown(int keycode) {
        switch(keycode) {
            case Input.Keys.UP:
                if(!moving) {
                    shiftTilesUp();
                }
                break;
            case Input.Keys.DOWN:
                if(!moving) {
                    shiftTilesDown();
                }
                break;
            case Input.Keys.LEFT:
                if(!moving) {
                    shiftTilesLeft();
                }
                break;
            case Input.Keys.RIGHT:
                if(!moving) {
                    shiftTilesRight();
                }
                break;
        }
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public void render () {
        update();
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.95f, 0.95f, 0.95f, 1f);
        for(int c = 0; c < gridSpaces.length; c++) {
            for(int r = 0; r < gridSpaces[c].length; r++) {
                GridSpace gs = gridSpaces[c][r];
                shapeRenderer.rect(gs.getPos().x, gs.getPos().y, Tile.SIZE, Tile.SIZE);
            }
        }
        shapeRenderer.end();

        batch.begin();
        for(int c = 0; c < gridSpaces.length; c++) {
            for(int r = 0; r < gridSpaces[c].length; r++) {
                GridSpace gs = gridSpaces[c][r];
                if(!gs.empty && gs.getTile() != null) {
                    gs.getTile().render(batch);
                }
            }
        }
        batch.end();
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
    
    public void shiftTile(int c, int r, int tc, int tr) {
        moving = true;
        Tile tile = gridSpaces[c][r].getTile();
        if(tc != c) {
            tile.setXByColumn(tc, tweenManager, moveCallback);
        }
        else if(tr != r) {
            tile.setYByRow(tr, tweenManager, moveCallback);
        }

        moveAmount++;
        gridSpaces[c][r].setTile(null);
        gridSpaces[c][r].empty = true;

        gridSpaces[tc][tr].setTile(tile);
        gridSpaces[tc][tr].empty = false;
    }

    public void shiftTilesDown() {
        moveAmount = 0;
        for(int c = 0; c < gridSpaces.length; c++) {
            for(int r = 0; r < gridSpaces[c].length; r++) {
                if(!gridSpaces[c][r].empty) {
                    int count = 0;
                    for(int belowRow = 0; belowRow < r; belowRow++) {
                        if(gridSpaces[c][belowRow].empty) {
                            count++;
                        }
                    }

                    if(count > 0) {
                        shiftTile(c, r, c, r - count);
                        tryToCombine(c, r - count, c, r - count - 1);
                    }
                }
            }
        }
    }

    public void shiftTilesLeft() {
        moveAmount = 0;
        for(int r = 0; r < gridSpaces[0].length; r++) {
            for(int c = 0; c < gridSpaces.length; c++) {
                if(!gridSpaces[c][r].empty) {
                    int count = 0;
                    for(int leftRow = 0; leftRow < c; leftRow++) {
                        if(gridSpaces[leftRow][r].empty) {
                            count++;
                        }
                    }

                    if(count > 0) {
                        shiftTile(c, r, c - count, r);
                        tryToCombine(c - count, r, c - count - 1, r);
                    }
                }
            }
        }
    }

    public void shiftTilesRight() {
        moveAmount = 0;
        for(int r = 0; r < gridSpaces[0].length; r++) {
            for(int c = gridSpaces.length - 1; c >= 0; c--) {
                if(!gridSpaces[c][r].empty) {
                    int count = 0;
                    for(int rightRow = gridSpaces.length - 1; rightRow > c; rightRow--) {
                        if(gridSpaces[rightRow][r].empty) {
                            count++;
                        }
                    }

                    if(count > 0) {
                        shiftTile(c, r, c + count, r);
                        tryToCombine(c + count, r, c + count + 1, r);
                    }
                }
            }
        }
    }

    public void shiftTilesUp() {
        moveAmount = 0;
        for(int c = 0; c < gridSpaces.length; c++) {
            int cLen = gridSpaces[c].length - 1;
            for(int r = cLen; r >= 0; r--) {
                if(!gridSpaces[c][r].empty) {
                    int count = 0;
                    for(int aboveRow = cLen; aboveRow > r; aboveRow--) {
                        if (gridSpaces[c][aboveRow].empty) {
                            count++;
                        }
                    }

                    if(count > 0) {
                        shiftTile(c, r, c, r + count);
                        tryToCombine(c, r + count, c, r + count + 1);
                    }
                }
            }
        }
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    public void tryToCombine(int column, int row, int targetColumn, int targetRow) {
        if(targetColumn > -1 && targetRow > -1 && gridSpaces.length > targetColumn && gridSpaces[targetColumn].length > targetRow) {
            if(gridSpaces[targetColumn][targetRow].getTileValue() == gridSpaces[column][row].getTileValue()) {
                GridSpace oldSpace = gridSpaces[column][row];
                GridSpace targetSpace = gridSpaces[targetColumn][targetRow];
                oldSpace.setTile(null);
                oldSpace.empty = true;
                targetSpace.getTile().setValue(targetSpace.getTileValue() + 1);
            }
        }
    }

    public void update() {
        tweenManager.update(Gdx.graphics.getDeltaTime());
    }

}
