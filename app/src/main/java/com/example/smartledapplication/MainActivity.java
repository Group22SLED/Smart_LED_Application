package com.example.smartledapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    //define variables for the buttons and textviews
    private Button configledbtn;
    private ToggleButton onoffbtn;
    private TextView onoffTV;
    private Button BTPairbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        onoffbtn = (ToggleButton) findViewById(R.id.togglebtn);
        onoffTV = (TextView) findViewById(R.id.textview);
        onoffbtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (onoffbtn.isChecked()){
                    onoffTV.setText("LEDs are on");
                    returnOne();
                }
                else{
                    onoffTV.setText("LEDs are off");
                    returnZero();
                }
            }
        });
        //when clicked these buttons will call the below methods and open a new activity page
        configledbtn = (Button) findViewById(R.id.configledbutton);
        configledbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                controlLEDs();
            }
        });
        BTPairbtn = (Button) findViewById(R.id.BTPairbutton);
        BTPairbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                controlBTPair();
            }
        });
    }
    //these method pass an intent to open a new activity for each of these buttons once pressed
    public void controlLEDs(){
        Intent intent = new Intent(this, ConfigLEDs.class);
        startActivity(intent);
    }
    public void controlBTPair(){
        Intent intent = new Intent(this, BTPairing.class);
        startActivity(intent);
    }
    //these methods return a 1 or 0 binary based on the state chosen of the LEDs
    public int returnOne(){
        System.out.println("1");
        return 1;
    }
    public int returnZero(){
        System.out.println("0");
        return 0;
    }
}