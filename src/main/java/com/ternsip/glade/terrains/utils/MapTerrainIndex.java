package com.ternsip.glade.terrains.utils;

public class MapTerrainIndex {
	private int currentTerrain;
	private int currentTop;
	private int currentBottom;
	private int currentLeft;
	private int currentRight;
	private int currentCTL;
	private int currentCTR;
	private int currentCBL;
	private int currentCBR;
	
	public MapTerrainIndex(){
		this.currentTerrain = 4;
		this.currentTop = 1;
		this.currentBottom = 7;
		this.currentLeft = 3;
		this.currentRight = 5;
		this.currentCTL = 0;
		this.currentCTR = 2;
		this.currentCBL = 6;
		this.currentCBR = 8;
	}
	
	public static MapTerrainIndex.Builder builder(){
		return new Builder();
	}
	
	private MapTerrainIndex(int currentTerrain, int currentTop, int currentBottom, int currentLeft, int currentRight,
			int currentCTL, int currentCTR, int currentCBL, int currentCBR) {
		this.currentTerrain = currentTerrain;
		this.currentTop = currentTop;
		this.currentBottom = currentBottom;
		this.currentLeft = currentLeft;
		this.currentRight = currentRight;
		this.currentCTL = currentCTL;
		this.currentCTR = currentCTR;
		this.currentCBL = currentCBL;
		this.currentCBR = currentCBR;
	}

	public int getCurrentTerrain() {
		return currentTerrain;
	}

	public void setCurrentTerrain(int currentTerrain) {
		this.currentTerrain = currentTerrain;
	}

	public int getCurrentTop() {
		return currentTop;
	}

	public void setCurrentTop(int currentTop) {
		this.currentTop = currentTop;
	}

	public int getCurrentBottom() {
		return currentBottom;
	}

	public void setCurrentBottom(int currentBottom) {
		this.currentBottom = currentBottom;
	}

	public int getCurrentLeft() {
		return currentLeft;
	}

	public void setCurrentLeft(int currentLeft) {
		this.currentLeft = currentLeft;
	}

	public int getCurrentRight() {
		return currentRight;
	}

	public void setCurrentRight(int currentRight) {
		this.currentRight = currentRight;
	}

	public int getCurrentCTL() {
		return currentCTL;
	}

	public void setCurrentCTL(int currentCTL) {
		this.currentCTL = currentCTL;
	}

	public int getCurrentCTR() {
		return currentCTR;
	}

	public void setCurrentCTR(int currentCTR) {
		this.currentCTR = currentCTR;
	}

	public int getCurrentCBL() {
		return currentCBL;
	}

	public void setCurrentCBL(int currentCBL) {
		this.currentCBL = currentCBL;
	}

	public int getCurrentCBR() {
		return currentCBR;
	}

	public void setCurrentCBR(int currentCBR) {
		this.currentCBR = currentCBR;
	}
	
	
	public static class Builder{
		private int currentTerrain;
		private int currentTop;
		private int currentBottom;
		private int currentLeft;
		private int currentRight;
		private int currentCTL;
		private int currentCTR;
		private int currentCBL;
		private int currentCBR;
		
	
		public Builder currentTerrain(int currentTerrain){
			this.currentTerrain = currentTerrain;
			return this;
		}
		
		public Builder currentTop(int currentTop){
			this.currentTop = currentTop;
			return this;
		}
		
		public Builder currentBottom(int currentBottom){
			this.currentBottom = currentBottom;
			return this;
		}
		
		public Builder currentLeft(int currentLeft){
			this.currentLeft = currentLeft;
			return this;
		}
		
		public Builder currentRight(int currentRight){
			this.currentRight = currentRight;
			return this;
		}
		public Builder currentCTL(int currentCTL){
			this.currentCTL = currentCTL;
			return this;
		}
		public Builder currentCTR(int currentCTR){
			this.currentCTR = currentCTR;
			return this;
		}
		public Builder currentCBL(int currentCBL){
			this.currentCBL = currentCBL;
			return this;
		}
		public Builder currentCBR(int currentCBR){
			this.currentCBR = currentCBR;
			return this;
		}
		
		public MapTerrainIndex build(){
			return new MapTerrainIndex(currentTerrain, currentTop, 
					currentBottom, currentLeft, currentRight, 
					currentCTL, currentCTR, currentCBL, currentCBR);
		}
	}
	
}
