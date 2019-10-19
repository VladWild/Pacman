package com.vladwild.game.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.TimeUtils;
import com.vladwild.game.InputProcessors.InputProcessorGame;
import com.vladwild.game.animals.Direction;
import com.vladwild.game.animals.Monster;
import com.vladwild.game.animals.Pacman;
import com.vladwild.game.animals.State;
import com.vladwild.game.field.Arrow;
import com.vladwild.game.field.Bonus;
import com.vladwild.game.field.Field;
import com.vladwild.game.field.Point;
import com.vladwild.game.field.SizePoint;
import com.vladwild.game.records.PreferencesRecords;
import com.vladwild.game.records.Record;
import com.vladwild.game.resource.ResourceManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

//класс уровня и геймплея gameScreen
public class GameScreen implements Screen {
    private static final String PROPERTY_PICTURIES = "property\\pictures.properties";       //файл prorerties картинок
    private static final String PROPERTY_FONTS = "property\\fonts.properties";              //файл prorerties шрифтов
    private static final String PAUSE_PLAY_KEY = "PausePlay";                               //название ключа картинки паузы
    private static final String FONT_KEY = "font_large";                                    //название ключа шрифта

    private static final String BUTTONS_PAUSE_ATLAS = "ui/pause/button.pack";               //адреса картинок кнопок и названия их скинов
    private static final String BUTTONS_CONTINUE_HOME_ATLAS = "ui/pauseContinueExit/button.pack";
    private static final String BUTTON_PAUSE_UP = "pause_up";
    private static final String BUTTON_PAUSE_DOWN = "pause_down";
    private static final String BUTTON_CONTINUE_UP = "continue_up";
    private static final String BUTTON_CONTINUE_DOWN = "continue_down";
    private static final String BUTTON_HOME_UP = "home_up";
    private static final String BUTTON_HOME_DOWN = "home_down";

    private Stage stage, stagePause;                                   //объекты для кнопок в паузе
    private Table tablePause;
    private Button pause, pauseContinue, pauseHome;
    private TextureAtlas atlas, atlasPause;
    private Skin skin, skinPause;

    private static final int DISTANSE = 24;                            //дистанция столкновения пекмана и зверя
    private static final int SPEED_PACMAN = 4;                         //скорость пекмана
    private static final int SPEED_MONSTER_LIVE = 5;                   //скорость монстра, который живет
    private static final int SPEED_MONSTER_RUN = 2;                    //скорость монстра, который бежит
    private static final int SPEED_MONSTER_DEAD = 8;                   //скорость монстра, который умер
    public static final long DELAY_AFTER_DEATH_PACMAN = (long) 2e+9;   //задержка после смерти пекмана
    public static final long DELAY_EAT_PACMAN = (long) 1e+9;           //задержка после того, как пекман съел монстра
    public static final long TIME_DELEY_RUN_MONSTERS = (long) 10e+9;   //время убегания монстров
    public static final long TIME_RUNNING_MONSTERS = (long) 3e+9;      //время мерцания монстров
    public static final long TIME_READY = (long) 5e+9;
    public static final long TIME_ADD_LIVE = (long) 4e+9;
    public static final long TIME_ADD_BONUS = (long) 60e+9;
    public static final long TIME_SHOW_FONT = (long) 4e+9;
    public static final long TIME_CLEAR_BONUS = (long) 20e+9;
    public static final int NUMBER_TEXTURE_DEAD = 20;                  //количество показываемых текстур при смерти пекмана

    public static long currentTimeDead;                                //текущее время смерти пекмана
    public static long currentTimeEat;                                 //текущее время съедания пекманом монстра
    public static long currentTimeRunMonsters;                         //текущее время убегания монстров
    public static long currentTimeReady;
    public static long currentTimePause;
    public static long currentTimeAddLive;
    public static long currentTimeAddBonus;
    public static long currentTimeShowFont;
    public static long currentTimeClaerBonus;
    public static final int coordinateX = 15;                          //координата X появления пекмана
    public static final int coordinateY = 7;                           //координата Y появления пекмана

    private StateGame stateGame;                                       //состояние игры PLAY, READY, PAUSE
    GameScreen gamePlayScreen;                                         //объект GameScreen
    InputMultiplexer inputMultiplexer;                                 //мультиплексор
    boolean readyClick;                                                //флаг остановки животных в режиме READY

    private static int distance_button;                                //расстояние между кнопками в паузе
    private static final int PART_PAUSE_SPACE = 50;                    //ширина расстояния между кнопками
    private static int width_pause;
    private static int heigth_pause;
    private static final double PART_SCREEN_WIDTH_GAMESCREEN = 4.2;
    private static final double PART_SCREEN_WIDTH_PAUSE = 7;

    private static FileHandle font;                                    //все для надписей
    private static BitmapFont fontReady, fontLevel, fontNumberLevel, fontScore, fontNumberScore, fontHighScore, fontNumberHighScore, fontNumberLive, fontEatPacman;
    private static final float SIZE_READY = 1.2f;
    private static final float SIZE_LEVEL = 1.8f;
    private static final float SIZE_NUMBER_LEVEL = 4f;
    private static final float SIZE_SCORE = 1.2f;
    private static final float SIZE_NUMBER_SCORE = 1.6f;
    private static final float SIZE_HIGH_SCORE = 1.2f;
    private static final float SIZE_NUMBER_HIGH_SCORE = 1.6f;
    private static final float SIZE_NUMBER_LIVE = 1.6f;
    private static final float SIZE_POINTS_EAT_PACMAN = 0.6f;
    private static final String READY = "READY!";
    private static final String LEVEL = "LEVEL";
    private static final String SCORE = "SCORE";
    private static final String HIGH_SCORE = "HIGH SCORE";
    private static final String LIVE = "x";
    private static final String LIVE_ADD = "1UP";
    private static final int READY_X = 840;
    private static final int READY_Y = 1280;
    private static final int LEVEL_X = 1480;
    private static final int LEVEL_Y = 370;
    private static final int NUMBER_LEVEL_X = 1650;
    private static final int NUMBER_LEVEL_Y = 240;
    private static final int SCORE_X = 960;
    private static final int SCORE_Y = 2650;
    private static final int NUMBER_SCORE_X = 960;
    private static final int NUMBER_SCORE_Y = 2570;
    private static final int HIGH_SCORE_X = 1660;
    private static final int HIGH_SCORE_Y = 2650;
    private static final int NUMBER_HIGH_SCORE_X = 1660;
    private static final int NUMBER_HIGH_SCORE_Y = 2570;
    private static final int NUMBER_LIVE_X = 160;
    private static final int NUMBER_LIVE_Y = 2620;
    private static final int FONT_BONUS_X = 960;
    private static final int FONT_BONUS_Y = 16;
    private static final int SHOW_BONUS_X = 960;
    private static final int SHOW_BONUS_Y = 4;
    private static final int SIZE_SHOW_BONUS_X = 52;
    private static final int SIZE_SHOW_BONUS_Y = 52;
    private static final int POINTS_EAT_PACMAN = 46;

    private static final int POINT_SMALL = 10;
    private static final int POINT_BIG = 100;
    private static final int POINT_EAT_MONSTER = 200;

    private static final int LIVE_SPRITE_PACMAN_X = 32;
    private static final int LIVE_SPRITE_PACMAN_Y = 2528;
    private static final int LIVE_SPRITE_PACMAN_WIDTH = 128;
    private static final int LIVE_SPRITE_PACMAN_HEIGTH = 128;

    public static final int POINT_ADD_LIVE = 10000;
    private static String numberPointOrLive;
    private Record highRecord;
    private Monster monsterEat;

    public static final int BONUS_X = 14;
    public static final int BONUS_Y = 13;

    private static Arrow arrow;
    private static final int ARROW_X1 = 384;
    private static final int ARROW_Y1 = 16;
    private static final int ARROW_X2 = 1152;
    private static final int ARROW_Y2 = 306;

    public final PacmanGame game;                                      //объект Game
    public MainMenuScreen screen;                                      //объект MainMenuScreen
    OrthographicCamera camera;                                         //ортографическая камера
    Sprite pausePlay;                                                  //спрайт паузы
    SpriteBatch batch;                                                 //SpriteBatch GameScreen
    Field field;                                                       //игровое поле
    InputProcessorGame inputProcessor;                                 //геймплейный inputProcessor
    private static final int SENSITIVITY_INPUT_PROCESSOR = 40;         //чувствительность  inputProcessor
    Pacman pacman;                                                     //объект Pacman
    public int[][] matrixLevelLogic;                                   //логическая матрица уровня
    public boolean[][] nodalPointsMatrix;                              //логическая матрица узлов уровня
    long currentTime;                                                  //время после создания всех объектов конструктором
    Point point;                                                       //объект Point
    Iterator<Point> iterationPoints;                                   //итератор Points
    final int monstersAll = 8;                                         //количество разных текстур монстров
    int monstersNumber;                                                //номор монстра
    Integer numberMonster;                                             //номор монстра в классе обертке
    Monster monster;                                                   //объект Monster
    List<Monster> monsters;                                            //список монстров
    Iterator<Monster> iterationMonsters;                               //итератор монстров
    Set<Bonus> bonusesEat;                                             //бонусы, съеденые пекманом
    Bonus bonusCurrent;                                                //текущий бонус
    public int shift;                                                  //смещение

    public GameScreen (PacmanGame game, MainMenuScreen screen, int monstersNumber, String nameLevel, int shift) {
        this.game = game;
        this.shift = shift;
        this.screen = screen;
        this.gamePlayScreen = this;
        this.readyClick = true;

        this.stateGame = StateGame.READY;
        currentTimeReady = TimeUtils.nanoTime();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, screen.fieldSizeX * screen.blockSizeX,
                screen.down * screen.blockSizeY + screen.fieldSizeY * screen.blockSizeY + screen.up * screen.blockSizeY);

        pausePlay = new Sprite(new Texture(new ResourceManager(PROPERTY_PICTURIES).getFileHandle(PAUSE_PLAY_KEY)));
        field = new Field(this, nameLevel);
        matrixLevelLogic = this.field.getStateMatrixLogic();
        nodalPointsMatrix = this.field.getNodalPointsMatrix();

        batch = new SpriteBatch();

        this.pacman = new Pacman(this, Direction.LEFT, new GridPoint2(coordinateX, coordinateY + this.shift), (long) 10e+6, 3);

        this.monstersNumber = monstersNumber;
        monsters = new ArrayList<Monster>();
        List<Integer> listNumber = formingListMonsters();
        int i = 0;
        for (Integer number : listNumber) {
            monsters.add(new Monster(this, Direction.Random(), (long) 10e+6, number.intValue(), i++));
        }

        inputProcessor = new InputProcessorGame(SENSITIVITY_INPUT_PROCESSOR, Direction.LEFT);
        inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(inputProcessor);

        highRecord = PreferencesRecords.getHighRecord();

        this.bonusCurrent = null;
        currentTimeAddBonus = TimeUtils.nanoTime();
        bonusesEat = new HashSet<Bonus>();

        arrow = new Arrow();

        currentTime = TimeUtils.nanoTime();

    }

    public GameScreen(PacmanGame game, MainMenuScreen screen, Pacman pacman, List<Monster> monsters, Set<Bonus> bonusesEat, Arrow arrow, Sprite pausePlay,
                      int monstersNumber, String nameLevel, int shift) {
        this.game = game;
        this.shift = shift;
        this.screen = screen;
        this.gamePlayScreen = this;
        this.readyClick = true;
        this.pausePlay = pausePlay;

        this.stateGame = StateGame.READY;
        currentTimeReady = TimeUtils.nanoTime();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, screen.fieldSizeX * screen.blockSizeX,
                screen.down * screen.blockSizeY + screen.fieldSizeY * screen.blockSizeY + screen.up * screen.blockSizeY);

        field = new Field(this, nameLevel);
        matrixLevelLogic = this.field.getStateMatrixLogic();
        nodalPointsMatrix = this.field.getNodalPointsMatrix();

        batch = new SpriteBatch();

        this.pacman = pacman;
        this.pacman.setScreen(this);
        this.pacman.newStartCoordinates();
        this.pacman.incrementLevel();

        this.monstersNumber = monstersNumber;
        this.monsters = monsters;
        if (monsters.size() < screen.MAX_NUMBER_MONSTERS) {
            Integer number = formingNumberMonstersAdd(monsters);
            monsters.add(new Monster(this, Direction.Random(), (long) 10e+6, number.intValue(), monsters.size() + 1));
        }
        for(Monster monster : monsters) {
            monster.setScreen(this);
            monster.newStartCoordinates();
        }

        inputProcessor = new InputProcessorGame(SENSITIVITY_INPUT_PROCESSOR, Direction.LEFT);
        inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(inputProcessor);

        highRecord = PreferencesRecords.getHighRecord();

        this.bonusCurrent = null;
        currentTimeAddBonus = TimeUtils.nanoTime();
        this.bonusesEat = bonusesEat;
        this.arrow = arrow;

        currentTime = TimeUtils.nanoTime();

    }

    //формируем список номеров монстров, первые 8 не должны повторяться
    private List<Integer> formingListMonsters(){
        List<Integer> listNumber = new ArrayList<Integer>();
        for (int i = 0; i < monstersNumber; i++){
            if (i < monstersAll) {
                boolean presence = true;
                while(presence)
                {
                    presence = false;
                    numberMonster = (int) (1 + (int) (Math.random() * monstersAll));
                    if (listNumber.isEmpty()) {
                        presence = false;
                        break;
                    } else {
                        for (Integer number : listNumber) {
                            if (number.equals(numberMonster)) {
                                presence = true;
                                break;
                            }
                        }
                    }
                }
            } else {
                numberMonster = new Integer( (int) (1 + (int) (Math.random() * monstersAll)));
            }
            listNumber.add(numberMonster);
        }
        return listNumber;
    }

    //формируем номер монстра на следующем уровне
    private Integer formingNumberMonstersAdd(List<Monster> monsters){
        Integer number = new Integer((int) (1 + (int) (Math.random() * monstersAll)));
        boolean presence = true;
        if (monsters.size() < monstersAll){
            while (presence){
                presence = false;
                for(Monster monster : monsters){
                    if (number.intValue() == monster.getNumberMonster()){
                        number = new Integer((int) (1 + (int) (Math.random() * monstersAll)));
                        presence = true;
                        break;
                    };
                }
            }
        }
        return number;
    }

    @Override
    public void show() {

        //формируем кнопку паузы во время игры
        stage = new Stage();

        atlas = new TextureAtlas(BUTTONS_PAUSE_ATLAS);
        skin = new Skin(atlas);

        TextButton.TextButtonStyle stylePause = new TextButton.TextButtonStyle();
        stylePause.up = skin.getDrawable(BUTTON_PAUSE_UP);
        stylePause.down = skin.getDrawable(BUTTON_PAUSE_DOWN);

        pause = new Button(stylePause);
        pause.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gamePlayScreen.stateGame = StateGame.PAUSE;
                currentTimePause = TimeUtils.nanoTime();
            }
        });
        pause.setSize((int) Gdx.graphics.getWidth() / (float) PART_SCREEN_WIDTH_GAMESCREEN,
                (int) Gdx.graphics.getWidth() / (float) PART_SCREEN_WIDTH_GAMESCREEN);
        pause.setPosition(0, 0);

        stage.addActor(pause);

        inputMultiplexer.addProcessor(stage);

        //формируем кнопки продолжения игры и возвращения на главный экран во время паузы
        stagePause = new Stage();

        atlasPause = new TextureAtlas(BUTTONS_CONTINUE_HOME_ATLAS);
        skinPause = new Skin(atlasPause);

        tablePause = new Table(skinPause);

        TextButton.TextButtonStyle styleContinue = new TextButton.TextButtonStyle();
        styleContinue.up = skinPause.getDrawable(BUTTON_CONTINUE_UP);
        styleContinue.down = skinPause.getDrawable(BUTTON_CONTINUE_DOWN);

        pauseContinue = new Button(styleContinue);
        pauseContinue.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gamePlayScreen.stateGame = StateGame.READY;
                currentTimeReady = TimeUtils.nanoTime();
                readyClick = true;
            }
        });

        TextButton.TextButtonStyle styleHome = new TextButton.TextButtonStyle();
        styleHome.up = skinPause.getDrawable(BUTTON_HOME_UP);
        styleHome.down = skinPause.getDrawable(BUTTON_HOME_DOWN);

        pauseHome = new Button(styleHome);
        pauseHome.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                PreferencesRecords.write(new Record(pacman.getPoints(), pacman.getLevel()));
                game.setScreen(new MainMenuScreen(game));
            }
        });

        distance_button = (int) Gdx.graphics.getHeight() / PART_PAUSE_SPACE;
        width_pause = (int) (Gdx.graphics.getWidth() / (float) PART_SCREEN_WIDTH_PAUSE);
        heigth_pause = (int) (Gdx.graphics.getWidth() / (float) PART_SCREEN_WIDTH_PAUSE);

        tablePause.add(pauseContinue).size(width_pause, heigth_pause);
        tablePause.getCell(pauseContinue).space(PART_PAUSE_SPACE);
        tablePause.row();
        tablePause.add(pauseHome).size(width_pause, heigth_pause);
        tablePause.getCell(pauseHome).space(PART_PAUSE_SPACE);

        tablePause.setSize(Gdx.graphics.getWidth(), (int) Gdx.graphics.getHeight());
        tablePause.setPosition(0, 0);

        stagePause.addActor(tablePause);

        //формирование шрифта
        font = new ResourceManager(PROPERTY_FONTS).getFileHandle(FONT_KEY);

        //READY!
        fontReady = new BitmapFont(font);
        fontReady.setColor(Color.RED);
        fontReady.getData().setScale(SIZE_READY);

        //LEVEL
        fontLevel = new BitmapFont(font);
        fontLevel.setColor(Color.SKY);
        fontLevel.getData().setScale(SIZE_LEVEL);

        //Number Level
        fontNumberLevel = new BitmapFont(font);
        fontNumberLevel.setColor(Color.SKY);
        fontNumberLevel.getData().setScale(SIZE_NUMBER_LEVEL);

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

        //Number Live
        fontNumberLive = new BitmapFont(font);
        fontNumberLive.setColor(Color.SKY);
        fontNumberLive.getData().setScale(SIZE_NUMBER_LIVE);

        //Pacman Eat Monster
        fontEatPacman = new BitmapFont(font);
        fontEatPacman.setColor(Color.SKY);
        fontEatPacman.getData().setScale(SIZE_POINTS_EAT_PACMAN);

    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0.2f, 0.2f, 0.9f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        switch (stateGame){
            case PLAY:

                if (pacman.getLive() <= 0){
                    PreferencesRecords.write(new Record(pacman.getPoints(), pacman.getLevel()));
                    if (pacman.getPoints() > highRecord.getScore()){
                        game.setScreen(new GameOver(game, screen, pacman.getPoints(), pacman.getPoints()));
                    } else {
                        game.setScreen(new GameOver(game, screen, pacman.getPoints(), highRecord.getScore()));
                    }
                }

                if (pacman.getState() == State.EAT || pacman.getState() == State.DEAD){
                    Gdx.input.setInputProcessor(inputProcessor);
                } else {
                    Gdx.input.setInputProcessor(inputMultiplexer);
                }

                camera.update();

                batch.setProjectionMatrix(camera.combined);

                batch.begin();
                //long time = TimeUtils.nanoTime();
                batch.draw(field.getPlaceDown(), 0, 0);                                                                     //показ низа
                batch.draw(field.getPlaceUp(), 0, screen.down * screen.blockSizeY + screen.blockSizeY * screen.fieldSizeY); //показ верха
                batch.draw(field.getLevel(), 0, screen.down * screen.blockSizeY);                                           //показ поля
                iterationPoints = field.getIteratorPoint();                                                                 //показ точек
                while (iterationPoints.hasNext()) {
                    point = iterationPoints.next();
                    batch.draw(point.getSprite(), point.getPositionPixel().x,
                            point.getPositionPixel().y + screen.down * screen.blockSizeY);
                }
                if (bonusCurrent != null){
                    batch.draw(bonusCurrent.getSprite(), bonusCurrent.getPosition().x, bonusCurrent.getPosition().y + screen.down * screen.blockSizeY);
                }
                if (TimeUtils.nanoTime() - currentTimeShowFont < TIME_SHOW_FONT) {
                    fontEatPacman.draw(batch, numberPointOrLive, FONT_BONUS_X - String.valueOf(numberPointOrLive).length() * fontEatPacman.getSpaceWidth() / 2, (BONUS_Y + shift + 1) * screen.blockSizeY + screen.down * screen.blockSizeY - FONT_BONUS_Y);
                }
                iterationMonsters = monsters.iterator();                                                                    //показ зверей
                while (iterationMonsters.hasNext()) {
                    monster = iterationMonsters.next();
                    batch.draw(monster.getSprite(), monster.getPositionPixelsStart().x, monster.getPositionPixelsStart().y + screen.down * screen.blockSizeY);
                }
                batch.draw(pacman.getSprite(), pacman.getPositionPixelsStart().x, pacman.getPositionPixelsStart().y + screen.down * screen.blockSizeY); //показ пекмана
                if (TimeUtils.nanoTime() - currentTimeAddLive > TIME_ADD_LIVE) {
                    batch.draw(pacman.getLiveSprite(), LIVE_SPRITE_PACMAN_X , LIVE_SPRITE_PACMAN_Y, LIVE_SPRITE_PACMAN_WIDTH, LIVE_SPRITE_PACMAN_HEIGTH);
                } else {
                    if (( (int) ((TimeUtils.nanoTime() - currentTimeAddLive) / (long) 5e+8)) % 2 == 0) {
                        batch.draw(pacman.getLiveSprite(), LIVE_SPRITE_PACMAN_X , LIVE_SPRITE_PACMAN_Y, LIVE_SPRITE_PACMAN_WIDTH, LIVE_SPRITE_PACMAN_HEIGTH);
                    }
                }
                if (!bonusesEat.isEmpty()) {
                    int numberBonus = 0;
                    for(Bonus bonus: bonusesEat){
                        batch.draw(bonus.getSprite(), SHOW_BONUS_X - bonusesEat.size() * SIZE_SHOW_BONUS_X / 2 + numberBonus++ * SIZE_SHOW_BONUS_X, screen.down * screen.blockSizeY - SIZE_SHOW_BONUS_Y + SHOW_BONUS_Y, SIZE_SHOW_BONUS_X, SIZE_SHOW_BONUS_Y);
                    }
                }
                batch.draw(arrow.getSprite(inputProcessor.getDirection()), ARROW_X1, ARROW_Y1, ARROW_X2, ARROW_Y2);
                fontLevel.draw(batch, LEVEL, LEVEL_X, LEVEL_Y);
                fontNumberLevel.draw(batch, String.valueOf(pacman.getLevel()), NUMBER_LEVEL_X - String.valueOf(pacman.getLevel()).length() * fontNumberLevel.getSpaceWidth() / 2, NUMBER_LEVEL_Y);
                fontScore.draw(batch, SCORE, SCORE_X  - (int) SCORE.length() * fontScore.getSpaceWidth() / 2, SCORE_Y);
                fontNumberScore.draw(batch, String.valueOf(pacman.getPoints()), NUMBER_SCORE_X - (int) String.valueOf(pacman.getPoints()).length() * fontNumberScore.getSpaceWidth() / 2, NUMBER_SCORE_Y);
                fontHighScore.draw(batch, HIGH_SCORE, HIGH_SCORE_X - (int) HIGH_SCORE.length() * fontHighScore.getSpaceWidth() / 2, HIGH_SCORE_Y);
                if (pacman.getPoints() > highRecord.getScore()){
                    fontNumberHighScore.draw(batch, String.valueOf(pacman.getPoints()), NUMBER_HIGH_SCORE_X - (int) String.valueOf(pacman.getPoints()).length() * fontNumberHighScore.getSpaceWidth() / 2, NUMBER_HIGH_SCORE_Y);
                } else {
                    fontNumberHighScore.draw(batch, String.valueOf(highRecord.getScore()), NUMBER_HIGH_SCORE_X - (int) String.valueOf(highRecord.getScore()).length() * fontNumberHighScore.getSpaceWidth() / 2, NUMBER_HIGH_SCORE_Y);
                }
                fontLevel.draw(batch, LIVE + String.valueOf(pacman.getLive() - 1), NUMBER_LIVE_X, NUMBER_LIVE_Y);
                if (pacman.getState() == State.EAT){
                    fontEatPacman.draw(batch, String.valueOf(pacman.getNumberEatMonster() * POINT_EAT_MONSTER),
                            (int) ((pacman.getPositionPixelsStart().x + monsterEat.getPositionPixelsStart().x + screen.blockSizeX) / 2) -
                                    (int) (String.valueOf(pacman.getNumberEatMonster() * POINT_EAT_MONSTER).length() * fontEatPacman.getSpaceWidth() / 2),
                            (int) ((pacman.getPositionPixelsStart().y + monsterEat.getPositionPixelsStart().y + 2 * screen.down * screen.blockSizeY) / 2) + POINTS_EAT_PACMAN);
                }
                //System.out.println(TimeUtils.nanoTime() - time);
                batch.end();

                stage.act(delta);
                stage.draw();

                //добовляем бонус на поле, когда прошло определенное время
                if (((TimeUtils.nanoTime() - currentTimeAddBonus) > TIME_ADD_BONUS) && (bonusCurrent == null)){
                    bonusCurrent = new Bonus(this);
                    currentTimeClaerBonus = TimeUtils.nanoTime();
                }

                //едим бонус, если он присутствует на поле
                if (bonusCurrent != null) {
                    if((Math.abs(pacman.getPositionPixelsStart().x - bonusCurrent.getPosition().x) +
                            Math.abs(pacman.getPositionPixelsStart().y - bonusCurrent.getPosition().y) <= SPEED_PACMAN / 2)) {
                        if (bonusCurrent.getNumber() == 11){
                            pacman.incrementLive(false);
                            currentTimeAddLive = TimeUtils.nanoTime();
                            numberPointOrLive = LIVE_ADD;
                        } else {
                            pacman.addPoint(bonusCurrent.getPoints());
                            numberPointOrLive = String.valueOf(bonusCurrent.getPoints());
                            bonusesEat.add(bonusCurrent);
                        }
                        bonusCurrent = null;
                        currentTimeAddBonus = TimeUtils.nanoTime();
                        currentTimeShowFont = TimeUtils.nanoTime();
                    }
                }

                //удаляем бонус, когда прошло определенное время
                if (bonusCurrent != null) {
                    if (TimeUtils.nanoTime() - currentTimeClaerBonus > TIME_CLEAR_BONUS){
                        bonusCurrent = null;
                        currentTimeAddBonus = TimeUtils.nanoTime();
                    }
                }

                //инкрементим жизнь в случае достижения определенного количества очков
                if (pacman.getPoints() >= pacman.getBeginMultiplerLive()){
                    pacman.incrementLive(true);
                    currentTimeAddLive = TimeUtils.nanoTime();
                }

                //если пекмен в центре квадрата, то удаляем кнопку, находящуюся в этом квадрате
                if (pacman.inCenterLogicalSquare()) {
                    if (field.getSizePoint(pacman.getPositionLogic().x, pacman.getPositionLogic().y) == SizePoint.BIG) {
                        for (Monster monster : monsters) {
                            if(monster.getState() != State.DEAD) {
                                monster.setState(State.RUN);
                            }
                        }
                        field.deletePoint(pacman.getPositionLogic().x, pacman.getPositionLogic().y);
                        pacman.addPoint(POINT_BIG);
                        currentTimeRunMonsters = TimeUtils.nanoTime();
                        pacman.resetNumberEatMonster();
                    }
                    if (field.getSizePoint(pacman.getPositionLogic().x, pacman.getPositionLogic().y) == SizePoint.SMALL) {
                        field.deletePoint(pacman.getPositionLogic().x, pacman.getPositionLogic().y);
                        pacman.addPoint(POINT_SMALL);
                    }
                }

                //если пекман жив, то меняем его направление движение когда это можно
                if (pacman.getState() == State.LIVE) {
                    switch (inputProcessor.getDirection()){
                        case LEFT:
                            inputProcessor.setDirection(Direction.LEFT);
                            break;
                        case RIGTH:
                            inputProcessor.setDirection(Direction.RIGTH);
                            break;
                        case DOWN:
                            inputProcessor.setDirection(Direction.DOWN);
                            break;
                        case UP:
                            inputProcessor.setDirection(Direction.UP);
                            break;
                    }
                }

                //движение пекмана
                if (TimeUtils.nanoTime() - pacman.getCurrentTime() > pacman.getSpeed()) {
                    if (pacman.getState() == State.LIVE || pacman.getDirection() != Direction.STOP){
                        pacman.move(inputProcessor.getDirection(), SPEED_PACMAN);
                    }
                }

                //движение монстра
                for (Monster monster : monsters) {
                    if (TimeUtils.nanoTime() - monster.getCurrentTime() > monster.getSpeed()) {
                        if (monster.getState() != State.EAT || monster.getDirection() != Direction.STOP){
                            switch (monster.getState()) {
                                case LIVE:
                                    monster.move(monster.getDirection(), SPEED_MONSTER_LIVE);                                     //движение монстра
                                    break;
                                case RUN:
                                    monster.move(monster.getDirection(), SPEED_MONSTER_RUN);                                      //движение монстра
                                    break;
                                case DEAD:
                                    monster.move(monster.getDirection(), SPEED_MONSTER_DEAD);                                     //движение монстра
                                    break;
                            }
                        }
                    }
                }

                //столкновение монстра и пекмана
                for (Monster monster : monsters) {
                    if((Math.abs(monster.getPositionPixelsStart().x - pacman.getPositionPixelsStart().x) +
                            Math.abs(monster.getPositionPixelsStart().y - pacman.getPositionPixelsStart().y)) < DISTANSE) {
                        switch (monster.getState()){
                            case LIVE:
                                for (Monster monsterCurrent : monsters) {
                                    monsterCurrent.setPositionStop();
                                }
                                pacman.setPositionStop();
                                if (pacman.getState() == State.LIVE) {
                                    currentTimeDead = TimeUtils.nanoTime();
                                    pacman.setState(State.DEAD);
                                } else {
                                    if ((TimeUtils.nanoTime() - currentTimeDead) > DELAY_AFTER_DEATH_PACMAN) {
                                        for (Monster monsterCurrent : monsters) {
                                            monsterCurrent.newStartCoordinates();
                                            pacman.setState(State.LIVE);
                                            pacman.newStartCoordinates();
                                            inputProcessor.setDirection(Direction.LEFT);
                                        }
                                        stateGame = StateGame.READY;
                                        currentTimeReady = TimeUtils.nanoTime();
                                        currentTimeAddBonus = TimeUtils.nanoTime();
                                        pacman.decrementLive();
                                        bonusCurrent = null;
                                        readyClick = true;
                                    }
                                }
                                break;
                            case RUN:
                                if (pacman.getState() != State.EAT) {
                                    for (Monster monsterCurrent : monsters) {
                                        monsterCurrent.setPositionStop();
                                    }
                                    pacman.setPositionStop();
                                    monster.setState(State.EAT);
                                    pacman.setState(State.EAT);
                                    monsterEat = monster;
                                    pacman.incrementNumberEatMonster();
                                    pacman.addPoint(pacman.getNumberEatMonster() * POINT_EAT_MONSTER);
                                    //для безопасности
                                    if (pacman.getState() != State.EAT) {
                                        pacman.setState(State.EAT);
                                    }
                                    currentTimeRunMonsters += DELAY_EAT_PACMAN;
                                    currentTimeEat = TimeUtils.nanoTime();
                                }
                            case EAT:
                                if ((TimeUtils.nanoTime() - currentTimeEat) > DELAY_EAT_PACMAN) {
                                    monster.setState(State.DEAD);
                                    pacman.nextDiriction(SPEED_PACMAN);
                                    pacman.setState(State.LIVE);
                                    for (Monster monsterCurrent : monsters) {
                                        switch (monsterCurrent.getState()) {
                                            case LIVE:
                                                monsterCurrent.nextDiriction(SPEED_MONSTER_LIVE);                                     //движение монстра
                                                break;
                                            case RUN:
                                                monsterCurrent.nextDiriction(SPEED_MONSTER_RUN);                                      //движение монстра
                                                break;
                                            case DEAD:
                                                monsterCurrent.nextDiriction(SPEED_MONSTER_DEAD);                                     //движение монстра
                                                break;
                                        }
                                    }
                                }
                                break;


                        }

                    }
                }

                //если пекман ест, то все монстры должны остановиться
                if (pacman.getState() == State.EAT) {
                    for(Monster monster : monsters){
                        if (monster.getDirection() != Direction.STOP) {
                            monster.setPositionStop();
                        }
                    }
                }

                //если монстр бежит, то монстер жив
                if (TimeUtils.nanoTime() - currentTimeRunMonsters > TIME_DELEY_RUN_MONSTERS) {
                    for (Monster monster : monsters) {
                        if (monster.getState() == State.RUN){
                            monster.setState(State.LIVE);
                        }
                    }
                    pacman.resetNumberEatMonster();
                }

                //для безопасности
                //если пекман ест и не остановлен, то бегущие монстры оживают
                if (pacman.getState() == State.EAT && pacman.getDirection() != Direction.STOP) {
                    System.out.println(pacman.getState() + " " + pacman.getDirection());
                    for (Monster monster : monsters) {
                        if (monster.getState() == State.RUN){
                            monster.setState(State.LIVE);
                            monster.nextDiriction(SPEED_MONSTER_LIVE);
                        }
                    }
                    pacman.setState(State.LIVE);
                }

                //если все Points съедены, то переходим на следюющий уровень
                if (field.getIsEmptyPoints()){
                    //this.dispose();
                    ++screen.numberLevel;
                    if (screen.numberLevel > screen.LEVEL_NAMES.length - 1) {
                        screen.numberLevel = 0;
                    }
                    ++screen.numberMonsters;
                    if (screen.numberMonsters > screen.MAX_NUMBER_MONSTERS) {
                        screen.numberMonsters = screen.MAX_NUMBER_MONSTERS;
                    }
                    this.game.setScreen(new GameScreen(this.game, this.screen, this.pacman, this.monsters, this.bonusesEat, this.arrow, this.pausePlay,
                            screen.numberMonsters, screen.LEVEL_NAMES[screen.numberLevel], screen.SHIFTS[screen.numberLevel]));
                }

                break;
            case READY:

                if (pacman.getLive() <= 0){
                    PreferencesRecords.write(new Record(pacman.getPoints(), pacman.getLevel()));
                    if (pacman.getPoints() > highRecord.getScore()){
                        game.setScreen(new GameOver(game, screen, pacman.getPoints(), pacman.getPoints()));
                    } else {
                        game.setScreen(new GameOver(game, screen, pacman.getPoints(), highRecord.getScore()));
                    }
                }

                Gdx.input.setInputProcessor(inputMultiplexer);

                camera.update();

                batch.setProjectionMatrix(camera.combined);

                batch.begin();
                //long time = TimeUtils.nanoTime();
                batch.draw(field.getPlaceDown(), 0, 0);                                                                     //показ низа
                batch.draw(field.getPlaceUp(), 0, screen.down * screen.blockSizeY + screen.blockSizeY * screen.fieldSizeY); //показ верха
                batch.draw(field.getLevel(), 0, 0 + screen.down * screen.blockSizeY);                                       //показ поля
                iterationPoints = field.getIteratorPoint();                                                                 //показ точек
                while (iterationPoints.hasNext()) {
                    point = iterationPoints.next();
                    batch.draw(point.getSprite(), point.getPositionPixel().x,
                            point.getPositionPixel().y + screen.down * screen.blockSizeY);
                }
                if (bonusCurrent != null){
                    batch.draw(bonusCurrent.getSprite(), bonusCurrent.getPosition().x, bonusCurrent.getPosition().y + screen.down * screen.blockSizeY);
                }
                iterationMonsters = monsters.iterator();                                                                    //показ зверей
                while (iterationMonsters.hasNext()) {
                    monster = iterationMonsters.next();
                    batch.draw(monster.getSprite(), monster.getPositionPixelsStart().x, monster.getPositionPixelsStart().y + screen.down * screen.blockSizeY);
                }
                batch.draw(pacman.getSprite(), pacman.getPositionPixelsStart().x, pacman.getPositionPixelsStart().y + screen.down * screen.blockSizeY); //показ пекмана
                batch.draw(pacman.getLiveSprite(), LIVE_SPRITE_PACMAN_X , LIVE_SPRITE_PACMAN_Y, LIVE_SPRITE_PACMAN_WIDTH, LIVE_SPRITE_PACMAN_HEIGTH);
                if (( (int) ((TimeUtils.nanoTime() - currentTimeReady) / (long) 1e+9)) % 2 == 0) {
                    fontReady.draw(batch, READY, READY_X, READY_Y + shift * screen.blockSizeY);
                }
                if (!bonusesEat.isEmpty()) {
                    int numberBonus = 0;
                    for(Bonus bonus: bonusesEat){
                        batch.draw(bonus.getSprite(), SHOW_BONUS_X - bonusesEat.size() * SIZE_SHOW_BONUS_X / 2 + numberBonus++ * SIZE_SHOW_BONUS_X, screen.down * screen.blockSizeY - SIZE_SHOW_BONUS_Y + SHOW_BONUS_Y, SIZE_SHOW_BONUS_X, SIZE_SHOW_BONUS_Y);
                    }
                }
                batch.draw(arrow.getSprite(inputProcessor.getDirection()), ARROW_X1, ARROW_Y1, ARROW_X2, ARROW_Y2);
                fontLevel.draw(batch, LEVEL, LEVEL_X, LEVEL_Y);
                fontNumberLevel.draw(batch, String.valueOf(pacman.getLevel()), NUMBER_LEVEL_X - String.valueOf(pacman.getLevel()).length() * fontNumberLevel.getSpaceWidth() / 2, NUMBER_LEVEL_Y);
                fontScore.draw(batch, SCORE, SCORE_X  - (int) SCORE.length() * fontScore.getSpaceWidth() / 2, SCORE_Y);
                fontNumberScore.draw(batch, String.valueOf(pacman.getPoints()), NUMBER_SCORE_X - (int) String.valueOf(pacman.getPoints()).length() * fontNumberScore.getSpaceWidth() / 2, NUMBER_SCORE_Y);
                fontHighScore.draw(batch, HIGH_SCORE, HIGH_SCORE_X - (int) HIGH_SCORE.length() * fontHighScore.getSpaceWidth() / 2, HIGH_SCORE_Y);
                if (pacman.getPoints() > highRecord.getScore()){
                    fontNumberHighScore.draw(batch, String.valueOf(pacman.getPoints()), NUMBER_HIGH_SCORE_X - (int) String.valueOf(pacman.getPoints()).length() * fontNumberHighScore.getSpaceWidth() / 2, NUMBER_HIGH_SCORE_Y);
                } else {
                    fontNumberHighScore.draw(batch, String.valueOf(highRecord.getScore()), NUMBER_HIGH_SCORE_X - (int) String.valueOf(highRecord.getScore()).length() * fontNumberHighScore.getSpaceWidth() / 2, NUMBER_HIGH_SCORE_Y);
                }
                fontLevel.draw(batch, LIVE + String.valueOf(pacman.getLive() - 1), NUMBER_LIVE_X, NUMBER_LIVE_Y);
                //System.out.println(TimeUtils.nanoTime() - time);
                batch.end();

                stage.act(delta);
                stage.draw();

                //остановка всех животных, выполняется один раз
                if(readyClick){
                    for (Monster monsterCurrent : monsters) {
                        monsterCurrent.setPositionStop();
                    }
                    pacman.setPositionStop();
                    readyClick = false;
                }

                //если коснулись экрана или прошло определенное время, то продолжаем играть
                if (Gdx.input.isTouched() || ((TimeUtils.nanoTime() - currentTimeReady) > TIME_READY)) {
                    pacman.nextDiriction(SPEED_PACMAN);
                    pacman.setState(State.LIVE);
                    for (Monster monsterCurrent : monsters) {
                        switch (monsterCurrent.getState()) {
                            case LIVE:
                                monsterCurrent.nextDiriction(SPEED_MONSTER_LIVE);                                     //движение монстра
                                break;
                            case RUN:
                                monsterCurrent.nextDiriction(SPEED_MONSTER_RUN);                                      //движение монстра
                                break;
                            case DEAD:
                                monsterCurrent.nextDiriction(SPEED_MONSTER_DEAD);                                     //движение монстра
                                break;
                        }
                    }
                    stateGame = StateGame.PLAY;
                    currentTimePause = TimeUtils.nanoTime() - currentTimePause;
                    for(Monster monster: monsters){
                        if (monster.getState() == State.RUN){
                            currentTimeRunMonsters += currentTimePause;
                            break;
                        }
                    }
                    currentTimeAddBonus = TimeUtils.nanoTime();
                    break;
                }

                break;
            case PAUSE:

                Gdx.input.setInputProcessor(stagePause);

                camera.update();

                batch.setProjectionMatrix(camera.combined);
                batch.begin();
                batch.draw(pausePlay, 0, 0, screen.fieldSizeX * screen.blockSizeX,
                        screen.up * screen.blockSizeY + screen.fieldSizeY * screen.blockSizeY + screen.down * screen.blockSizeY);
                batch.end();

                stagePause.act(delta);
                stagePause.draw();

                break;
        }


    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {
        stateGame = StateGame.PAUSE;
        currentTimePause = TimeUtils.nanoTime();
    }

    @Override
    public void resume() {
        stateGame = StateGame.PAUSE;
        currentTimePause = TimeUtils.nanoTime();
    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        screen.dispose();
        monsters.clear();
    }

}

