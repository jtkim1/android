package com.kjt.android.calendar_vacation;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class dialogPopupActivity extends Activity {

    String sDate, dCount;
    Integer selectYear, selectMonth, selectday;
    boolean dbCheck;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_dialog_popup);

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams  layoutParams = new WindowManager.LayoutParams();
        layoutParams.flags  = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount  = 0.7f;
        getWindow().setAttributes(layoutParams);
        setContentView(R.layout.activity_dialog_popup);

        Intent intent = getIntent();
        if(intent != null) {
            selectYear = intent.getExtras().getInt("year");
            selectMonth = intent.getExtras().getInt("month")+1;
            selectday = intent.getExtras().getInt("dayOfMonth");

            sDate = selectYear+"년 "+selectMonth+"월 "+selectday+"일";
//            Toast.makeText(getApplicationContext(),sDate,Toast.LENGTH_LONG).show();
            TextView startDay = (TextView) findViewById(R.id.vc_dialog_startday_value);
            startDay.setText(sDate);
        }

        dbCheck = ((MainActivity)MainActivity.mainAC).selectSearchDate(selectYear,selectMonth,selectday);
        TextView leftButton = (TextView) findViewById(R.id.vc_dialog_okbutton);
        if(dbCheck){
            leftButton.setText("삭제");
        }else{
            leftButton.setText("저장");
        }
        Spinner spinner = (Spinner) findViewById(R.id.vc_dialog_count_spinner);
// Create an ArrayAdapter using the string array and a default spinner layout
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.spinnerDayArray, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setSelection(0);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(getApplicationContext(),adapter.getItem(position)+"을선택함.", Toast.LENGTH_LONG).show();
                dCount = (String)adapter.getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Rect dialogBounds = new Rect();
        getWindow().getDecorView().getHitRect(dialogBounds);
        if(!dialogBounds.contains((int)ev.getX(),(int)ev.getY())){
            return false;
        }
        return super.dispatchTouchEvent(ev);
    }
    //저장버튼 누를때
    public void vcSaveButton(View v){
        if(dbCheck){
            ((MainActivity)MainActivity.mainAC).delete(selectYear,selectMonth,selectday);
        }else{
            Calendar cCal = Calendar.getInstance();
            cCal.set(selectYear,(selectMonth-1),(selectday-1));
            cCal.add(Calendar.DATE,Integer.parseInt(dCount));
            SimpleDateFormat formatDate = new SimpleDateFormat("yyy-MM-dd", Locale.getDefault());
            SimpleDateFormat formatDate1 = new SimpleDateFormat("yyy-MM-dd", Locale.getDefault());
            java.util.Date changEndDate = cCal.getTime();

            cCal.set(selectYear,(selectMonth-1),selectday);
            java.util.Date changStartDate = cCal.getTime();
            String[] eArrayDate = formatDate1.format(changStartDate).split("-");

//        Log.d("dialogPopupActivity","formatDate1.format(changStartDate)="+formatDate1.format(changStartDate));
//        Log.d("dialogPopupActivity","formatDate.format(changEndDate)="+formatDate.format(changEndDate));
//        Log.d("dialogPopupActivity","Integer.parseInt(dCount)="+Integer.parseInt(dCount));
//        ((MainActivity)MainActivity.mainAC).insert(formatDate1.format(changStartDate), formatDate.format(changEndDate),Integer.parseInt(dCount),"1");
            int count = 0;
            for(int i=0 ;  ; i++){
                int dayMonth = selectMonth-1;
                int dayDay = selectday+i;

                cCal.set(selectYear,dayMonth,dayDay);
                int dayNum = cCal.get(Calendar.DAY_OF_WEEK);


                if(dayNum == 1 || dayNum == 7){
                    //토요일,일요일은 제외
                }else if(Integer.parseInt(dCount) == count){
                    //휴가일수만큼 돌았으면 중지.
                    break;
                }else{
                    //휴가일수만큼 저장
                    ((MainActivity)MainActivity.mainAC).insert(selectYear,(cCal.get(Calendar.MONTH)+1),cCal.get(Calendar.DATE),eArrayDate,1,"1");
                    count++;
                }

            }
//        ((MainActivity)MainActivity.mainAC).insert(selectYear,selectMonth,selectday,eArrayDate,1,"1");

        }
        ((MainActivity)MainActivity.mainAC).selectYearMonth(selectYear,selectMonth);
        ((MainActivity)MainActivity.mainAC).setCalendarDate(selectYear,selectMonth);
        ((MainActivity)MainActivity.mainAC).changVacationCount();
        finish();
    }
    public void vcCancelButton(View v){
        finish();
    }


}
