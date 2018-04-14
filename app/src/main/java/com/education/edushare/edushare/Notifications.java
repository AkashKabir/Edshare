package com.education.edushare.edushare;

/**
 * Created by Rithika on 15-Jun-17.
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

public class Notifications extends Service {
    String var1;
    String temp, temp1;
    private FirebaseDatabase mFirebaseDatabase;
    FirebaseUser user;
    private DatabaseReference mMessagesDatabaseReference, gref;
    private ChildEventListener mChildEventListener;
    private String lastmessage="";


    @Override
    public void onDestroy() {
        Log.d("NOTIFICATION SERVICE", "onDestroy Called");
        stopSelf();
        mMessagesDatabaseReference.removeEventListener(mChildEventListener);
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
              Log.d("NOTIFICATION SERVICE", "onStartCommand executed");
              Log.d("NOTIFICATION SERVICE", "onStartCommand notification service running");
              user = FirebaseAuth.getInstance().getCurrentUser();
              mFirebaseDatabase = FirebaseDatabase.getInstance();
              mMessagesDatabaseReference = mFirebaseDatabase.getReference().child("users").child(user.getUid()).child("NewRequests");
              mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    if (dataSnapshot.exists()) {
                        String txt = dataSnapshot.getValue().toString();
                        Log.d("NOTIFICATION SERVICE", "onStartCommand: onChildAdded");
                        if(lastmessage.equals(txt)){
                            lastmessage=txt;
                        }else{
                            sendNotification(txt);
                            lastmessage=txt;
                        }
                        mMessagesDatabaseReference.child(dataSnapshot.getKey()).removeValue();
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {


                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
            mMessagesDatabaseReference.addChildEventListener(mChildEventListener);


            return Service.START_STICKY;
        }catch (Exception e){
            e.printStackTrace();
            return START_NOT_STICKY;
        }
    }

        @Override
        public void onCreate () {
            super.onCreate();
            Log.d("NOTIFICATION SERVICE", "onCreate: ");
        }

        @Nullable
        @Override
        public IBinder onBind (Intent intent){
            return null;
        }

    public void sendNotification(String txt) {
        Log.d("NOTIFICATION SERVICE", "Send Notification: ");
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_star_yellow)
                        .setContentTitle("EdShare")
                        .setLargeIcon(BitmapFactory.decodeResource(this.getResources(),
                                R.drawable.ic_male_user))
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(txt))
                        .setContentText(txt)
                        .setDefaults(NotificationCompat.DEFAULT_SOUND);

        Intent resultIntent = new Intent(this, HomeActivity.class);

// Because clicking the notification opens a new ("special") activity, there's
// no need to create an artificial back stack.
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        mBuilder.setContentIntent(resultPendingIntent);

        Random random = new Random();
        int m = random.nextInt(9999 - 1000) + 1000;

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(m, mBuilder.build());

    }
}
