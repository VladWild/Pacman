package com.vladwild.game.field;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.vladwild.game.game.GameScreen;
import com.vladwild.game.resource.ResourceManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;

//класс, формирующий игровое поле в SpriteBatch
public class Field {
    private static final String PROPERTY_TEXTURES = "property\\textures.properties";            //адрес файла textures.properties"
    private static final String PROPERTY_LEVELS = "property\\levels.properties";                //адрес файла levels.properties"
    private String nameLevel;                                                                   //ключ в properties к соответствующему уровню уровню

    private GameScreen gameScreen;                                                              //объект GameScreen
    private int[][] stateMatrix;                                                                //матрица уровня, считанныя из файла
    private int[][] stateMatrixLogic;                                                           //логическая матрица игрового поля
    private boolean[][] nodalPointsMatrix;                                                      //матрица узловых точек
    private Pixmap elements[] = new Pixmap[19];                                                 //все элементы Pixmap для отрисовки уровня
    private Sprite level;                                                                       //спрайт всего уровня
    private HashSet<Point> points;                                                              //коллекция точек
    private Iterator<Point> iterator;                                                           //итератор коллекции точек
    private Sprite placeDown;
    private Sprite placeUp;


    public Field(GameScreen gameScreen, String nameLevel) {
        this.gameScreen = gameScreen;
        this.nameLevel = nameLevel;

        this.stateMatrix = readMatrixfromFileCSV();
        this.stateMatrixLogic = getMatrixLogic();
        this.nodalPointsMatrix = getFormingNodalPointsMatrix();

        this.points = new HashSet<Point>();

        ResourceManager manager = new ResourceManager(PROPERTY_TEXTURES);                       //создаем менаджер текстур

        for (int i = 1; i < 19; i++) {
            elements[i] = new Pixmap(manager.getFileHandle(String.valueOf(18)));
            elements[i].drawPixmap(new Pixmap(manager.getFileHandle(String.valueOf(i))), 0, 0);
        }

        drawField();
        drawPoints();
        drawPlaceDown();
        drawPlaceUp();


    }

    //отрисовка текстуры уровня
    private void drawField() {
        Texture level = new Texture(gameScreen.screen.blockSizeX * gameScreen.screen.fieldSizeX,
                gameScreen.screen.blockSizeY * gameScreen.screen.fieldSizeY, Pixmap.Format.RGBA8888);
        for (int i = 0; i < gameScreen.screen.fieldSizeX; i++) {
            for (int j = 0; j < gameScreen.screen.fieldSizeY; j++) {
                if ((stateMatrix[j][i] == 16) || (stateMatrix[j][i] == 17)) {
                    level.draw(elements[18], i * gameScreen.screen.blockSizeX, j * gameScreen.screen.blockSizeY);
                } else {
                    level.draw(elements[stateMatrix[j][i]], i * gameScreen.screen.blockSizeX, j * gameScreen.screen.blockSizeY);
                }
            }
        }
        this.level = new Sprite(level);
    }

    //создание коллекции объектов точек
    private void drawPoints() {
        for (int i = 0; i < gameScreen.screen.fieldSizeX; i++) {
            for (int j = 0; j < gameScreen.screen.fieldSizeY; j++) {
                //if ((i == 12 && j == 30) ||(i == 13 && j == 30) || (i == 14 && j == 30) || (i == 15 && j == 30) ||(i == 16 && j == 30) ||(i == 17 && j == 30)||(i == 18 && j == 30)) {//
                    if (stateMatrix[j][i] == 16) {
                        this.points.add(new Point(i, gameScreen.screen.fieldSizeY - j - 1, SizePoint.BIG, gameScreen));
                    }
                    if (stateMatrix[j][i] == 17) {
                        this.points.add(new Point(i, gameScreen.screen.fieldSizeY - j - 1, SizePoint.SMALL, gameScreen));
                    }
                //}//
            }
        }
        this.iterator = points.iterator();
    }

    //чтение матрицы поля из файла txt
    private int[][] readMatrixfrоmFile() {
        int[][] stateMatrix = new int[gameScreen.screen.fieldSizeY][gameScreen.screen.fieldSizeX];  //создание матрицы
        FileHandle file = new ResourceManager(PROPERTY_LEVELS).getFileHandle(nameLevel);            //чтение файла уровня
        String[] lines = file.readString().split("[\\n,\\r]");                                      //вбиваем в матрицу stateMatrix уровень
        for (int i = 0; i < gameScreen.screen.fieldSizeY; i++) {
            int j = 0;
            for (String elem : lines[2 * i].split(" ")) {
                stateMatrix[i][j++] = Integer.valueOf(elem);
            }
        }
        return stateMatrix;
    }

    //чтение матрицы поля из файла CSV
    private int[][] readMatrixfromFileCSV() {
        int[][] stateMatrix = new int[gameScreen.screen.fieldSizeY][gameScreen.screen.fieldSizeX];  //создание матрицы
        FileHandle file = new ResourceManager(PROPERTY_LEVELS).getFileHandle(nameLevel);            //чтение файла уровня
        BufferedReader fileReader = new BufferedReader(file.reader());
        String line = "";
        int j = 0;
        try {
            while ((line = fileReader.readLine()) != null) {
                String[] tokens = line.split(";");
                int i = 0;
                for (String elem : tokens) {
                    stateMatrix[j][i++] = Integer.valueOf(elem);
                }
                j++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stateMatrix;
    }

    //получение логического поля
    private int[][] getMatrixLogic(){
        int[][] matrix = new int[gameScreen.screen.fieldSizeY][gameScreen.screen.fieldSizeX];
        for (int i = 0; i < gameScreen.screen.fieldSizeX; i++) {
            for (int j = 0; j < gameScreen.screen.fieldSizeY; j++) {
                if ((stateMatrix[j][i] == 16) || (stateMatrix[j][i] == 17) || (stateMatrix[j][i] == 18)){
                    matrix[(gameScreen.screen.fieldSizeY - 1) - j][i] = 1;
                } else {
                    if ((stateMatrix[j][i] == 14) || (stateMatrix[j][i] == 15)){
                        matrix[(gameScreen.screen.fieldSizeY - 1) - j][i] = 2;
                    }
                    else {
                        matrix[(gameScreen.screen.fieldSizeY - 1) - j][i] = 0;
                    }
                }
            }
        }
        return matrix;
    }

    //формирование матрицы узловых точек
    private boolean[][] getFormingNodalPointsMatrix(){
        boolean[][] nodalPointsMatrix = new boolean[gameScreen.screen.fieldSizeY][gameScreen.screen.fieldSizeX];  //создание матрицы
        for (int i = 2; i < this.gameScreen.screen.fieldSizeX - 2; i++)
        {
            for (int j = 2; j < this.gameScreen.screen.fieldSizeY - 2; j++)
            {
                if((((stateMatrixLogic[j - 1][i] != 0) && (stateMatrixLogic[j][i + 1]!=0)) ||
                    ((stateMatrixLogic[j][i + 1] != 0) && (stateMatrixLogic[j + 1][i] != 0)) ||
                    ((stateMatrixLogic[j + 1][i]!= 0) && (stateMatrixLogic[j][i - 1] != 0)) ||
                    ((stateMatrixLogic[j][i - 1] != 0) && (stateMatrixLogic[j - 1][i] != 0))) &&
                    stateMatrixLogic[j][i] == 1)
                    {
                        nodalPointsMatrix[j][i] = true;
                    }
            }
        }
        nodalPointsMatrix[16 + gameScreen.shift][13] = true;
        nodalPointsMatrix[16 + gameScreen.shift][16] = true;

        //
        /*
        for (int j = 0; j < gameScreen.screen.fieldSizeY; j++){
            for (int i = 0; i < gameScreen.screen.fieldSizeX; i++) {
                    System.out.print(String.valueOf(stateMatrixLogic[gameScreen.screen.fieldSizeY - j - 1][i]) + " ");
            }
            System.out.println();
        }

        System.out.println("---------------------------------");

        for (int j = 0; j < gameScreen.screen.fieldSizeY; j++){
            for (int i = 0; i < gameScreen.screen.fieldSizeX; i++) {
                if (nodalPointsMatrix[gameScreen.screen.fieldSizeY - j - 1][i]) {
                    System.out.print("1 ");
                } else {
                    System.out.print("0 ");
                }
            }
            System.out.println();
        }
        */
        //

        return nodalPointsMatrix;
    }

    //формирование нижней границы GameScreen
    private void drawPlaceDown(){
        Texture placeDown = new Texture(gameScreen.screen.blockSizeX * gameScreen.screen.fieldSizeX,
                gameScreen.screen.down * gameScreen.screen.blockSizeY, Pixmap.Format.RGBA8888);
        for (int i = 0; i < gameScreen.screen.fieldSizeX; i++) {
            for (int j = 0; j < gameScreen.screen.down; j++) {
                placeDown.draw(elements[18], i * gameScreen.screen.blockSizeX, j * gameScreen.screen.blockSizeY);
            }
        }
        this.placeDown = new Sprite(placeDown);
    }

    //формирование верхней границы GameScreen
    private void drawPlaceUp(){
        Texture placeUp = new Texture(gameScreen.screen.blockSizeX * gameScreen.screen.fieldSizeX,
                gameScreen.screen.up * gameScreen.screen.blockSizeY, Pixmap.Format.RGBA8888);
        for (int i = 0; i < gameScreen.screen.fieldSizeX; i++) {
            for (int j = 0; j < gameScreen.screen.up; j++) {
                placeUp.draw(elements[18], i * gameScreen.screen.blockSizeX, j * gameScreen.screen.blockSizeY);
            }
        }
        this.placeUp = new Sprite(placeUp);
    }

    //получение итератора
    public Iterator<Point> getIteratorPoint(){
        this.iterator = points.iterator();
        return this.iterator;
    }

    //удаление Point, которую съел Pacman
    public void deletePoint(int x, int y){
        iterator = points.iterator();
        while (iterator.hasNext()) {
            Point point = iterator.next();
            if ((point.getLogicPixel().x == x) && (point.getLogicPixel().y == y)) {
                iterator.remove();
                break;
            }
        }
    }

    //возвращает размер Point
    public SizePoint getSizePoint(int x, int y){
        iterator = points.iterator();
        while (iterator.hasNext()) {
            Point point = iterator.next();
            if ((point.getLogicPixel().x == x) && (point.getLogicPixel().y == y)) {
                return point.getSizePoint();
            }
        }
        return null;
    }

    //получение текстуры уровня
    public Sprite getLevel(){
        return this.level;
    }

    //получение логического поля матрицы
    public int[][] getStateMatrixLogic() {return stateMatrixLogic; }

    //получение поля узловых точек
    public boolean[][] getNodalPointsMatrix() {return nodalPointsMatrix; }

    //получение спрайта нижней границы GameScreen
    public Sprite getPlaceDown() {return this.placeDown; }

    //получение спрайта верхней границы GameScreen
    public Sprite getPlaceUp() {return  this.placeUp; }

    //проверка Points на пустоту
    public boolean getIsEmptyPoints() {return  this.points.isEmpty(); }

}
