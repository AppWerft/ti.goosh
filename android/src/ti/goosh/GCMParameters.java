package ti.goosh;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.io.TiFileFactory;
import org.appcelerator.titanium.util.TiConvert;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Color;

public class GCMParameters {
	private String subject = "Subject";
	private String alert = "Alert";
	private String title = "Title";
	private String priority = "hight";
	private String sound;
	private String channel = "default";
	private String icon = "";
	private int color = Color.GRAY;
	private Boolean ongoing = false;
	private Boolean only_alert_once = false;
	private Boolean vibrate = true;
	private Boolean force_show_in_foreground = true;

	public GCMParameters(KrollDict options) throws JSONException {
		JSONObject defaults = loadJSONFromAsset();
		
		if (defaults.has("subject")) {
			this.subject = defaults.getString("subject");
		}
		if (options.containsKeyAndNotNull("subject")) {
			this.subject = options.getString("subject");
		}
		
		if (defaults.has("alert")) {
			this.alert = defaults.getString("alert");
		}
		if (options.containsKeyAndNotNull("alert")) {
			this.alert = options.getString("alert");
		}
		
		if (defaults.has(TiC.PROPERTY_TITLE)) {
			this.title = defaults.getString(TiC.PROPERTY_TITLE);
		}
		if (options.containsKeyAndNotNull(TiC.PROPERTY_TITLE)) {
			this.title = options.getString(TiC.PROPERTY_TITLE);
		}
		
		if (defaults.has("priority")) {
			this.priority = defaults.getString("priority");
		}
		if (options.containsKeyAndNotNull("priority")) {
			this.priority = options.getString("priority");
		}
		
		if (defaults.has("channel")) {
			this.channel = defaults.getString("channel");
		}
		if (options.containsKeyAndNotNull("channel")) {
			this.channel = options.getString("channel");
		}
		
		if (defaults.has("sound")) {
			this.sound = defaults.getString("sound");
		}
		if (options.containsKeyAndNotNull("sound")) {
			this.sound = options.getString("sound");
		}
		
		if (defaults.has("icon")) {
			this.icon = defaults.getString("icon");
		}
		if (options.containsKeyAndNotNull("icon")) {
			this.icon = options.getString("icon");
		}
		
		if (defaults.has("color")) {
			this.color = TiConvert.toColor(defaults.getString("color"));
		}
		if (options.containsKeyAndNotNull("color")) {
			this.color = TiConvert.toColor(options.getString("color"));
		}		
		
		if (defaults.has("ongoing")) {
			this.ongoing = defaults.getBoolean("ongoing");
		}
		if (options.containsKeyAndNotNull("ongoing")) {
			this.ongoing = defaults.getBoolean("ongoing");
		}
		
		if (defaults.has("only_alert_once")) {
			this.only_alert_once = defaults.getBoolean("only_alert_once");
		}
		if (options.containsKeyAndNotNull("only_alert_once")) {
			this.only_alert_once = defaults.getBoolean("only_alert_once");
		}	
		
		if (defaults.has("vibrate")) {
			this.vibrate = defaults.getBoolean("vibrate");
		}
		if (options.containsKeyAndNotNull("vibrate")) {
			this.vibrate = defaults.getBoolean("vibrate");
		}	
		
		if (defaults.has("force_show_in_foreground")) {
			this.force_show_in_foreground = defaults.getBoolean("force_show_in_foreground");
		}
		if (options.containsKeyAndNotNull("force_show_in_foreground")) {
			this.force_show_in_foreground = defaults.getBoolean("force_show_in_foreground");
		}	
		
	}

	public String getSubject() {
		return subject;
	}

	public String getAlert() {
		return alert;
	}

	public String getTitle() {
		return title != null ? title : "";
	}

	public String getPriority() {
		return priority;
	}

	public Boolean getOngoing() {
		return ongoing;
	}

	public void setOngoing(Boolean ongoing) {
		this.ongoing = ongoing;
	}

	public int getColor() {
		return color;
	}

	public Boolean getOnly_alert_once() {
		return only_alert_once;
	}

	public void setOnly_alert_once(Boolean only_alert_once) {
		this.only_alert_once = only_alert_once;
	}

	public Boolean getVibrate() {
		return vibrate;
	}

	public void setVibrate(Boolean vibrate) {
		this.vibrate = vibrate;
	}

	public String getSound() {
		return sound;
	}

	public Boolean getForce_show_in_foreground() {
		return force_show_in_foreground;
	}

	public void setForce_show_in_foreground(Boolean force_show_in_foreground) {
		this.force_show_in_foreground = force_show_in_foreground;
	}

	public String getChannel() {
		return channel;
	}

	public JSONObject loadJSONFromAsset() {
		String json = null;

		try {
			String url = TiGooshModule.getModule().resolveUrl(null,
					"gcm.defaults.json");
			InputStream inStream = TiFileFactory.createTitaniumFile(
					new String[] { url }, false).getInputStream();
			byte[] buffer = new byte[inStream.available()];
			inStream.read(buffer);
			inStream.close();
			json = new String(buffer, "UTF-8");

		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}
		try {
			return new JSONObject(json);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
}
