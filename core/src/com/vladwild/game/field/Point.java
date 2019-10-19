package com.vladwild.game.field;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.GridPoint2;
import com.vladwild.game.game.GameScreen;
import com.vladwild.game.resource.ResourceManager;

public class Point {
    private static final String PROPERTY_POINT = "property\\textures.properties";               //адрес файла "textures.properties"
    private static final String POINT_SMALL = "17";                                             //ключ в properties к маленькой кнопке
    private static final String POINT_BIG = "16";                                               //ключ в properties к большой кнопке

    private GridPoint2 positionLogic;
    private GridPoint2 positionPixel;
    private Sprite spriteCurrent;
    private SizePoint sizePoint;

    Point (int x, int y, SizePoint size, GameScreen gameScreen){
        if (size == SizePoint.SMALL) {
            spriteCurrent = new Sprite(new Texture(new ResourceManager(PROPERTY_POINT).getFileHandle(POINT_SMALL)));
            this.sizePoint = SizePoint.SMALL;
            //if (x == 12 && y == 8) {
            //    spriteCurrent = new Sprite(new Texture(new ResourceManager(PROPERTY_POINT).getFileHandle(POINT_BIG)));
            //    this.sizePoint = SizePoint.BIG;
            //}
        } else {
            spriteCurrent = new Sprite(new Texture(new ResourceManager(PROPERTY_POINT).getFileHandle(POINT_BIG)));
            this.sizePoint = SizePoint.BIG;
        }
        this.positionLogic = new GridPoint2(x, y);
        this.positionPixel = new GridPoint2(x * gameScreen.screen.blockSizeX, y * gameScreen.screen.blockSizeY);

    }

    public Sprite getSprite(){
        return this.spriteCurrent;
    }

    public GridPoint2 getPositionPixel(){
        return this.positionPixel;
    }

    public GridPoint2 getLogicPixel(){
        return this.positionLogic;
    }

    public SizePoint getSizePoint() { return this.sizePoint; }

}
