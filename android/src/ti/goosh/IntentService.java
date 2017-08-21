package ti.goosh;

import java.io.BufferedInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;


import org.appcelerator.titanium.TiActivity;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.util.TiRHelper;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class IntentService extends GcmListenerService {
	private static final String LCAT = "tigooshIS";
	private static final AtomicInteger atomic = new AtomicInteger(0);

	@Override
	public void onMessageReceived(String from, Bundle bundle) {
		Log.d(LCAT, "Push notification received from ~~~~~~~~~~: " + from);
		for (String key : bundle.keySet()) {
			Object value = bundle.get(key);
			Log.d(LCAT,
					String.format("key: %s => Value: %s (%s)", key,
							value.toString(), value.getClass().getName()));
		}

		JSONObject message;
		try {
			// AWS:
			if (bundle.containsKey("default")) {
				message = (new JSONObject(bundle.getString("default")))
						.getJSONObject("gcm");
				Log.d(LCAT, message.toString());
				parseNotification(message);
			} else {
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private int getResource(String type, String name) {
		int icon = 0;
		if (name != null) {
			int index = name.lastIndexOf(".");
			if (index > 0)
				name = name.substring(0, index);
			try {
				icon = TiRHelper.getApplicationResource(type + "." + name);
			} catch (TiRHelper.ResourceNotFoundException ex) {
				Log.e(LCAT, type + "." + name
						+ " not found; make sure it's in platform/android/res/"
						+ type);
			}
		}

		return icon;
	}

	private Bitmap getBitmapFromURL(String src) throws Exception {
		HttpURLConnection connection = (HttpURLConnection) (new URL(src))
				.openConnection();
		connection.setDoInput(true);
		connection.setUseCaches(false); // Android BUG
		connection.connect();
		return BitmapFactory.decodeStream(new BufferedInputStream(connection
				.getInputStream()));
	}

	private void showNotification(Context ctx, JSONObject message) {
		String title = "";
		String alert = "";
		try {
			title = message.getString("title");
			alert = message.getString("alert");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Log.w(LCAT, "Show Notification: TRUE ~~~~~~~~~~~~~~~");
		/* Create intent to (re)start the app's root activity (from gcmpush)*/
		String pkg = ctx.getPackageName();
        Intent launcherIntent = ctx.getPackageManager().getLaunchIntentForPackage(pkg);
        launcherIntent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        launcherIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		PendingIntent pendingIntent = PendingIntent.getActivity(ctx, 0, launcherIntent,
				Intent.FLAG_ACTIVITY_NEW_TASK);
		
		// build the manager:
		NotificationManager notificationManager = (NotificationManager) TiApplication
				.getInstance().getSystemService(Context.NOTIFICATION_SERVICE);
			launcherIntent.putExtra(TiGooshModule.INTENT_EXTRA, message.toString());

	
		// Start building notification

		NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx);
		int builder_defaults = 0;
		// adding pendingIntent
		builder.setContentIntent(pendingIntent);

		builder.setAutoCancel(false);
		builder.setPriority(NotificationCompat.PRIORITY_HIGH);

		// Title
		builder.setContentTitle(title);

		// alert
		builder.setContentText(alert);
		builder.setTicker(alert);

		// BigText

		if (message != null && message.has("big_text")) {
			NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
			try {
				bigTextStyle.bigText(message.getString("big_text"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (message.has("big_text_summary")) {
				try {
					bigTextStyle.setSummaryText(message
							.getString("big_text_summary"));
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			builder.setStyle(bigTextStyle);
		}

		// Icons
		try {
			int smallIcon = this.getResource("drawable", "notificationicon");
			if (smallIcon > 0) {
				builder.setSmallIcon(smallIcon);
			}
		} catch (Exception ex) {
			Log.e(LCAT, "Smallicon exception: " + ex.getMessage());
		}

		// Large icon
		if (message != null && message.has("icon")) {
			try {
				Bitmap icon = this.getBitmapFromURL(message.getString("icon"));
				builder.setLargeIcon(icon);
			} catch (Exception ex) {
				Log.e(LCAT, "Icon exception: " + ex.getMessage());
			}
		}

		// Color

		if (message != null && message.has("color")) {
			try {
				int color = Color.parseColor(message.getString("color"));
				builder.setColor(color);
			} catch (Exception ex) {
				Log.e(LCAT, "Color exception: " + ex.getMessage());
			}
		}

		// Badge
		if (message != null && message.has("badge")) {
			int badge;
			try {
				badge = message.getInt("badge");
				BadgeUtils.setBadge(ctx, badge);
				builder.setNumber(badge);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		// Sound
		if (message != null && message.has("sound")) {
			Object sound;
			try {
				sound = message.get("sound");
				if (sound instanceof Boolean && (((Boolean) sound) == true)
						|| (sound instanceof String)
						&& (((String) sound).equals("default"))) {
					builder_defaults |= Notification.DEFAULT_SOUND;
				} else if (sound instanceof String) {
					int resource = getResource("raw", (String) sound);
					builder.setSound(Uri.parse("android.resource://"
							+ ctx.getPackageName() + "/" + resource));
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		// Ongoing
		if (message != null && message.has("ongoing")) {
			try {
				Boolean ongoing = message.getBoolean("ongoing");
				builder.setOngoing(ongoing);
			} catch (Exception ex) {
				Log.e(LCAT, "Ongoing exception: " + ex.getMessage());
			}
		} else {
			builder_defaults |= Notification.DEFAULT_LIGHTS;
		}
		// Only alert once
		if (message != null && message.has("only_alert_once")) {
			try {
				Boolean oaoJson = message.getBoolean("only_alert_once");
			} catch (Exception ex) {
				Log.e(LCAT, "Only alert once exception: " + ex.getMessage());
			}
		} else {
			builder_defaults |= Notification.DEFAULT_LIGHTS;
		}

		// Builder defaults OR
		builder.setDefaults(builder_defaults);

		// Tag
		String tag = "";
		if (message != null && message.has("tag")) {
			try {
				tag = message.getString("tag");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// Nid
		int id = 0;

		id = atomic.getAndIncrement();

		// Send
		notificationManager.notify(tag, id, builder.build());

	}

	private void parseNotification(JSONObject message) {
		Context ctx = TiApplication.getInstance().getApplicationContext();
		TiGooshModule module = TiGooshModule.getModule();
		Boolean isAppInBackground = !testIfActivityIsTopInList()
				.getIsForeground();
		Log.d(LCAT, "~~~~~~~~ background=" + isAppInBackground);
		// Flag that determine if the message should be broadcasted to
		// TiGooshModule and call the callback
		Boolean sendMessage = !isAppInBackground;
		// Flag to show the system alert
		Boolean showNotification = isAppInBackground;

		// the title and alert

		// here wer have title, alert and data
		if (!isAppInBackground) {
			if (message != null && message.has("force_show_in_foreground")) {
				Boolean forceShowInForeground = false;
				try {
					forceShowInForeground = message
							.getBoolean("force_show_in_foreground");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				showNotification = (forceShowInForeground == true);
			} else {
				showNotification = false;
			}
		}
		
        TiApplication.getInstance().getAppProperties().setString("GCM_LAST_DATA", message.toString());
		if (sendMessage && module != null) {
			module.sendMessage(message.toString(), isAppInBackground);
		}

		if (showNotification) {
			showNotification(ctx, message);

		} else {
			Log.w(LCAT, "Show Notification: FALSE");
		}
	}

	static public TaskTestResult testIfActivityIsTopInList() {
		try {
			TaskTestResult result = new ForegroundCheck().execute(
					TiApplication.getInstance().getApplicationContext()).get();
			return result;
		} catch (InterruptedException e) {
			e.printStackTrace();
			return null;
		} catch (ExecutionException e) {
			e.printStackTrace();
			return null;
		}
	}

	private String getCountlyId(Bundle bundle) {
		String id = bundle.getString("c.i");
		return "{\"c.i\": \"" + id + "\"}";
	}
}
