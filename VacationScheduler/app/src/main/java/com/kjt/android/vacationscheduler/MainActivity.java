package com.kjt.android.vacationscheduler;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.CalendarView;


public class MainActivity extends AppCompatActivity {
    public static Context mainAC ;
    String dbName = "vacationD.db"; // name of Database;
    String tableName = "vacationDB"; // name of Table;
    int dbMode = Context.MODE_PRIVATE;
    // Database 관련 객체들
    SQLiteDatabase db;
    MySQLiteOpenHelper helper;


    CalendarView calendar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mainAC = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        calendar = (CalendarView) findViewById(R.id.calendarView);
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener(){
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                Intent intent = new Intent(getApplicationContext(),dialogPopupActivity.class);
//                intent.putExtra("date",year+"년 "+month+"월 "+dayOfMonth+"일");
                intent.putExtra("year",year);
                intent.putExtra("month",month);
                intent.putExtra("dayOfMonth",dayOfMonth);
                startActivity(intent);

            }
        });
        helper = new MySQLiteOpenHelper(MainActivity.this, // 현재 화면의 context

                "vacationD.db", // 파일명

                null, // 커서 팩토리

                1); // 버전 번호

        // 1. 데이터 저장

//        insert("2017-03-31", "2017-04-01", 1, "휴가임2.");

        // 2. 수정하기
//        update("유저1", 58); // 나이만 수정하기
        // 3. 삭제하기
//        delete("유저2");
        // 4. 조회하기

        select();

    }


    // insert

    public void insert(String sDay, String eDay, int dCount, String memo) {

        db = helper.getWritableDatabase(); // db 객체를 얻어온다. 쓰기 가능

        ContentValues values = new ContentValues();

        // db.insert의 매개변수인 values가 ContentValues 변수이므로 그에 맞춤

        // 데이터의 삽입은 put을 이용한다.

        values.put("startDay", sDay);

        values.put("endDay", eDay);

        values.put("dCount", dCount);

        values.put("memo", memo);

        db.insert("vacationDB", null, values); // 테이블/널컬럼핵/데이터(널컬럼핵=디폴트)

        // tip : 마우스를 db.insert에 올려보면 매개변수가 어떤 것이 와야 하는지 알 수 있다.

    }



    // update

    public void update (String name, int age) {

        db = helper.getWritableDatabase(); //db 객체를 얻어온다. 쓰기가능



        ContentValues values = new ContentValues();

        values.put("age", age);    //age 값을 수정

        db.update("vacationDB", values, "name=?", new String[]{name});

        /*

         * new String[] {name} 이런 간략화 형태가 자바에서 가능하다

         * 당연하지만, 별도로 String[] asdf = {name} 후 사용하는 것도 동일한 결과가 나온다.

         */



        /*

         * public int update (String table,

         * ContentValues values, String whereClause, String[] whereArgs)

         */

    }



    // delete

    public void delete (String sday) {

        db = helper.getWritableDatabase();

        db.delete("vacationDB", "startDay=?", new String[]{sday});

        Log.i("db", sday + "정상적으로 삭제 되었습니다.");

    }



    // select

    public void select() {



        // 1) db의 데이터를 읽어와서, 2) 결과 저장, 3)해당 데이터를 꺼내 사용



        db = helper.getReadableDatabase(); // db객체를 얻어온다. 읽기 전용

        Cursor c = db.query("vacationDB", null, null, null, null, null, null);



        /*

         * 위 결과는 select * from student 가 된다. Cursor는 DB결과를 저장한다. public Cursor

         * query (String table, String[] columns, String selection, String[]

         * selectionArgs, String groupBy, String having, String orderBy)

         */



        while (c.moveToNext()) {

            // c의 int가져와라 ( c의 컬럼 중 id) 인 것의 형태이다.

            int _id = c.getInt(c.getColumnIndex("_id"));

            String sday = c.getString(c.getColumnIndex("startDay"));

            String eday = c.getString(c.getColumnIndex("endDay"));

            int dCount = c.getInt(c.getColumnIndex("dCount"));

            String memo = c.getString(c.getColumnIndex("memo"));

            Log.i("db", "id: " + _id + ", startDay : " + sday + ", endDay : " + eday + ", dCount : " + dCount

                    + ", address : " + memo);

        }

    }

}

