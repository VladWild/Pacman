package com.vladwild.game.field;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.GridPoint2;
import com.vladwild.game.game.GameScreen;
import com.vladwild.game.resource.ResourceManager;

import java.util.Random;

public class Bonus {
    private static final String PROPERTY_BONUSES = "property\\bonuses.properties";                //адрес файла "bonuses.properties"
    private static final int NUMBER_BONUSES = 11;
    private static final int MULTIPLIER = 100;

    private Sprite spriteCurrent;
    private GridPoint2 positionPixel;
    private int number;
    private int points;

    public Bonus (GameScreen gameScreen) {
        positionPixel = new GridPoint2(gameScreen.BONUS_X * gameScreen.screen.blockSizeX + gameScreen.screen.blockSizeX / 2, (gameScreen.BONUS_Y + gameScreen.shift) * gameScreen.screen.blockSizeY);
        number = new Random().nextInt(NUMBER_BONUSES) + 1;
        spriteCurrent = new Sprite(new Texture(new ResourceManager(PROPERTY_BONUSES).getFileHandle(String.valueOf(number))));
        points = number * MULTIPLIER;
    }

    public Sprite getSprite(){
        return spriteCurrent;
    }

    public GridPoint2 getPosition(){
        return positionPixel;
    }

    public int getNumber(){
        return number;
    }

    public int getPoints(){
        return points;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Bonus bonus = (Bonus) o;

        if (number != bonus.number) return false;
        return points == bonus.points;

    }

    @Override
    public int hashCode() {
        int result = number;
        result = 31 * result + points;
        return result;
    }

}
