package com.vladwild.game.field;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.vladwild.game.animals.Direction;
import com.vladwild.game.resource.ResourceManager;

public class Arrow {
    private static final String PROPERTY_ARROW = "property\\arrow.properties";        //адрес файла "arrow.properties"
    private static final String LEFT = "left";
    private static final String RIGTH = "rigth";
    private static final String UP = "up";
    private static final String DOWN = "down";

    private static Sprite left;
    private static Sprite rigth;
    private static Sprite up;
    private static Sprite down;

    private static Sprite currentSprite;

    public Arrow(){
        left = new Sprite(new Texture(new ResourceManager(PROPERTY_ARROW).getFileHandle(LEFT)));
        rigth = new Sprite(new Texture(new ResourceManager(PROPERTY_ARROW).getFileHandle(RIGTH)));
        up = new Sprite(new Texture(new ResourceManager(PROPERTY_ARROW).getFileHandle(UP)));
        down = new Sprite(new Texture(new ResourceManager(PROPERTY_ARROW).getFileHandle(DOWN)));
    }

    public Sprite getSprite(Direction direction){
        switch (direction){
            case LEFT:
                currentSprite = left;
                break;
            case RIGTH:
                currentSprite = rigth;
                break;
            case UP:
                currentSprite = up;
                break;
            case DOWN:
                currentSprite = down;
                break;
        }
        return currentSprite;
    }

}
