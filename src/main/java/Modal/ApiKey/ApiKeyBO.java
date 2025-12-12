package Modal.ApiKey;

import java.util.ArrayList;

public class ApiKeyBO {
	private ApiKeyDAO dao = new ApiKeyDAO();
	ArrayList<ApiKey> ds;
	
	public ArrayList<ApiKey> readDB() throws Exception {
		ds = dao.readDB();
		return ds;
	}
	
	public String getApiKey() throws Exception {
		ArrayList<ApiKey> temp = new ArrayList<ApiKey>();
		temp = readDB();
		return temp.get(0).getKeyName();
	}
}
