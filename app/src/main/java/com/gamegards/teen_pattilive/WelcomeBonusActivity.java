package com.gamegards.teen_pattilive;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.gamegards.teen_pattilive.Adapter.WelcomeRewardAdapter;
import com.gamegards.teen_pattilive.model.WelcomeModel;

import java.util.ArrayList;

public class WelcomeBonusActivity extends AppCompatActivity {

    RecyclerView Reward_rec;
    WelcomeRewardAdapter welcomeRewardAdapter;

    ArrayList<WelcomeModel> welcomeList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_bonus);

        Reward_rec = findViewById(R.id.Reward_rec);
//        Reward_rec.setLayoutManager(new GridLayoutManager(this,5));
//        welcomeRewardAdapter = new WelcomeRewardAdapter(this,welcomeList);
        Reward_rec.setAdapter(welcomeRewardAdapter);

        Reward_rec.setLayoutManager(new LinearLayoutManager(WelcomeBonusActivity.this,RecyclerView.HORIZONTAL,false){
            @Override
            public boolean checkLayoutParams(RecyclerView.LayoutParams lp) {
                // force height of viewHolder here, this will override layout_height from xml
//                lp.height = getHeight() / 3;
                return true;
            }
        });


    }
}
