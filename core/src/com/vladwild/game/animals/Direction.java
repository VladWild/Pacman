package com.vladwild.game.animals;

public enum Direction {
    LEFT, RIGTH, UP, DOWN, STOP, nextDirection;

    public static Direction Random(){
        int random_number1 = 1 + (int) (Math.random() * 4);
        Direction direction = STOP;
        switch (random_number1) {
            case 1:
                direction = LEFT;
                break;
            case 2:
                direction = RIGTH;
                break;
            case 3:
                direction = UP;
                break;
            case 4:
                direction = DOWN;
                break;
        }
        return direction;
    }


}
