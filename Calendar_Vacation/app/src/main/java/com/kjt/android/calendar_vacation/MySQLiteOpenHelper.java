package com.kjt.android.calendar_vacation;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class MySQLiteOpenHelper extends SQLiteOpenHelper {
    SQLiteDatabase db;
    String SQLTableName = "calendar_vacationDB";
    String SQLTableVCountName = "vacation_countTable";

    public MySQLiteOpenHelper(Context context, String name,

                              SQLiteDatabase.CursorFactory factory, int version) {

        super(context, name, factory, version);

        // TODO Auto-generated constructor stub



    }



    @Override

    public void onCreate(SQLiteDatabase db) {

        // TODO Auto-generated method stub



        // SQL 쿼리문은 다음과 같은 형태로도 실행 할 수도 있다.



        // SQLiteOpenHelper 가 최초 실행 되었을 때

        String sql = "create table "+SQLTableName+" (" +

                "_id integer primary key autoincrement, " +
                "sYear CHAR(10), " +
                "sMonth CHAR(10), " +
                "sDay CHAR(10), " +
                "eYear CHAR(10), " +
                "eMonth CHAR(10), " +
                "eDay CHAR(10), " +
                "dCount int, " +
                "memo text);";



        db.execSQL(sql);
        String sql1 = "create table "+SQLTableVCountName+" (" +
                "total integer);";
        db.execSQL(sql1);
        String sql2 = "insert into "+SQLTableVCountName+" (total)"+
                "values(15);";
        db.execSQL(sql2);

    }



    @Override

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // db = 적용할 db, old/new 구 버전/신버전

        // TODO Auto-generated method stub

        /*

         * db 버전이 업그레이드 되었을 때 실행되는 메소드

         * 이 부분은 사용에 조심해야 하는 일이 많이 있다. 버전이 1인 사용자가 2로 바뀌면

         * 한번의 수정만 하면 되지만 버전이 3으로 되면 1인 사용자가 2, 3을 거쳐야 하고

         * 2인 사용자는 3 까지만 거치면 된다. 이렇게 증가할 수록 수정할 일이 많아지므로

         * 적절히 사용해야 하며 가능하면 최초 설계 시에 완벽을 기하는 것이 가장 좋을 것이다.

         * 테스트에서는 기존의 데이터를 모두 지우고 다시 만드는 형태로 하겠다.

         */



        String sql = "drop table if exists "+SQLTableName;

        db.execSQL(sql);



        onCreate(db); // 테이블을 지웠으므로 다시 테이블을 만들어주는 과정

    }

  /*  public void insert(String sDay, String eDay, int dCount, String memo, MySQLiteOpenHelper helper) {
        Log.d("MySQLiteOpenHelper","insert");
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

    public void select(MySQLiteOpenHelper helper) {
        Log.d("MySQLiteOpenHelper","select");


        // 1) db의 데이터를 읽어와서, 2) 결과 저장, 3)해당 데이터를 꺼내 사용



        db = helper.getReadableDatabase(); // db객체를 얻어온다. 읽기 전용

        Cursor c = db.query("calendar_vacationDB", null, null, null, null, null, null);



        *//*

         * 위 결과는 select * from student 가 된다. Cursor는 DB결과를 저장한다. public Cursor

         * query (String table, String[] columns, String selection, String[]

         * selectionArgs, String groupBy, String having, String orderBy)

         *//*



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
    // update

    public void update (String name, int age, MySQLiteOpenHelper helper) {

        db = helper.getWritableDatabase(); //db 객체를 얻어온다. 쓰기가능



        ContentValues values = new ContentValues();

        values.put("age", age);    //age 값을 수정

        db.update("calendar_vacationDB", values, "name=?", new String[]{name});

        *//*

         * new String[] {name} 이런 간략화 형태가 자바에서 가능하다

         * 당연하지만, 별도로 String[] asdf = {name} 후 사용하는 것도 동일한 결과가 나온다.

         *//*



        *//*

         * public int update (String table,

         * ContentValues values, String whereClause, String[] whereArgs)

         *//*

    }



    // delete

    public void delete (String sday, MySQLiteOpenHelper helper) {

        db = helper.getWritableDatabase();

        db.delete("calendar_vacationDB", "startDay=?", new String[]{sday});

        Log.i("db", sday + "정상적으로 삭제 되었습니다.");

    }*/
}

