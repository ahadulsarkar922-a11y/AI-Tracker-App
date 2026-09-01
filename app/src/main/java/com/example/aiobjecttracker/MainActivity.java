package com.example.aiobjecttracker;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // পারমিশন চেক করা
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
            startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION));
            finish();
            return;
        }
        
        startTracker();
        finish(); // মেইন অ্যাপ বন্ধ হয়ে যাবে, শুধু ভাসমান বক্স থাকবে
    }

    private void startTracker() {
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                250, 250, // বক্সের সাইজ
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY : WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        
        params.gravity = Gravity.TOP | Gravity.LEFT;
        
        FrameLayout box = new FrameLayout(this);
        box.setBackgroundColor(Color.parseColor("#5500E5FF")); // হালকা নীল রঙের বক্স

        // বক্সটি টেনে সরানোর লজিক
        box.setOnTouchListener(new View.OnTouchListener() {
            int initialX, initialY;
            float initialTouchX, initialTouchY;
            boolean locked = false;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    initialX = params.x;
                    initialY = params.y;
                    initialTouchX = event.getRawX();
                    initialTouchY = event.getRawY();
                } else if (event.getAction() == MotionEvent.ACTION_MOVE && !locked) {
                    params.x = initialX + (int) (event.getRawX() - initialTouchX);
                    params.y = initialY + (int) (event.getRawY() - initialTouchY);
                    wm.updateViewLayout(box, params);
                }
                return false;
            }
        });

        // লং প্রেস করলে লক হয়ে গাঢ় কালার হবে
        box.setOnLongClickListener(v -> {
            box.setBackgroundColor(Color.parseColor("#99FF0055")); // গাঢ় গোলাপি
            return true;
        });

        wm.addView(box, params);
    }
}
