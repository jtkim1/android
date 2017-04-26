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
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static java.lang.Integer.parseInt;


public class MainActivity extends AppCompatActivity {
    public static Context mainAC ;
    GridView mGridView;
    DateAdapter adapter;
    ArrayList arrData1;
    Calendar mCalToday;
    Calendar mCal;
    int thisYear, thisMonth;

    List<SaveDateClass> saveDateArray = new ArrayList<SaveDateClass>();
    List<SaveDateClass> saveYearMonthArray = new ArrayList<SaveDateClass>();

            // Database 관련 객체들
    SQLiteDatabase db;
    MySQLiteOpenHelper helper;
    String SQLTableName = "calendar_vacationDB";
    String SQLTableVCountName = "vacation_countTable";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mainAC = MainActivity.this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Calendar 객체 생성
        mCalToday = Calendar.getInstance();
        mCal = Calendar.getInstance();
        thisYear = mCal.get(Calendar.YEAR);
        thisMonth = mCal.get(Calendar.MONTH)+1;

        //DB 만들기 or 열기

        helper = new MySQLiteOpenHelper(MainActivity.this, // 현재 화면의 context

                "vacationDataBaseTest7.db", // 파일명

                null, // 커서 팩토리

                1); // 버전 번호
        selectYearMonth(2017,04);
//        select();
//        delete("1");
        final int usingCount = selecUsingVacationCount();
        Log.d("db","usingcount="+usingCount);
        select();

        // 달력 세팅
        setCalendarDate(thisYear, thisMonth);

        //spinner 객체 생성
        Spinner spinner = (Spinner) findViewById(R.id.vc_vacation_total_count_spinner);

        final ArrayAdapter<CharSequence> adapterCount = ArrayAdapter.createFromResource(this,
                R.array.spinnerVacationCountArray, android.R.layout.simple_spinner_item);

        adapterCount.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        spinner.setAdapter(adapterCount);
        int vTotalCount = selectVacationTotalCount();
        if(vTotalCount > 0){
            spinner.setSelection(vTotalCount-1);
        }else{
            spinner.setSelection(14);
        }


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(getApplicationContext(),adapter.getItem(position)+"을선택함.", Toast.LENGTH_LONG).show();
                Log.d("main","select total="+adapterCount.getItem(position));
                updateTotalCount(parseInt((String)adapterCount.getItem(position)));
                changVacationCount();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


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
    //남은 휴가 일수를 계산 해주는 함수.
    public void changVacationCount(){
        int totalCount = selectVacationTotalCount();
        int usingcount = selecUsingVacationCount();

        TextView remainderCount = (TextView)findViewById(R.id.remainderTextValue);
        remainderCount.setText((totalCount-usingcount)+" 일");
    }
    //월이 변경될때에 대한 함수.
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
    //달력 그리는 함수.
    public void setCalendarDate(int year , int month){
        Log.d("Mainactivity","setCalendarDate_year="+year);
        Log.d("Mainactivity","setCalendarDate_month="+month);
        selectYearMonth(year,month);
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
            arrData1.add(new DateClass((i+1), mCalToday.get(Calendar.DAY_OF_WEEK),year,month));
        }

        adapter = new DateAdapter(this, arrData1);

        mGridView = (GridView)findViewById(R.id.calGrid);
        mGridView.setAdapter(adapter);
    }

    //db 에 휴가 저장하는 함수.
    public void insert(int sYear,int sMonth, int sDay, String[] eArrayDate, int dCount, String memo) {
        Log.d("Mainactivity","insert");
        db = helper.getWritableDatabase(); // db 객체를 얻어온다. 쓰기 가능

        ContentValues values = new ContentValues();

        // db.insert의 매개변수인 values가 ContentValues 변수이므로 그에 맞춤

        // 데이터의 삽입은 put을 이용한다.

        values.put("sYear", sYear);
        values.put("sMonth", sMonth);
        values.put("sDay", sDay);
        values.put("eYear", eArrayDate[0]);
        values.put("eMonth", eArrayDate[1]);
        values.put("eDay", eArrayDate[2]);

        values.put("dCount", dCount);

        values.put("memo", memo);

        db.insert(SQLTableName, null, values); // 테이블/널컬럼핵/데이터(널컬럼핵=디폴트)

        // tip : 마우스를 db.insert에 올려보면 매개변수가 어떤 것이 와야 하는지 알 수 있다.

    }
    public void updateTotalCount(int totalCount) {
        Log.d("Mainactivity","updateTotalCount="+totalCount);
        db = helper.getWritableDatabase(); // db 객체를 얻어온다. 쓰기 가능

        ContentValues values = new ContentValues();

        // db.insert의 매개변수인 values가 ContentValues 변수이므로 그에 맞춤

        // 데이터의 삽입은 put을 이용한다.

        values.put("total", totalCount);


        db.update(SQLTableVCountName, values, null,null);


    }

    // update

    public void update (String name, int age) {

        db = helper.getWritableDatabase(); //db 객체를 얻어온다. 쓰기가능



        ContentValues values = new ContentValues();

        values.put("age", age);    //age 값을 수정

        db.update(SQLTableName, values, "name=?", new String[]{name});

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

    public void delete (int year, int month, int day) {


        db = helper.getWritableDatabase();
//        db.delete(SQLTableName, "dCount=?", new String[]{sday});

        String  sql = "delete from "+SQLTableName+" where sYear = "+year+" and sMonth= "+month+" and sDay="+day+";";
        db.execSQL(sql);
        Log.i("db", "정상적으로 삭제 되었습니다.");

    }

    //전체 휴가 db 검색하는 함수.
    public void select() {



        // 1) db의 데이터를 읽어와서, 2) 결과 저장, 3)해당 데이터를 꺼내 사용



        db = helper.getReadableDatabase(); // db객체를 얻어온다. 읽기 전용

        Cursor c = db.query(SQLTableName, null, null, null, null, null, null, null);



        /*

         * 위 결과는 select * from student 가 된다. Cursor는 DB결과를 저장한다. public Cursor

         * query (String table, String[] columns, String selection, String[]

         * selectionArgs, String groupBy, String having, String orderBy)

         */


        int i = 0;
        while (c.moveToNext()) {

            // c의 int가져와라 ( c의 컬럼 중 id) 인 것의 형태이다.

            int _id = c.getInt(c.getColumnIndex("_id"));

            String sYear = c.getString(c.getColumnIndex("sYear"));
            String sMonth = c.getString(c.getColumnIndex("sMonth"));
            String sDay = c.getString(c.getColumnIndex("sDay"));

            String eYear = c.getString(c.getColumnIndex("eYear"));
            String eMonth = c.getString(c.getColumnIndex("eMonth"));
            String eDay = c.getString(c.getColumnIndex("eDay"));


            int dCount = c.getInt(c.getColumnIndex("dCount"));

            String memo = c.getString(c.getColumnIndex("memo"));

            saveDateArray.add(new SaveDateClass(sYear, sMonth, sDay, eYear, eMonth, eDay,dCount,memo));
            Log.d("main","==="+saveDateArray.get(i).getsDate()[2]);
            i++;
            Log.i("db", "id: " + _id + ", sYear : " + sYear + ", sMonth : " + sMonth + ", sDay : " + sDay
                    + ", eYear : " + eYear + ", eMonth : " + eMonth + ", eDay : " + eDay
                    + ", dCount : " + dCount + ", address : " + memo);

        }

    }
    //휴가 전체 갯수 검색하는 함수.
    public int selectVacationTotalCount() {

//        db = helper.getReadableDatabase(); // db객체를 얻어온다. 읽기 전용
//        String  sql = "select * from "+SQLTableVCountName+";";
//        SQLTableVCountName
        db = helper.getReadableDatabase(); // db객체를 얻어온다. 읽기 전용

        Cursor c = db.query(SQLTableVCountName, null, null, null, null, null, null, null);


        Log.d("main","selectVacationTotalCount="+c.moveToNext());

        int total = c.getInt(c.getColumnIndex("total"));
        Log.i("mainActivity", "selectVacationTotalCount total : " + total );

        return total;

    }
    //휴가에서 중복된것 빼고 총 사용한 개수 검색하는 함수.
    public int selecUsingVacationCount() {

        db = helper.getReadableDatabase(); // db객체를 얻어온다. 읽기 전용
        String  sql = "select distinct sYear, sMonth, sDay from "+SQLTableName+";";
        Cursor c = db.rawQuery(sql,null);
        int count = 0;
        while (c.moveToNext()) {

            // c의 int가져와라 ( c의 컬럼 중 id) 인 것의 형태이다.

            String sYear = c.getString(c.getColumnIndex("sYear"));
            String sMonth = c.getString(c.getColumnIndex("sMonth"));
            String sDay = c.getString(c.getColumnIndex("sDay"));

//            Log.i("db", "selecUsingVacationCount_ sYear : " + sYear + ", sMonth : " + sMonth + ", sDay : " + sDay
//                    + ", eYear : ");

            count++;

        }
        return count;

    }
    public void selectYearMonth(int year, int month) {



        // 1) db의 데이터를 읽어와서, 2) 결과 저장, 3)해당 데이터를 꺼내 사용



        db = helper.getReadableDatabase(); // db객체를 얻어온다. 읽기 전용
        String  sql = "select * from "+SQLTableName+" where sYear = "+year+" and sMonth= "+month+";";
        Cursor c = db.rawQuery(sql,null);



        /*

         * 위 결과는 select * from student 가 된다. Cursor는 DB결과를 저장한다. public Cursor

         * query (String table, String[] columns, String selection, String[]

         * selectionArgs, String groupBy, String having, String orderBy)

         */

        saveYearMonthArray.clear();
        int i = 0;
        while (c.moveToNext()) {

            // c의 int가져와라 ( c의 컬럼 중 id) 인 것의 형태이다.

            int _id = c.getInt(c.getColumnIndex("_id"));

            String sYear = c.getString(c.getColumnIndex("sYear"));
            String sMonth = c.getString(c.getColumnIndex("sMonth"));
            String sDay = c.getString(c.getColumnIndex("sDay"));

            String eYear = c.getString(c.getColumnIndex("eYear"));
            String eMonth = c.getString(c.getColumnIndex("eMonth"));
            String eDay = c.getString(c.getColumnIndex("eDay"));


            int dCount = c.getInt(c.getColumnIndex("dCount"));

            String memo = c.getString(c.getColumnIndex("memo"));

            saveYearMonthArray.add(new SaveDateClass(sYear, sMonth, sDay, eYear, eMonth, eDay,dCount,memo));
//            Log.d("main","==="+saveYearMonthArray.get(i).getsDate()[2]);
            i++;
//            Log.i("db", "id: " + _id + ", sYear : " + sYear + ", sMonth : " + sMonth + ", sDay : " + sDay
//                    + ", eYear : " + eYear + ", eMonth : " + eMonth + ", eDay : " + eDay
//                    + ", dCount : " + dCount + ", address : " + memo);

        }

    }
    public boolean selectSearchDate(int year, int month, int day) {



        // 1) db의 데이터를 읽어와서, 2) 결과 저장, 3)해당 데이터를 꺼내 사용



        db = helper.getReadableDatabase(); // db객체를 얻어온다. 읽기 전용
        String  sql = "select * from "+SQLTableName+" where sYear = "+year+" and sMonth= "+month+" and sDay="+day+";";
        Cursor c = db.rawQuery(sql,null);
        Log.d("mainactivity","c="+c);
        Log.d("mainactivity","c.moveToNext="+c.moveToNext());

        return c.moveToNext();
    }

}

// GridView와 연결해주기위한 어댑터 구성
class DateAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<DateClass> arrData;
    private LayoutInflater inflater;
    private Holiday holiday = new Holiday();

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
//        GridView gridView = (GridView)convertView.findViewById(R.id.calGrid);
        if(arrData.get(position) == null){
            ViewText.setText("");
            convertView.setBackgroundColor(Color.GRAY);
        }else
            {
                ViewText.setText(arrData.get(position).getDay()+"");
                if(arrData.get(position).getDayofweek() == 1)
                {
                    ViewText.setTextColor(Color.RED);
                    convertView.setBackgroundColor(Color.WHITE);
                }
                else if(arrData.get(position).getDayofweek() == 7)
                {
                    ViewText.setTextColor(Color.BLUE);
                    convertView.setBackgroundColor(Color.WHITE);
                }
                else
                {

//                    Log.d("mainactivity","holiday.check(arrData.get(position).getFullDate())="+holiday.check(arrData.get(position).getFullDate()));
//                    Log.d("mainactivity","holiday.check(arrData.get(position).getFullDate())="+arrData.get(position).getFullDate());
                    if(holiday.check(arrData.get(position).getFullDate())){
                        ViewText.setTextColor(Color.RED);
                    }else{
                        ViewText.setTextColor(Color.BLACK);
                    }
    //                    ViewText.setTextColor(Color.BLACK);
                    convertView.setBackgroundColor(Color.WHITE);
                }

                for(int i=0 ; i<((MainActivity)MainActivity.mainAC).saveYearMonthArray.size() ; i++){

                    if(arrData.get(position).getDay() == parseInt(((MainActivity)MainActivity.mainAC).saveYearMonthArray.get(i).getsDate()[2])){
                        convertView.setBackgroundColor(Color.YELLOW);
                    }

                }
    //            Log.d("main", "JT:>>>>>>>>"+((MainActivity)MainActivity.mainAC).saveDateArray.get(0).getsDate()[2]);
            }
        ViewText.setHeight(150);

        return convertView;
    }
}
