package com.vladwild.game.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.vladwild.game.resource.ResourceManager;

import org.xml.sax.SAXException;

import java.io.IOException;

public class MainMenuScreen implements Screen {
    private static final String PROPERTY_PICTURIES = "property\\pictures.properties";
    private static final String START_GAME_KEY = "StartGame";

    private static final String BUTTONS_ATLAS = "ui/screen/button.pack";        //адреса картинок кнопок и названия их скинов
    private static final String BUTTON_START_UP = "button_start_up";
    private static final String BUTTON_START_DOWN = "button_start_down";
    private static final String BUTTON_RECORDS_UP = "button_records_up";
    private static final String BUTTON_RECORDS_DOWN = "button_records_down";
    private static final String BUTTON_AUTHORS_UP = "button_authors_up";
    private static final String BUTTON_AUTHORS_DOWN = "button_authors_down";
    private static final String BUTTON_EXIT_UP = "button_exit_up";
    private static final String BUTTON_EXIT_DOWN = "button_exit_down";

    private static int distance_button;                                         //расстояние между кнопками в меню
    private static final int PART_SCREEN_SPACE = 30;                            //ширина расстояния между кнопками
    private static int width;
    private static int heigth;
    private static final int PART_SCREEN_WIDTH = 2;
    private static final int PART_SCREEN_HEIGTH = 15;

    PacmanGame game;
    OrthographicCamera camera;
    Texture startGame;

    public static final String LEVEL_NAMES [] = {"first", "second", "third", "forth", "fifth"};
    public static final Integer SHIFTS [] = {1, 0, 0, 0, 0};

    public static int numberLevel; //0
    public static int numberMonsters; //4
    public static final int MAX_NUMBER_MONSTERS = 16; //16
    public final int fieldSizeX = 30;      //логическая ширина игрового поля
    public final int fieldSizeY = 33;      //логическая высота игрового поля
    public final int blockSizeX = 64;      //ширина игрового блока в пикселях
    public final int blockSizeY = 64;      //высота игрового блока в пикселях

    public final int up = 3;
    public final int down = 6;

    private MainMenuScreen mainMenuScreen;

    private Stage stage;                   //объекты для кнопок на главном экране
    private Table table;
    private Button start, records, authors, exit;
    private TextureAtlas atlas;
    private Skin skin;

    public MainMenuScreen(PacmanGame game) {
        this.game = game;
        this.mainMenuScreen = this;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, fieldSizeX * blockSizeX, fieldSizeY * blockSizeY);

        startGame = new Texture(new ResourceManager(PROPERTY_PICTURIES).getFileHandle(START_GAME_KEY));

        this.numberLevel = 0;
        this.numberMonsters = 4;

    }

    @Override
    public void show() {
        stage = new Stage();

        atlas = new TextureAtlas(BUTTONS_ATLAS);
        skin = new Skin(atlas);

        table = new Table(skin);

        TextButton.TextButtonStyle styleStart = new TextButton.TextButtonStyle();
        styleStart.up = skin.getDrawable(BUTTON_START_UP);
        styleStart.down = skin.getDrawable(BUTTON_START_DOWN);

        start = new Button(styleStart);
        start.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new GameScreen(game, mainMenuScreen,
                        numberMonsters, LEVEL_NAMES[numberLevel], SHIFTS[numberLevel]));
            }
        });

        TextButton.TextButtonStyle styleRecords = new TextButton.TextButtonStyle();
        styleRecords.up = skin.getDrawable(BUTTON_RECORDS_UP);
        styleRecords.down = skin.getDrawable(BUTTON_RECORDS_DOWN);

        records = new Button(styleRecords);
        records.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                try {
                    game.setScreen(new RecordsScreen(game, mainMenuScreen));
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (SAXException e) {
                    e.printStackTrace();
                }
            }
        });

        TextButton.TextButtonStyle styleAutors = new TextButton.TextButtonStyle();
        styleAutors.up = skin.getDrawable(BUTTON_AUTHORS_UP);
        styleAutors.down = skin.getDrawable(BUTTON_AUTHORS_DOWN);

        authors = new Button(styleAutors);
        authors.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                try {
                    game.setScreen(new AuthorsScreen(game, mainMenuScreen));
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (SAXException e) {
                    e.printStackTrace();
                }
            }
        });

        TextButton.TextButtonStyle styleExit = new TextButton.TextButtonStyle();
        styleExit.up = skin.getDrawable(BUTTON_EXIT_UP);
        styleExit.down = skin.getDrawable(BUTTON_EXIT_DOWN);

        exit = new Button(styleExit);
        exit.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        distance_button = (int) Gdx.graphics.getHeight() / PART_SCREEN_SPACE;
        width = (int) Gdx.graphics.getWidth() / PART_SCREEN_WIDTH;
        heigth = (int) Gdx.graphics.getHeight() / PART_SCREEN_HEIGTH;

        table.add(start).size(width, heigth);
        table.getCell(start).space(distance_button);
        table.row();
        table.add(records).size(width, heigth);
        table.getCell(records).space(distance_button);
        table.row();
        table.add(authors).size(width, heigth);
        table.getCell(authors).space(distance_button);
        table.row();
        table.add(exit).size(width, heigth);
        table.getCell(exit).space(distance_button);

        table.setSize(Gdx.graphics.getWidth(), (int) Gdx.graphics.getHeight() / 2);
        table.setPosition(0, 0);

        stage.addActor(table);

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0.55f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        Gdx.input.setInputProcessor(stage);

        camera.update();

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        game.batch.draw(startGame, 0, 0, fieldSizeX * blockSizeX, fieldSizeY * blockSizeY);
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
