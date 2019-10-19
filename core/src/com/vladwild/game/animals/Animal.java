package com.vladwild.game.animals;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.TimeUtils;
import com.vladwild.game.game.GameScreen;
import com.vladwild.game.resource.ResourceManager;

//абстрактный класс животных
public abstract class Animal {
    protected static final String PROPERTY_ANIMALS = "property\\animals.properties";     //константа String, содержащая адрес файла animals.properties
    private static final String ANIMAL_EAT = "animal_eat";
    protected GameScreen gameScreen;
    protected Animation walkAnimation[] = new Animation[4];
    protected Sprite eatSprite;
    protected Sprite spriteCurrent;
    protected Direction direction;
    protected Direction directionNext;
    protected GridPoint2 positionLogic;
    protected GridPoint2 positionPixel;
    protected long speed;
    protected long currentTime;
    protected State state;
    protected float stateTime;

    Animal(GameScreen gameScreen, Direction direction, GridPoint2 positionLogic, long speed) {
        this.gameScreen = gameScreen;
        this.direction = direction;
        this.positionLogic = positionLogic;
        this.positionPixel = new GridPoint2(this.gameScreen.screen.blockSizeX * positionLogic.x,
                this.gameScreen.screen.blockSizeY * positionLogic.y);
        this.eatSprite = new Sprite(new Texture(new ResourceManager(PROPERTY_ANIMALS).getFileHandle(ANIMAL_EAT)));
        this.speed = speed;
        this.currentTime = TimeUtils.nanoTime();
        this.stateTime = 0f;
        this.state = State.LIVE;
    }

    //проверка на нахождение персонажа в центре одного логического квадрата
    public boolean inCenterLogicalSquare() {
        if ((this.getPositionPixelsStart().x % gameScreen.screen.blockSizeX == 0) &&
                (this.getPositionPixelsStart().y % gameScreen.screen.blockSizeY == 0)){
            return true;
        } else {
            return false;
        }
    }

    //обнавление логических координат персонажа
    protected void changeLogic(){
        this.positionLogic.x = this.positionPixel.x / gameScreen.screen.blockSizeX;
        this.positionLogic.y = this.positionPixel.y / gameScreen.screen.blockSizeY;
    }

    private boolean testInField(){
        if ((getPositionPixelsStart().x > gameScreen.screen.blockSizeX) &&
        (getPositionPixelsStart().x < gameScreen.screen.blockSizeX * (gameScreen.screen.fieldSizeX - 2))) {
            return true;
        } else {
            return false;
        }
    }

    public void setPositionLeft(int pixel){
        if (testInField()) {
            if (Math.abs((this.positionPixel.x - pixel) - this.positionLogic.x * gameScreen.screen.blockSizeX) >
                    gameScreen.screen.blockSizeX) {
                pixel = this.positionPixel.x % (gameScreen.screen.blockSizeX * (this.positionLogic.x - 1));
            }
            //для безопастности
            if (pixel >= gameScreen.screen.blockSizeX) {
                this.positionLogic = new GridPoint2(this.gameScreen.screen.blockSizeX * (this.gameScreen.screen.fieldSizeX - 1), 16 + gameScreen.shift);
                this.positionPixel = new GridPoint2(this.gameScreen.screen.blockSizeX * this.positionLogic.x,
                        this.gameScreen.screen.blockSizeY * this.positionLogic.y);
            }
        } else {
            pixel = 2;
        }
        this.positionPixel.set(this.positionPixel.x - pixel, this.positionPixel.y);
        this.direction = Direction.LEFT;
        this.currentTime = TimeUtils.nanoTime();
        if (inCenterLogicalSquare()) {
            changeLogic();
        }
    }

    public void setPositionRigth(int pixel){
        if (testInField()) {
            if (Math.abs((this.positionPixel.x + pixel) - this.positionLogic.x * gameScreen.screen.blockSizeX) >
                    gameScreen.screen.blockSizeX) {
                pixel = (gameScreen.screen.blockSizeX * (this.positionLogic.x + 1)) % this.positionPixel.x;
            }
            //для безопастности
            if (pixel >= gameScreen.screen.blockSizeX) {
                this.positionLogic = new GridPoint2(0, 16 + gameScreen.shift);
                this.positionPixel = new GridPoint2(this.gameScreen.screen.blockSizeX * this.positionLogic.x,
                        this.gameScreen.screen.blockSizeY * this.positionLogic.y);
            }
            //}
        } else {
            pixel = 2;
        }
        this.positionPixel.set(this.positionPixel.x + pixel, this.positionPixel.y);
        this.direction = Direction.RIGTH;
        this.currentTime = TimeUtils.nanoTime();
        if (inCenterLogicalSquare()) {
            changeLogic();
        }
    }

    public void setPositionUp(int pixel){
        if (Math.abs((this.positionPixel.y + pixel) - this.positionLogic.y * gameScreen.screen.blockSizeY) >
                gameScreen.screen.blockSizeY ) {
            pixel = (gameScreen.screen.blockSizeY * (this.positionLogic.y + 1)) % this.positionPixel.y;
        }
        this.positionPixel.set(this.positionPixel.x, this.positionPixel.y + pixel);
        this.direction = Direction.UP;
        this.currentTime = TimeUtils.nanoTime();
        if (inCenterLogicalSquare()) {
            changeLogic();
        }
    }

    public void setPositionDown(int pixel){
        if (Math.abs((this.positionPixel.y - pixel) - this.positionLogic.y * gameScreen.screen.blockSizeY) >
                gameScreen.screen.blockSizeY ) {
            pixel = this.positionPixel.y % (gameScreen.screen.blockSizeY * (this.positionLogic.y - 1));
        }
        this.positionPixel.set(this.positionPixel.x, (int) (this.positionPixel.y - pixel));
        this.direction = Direction.DOWN;
        this.currentTime = TimeUtils.nanoTime();
        if (inCenterLogicalSquare()) {
            changeLogic();
        }
    }


    public void setPositionStop(){
        this.positionPixel.set(this.positionPixel.x, this.positionPixel.y);
        this.directionNext = this.direction;
        this.direction = Direction.STOP;
        this.currentTime = TimeUtils.nanoTime();
        if (inCenterLogicalSquare()) {
            changeLogic();
        }
    }

    //ввод направления
    protected void setDirection(Direction directionIn, int pixel){
        switch (directionIn){
            case LEFT:
                this.setPositionLeft(pixel);
                break;
            case RIGTH:
                this.setPositionRigth(pixel);
                break;
            case DOWN:
                this.setPositionDown(pixel);
                break;
            case UP:
                this.setPositionUp(pixel);
                break;
        }
    }

    public Direction getDirection(){return this.direction; }

    public void setDirection(Direction direction) {this.direction = direction; }

    //присвоение нового экрана при смене уровня
    public  void  setScreen(GameScreen gameScreen) {this.gameScreen = gameScreen;}

    public GridPoint2 getPositionLogic(){
        return this.positionLogic;
    }

    public GridPoint2 getPositionPixelsStart(){return new GridPoint2(positionPixel.x, positionPixel.y); }

    public long getSpeed(){
        return  this.speed;
    }

    public long getCurrentTime(){
        return this.currentTime;
    }

    public State getState() { return  state; }

    abstract public Sprite getSprite();

    abstract public void newStartCoordinates();

    abstract public void setState(State state);

    abstract public void move(Direction directionIn, int pixel);

    abstract public void nextDiriction(int pixel);

    abstract protected boolean reverseDirection(Direction direction);

}
