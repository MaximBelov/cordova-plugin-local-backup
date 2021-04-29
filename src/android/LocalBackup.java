package org.apache.cordova;

import android.content.Context;
import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class LocalBackup extends CordovaPlugin {

  static final String LOG_TAG = "CordovaLocalBackupPlugin";
  static final String FILE_NAME = "backupData.json";

  static final Object sDataLock = new Object();

  public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
    Log.d(LOG_TAG, "JS call: " + action);
    if (action.equals("create")) {
      JSONObject data = args.getJSONObject(0);
      callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, create(data, callbackContext)));
      return true;
    } else if (action.equals("read")) {
      callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, read()));
      return true;
    } else if (action.equals("exists")) {
      callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, exists()));
      return true;
    } else if (action.equals("remove")) {
      callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, remove()));
      return true;
    }

    callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.ERROR));
    return false;
  }

  private boolean create(JSONObject data, CallbackContext callbackContext) {

    cordova.getThreadPool().execute(new Runnable() {
      public void run() {
        BufferedWriter writer = null;
        try {
          Context context = cordova.getActivity().getApplicationContext();
          File file = new File(context.getFilesDir(), FILE_NAME);
          writer = new BufferedWriter(new FileWriter(file));
          writer.write(data.toString());
        } catch (IOException e) {
          Log.e(LOG_TAG, e.getMessage());
        } finally {
          try {
            if (writer != null) {
              writer.close();
            }
          } catch (IOException e) {
            Log.e(LOG_TAG, "Failed to save backup" + e.getMessage());
          }
        }
        return true;
      }
    });

  }

  private JSONObject read() {
    BufferedReader reader = null;

    try {
      Context context = cordova.getActivity().getApplicationContext();
      File file = new File(context.getFilesDir(), FILE_NAME);

      if (file.exists() && !file.isDirectory()) {
        reader = new BufferedReader(new FileReader(file));
        StringBuilder sb = new StringBuilder();
        String line = reader.readLine();

        while (line != null) {
          sb.append(line);
          line = reader.readLine();
        }
        String fileContents = sb.toString();

        return new JSONObject(fileContents);
      }
    } catch (IOException e) {
      Log.e(LOG_TAG, "Failed to open backup file" + e.getMessage());
    } catch (JSONException e) {
      Log.e(LOG_TAG, "Failed to parse JSON" + e.getMessage());
    } finally {
      try {
        if (reader != null) {
          reader.close();
        }
      } catch (IOException e) {
        Log.e(LOG_TAG, e.getMessage());
      }
    }

    return new JSONObject();
  }

  private boolean exists() {
    Context context = cordova.getActivity().getApplicationContext();
    File file = new File(context.getFilesDir(), FILE_NAME);

    return file.exists() && !file.isDirectory();
  }

  private boolean remove() {
    Context context = cordova.getActivity().getApplicationContext();
    File file = new File(context.getFilesDir(), FILE_NAME);

    if (file.exists() && !file.isDirectory()) {
      Log.e(LOG_TAG, "Failed to delete backup");

      return file.delete();
    }

    return false;
  }

}
