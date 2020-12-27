package com.weatherapp.model;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.weatherapp.R;
import com.weatherapp.model.Constants;

public class MainReceiver extends BroadcastReceiver {
    private static final String TAG = "MessageBroadcastReceiver";
    private int messageId = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Обнаружено сообщение: " +
                        intent.getStringExtra(Constants.BROADCAST_MESSAGE),
                Toast.LENGTH_LONG).show();
        // This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        // Получить параметр сообщения
        String message = intent.getStringExtra(Constants.BROADCAST_MESSAGE);
        String title = intent.getStringExtra(Constants.BROADCAST_TITLE);
        if (message == null) message = "";
        if (title == null) title = "";
        if (true) {
            Log.d(TAG, message);
        }
        // создать нотификацию
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, Constants.BROADCAST_MAIN_CHANNEL)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message);
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(messageId++, builder.build());

    }
}