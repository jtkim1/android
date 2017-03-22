package com.kjt.android.vacationscheduler;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CalendarView;

public class MainActivity extends AppCompatActivity {

    CalendarView calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        calendar = (CalendarView) findViewById(R.id.calendarView);

        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener(){
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
//                Toast.makeText(getApplicationContext(),dayOfMonth+"/"+month+"/"+year,Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(),dialogPopupActivity.class);
                intent.putExtra("date",year+"년 "+month+"월 "+dayOfMonth+"일");
                startActivity(intent);


                /*LayoutInflater inflater = getLayoutInflater();
                final View dialogView = inflater.inflate(R.layout.vacation_save,null);
                AlertDialog.Builder buider= new AlertDialog.Builder(MainActivity.this); //AlertDialog.Builder 객체 생성
                buider.setTitle("휴가 저장하기"); //Dialog 제목
                buider.setIcon(android.R.drawable.ic_menu_add); //제목옆의 아이콘 이미지(원하는 이미지 설정)
                TextView startDay = (TextView)dialogView.findViewById(R.id.vs_vacation_start_value);
                startDay.setText(year+"/"+month+"/"+dayOfMonth);

                buider.setView(dialogView); //위에서 inflater가 만든 dialogView 객체 세팅 (Customize)
                //설정한 값으로 AlertDialog 객체 생성
                AlertDialog dialog=buider.create();

                //Dialog의 바깥쪽을 터치했을 때 Dialog를 없앨지 설정
                dialog.setCanceledOnTouchOutside(false);//없어지지 않도록 설정

                //Dialog 보이기
                dialog.show();*/





//                startActivity(new Intent(MainActivity.this, VacationDialogActivity.class));

            }
        });
//        Spinner spinner = (Spinner) findViewById(R.id.day_spinner);
//        /*
//
//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
//                R.array.spinnerDayArray, android.R.layout.simple_spinner_item);
//
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//
//        spinner.setAdapter(adapter);*/
//        List<String> categories = new ArrayList<String>();
//
//        categories.add("1일");
//        categories.add("2일");
//        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
//        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner.setAdapter(dataAdapter);

    }
}
