package com.example.urpad;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class splashScreen extends AppCompatActivity {

    private static int splash_screen=5000;
    ImageView img;
    TextView logo,sologan1,sologan2;
    Animation top,bottom;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        img=findViewById(R.id.gifnote);
        logo=findViewById(R.id.logo);
        sologan1=findViewById(R.id.slogan1);
        sologan2=findViewById(R.id.slogan2);
        top= AnimationUtils.loadAnimation(this,R.anim.top_animation);
        bottom= AnimationUtils.loadAnimation(this,R.anim.bottom_animation);

        img.setAnimation(top);
        logo.setAnimation(top);
        sologan1.setAnimation(bottom);
        sologan2.setAnimation(bottom);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i=new Intent(splashScreen.this,StartUp.class);

                Pair[] pairs=new Pair[4];
                pairs[0]=new Pair<View,String>(img,"logo_image");
                pairs[1]=new Pair<View,String>(logo,"logo_name");
                pairs[2]=new Pair<View,String>(sologan1,"logo_slogan");
                pairs[3]=new Pair<View,String>(sologan2,"signup_tran");

               ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(splashScreen.this,pairs);
                startActivity(i,options.toBundle());
                 finish();

            }
        },splash_screen);
    }
}