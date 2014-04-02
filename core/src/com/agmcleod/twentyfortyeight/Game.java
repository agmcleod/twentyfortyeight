package com.agmcleod.twentyfortyeight;

import aurelienribon.tweenengine.Tween;
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
    private boolean moving;
    private Texture numbersTexture;
    private ShapeRenderer shapeRenderer;
    private TweenManager tweenManager;

    enum Direction {
        UP, DOWN, LEFT, RIGHT
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
    }

    public void assignTile() {
        int c = MathUtils.random(0, 3);
        int r = MathUtils.random(0, 3);
        GridSpace gs = gridSpaces[c][r];
        if(!gs.empty) {
            assignTile();
        }
        else {
            gs.setTile(new Tile(new Vector2(gs.getPos().x, gs.getPos().y), numbersTexture));
            gs.empty = false;
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        numbersTexture.dispose();
        shapeRenderer.dispose();
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

    public boolean canMoveInDirection(Direction direction) {
        switch(direction) {
            case UP:
                boolean canMoveUp = false;
                for(int c = 0; c < gridSpaces.length; c++) {
                    if(gridSpaces[c][3].empty) {
                        canMoveUp = true;
                    }
                }
                return canMoveUp;
            case DOWN:
                boolean canMoveDown = false;
                for(int c = 0; c < gridSpaces.length; c++) {
                    if(gridSpaces[c][0].empty) {
                        canMoveDown = true;
                    }
                }
                return canMoveDown;
            case LEFT:
                boolean canMoveLeft = false;
                for(int r = 0; r < gridSpaces[0].length; r++) {
                    if(gridSpaces[0][r].empty) {
                        canMoveLeft = true;
                    }
                }
                return canMoveLeft;
            case RIGHT:
                boolean canMoveRight = false;
                for(int r = 0; r < gridSpaces[3].length; r++) {
                    if(gridSpaces[3][r].empty) {
                        canMoveRight = true;
                    }
                }
                return canMoveRight;
            default:
                return false;
        }
    }

    @Override
    public boolean keyDown(int keycode) {
        switch(keycode) {
            case Input.Keys.UP:
                if(canMoveInDirection(Direction.UP)) {
                    shiftTilesUp();
                }
                break;
            case Input.Keys.DOWN:
                if(canMoveInDirection(Direction.DOWN)) {
                    shiftTilesDown();
                }
                break;
            case Input.Keys.LEFT:
                if(canMoveInDirection(Direction.LEFT)) {
                    shiftTilesLeft();
                }
                break;
            case Input.Keys.RIGHT:
                if(canMoveInDirection(Direction.LEFT)) {
                    shiftTilesRight();
                }
                break;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    public void shiftTilesDown() {
        moving = true;
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
                        Tile tile = gridSpaces[c][r].getTile();
                        tile.setYByRow(r - count, tweenManager);
                        gridSpaces[c][r].setTile(null);
                        gridSpaces[c][r].empty = true;

                        gridSpaces[c][r - count].setTile(tile);
                        gridSpaces[c][r - count].empty = false;
                    }
                }
            }
        }
    }

    public void shiftTilesLeft() {
        moving = true;
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
                        Tile tile = gridSpaces[c][r].getTile();
                        tile.setXByColumn(c - count, tweenManager);
                        gridSpaces[c][r].setTile(null);
                        gridSpaces[c][r].empty = true;

                        gridSpaces[c - count][r].setTile(tile);
                        gridSpaces[c - count][r].empty = false;
                    }
                }
            }
        }
    }

    public void shiftTilesRight() {
        moving = true;
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
                        Tile tile = gridSpaces[c][r].getTile();
                        tile.setXByColumn(c + count, tweenManager);
                        gridSpaces[c][r].setTile(null);
                        gridSpaces[c][r].empty = true;

                        gridSpaces[c + count][r].setTile(tile);
                        gridSpaces[c + count][r].empty = false;
                    }
                }
            }
        }
    }

    public void shiftTilesUp() {
        moving = true;
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
                        Tile tile = gridSpaces[c][r].getTile();
                        tile.setYByRow(r + count, tweenManager);
                        gridSpaces[c][r].setTile(null);
                        gridSpaces[c][r].empty = true;

                        gridSpaces[c][r + count].setTile(tile);
                        gridSpaces[c][r + count].empty = false;
                    }
                }
            }
        }
    }

    public void update() {
        tweenManager.update(Gdx.graphics.getDeltaTime());
    }

}
