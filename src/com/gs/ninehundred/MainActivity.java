package com.gs.ninehundred;

import java.util.ArrayList;
import java.util.List;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.GridLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
    
    private List<TextView> listTxtView = new ArrayList<TextView>();
    private final double gridPercent = 0.9;
    private GridLayout gridLayout;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        
        WindowManager wm = this.getWindowManager();
        Point size = new Point();
        wm.getDefaultDisplay().getSize(size);
        if (size.x > size.y) {
            int statusBarHeight = 0;
            int actionBarheight = 0;
            
            int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                statusBarHeight = getResources().getDimensionPixelSize(resourceId);
            }
            
            TypedValue tv = new TypedValue();
            if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
                actionBarheight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
            }
            System.out.println(String.format("actionBarheight:%d, statusBarHeight:%d", actionBarheight, statusBarHeight));
            
            size.y = size.y - statusBarHeight - actionBarheight;
        }
        
        System.out.println(String.format("size:%s", size.toString()));
        
        int screenWidth = (size.x < size.y ? size.x : size.y);
        int gridAreaWidth = (int)(screenWidth * gridPercent);
        int gridWidth = (gridAreaWidth / 30) - 1;
        int padding = (screenWidth - gridAreaWidth) / 2;
        
        
        RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        rl.addRule(RelativeLayout.CENTER_HORIZONTAL, 1);
        rl.setMargins(0, padding, 0, 0);
        gridLayout = new GridLayout(this);
        gridLayout.setLayoutParams(rl);
        gridLayout.setRowCount(30);
        gridLayout.setColumnCount(30);
        gridLayout.setPadding(0, 0, 1, 1);
//        gridLayout.setBackgroundColor(0xFF555555);
        gridLayout.setBackgroundColor(0xFFFFFFFF);
        
        Animation animation = AnimationUtils.loadAnimation(this, R.animator.anim_pop);
        LayoutAnimationController lac = new LayoutAnimationController(animation);
        lac.setOrder(LayoutAnimationController.ORDER_RANDOM);
        lac.setDelay(0.025f);
        for (int i = 0; i < 900; i++) {
            TextView txt = new TextView(this);
            GridLayout.LayoutParams gl = new GridLayout.LayoutParams();
            gl.setMargins(1, 1, 0, 0);
            txt.setWidth(gridWidth);
            txt.setHeight(gridWidth);
            txt.setLayoutParams(gl);
            listTxtView.add(txt);
            gridLayout.addView(txt);
        }
        
        gridLayout.setLayoutAnimation(lac);
        updateGrid();
        
        RelativeLayout relativeLayout = new RelativeLayout(this);
        relativeLayout.addView(gridLayout);
        setContentView(relativeLayout);
    }
    
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        
        int colorFrom = 0xFFFFFFFF;
        int colorTo = 0xFF777777;
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setStartDelay(1125);
        colorAnimation.setDuration(800);
        colorAnimation.addUpdateListener(new AnimatorUpdateListener() {
            
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                gridLayout.setBackgroundColor((int) animation.getAnimatedValue());
            }
        });
        colorAnimation.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        
        updateGrid();
    }
    
    private void updateGrid() {
        int len = listTxtView.size();
        int months = getPassedMonths();
        for (int i = 0; i < len; i++) {
            if (i >= months) {
                listTxtView.get(i).setBackgroundColor(0xFFFFFFFF);
            } else {
                listTxtView.get(i).setBackgroundColor(0xFFFF5555);
            }
        }
    }
    
    private int getPassedMonths() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String year = sp.getString("year", "1990");
        String month = sp.getString("month", "1");
        
        int setY = Integer.valueOf(year);
        int setM = Integer.valueOf(month);
        
        Time t = new Time();
        t.setToNow();
        int nowY = t.year;
        int nowM = t.month;
        
        if ((setM < 0) || (setM > 12)) {
            return 0;
        }
        
        int subY = nowY - setY;
        int subM = nowM - setM;
        
        int passedMonths = subY * 12 + subM;
        
        System.out.println(String.format("year:%s, month:%s, passed:%d", year, month, passedMonths));
        
        return (passedMonths < 0) ? 0 : passedMonths;
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent("com.gs.ninehundred.setting");
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
