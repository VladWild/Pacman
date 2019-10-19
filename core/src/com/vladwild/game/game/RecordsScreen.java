package com.vladwild.game.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.vladwild.game.records.PreferencesRecords;
import com.vladwild.game.records.Record;
import com.vladwild.game.resource.ResourceManager;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RecordsScreen implements Screen {
    private static final String PROPERTY_PICTURIES = "property\\pictures.properties";       //файл prorerties картинок
    private static final String PROPERTY_FONTS = "property\\fonts.properties";              //файл prorerties шрифтов
    private static final String RECORD_GAME_KEY = "RecordsGame";                            //название ключа картинки
    private static final String FONT_KEY = "font";                                          //название ключа шрифта
    private static final String HEAD = "SCORE LEVEL";                                       //название заголовка

    private static final String BUTTON_ATLAS = "ui/back/button.pack";       //адреса картинок кнопок и названия их скинов
    private static final String BUTTON_BACK_UP = "back_up";
    private static final String BUTTON_BACK_DOWN = "back_down";

    private static final int PARTH_SCREEN_BACK = 4;                         //ширина и высота кнопки паузы

    private static final double PART_CAMERA_WIDTH = 3.1;
    private static final double PART_CAMERA_HEIGTH = 1.2;

    PacmanGame game;
    OrthographicCamera camera;
    Sprite recordGame;

    private Stage stage;            //объекты для кнопоки возврата в меню
    private Button back;
    private TextureAtlas atlas;
    private Skin skin;

    private BitmapFont font;
    private String tableString;

    private MainMenuScreen screen;

    private static List<Record> records = new ArrayList<Record>();

    public  RecordsScreen(PacmanGame game, MainMenuScreen screen) throws IOException, SAXException {
        this.game = game;
        this.screen = screen;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, screen.fieldSizeX * screen.blockSizeX, screen.fieldSizeY * screen.blockSizeY);

        recordGame = new Sprite(new Texture(new ResourceManager(PROPERTY_PICTURIES).getFileHandle(RECORD_GAME_KEY)));

        records = PreferencesRecords.read();

        int maxLenght = (HEAD.length() > records.get(0).getLength()) ? HEAD.length() : records.get(0).getLength();
        int numberSpace = (maxLenght - HEAD.length()) / 2;
        for (int j = 0; j < numberSpace; j++) {
            tableString += " ";
        }
        tableString = HEAD + "\n";
        int i = 0;
        for (Record record : records) {
            numberSpace = (maxLenght - records.get(i).getLength()) / 2;
            for (int j = 0; j < numberSpace; j++) {
                tableString += " ";
            }
            tableString += record.getScore() + " " + record.getLevel() + "\n";
            i++;
        }

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

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(new ResourceManager(PROPERTY_FONTS).getFileHandle(FONT_KEY));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.color = Color.BLUE;
        parameter.size = 100;
        parameter.borderColor = Color.GOLD;
        parameter.borderWidth = 2;
        parameter.shadowColor = Color.DARK_GRAY;
        parameter.shadowOffsetX = 6;
        parameter.shadowOffsetY = 6;

        parameter.magFilter = Texture.TextureFilter.Linear;
        parameter.minFilter = Texture.TextureFilter.Linear;

        font = generator.generateFont(parameter);

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0.55f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        Gdx.input.setInputProcessor(stage);

        camera.update();

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        game.batch.draw(recordGame, 0, 0, screen.fieldSizeX * screen.blockSizeX, screen.fieldSizeY * screen.blockSizeY);
        font.draw(game.batch, tableString, (int) (camera.viewportWidth / PART_CAMERA_WIDTH),
                (int) (camera.viewportHeight / PART_CAMERA_HEIGTH));
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
