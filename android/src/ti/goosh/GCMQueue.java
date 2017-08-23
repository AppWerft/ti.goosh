package ti.goosh;

import org.appcelerator.titanium.TiApplication;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Context;
import android.database.Cursor; 
import android.database.sqlite.SQLiteDatabase; 

public class GCMQueue  {
	Context ctx = TiApplication.getInstance().getApplicationContext();
	public GCMQueue() {
		
		
		SQLiteDatabase mydatabase = ctx.openOrCreateDatabase("GCMQUEUE",Context.MODE_PRIVATE,
				null);
		mydatabase.close();

	}

	public void insertMessage(JSONObject message) {
		SQLiteDatabase mydatabase = ctx.openOrCreateDatabase("GCMQUEUE",
				Context.MODE_PRIVATE, null);
		mydatabase
				.execSQL("CREATE TABLE IF NOT EXISTS queue(id TEXT,ctime INTEGER, payload TEXT,done INTEGER);");
		String sql;
		try {
			sql = "INSERT INTO queue VALUES('"
					+ message.getString("google.message_id") + "',"
					+ String.valueOf(message.getLong("google.sent_time")) + ",'"
					+ message.toString() + "',0);";
			mydatabase.execSQL(sql);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mydatabase.close();
	}
}
