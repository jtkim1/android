package com.kjt.android.calendar_vacation;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;


public class MainActivity extends AppCompatActivity {
    public static Context mainAC ;
    GridView mGridView;
    DateAdapter adapter;
    ArrayList arrData1;
    Calendar mCalToday;
    Calendar mCal;
    int thisYear, thisMonth;


    // Database 관련 객체들
    SQLiteDatabase db;
    MySQLiteOpenHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Calendar 객체 생성
        mCalToday = Calendar.getInstance();
        mCal = Calendar.getInstance();
        thisYear = mCal.get(Calendar.YEAR);
        thisMonth = mCal.get(Calendar.MONTH)+1;

        //DB 만들기 or 열기
        helper = new MySQLiteOpenHelper(MainActivity.this, // 현재 화면의 context

                "vacationD1.db", // 파일명

                null, // 커서 팩토리

                1); // 버전 번호

        select();

        // 달력 세팅
        setCalendarDate(thisYear, thisMonth);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getApplicationContext(),dialogPopupActivity.class);
//                intent.putExtra("date",year+"년 "+month+"월 "+dayOfMonth+"일");
                intent.putExtra("year",thisYear);
                intent.putExtra("month",(thisMonth-1));
                intent.putExtra("dayOfMonth",adapter.getday(position));
                Log.d("MainActivity","thisYear="+thisYear);
                Log.d("MainActivity","thisMonth="+thisMonth);
                Log.d("MainActivity","day="+ adapter.getday(position));

                startActivity(intent);
            }
        });

    }
    public void changMonth(View v){
        switch(v.getId()){
            case R.id.prev:
                if(thisMonth > 1)
                {
                    thisMonth--;
                    setCalendarDate(thisYear, thisMonth);
                }
                else
                {
                    thisYear--;
                    thisMonth = 12;
                    setCalendarDate(thisYear, thisMonth);
                }
                break;
            case R.id.next:
                if(thisMonth < 12) {
                    thisMonth++;
                    setCalendarDate(thisYear, thisMonth);
                } else {
                    thisYear++;
                    thisMonth = 1;
                    setCalendarDate(thisYear, thisMonth);
                }


                break;

        }
    }

    public void setCalendarDate(int year , int month){
        arrData1 = new ArrayList();
        //title 현재 년도월 표시
        TextView ViewTextTitle = (TextView) findViewById(R.id.maintext);
        ViewTextTitle.setText(year+"년 "+month+"월");

        // 1일에 맞는 요일을 세팅하기 위한 설정
        mCalToday.set(year, month-1, 1);

        int startday = mCalToday.get(Calendar.DAY_OF_WEEK);
        if(startday != 1)
        {
            for(int i=0; i<startday-1; i++)
            {
                arrData1.add(null);
            }
        }

        // 요일은 +1해야 되기때문에 달력에 요일을 세팅할때에는 -1 해준다.
        mCal.set(Calendar.MONTH, month-1);

        for (int i = 0; i < mCal.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
            mCalToday.set(year, month-1, (i+1));
            arrData1.add(new DateClass((i+1), mCalToday.get(Calendar.DAY_OF_WEEK)));
        }

        adapter = new DateAdapter(this, arrData1);

        mGridView = (GridView)findViewById(R.id.calGrid);
        mGridView.setAdapter(adapter);
    }


    public void insert(String sDay, String eDay, int dCount, String memo) {
        Log.d("Mainactivity","insert");
        db = helper.getWritableDatabase(); // db 객체를 얻어온다. 쓰기 가능

        ContentValues values = new ContentValues();

        // db.insert의 매개변수인 values가 ContentValues 변수이므로 그에 맞춤

        // 데이터의 삽입은 put을 이용한다.

        values.put("startDay", sDay);

        values.put("endDay", eDay);

        values.put("dCount", dCount);

        values.put("memo", memo);

        db.insert("calendar_vacationDB", null, values); // 테이블/널컬럼핵/데이터(널컬럼핵=디폴트)

        // tip : 마우스를 db.insert에 올려보면 매개변수가 어떤 것이 와야 하는지 알 수 있다.

    }



    // update

    public void update (String name, int age) {

        db = helper.getWritableDatabase(); //db 객체를 얻어온다. 쓰기가능



        ContentValues values = new ContentValues();

        values.put("age", age);    //age 값을 수정

        db.update("calendar_vacationDB", values, "name=?", new String[]{name});

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

        db.delete("calendar_vacationDB", "startDay=?", new String[]{sday});

        Log.i("db", sday + "정상적으로 삭제 되었습니다.");

    }



    // select

    public void select() {



        // 1) db의 데이터를 읽어와서, 2) 결과 저장, 3)해당 데이터를 꺼내 사용



        db = helper.getReadableDatabase(); // db객체를 얻어온다. 읽기 전용

        Cursor c = db.query("calendar_vacationDB", null, null, null, null, null, null);



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

// GridView와 연결해주기위한 어댑터 구성
class DateAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<DateClass> arrData;
    private LayoutInflater inflater;

    public DateAdapter(Context c, ArrayList<DateClass> arr) {
        this.context = c;
        this.arrData = arr;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    public int getCount() {
        return arrData.size();
    }

    public Object getItem(int position) {
        return arrData.get(position);
    }

    public long getItemId(int position) {
        return position;
    }
    public int getday(int position){ return arrData.get(position).getDay(); }

    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.viewitem, parent, false);
        }


        TextView ViewText = (TextView)convertView.findViewById(R.id.ViewText);
        if(arrData.get(position) == null)
            ViewText.setText("");
        else
        {
            ViewText.setText(arrData.get(position).getDay()+"");
            if(arrData.get(position).getDayofweek() == 1)
            {
                ViewText.setTextColor(Color.RED);
                ViewText.setBackgroundColor(Color.BLACK);

            }
            else if(arrData.get(position).getDayofweek() == 7)
            {
                ViewText.setTextColor(Color.BLUE);

            }
            else
            {
                ViewText.setTextColor(Color.BLACK);

            }
        }
        ViewText.setHeight(150);

        return convertView;
    }
}
