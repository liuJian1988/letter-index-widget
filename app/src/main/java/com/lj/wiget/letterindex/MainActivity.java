package com.lj.wiget.letterindex;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.lj.wiget.letterlib.LetterIndexView;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private LetterIndexView mLetterId;
    private TextView tvMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvMsg = this.findViewById(R.id.tv_msg);
        mLetterId = findViewById(R.id.letter_id);
        // 设置监听
        mLetterId.setOnLetterSelectedListener((oldLetter, newLetter, index) -> {
            tvMsg.setText(oldLetter + "  " + newLetter + " " + index);
        });
        // 设置数据源
        mLetterId.setLetterDataAndRefresh(Arrays.asList("上", "天", "揽", "明", "月", "下", "海", "捉", "老", "憋"));

        /** 设置样式 **/
        //1、设置字体大小
        mLetterId.setTextSize(30);
        //2、设置选中后背景padding
        mLetterId.setBgPadding(2);
        //3、设置选中背景颜色
        mLetterId.setBgColor(Color.CYAN);
        //4、设置字母颜色
        mLetterId.setLetterSelectedColor(Color.YELLOW)
                .setLetterUnSelectedColor(Color.DKGRAY);
    }
}