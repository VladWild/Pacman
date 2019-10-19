package com.vladwild.game.InputProcessors;

import com.badlogic.gdx.InputProcessor;
import com.vladwild.game.animals.Direction;

public class InputProcessorGame implements InputProcessor {
    private Direction direction;
    private int screenX0;
    private int screenY0;
    private int screenX;
    private int screenY;
    private int offset;
    private int pointer;

    public InputProcessorGame(int offset, Direction direction){
        this.offset = offset;
        this.direction = direction;
    }

    @Override
    public boolean keyDown(int keycode) {
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
        screenX0 = -1;
        screenY0 = -1;
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        screenX0 = -1;
        screenY0 = -1;
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if ((screenX0 == -1) && (screenY0 == -1)) {
            this.screenX0 = screenX;
            this.screenY0 = screenY;
        }
        this.screenX = screenX;
        this.screenY = screenY;
        this.pointer = pointer;
        if (this.screenX - this.screenX0 > offset) {
            this.screenX0 = screenX;
            this.screenY0 = screenY;
            this.direction = Direction.RIGTH;
        }
        if (this.screenX - this.screenX0 < -offset) {
            this.screenX0 = screenX;
            this.screenY0 = screenY;
            this.direction = Direction.LEFT;
        }
        if (this.screenY - this.screenY0 > offset) {
            this.screenX0 = screenX;
            this.screenY0 = screenY;
            this.direction = Direction.DOWN;
        }
        if (this.screenY - this.screenY0 < -offset) {
            this.screenX0 = screenX;
            this.screenY0 = screenY;
            this.direction = Direction.UP;
        }
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

    public int getScreenX(){
        return this.screenX;
    }

    public int getScreenY(){
        return this.screenY;
    }

    public int getPointer(){
        return this.pointer;
    }

    public int getScreenX0(){
        return this.screenX0;
    }

    public int getScreenY0(){
        return  this.screenY0;
    }

    public int getSubtractionX(){
        return this.screenX - this.screenX0;
    }

    public int getSubtractionY(){
        return this.screenY - this.screenY0;
    }

    public Direction getDirection(){
        return this.direction;
    }

    public void setDirection(Direction direction){
        this.direction = direction;
    }
}
