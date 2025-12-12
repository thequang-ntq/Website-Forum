package Modal.ApiKey;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import Config.DBConfig;

public class ApiKeyDAO {
	public ArrayList<ApiKey> readDB() throws Exception {
		ArrayList<ApiKey> ds = new ArrayList<ApiKey>();
		String sql = "SELECT * FROM ApiKey";
		PreparedStatement pr = DBConfig.getInstance().getCn().prepareStatement(sql);
		ResultSet rs = pr.executeQuery();
		while(rs.next()) {
			int ID = rs.getInt("ID");
			String KeyName = rs.getNString("KeyName");
			ds.add(new ApiKey(ID, KeyName));
		}
		
		pr.close();
		rs.close();
		return ds;
	}
	
	// API Key cho ChatGPT
	public String getOpenAIKey() throws Exception {
	    String apikey = null;
	    String sql = "SELECT KeyName FROM ApiKey WHERE KeyName LIKE 'sk-proj%'";
	    PreparedStatement pr = DBConfig.getInstance().getCn().prepareStatement(sql);
	    ResultSet rs = pr.executeQuery();
	    
	    // Lấy key đầu tiên trong ds thỏa điều kiện
	    if (rs.next()) {
            apikey = rs.getString("KeyName");
        }
	    
	    rs.close();
	    pr.close();
	    return apikey;
	}
}
