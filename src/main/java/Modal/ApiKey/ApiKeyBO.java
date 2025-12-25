package Modal.ApiKey;

import java.util.ArrayList;

public class ApiKeyBO {
	private ApiKeyDAO dao = new ApiKeyDAO();
	ArrayList<ApiKey> ds;
	
	public ArrayList<ApiKey> readDB() throws Exception {
		ds = dao.readDB();
		return ds;
	}
	
	// Lấy phần tử đầu tiên trong danh sách chính là key của Gemini AI
	public String getApiKey() throws Exception {
		ArrayList<ApiKey> temp = new ArrayList<ApiKey>();
		temp = readDB();
		return temp.get(0).getKeyName();
	}
	
	// Lấy key của OpenAI (ChatGPT)
	public String getOpenAIKey() throws Exception {
		return dao.getOpenAIKey();
	}
}
