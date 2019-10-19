package com.vladwild.game.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.vladwild.game.resource.ResourceManager;

import org.xml.sax.SAXException;

import java.io.IOException;

public class AuthorsScreen implements Screen {
    private static final String PROPERTY_PICTURIES = "property\\pictures.properties";       //файл prorerties картинок
    private static final String AUTHORS_GAME_KEY = "Authors";                               //название ключа картинки

    private static final String BUTTON_ATLAS = "ui/back/button.pack";       //адреса картинок кнопок и названия их скинов
    private static final String BUTTON_BACK_UP = "back_up";
    private static final String BUTTON_BACK_DOWN = "back_down";

    private static final int PARTH_SCREEN_BACK = 4;                         //ширина и высота кнопки паузы

    PacmanGame game;
    OrthographicCamera camera;
    Sprite authors;

    private Stage stage;                //объекты для кнопоки возврата в меню
    private Button back;
    private TextureAtlas atlas;
    private Skin skin;

    private MainMenuScreen screen;

    public AuthorsScreen(PacmanGame game, MainMenuScreen screen) throws IOException, SAXException {
        this.game = game;
        this.screen = screen;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, screen.fieldSizeX * screen.blockSizeX, screen.fieldSizeY * screen.blockSizeY);

        authors = new Sprite(new Texture(new ResourceManager(PROPERTY_PICTURIES).getFileHandle(AUTHORS_GAME_KEY)));

    }


    @Override
    public void show() {
        stage = new Stage();

        atlas = new TextureAtlas(BUTTON_ATLAS);
        skin = new Skin(atlas);

        TextButton.TextButtonStyle styleBack = new TextButton.TextButtonStyle();
        styleBack.up = skin.getDrawable(BUTTON_BACK_UP);
        styleBack.down = skin.getDrawable(BUTTON_BACK_DOWN);

        back = new Button(styleBack);
        back.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new MainMenuScreen(game));
            }
        });
        back.setSize((int) Gdx.graphics.getWidth() / PARTH_SCREEN_BACK, (int) Gdx.graphics.getWidth() / PARTH_SCREEN_BACK);
        back.setPosition(Gdx.graphics.getWidth() - (int) Gdx.graphics.getWidth() / PARTH_SCREEN_BACK, 0);

        stage.addActor(back);

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0.55f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        Gdx.input.setInputProcessor(stage);

        camera.update();

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        game.batch.draw(authors, 0, 0, screen.fieldSizeX * screen.blockSizeX, screen.fieldSizeY * screen.blockSizeY);
        game.batch.end();

        stage.act(delta);
        stage.draw();

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
