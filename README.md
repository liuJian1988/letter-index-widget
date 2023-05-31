# LetterIndexWiget

#### 1、介绍
字母索引控件，仿小米联系人字母列表，控件代码超级简单，很适合学习，为二次开发提供思路。

#### 2、使用方法

使用源码引入的方式，方便修改，将`letterlib` 引入到项目中，通过 `xml` 属性的方式设置控件

```xml
        <com.lj.wiget.letterlib.LetterIndexView
            android:id="@+id/letter_id"
            android:layout_width="wrap_content"
            android:paddingHorizontal="5dp"
            android:layout_height="wrap_content"
            android:layout_gravity="center_vertical"
            android:layout_marginStart="10dp"
            android:background="@drawable/letter_layout_bg"
            android:paddingVertical="20dp"
            app:letterPadding="2dp"
            app:letterSelectedBgColor="@color/purple_200"
            app:letterSelectedColor="@color/black"
            app:letterSpaceBetween="20dp"
            app:textSize="20dp"
            app:unSelectedLetterColor="@color/white" />
```

也可以使用 `java` 原生代码进行设置，上面的属性，都有对应的相应的方法设置。

如何监听，选中状态呢？

```java
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

```



