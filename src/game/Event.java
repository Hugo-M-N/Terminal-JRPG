package game;

public enum Event {
	First_Explore(true),
	First_Forest(true),
	King_Goblin(false),
	King_Goblin_Defeated(false),
	First_Village(true);
	
	
	private boolean Status;

	Event(boolean status){
		this.setStatus(status);
	}

	public boolean getStatus() {
		return Status;
	}

	public void setStatus(boolean status) {
		Status = status;
	}
}
