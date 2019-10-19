package com.vladwild.game.animals;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.utils.TimeUtils;
import com.vladwild.game.game.GameScreen;
import com.vladwild.game.resource.ResourceManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Monster extends Animal {
    private static final String MONSTER_textures [][] =                                         //массив ключей в файле properties
            {{"monster_0" ,"_left_01"}, {"monster_0", "_left_02"}, {"monster_0", "_rigth_01"}, {"monster_0", "_rigth_02"},
                    {"monster_0", "_down_01"}, {"monster_0", "_down_02"}, {"monster_0", "_up_01"}, {"monster_0", "_up_02"}};
    private static final String MONSTER_RUN [] = {"monster_01_runing", "monster_02_runing",     //массив ключей в файле properties
                                                  "monster_03_runing", "monster_04_runing"};
    private static final String MONSTER_DEAD [] =                                               //массив ключей в файле properties
            {"monster_dead_left", "monster_dead_rigth", "monster_dead_down", "monster_dead_up"};
    private static final int numberTextures = 8;                                                //количество текстур одного монстра
    private static final int numberTexturesSide = 2;                                            //количество текстур монстра с одной стороны
    private static final int numberSide = 4;
    private static final int numberRaning = 4;
    private static final int coordinateX = 13; //13
    private static final int coordinateY = 16; //16
    private static final int MAX_VALUE_SPEED = 1;
    private static final float speedAnimation = 0.1f;
    private static Animation animationRun;
    private static Animation animationRuning;
    private static Sprite [] deadSprite;
    private Map<Direction, Float> hashMapPoint;
    private List<Entry<Direction, Float>> list;
    private int numberMonsters;
    private int number;
    private int speed;

    public Monster(GameScreen gameScreen, Direction direction, long speed, int numberMonster, int number) {
        super(gameScreen, direction, new GridPoint2(coordinateX + number % 4, coordinateY + gameScreen.shift), speed);
        this.numberMonsters = numberMonster;
        Sprite sprites[] = new Sprite[numberTextures];

        String MONSTER_textures_key [] = new String[numberTextures];
        for (int i = 0; i < numberTextures; i++){
            MONSTER_textures_key [i] = MONSTER_textures[i][0] + String.valueOf(numberMonster) + MONSTER_textures[i][1];
        }

        for (int i = 0; i < numberTextures; i++) {
            sprites[i] = new Sprite(new Texture(new ResourceManager(PROPERTY_ANIMALS).getFileHandle(MONSTER_textures_key[i])));
        }
        for (int i = 0; i < numberTextures/numberTexturesSide; i++) {
            this.walkAnimation[i] = new Animation(speedAnimation, sprites[numberTexturesSide * i], sprites[numberTexturesSide * i + 1]);
        }

        Sprite spritesRun[] = new Sprite[numberTexturesSide];
        for (int i = 0; i < numberTexturesSide; i++) {
            spritesRun[i] = new Sprite(new Texture(new ResourceManager(PROPERTY_ANIMALS).getFileHandle(MONSTER_RUN[i])));
        }
        this.animationRun = new Animation(speedAnimation, spritesRun);

        deadSprite = new Sprite[numberSide];
        for (int i = 0; i < numberSide; i++) {
            deadSprite[i] = new Sprite(new Texture(new ResourceManager(PROPERTY_ANIMALS).getFileHandle(MONSTER_DEAD[i])));
        }

        Sprite spritesRuning[] = new Sprite[numberRaning];
        for (int i = 0; i < numberRaning; i++) {
            spritesRuning[i] = new Sprite(new Texture(new ResourceManager(PROPERTY_ANIMALS).getFileHandle(MONSTER_RUN[i])));
        }
        this.animationRuning = new Animation(speedAnimation, spritesRuning);

        this.number = number;
        speed = 0;

    }

    //смена позиции монстра при выходде за пределы поля
    private void setPositionLogic(GridPoint2 newPosition) {
        if (this.positionLogic.x > (int) (gameScreen.screen.fieldSizeX / 2)) {
            this.positionLogic = newPosition;
            this.positionPixel = new GridPoint2(this.gameScreen.screen.blockSizeX * (newPosition.x - 1),
                    this.gameScreen.screen.blockSizeY * newPosition.y);
        } else {
            this.positionLogic = newPosition;
            this.positionPixel = new GridPoint2(this.gameScreen.screen.blockSizeX * newPosition.x,
                    this.gameScreen.screen.blockSizeY * newPosition.y);
        }
    }

    //проверка на невозможность возврата домой
    private boolean home(Direction newDirection){
        if ((((this.getPositionLogic().x == 14) && (this.getPositionLogic().y == (19 + gameScreen.shift))) ||
            ((this.getPositionLogic().x == 15) && (this.getPositionLogic().y == (19 + gameScreen.shift)))) &&
            (newDirection == Direction.DOWN)){
            return true;
        } else {
            return false;
        }
    }

    private boolean addSpeed(){
        speed++;
        if (speed > MAX_VALUE_SPEED){
            speed = 0;
            return true;
        } else {
            return false;
        }
    }

    //смена направления в обратную сторону
    public Direction dirictionReverse(Direction directionIn){
        Direction direction = null;
        switch (directionIn) {
            case LEFT:
                direction = Direction.RIGTH;
                break;
            case RIGTH:
                direction = Direction.LEFT;
                break;
            case UP:
                direction = Direction.DOWN;
                break;
            case DOWN:
                direction = Direction.UP;
                break;
            case STOP:
                direction = Direction.STOP;
                break;
            }
        return direction;
    }

    //получение номера монстра
    public int getNumberMonster(){
        return this.numberMonsters;
    }

    //движение монстра
    @Override
    public void move(Direction directionIn, int pixel) {
        switch (this.state){
            case DEAD:
                if (this.inCenterLogicalSquare()) {
                    if ((getPositionLogic().x > 0) && (getPositionLogic().x < this.gameScreen.screen.fieldSizeX - 1)) {
                        if (this.gameScreen.nodalPointsMatrix[this.getPositionLogic().y][this.getPositionLogic().x] == true) {
                            if (!(this.getPositionLogic().x == 14 && this.getPositionLogic().y == 16 + gameScreen.shift) ||
                                   !(this.getPositionLogic().x == 15 && this.getPositionLogic().y == 16 + gameScreen.shift)){
                                hashMapPoint = new HashMap<Direction, Float>();
                                hashMapPoint.put(Direction.UP, new Float(Math.sqrt(Math.pow(14 - this.getPositionLogic().x, 2) +
                                        Math.pow(16 + gameScreen.shift - (this.getPositionLogic().y + 1), 2))));
                                hashMapPoint.put(Direction.DOWN, new Float(Math.sqrt(Math.pow(14 - this.getPositionLogic().x, 2) +
                                        Math.pow(16 + gameScreen.shift - (this.getPositionLogic().y - 1), 2))));
                                hashMapPoint.put(Direction.LEFT, new Float(Math.sqrt(Math.pow(14 - (this.getPositionLogic().x - 1), 2) +
                                        Math.pow(16 + gameScreen.shift - this.getPositionLogic().y, 2))));
                                hashMapPoint.put(Direction.RIGTH, new Float(Math.sqrt(Math.pow(14 - (this.getPositionLogic().x + 1), 2) +
                                        Math.pow(16 + gameScreen.shift - this.getPositionLogic().y, 2))));
                                list = new ArrayList<Entry<Direction, Float>>(hashMapPoint.entrySet());
                                Collections.sort(list, new Comparator<Entry<Direction, Float>>() {
                                    @Override
                                    public int compare(Entry<Direction, Float> o1, Entry<Direction, Float> o2) {
                                        return o1.getValue().compareTo(o2.getValue());
                                    }
                                });

                                //for(Entry<Direction, Float> item : list){
                                //    System.out.println(item.getKey() + " " + item.getValue());
                                //}
                                //System.out.println("x=" + this.getPositionLogic().x + " y=" + this.getPositionLogic().y);
                                //System.out.println("-------------------------");

                                Direction newDiriction = null;
                                boolean side = true;
                                for(Entry<Direction, Float> item : list){
                                    newDiriction = item.getKey();
                                    switch (newDiriction) {
                                        case LEFT:
                                            side = this.gameScreen.matrixLevelLogic[this.getPositionLogic().y][this.getPositionLogic().x - 1] != 0;
                                            break;
                                        case RIGTH:
                                            side = this.gameScreen.matrixLevelLogic[this.getPositionLogic().y][this.getPositionLogic().x + 1] != 0;
                                            break;
                                        case DOWN:
                                            side = this.gameScreen.matrixLevelLogic[this.getPositionLogic().y - 1][this.getPositionLogic().x] != 0;
                                            break;
                                        case UP:
                                            side = this.gameScreen.matrixLevelLogic[this.getPositionLogic().y + 1][this.getPositionLogic().x] != 0;
                                            break;
                                    }
                                    if (side && !reverseDirection(newDiriction)) {
                                        break;
                                    }
                                }
                                hashMapPoint.clear();

                                this.direction = newDiriction;
                                this.setDirection(this.direction, pixel);
                            }
                            if ((this.getPositionLogic().x == 14 && this.getPositionLogic().y == 16 + gameScreen.shift) ||
                                    (this.getPositionLogic().x == 15 && this.getPositionLogic().y == 16 + gameScreen.shift)){
                                this.state = State.LIVE;
                            }
                        } else {
                            this.setDirection(this.getDirection(), pixel);
                        }
                    } else {
                        if (this.getPositionLogic().x > this.gameScreen.screen.fieldSizeX) {
                            this.setPositionLogic(new GridPoint2(0, 16 + gameScreen.shift));
                        }
                        if (this.getPositionLogic().x < 0) {
                            this.setPositionLogic(new GridPoint2(this.gameScreen.screen.fieldSizeX, 16 + gameScreen.shift));
                        }
                        this.setDirection(this.getDirection(), pixel);
                    }
                } else {
                    this.setDirection(this.getDirection(), pixel);
                }
                break;
            default:
                //if ((getPositionLogic().x > 1) && (getPositionLogic().x < this.gameScreen.screen.fieldSizeX - 2) &&
                //(state == State.LIVE)) {
                //    pixel += addSpeed() ? 1 : 0;
                //}
                if (this.inCenterLogicalSquare()) {
                    if ((getPositionLogic().x > 0) && (getPositionLogic().x < this.gameScreen.screen.fieldSizeX - 1) &&
                            (getPositionLogic().y > 0) && (getPositionLogic().y < this.gameScreen.screen.fieldSizeY - 1)) {
                        if (this.gameScreen.nodalPointsMatrix[this.getPositionLogic().y][this.getPositionLogic().x] == true) {
                            {
                                Direction newDiriction;
                                boolean side = true;
                                do {
                                    newDiriction = Direction.Random();
                                    switch (newDiriction) {
                                        case LEFT:
                                            side = this.gameScreen.matrixLevelLogic[this.getPositionLogic().y][this.getPositionLogic().x - 1] != 0;
                                            break;
                                        case RIGTH:
                                            side = this.gameScreen.matrixLevelLogic[this.getPositionLogic().y][this.getPositionLogic().x + 1] != 0;
                                            break;
                                        case DOWN:
                                            side = this.gameScreen.matrixLevelLogic[this.getPositionLogic().y - 1][this.getPositionLogic().x] != 0;
                                            break;
                                        case UP:
                                            side = this.gameScreen.matrixLevelLogic[this.getPositionLogic().y + 1][this.getPositionLogic().x] != 0;
                                            break;
                                    }
                                } while ((!side) || (this.reverseDirection(newDiriction) || (home(newDiriction))));

                                this.direction = newDiriction;
                                this.setDirection(this.direction, pixel);
                            }
                        } else {
                            this.setDirection(this.getDirection(), pixel);
                        }
                    } else {
                        if (this.getPositionLogic().x > this.gameScreen.screen.fieldSizeX) {
                            this.setPositionLogic(new GridPoint2(0, 16 + gameScreen.shift));
                        }
                        if (this.getPositionLogic().x < 0) {
                            this.setPositionLogic(new GridPoint2(this.gameScreen.screen.fieldSizeX, 16 + gameScreen.shift));
                        }
                        this.setDirection(this.getDirection(), pixel);
                    }
                } else {
                    this.setDirection(this.getDirection(), pixel);
                }
                break;
        }
    }

    @Override
    public void nextDiriction(int pixel) {
        if (!this.inCenterLogicalSquare()) {
            this.direction = this.directionNext;
        } else {
            this.direction = this.dirictionReverse(this.directionNext);
        }
    }

    //возрождение монстров при смерти пекмана
    @Override
    public void newStartCoordinates(){
        this.positionLogic = new GridPoint2(coordinateX + number % 4, coordinateY + gameScreen.shift);
        this.positionPixel = new GridPoint2(this.gameScreen.screen.blockSizeX * getPositionLogic().x,
                this.gameScreen.screen.blockSizeY * getPositionLogic().y);
        direction = Direction.Random();
        state = State.LIVE;
    }

    //проверка монстра на противоположное направление
    @Override
    protected boolean reverseDirection(Direction newDiriction){
        boolean reverse = false;
        if (this.direction == Direction.LEFT && newDiriction == Direction.RIGTH) {
            reverse = true;
        }
        if (this.direction == Direction.RIGTH && newDiriction == Direction.LEFT) {
            reverse = true;
        }
        if (this.direction == Direction.UP && newDiriction == Direction.DOWN) {
            reverse = true;
        }
        if (this.direction == Direction.DOWN && newDiriction == Direction.UP) {
            reverse = true;
        }
        if ((this.getPositionLogic().x == 13 && this.getPositionLogic().y == 16 + gameScreen.shift) ||                         //shift
            (this.getPositionLogic().x == 16 && this.getPositionLogic().y == 16 + gameScreen.shift)){
            reverse = false;
        }
        return reverse;
    }

    //получение спрайта монстра
    @Override
    public Sprite getSprite(){
        this.stateTime += Gdx.graphics.getDeltaTime();
        switch (this.state) {
            case LIVE:
                switch (this.direction){
                    case LEFT:
                        this.spriteCurrent = (Sprite) this.walkAnimation[0].getKeyFrame(stateTime, true);
                        break;
                    case RIGTH:
                        this.spriteCurrent = (Sprite) this.walkAnimation[1].getKeyFrame(stateTime, true);
                        break;
                    case DOWN:
                        this.spriteCurrent = (Sprite) this.walkAnimation[2].getKeyFrame(stateTime, true);
                        break;
                    case UP:
                        this.spriteCurrent = (Sprite) this.walkAnimation[3].getKeyFrame(stateTime, true);
                        break;
                }
                break;
            case RUN:
                if (TimeUtils.nanoTime() - gameScreen.currentTimeRunMonsters >
                        gameScreen.TIME_DELEY_RUN_MONSTERS - gameScreen.TIME_RUNNING_MONSTERS &&
                        this.getDirection() != Direction.STOP) {
                    this.spriteCurrent = (Sprite) this.animationRuning.getKeyFrame(stateTime, true);
                } else {
                    this.spriteCurrent = (Sprite) this.animationRun.getKeyFrame(stateTime, true);
                }
                break;
            case DEAD:
                switch (this.direction){
                    case LEFT:
                        this.spriteCurrent = deadSprite[0];
                        break;
                    case RIGTH:
                        this.spriteCurrent = deadSprite[1];
                        break;
                    case DOWN:
                        this.spriteCurrent = deadSprite[2];
                        break;
                    case UP:
                        this.spriteCurrent = deadSprite[3];
                        break;
                }
                break;
            case EAT:
                this.spriteCurrent = this.eatSprite;
                break;
        }

        return this.spriteCurrent;
    }

    @Override
    public void setState(State state) {
        this.state = state;
    }

}
