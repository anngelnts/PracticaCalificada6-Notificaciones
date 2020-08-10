package com.desarrollo.practicacalificada6;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String PRIMARY_CHANNEL_ID = "channel_1";
    private static final String PRIMARY_CHANNEL_NAME = "channel_orders";
    private static final int NOTIFICATION_ID = 0;
    public static final String ACTION_ORDER = "action_order";

    private NotificationManager notificationManager;
    private Order order;

    private TextView code_edit_text;
    private TextView description_edit_text;
    private TextView price_edit_text;
    private Button button_notify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        code_edit_text = findViewById(R.id.code_edit_text);
        description_edit_text = findViewById(R.id.description_edit_text);
        price_edit_text = findViewById(R.id.price_edit_text);

        button_notify = findViewById(R.id.button_notify);
        button_notify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String code = code_edit_text.getText().toString();
                String description = description_edit_text.getText().toString();
                String priceString = price_edit_text.getText().toString();

                if(code.isEmpty()){
                    Toast.makeText(MainActivity.this, "Por favor, ingrese un código", Toast.LENGTH_SHORT).show();
                }else if(description.isEmpty()){
                    Toast.makeText(MainActivity.this, "Por favor, ingrese una descripción", Toast.LENGTH_SHORT).show();
                }else if(priceString.isEmpty()){
                    Toast.makeText(MainActivity.this, "Por favor, ingrese un precio", Toast.LENGTH_SHORT).show();
                }else{
                    Double price = Double.parseDouble(priceString);
                    Order newOrder = new Order();
                    newOrder.setCode(code);
                    newOrder.setDescription(description);
                    newOrder.setPrice(price);
                    order = newOrder;
                    sendNotification();
                }
            }
        });

        createNotificationChannel();
    }

    public void sendNotification(){
        NotificationCompat.Builder notifyBuilder = getNotificationBuilder();
        notificationManager.notify(NOTIFICATION_ID, notifyBuilder.build());
    }

    public void createNotificationChannel(){
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            //Create Notification Channel
            NotificationChannel notificationChannel = new NotificationChannel(PRIMARY_CHANNEL_ID, PRIMARY_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription("Notification from Orders");
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    private NotificationCompat.Builder getNotificationBuilder(){

        Intent intent = new Intent(this, OrderBroadcastReceiver.class);
        intent.setAction(ACTION_ORDER);
        intent.putExtra("code", order.getCode());
        intent.putExtra("description", order.getDescription());
        intent.putExtra("price", order.getPrice());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,0, intent, 0);

        String bodyNotify = "Su código de Pedido es " + order.getCode();

        //custom notification sound
        Uri soundCustom = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getApplicationContext().getPackageName() + "/" + R.raw.notification);

        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(this, PRIMARY_CHANNEL_ID);
        notifyBuilder.setSmallIcon(R.drawable.ic_bell);
        notifyBuilder.setContentTitle("¡Tiene un nuevo Pedido!");
        notifyBuilder.setContentText(bodyNotify);
        notifyBuilder.setTicker(bodyNotify);
        notifyBuilder.setSound(soundCustom);
        notifyBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        notifyBuilder.setAutoCancel(true);
        notifyBuilder.setContentIntent(pendingIntent);
        notifyBuilder.addAction(0, getString(R.string.button_more), pendingIntent);
        return notifyBuilder;
    }
}
