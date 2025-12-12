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
}
