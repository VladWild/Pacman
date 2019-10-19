package com.vladwild.game.records;

public class Record {
	private int score;
	private int level;
	
	Record(){}
	
	public Record(int score, int level){
		this.score = score;
		this.level = level;
	}
	
	public void setScoreLevel(int score, int level){
		this.score = score;
		this.level = level;
	}
	
	public void setScore(int score){
		this.score = score;
	}
	
	public void setLevel(int level){
		this.level = level;
	}
	
	public int getScore(){
		return this.score;
	}
	
	public int getLevel(){
		return this.level;
	}

	public int getLength(){
		return String.valueOf(Math.abs(score)).length() + String.valueOf(Math.abs(level)).length() + 1;
	}

	@Override
	public String toString() {
		return "Record [score=" + score + ", level=" + level + "]";
	}

}
