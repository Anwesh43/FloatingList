package com.anwesome.ui.floatinglistdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.anwesome.ui.floatinglist.FloatingList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String options[] = {"hello","add","del","subtract"};
        final FloatingList floatingList = new FloatingList(this);
        for(int i=0;i<options.length;i++) {
            floatingList.addItem(options[i]);
        }
        floatingList.addToParent();
        Button showBtn = (Button)findViewById(R.id.show_list);
        showBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floatingList.show();
            }
        });
    }
}
