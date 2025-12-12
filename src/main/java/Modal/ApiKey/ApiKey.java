package Modal.ApiKey;

public class ApiKey {
	private int ID;
	private String KeyName;
	public ApiKey() {
		super();
		// TODO Auto-generated constructor stub
	}
	public ApiKey(int iD, String keyName) {
		super();
		ID = iD;
		KeyName = keyName;
	}
	public int getID() {
		return ID;
	}
	public void setID(int iD) {
		ID = iD;
	}
	public String getKeyName() {
		return KeyName;
	}
	public void setKeyName(String keyName) {
		KeyName = keyName;
	}
	
}
