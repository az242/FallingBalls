package DataTypes;

public enum Power {
	Wall(0,"Wall",30,5),
	Invincibility(1,"Invincibility",45,6),
	Wormhole(2,"Wormhole",60,7);
	int index,cd,length;
	String name;
	Power(int index, String name,int cooldown,int length){
		this.index=index;
		this.name = name;
		cd=cooldown;
		this.length=length;
	}
	public int getIndex(){
		return index;
	}
	public String getName(){
		return name;
	}
	public Power getPower(){
		return this;
	}
	public int getCooldown() {
		return cd;
	}
	public int getLength() {
		return length;
	}

}
