package com.example.fariseev_ps;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.HashMap;

import static android.content.Context.NOTIFICATION_SERVICE;

class NotificationUtils {



    private static NotificationUtils instance;

    private static Context context;
    public static final String NOTIFICATION_CHANNEL_ID_SERVICE = "com.mypackage.service";
    public static final String NOTIFICATION_CHANNEL_ID_INFO = "com.mypackage.download_info";

    private NotificationManager manager; // Системная утилита, упарляющая уведомлениями
    private int lastId = 0; //постоянно увеличивающееся поле, уникальный номер каждого уведомления
    private HashMap<Integer, Notification> notifications; //массив ключ-значение на все отображаемые пользователю уведомления


    //приватный контструктор для Singleton
    private NotificationUtils(Context context) {
        NotificationUtils.context = context;
        manager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        notifications = new HashMap<Integer, Notification>();
    }


    /**
     * Получение ссылки на синглтон
     */
    public static NotificationUtils getInstance(Context context) {
        if (instance == null) {
            instance = new NotificationUtils(context);
        } else {
            NotificationUtils.context = context;
        }
        return instance;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public int createInfoNotification(String message){
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = notificationManager.getNotificationChannel(message);
            if (mChannel == null) {
                mChannel = new NotificationChannel(message, "Справочник", importance);
                mChannel.setDescription("Справочник");
                //mChannel.enableVibration(true);
                //mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                notificationManager.createNotificationChannel(mChannel);
            }
            builder = new NotificationCompat.Builder(context, message);

            Intent intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

            builder.setContentTitle("Справочник")  // required
                    .setSmallIcon(R.mipmap.sprkpmesicon) // required
                    .setContentText(message)  // required
                    //.setDefaults(Notification.DEFAULT_ALL)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent);
                   // .setTicker(message)
                    //.setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});


        }
        else {
            final Intent emptyIntent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, emptyIntent, 0);
            builder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.mipmap.sprkpmes) //иконка уведомления
                    .setAutoCancel(true) //уведомление закроется по клику на него
                    .setTicker(message) //текст, который отобразится вверху статус-бара при создании уведомления
                    .setContentText(message) // Основной текст уведомления
                    .setContentIntent(pendingIntent)
                    .setWhen(System.currentTimeMillis()) //отображаемое время уведомления
                    .setContentTitle("Справочник"); //заголовок уведомления
            // .setDefaults(Notification.DEFAULT_ALL); // звук, вибро и диодный индикатор выставляются по умолчанию

            Log.d("--", "Уведомление - " + message);
          //  notificationManager.notify(lastId, builder.build());
            notifications.put(lastId, builder.build()); //теперь мы можем обращаться к нему по id
        }
        Notification notification = builder.build();
        notificationManager.notify(lastId, notification);

        return lastId++;
    }


}
