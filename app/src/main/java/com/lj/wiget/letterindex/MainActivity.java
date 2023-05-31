package com.lj.wiget.letterindex;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.lj.wiget.letterlib.LetterIndexView;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private LetterIndexView mLetterId;
    private TextView mTvMsg;
    private TextView mTvLetterTip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTvLetterTip = this.findViewById(R.id.tv_letter_tip);
        mTvMsg = this.findViewById(R.id.tv_msg);
        mLetterId = findViewById(R.id.letter_id);
        // 设置监听
        mLetterId.setOnLetterSelectedListener(new LetterIndexView.OnLetterSelectedListener() {
            @Override
            public void onLetterSelectedChange(String oldLetter, String newLetter, int index, double centerY) {
                mTvMsg.setText(oldLetter + "  " + newLetter + " " + index);
                mTvLetterTip.setVisibility(View.VISIBLE);
                mTvLetterTip.setText(newLetter);
                float tipPosition = (float) (mLetterId.getTop() + centerY - mTvLetterTip.getHeight() / 2);
                mTvLetterTip.setTranslationY(tipPosition);
            }

            @Override
            public void onLetterUp() {
                mTvLetterTip.setVisibility(View.GONE);
            }
        });
        // 设置数据源
        mLetterId.setLetterDataAndRefresh(Arrays.asList("A", "上", "天", "揽", "明", "月", "下", "海", "捉", "老", "憋"));
    }
}