package com.example.smartledapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import top.defaults.colorpicker.ColorPickerPopup;


public class ConfigLEDs extends AppCompatActivity {
    //creating the variable for the buttons and text views
    private TextView LED1textview;
    private Button mSetColorButton, mPickColorButton;
    private View mColorPreview;
    private int mDefaultColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_l_e_ds);
        //locating the widgets from xml layout by their IDs
        LED1textview = findViewById(R.id.LED1_heading);
        mPickColorButton = findViewById(R.id.pick_color_button);
        mSetColorButton = findViewById(R.id.set_color_button);
        mColorPreview = findViewById(R.id.preview_selected_color);
        mDefaultColor = 0;
        // when the pick color button is pressed, a color wheel and brightness slider is shown, where the default color is red
        mPickColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ColorPickerPopup.Builder(ConfigLEDs.this)
                        .initialColor(Color.RED).enableBrightness(true).enableAlpha(false)
                        .okTitle("Choose").cancelTitle("Cancel").showIndicator(true).showValue(false).build().show(v, new ColorPickerPopup.ColorPickerObserver() {
                            @Override
                            public void onColorPicked(int color) {
                                mDefaultColor = color;
                                mColorPreview.setBackgroundColor(mDefaultColor);
                            }
                        });
            }
        });
        //when set color button is pressed the color of the LEDs is set to the color chosen in the previous method
        mSetColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LED1textview.setTextColor(mDefaultColor);
                getFormattedColorValue();
            }
        });
    }
    //this method formats the RGB values into a 24-bit binary value
    private String getFormattedColorValue(){
        String Bin = Integer.toBinaryString(mDefaultColor);
        String BinFormatted = Bin.substring(8,32);
        System.out.println(BinFormatted);
        return BinFormatted;
    }

}