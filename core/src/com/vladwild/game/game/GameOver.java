package com.vladwild.game.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.vladwild.game.resource.ResourceManager;

public class GameOver implements Screen {
    private static final String PROPERTY_PICTURIES = "property\\pictures.properties";       //файл prorerties картинок
    private static final String PROPERTY_FONTS = "property\\fonts.properties";              //файл prorerties шрифтов
    private static final String FONT_KEY = "font_large";                                    //название ключа шрифта
    private static final String GAMEOVER_KEY = "GameOver";                                  //название ключа картинки

    PacmanGame game;
    OrthographicCamera camera;
    Sprite gameover;

    private MainMenuScreen screen;

    private int score, highScore;

    private static FileHandle font;                                                         //все для надписей
    private static BitmapFont fontScore, fontNumberScore, fontHighScore, fontNumberHighScore;
    private static final String SCORE = "SCORE";
    private static final String HIGH_SCORE = "HIGH SCORE";
    private static final float SIZE_SCORE = 3f;
    private static final float SIZE_NUMBER_SCORE = 2.6f;
    private static final float SIZE_HIGH_SCORE = 3f;
    private static final float SIZE_NUMBER_HIGH_SCORE = 2.6f;
    private static final int SCORE_X = 960;
    private static final int SCORE_Y = 1150;
    private static final int NUMBER_SCORE_X = 960;
    private static final int NUMBER_SCORE_Y = 950;
    private static final int HIGH_SCORE_X = 960;
    private static final int HIGH_SCORE_Y = 650;
    private static final int NUMBER_HIGH_SCORE_X = 960;
    private static final int NUMBER_HIGH_SCORE_Y = 450;

    public GameOver(PacmanGame game, MainMenuScreen screen, int score, int highScore){
        this.game = game;
        this.screen = screen;
        this.score = score;
        this.highScore = highScore;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, screen.fieldSizeX * screen.blockSizeX, screen.fieldSizeY * screen.blockSizeY);

        gameover = new Sprite(new Texture(new ResourceManager(PROPERTY_PICTURIES).getFileHandle(GAMEOVER_KEY)));
    }

    @Override
    public void show() {

        //формирование шрифта
        font = new ResourceManager(PROPERTY_FONTS).getFileHandle(FONT_KEY);

        //SCORE
        fontScore = new BitmapFont(font);
        fontScore.setColor(Color.SKY);
        fontScore.getData().setScale(SIZE_SCORE);

        //Number Score
        fontNumberScore = new BitmapFont(font);
        fontNumberScore.setColor(Color.SKY);
        fontNumberScore.getData().setScale(SIZE_NUMBER_SCORE);

        //HIGH SCORE
        fontHighScore = new BitmapFont(font);
        fontHighScore.setColor(Color.SKY);
        fontHighScore.getData().setScale(SIZE_HIGH_SCORE);

        //Number High Score
        fontNumberHighScore = new BitmapFont(font);
        fontNumberHighScore.setColor(Color.SKY);
        fontNumberHighScore.getData().setScale(SIZE_NUMBER_HIGH_SCORE);


    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0.55f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        game.batch.draw(gameover, 0, 0, screen.fieldSizeX * screen.blockSizeX, screen.fieldSizeY * screen.blockSizeY);
        fontScore.draw(game.batch, SCORE, SCORE_X  - (int) SCORE.length() * fontScore.getSpaceWidth() / 2, SCORE_Y);
        fontNumberScore.draw(game.batch, String.valueOf(score), NUMBER_SCORE_X - (int) String.valueOf(score).length() * fontNumberScore.getSpaceWidth() / 2, NUMBER_SCORE_Y);
        fontHighScore.draw(game.batch, HIGH_SCORE, HIGH_SCORE_X - (int) HIGH_SCORE.length() * fontHighScore.getSpaceWidth() / 2, HIGH_SCORE_Y);
        fontNumberHighScore.draw(game.batch, String.valueOf(highScore), NUMBER_HIGH_SCORE_X - (int) String.valueOf(highScore).length() * fontNumberHighScore.getSpaceWidth() / 2, NUMBER_HIGH_SCORE_Y);
        game.batch.end();

        if(Gdx.input.isTouched()){
            game.setScreen(new MainMenuScreen(game));
        }

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
