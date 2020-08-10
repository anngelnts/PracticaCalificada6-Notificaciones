package com.desarrollo.practicacalificada6;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

public class OrderBroadcastReceiver extends BroadcastReceiver {

    private static final String PRIMARY_CHANNEL_ID = "channel_2";
    private static final String PRIMARY_CHANNEL_NAME = "channel_check";
    private static final int NOTIFICATION_ID = 1;

    private NotificationManager notificationManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        if(MainActivity.ACTION_ORDER.equals(intent.getAction())) {
            Order order = new Order();
            order.setCode(intent.getStringExtra("code"));
            order.setDescription(intent.getStringExtra("description"));
            order.setPrice(intent.getDoubleExtra("price", 0));
            showNotification(context, order);
            sendNotification(context, order);
        }
    }

    private void showNotification(Context context, Order order){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(order.getCode()).append(" - ").append(order.getDescription()).append(" - S/ ").append(order.getPrice());
        Toast.makeText(context, stringBuilder, Toast.LENGTH_SHORT).show();
    }

    private void sendNotification(Context context, Order order){

        String titleNotify ="Su Pedido " + order.getCode() + " ha sido confirmado";
        String bodyNotify = order.getDescription() + " S/ " + order.getPrice();

        //default notification sound
        Uri soundDefault = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(context, PRIMARY_CHANNEL_ID);
        notifyBuilder.setSmallIcon(R.drawable.ic_tick);
        notifyBuilder.setContentTitle(titleNotify);
        notifyBuilder.setContentText(bodyNotify);
        notifyBuilder.setTicker(bodyNotify);
        notifyBuilder.setSound(soundDefault);
        notifyBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        notifyBuilder.setAutoCancel(true);

        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            //Create Notification Channel
            NotificationChannel notificationChannel = new NotificationChannel(PRIMARY_CHANNEL_ID, PRIMARY_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("Notification from Checks");
            notificationManager.createNotificationChannel(notificationChannel);
        }
        assert notificationManager != null;
        notificationManager.notify(NOTIFICATION_ID, notifyBuilder.build());
    }
}
