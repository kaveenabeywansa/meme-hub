package com.app.memes.memesocialmedia;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // init comps and animations
        ImageView meme_01 = (ImageView) findViewById(R.id.meme_01);
        ImageView meme_02 = (ImageView) findViewById(R.id.meme_02);
        ImageView meme_03 = (ImageView) findViewById(R.id.meme_03);
        ImageView meme_04 = (ImageView) findViewById(R.id.meme_04);
        TextView loading_txt = (TextView) findViewById(R.id.splash_text);

        Animation frombottom = AnimationUtils.loadAnimation(this, R.anim.float_from_bottom),
                fromleft = AnimationUtils.loadAnimation(this, R.anim.float_from_left),
                fromright = AnimationUtils.loadAnimation(this, R.anim.float_from_right),
                fromTop = AnimationUtils.loadAnimation(this, R.anim.float_from_top);

        // set animations to comps
//        loading_txt.setAnimation(fromleft);
        meme_01.setAnimation(fromright);
        meme_02.setAnimation(fromleft);
        meme_03.setAnimation(frombottom);
        meme_04.setAnimation(fromTop);

        // splash screen display time in ms
        int SPLASH_DISPLAY_LENGTH = 2000;

        // open the main activity after the splash screen
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent mainMenu = new Intent(SplashScreen.this, MainActivity.class);
                SplashScreen.this.startActivity(mainMenu);
                SplashScreen.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
