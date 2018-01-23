package com.callor.lession.todolist.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.callor.lession.todolist.MemoVO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by callor on 2018-01-18.
 */

public class DBHelper extends SQLiteOpenHelper {


    public DBHelper(Context context) {
        super(context,DBContract.DB_NAME,null,1);
    }

    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    // 어플이 최초로 실행 될때 한번만 호출되는 method
    // DB와 DB table을 생성하는 부분을 작성
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DBContract.DBCreate.DB_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public long saveMemo(MemoVO vo) {

        // DB를 쓰기용으로 열어라
        SQLiteDatabase dbConn = this.getWritableDatabase();
        ContentValues sqlValues = new ContentValues();

        // 넘겨받은 vo에서 각각 항목을 꺼내서 sqlValues에 추가
        sqlValues.put(DBContract.DBColumn.MEMO_DATE, vo.getStrDate());
        sqlValues.put(DBContract.DBColumn.MEMO_TIME, vo.getStrTime());
        sqlValues.put(DBContract.DBColumn.MEMO_TEXT, vo.getStrMemo());

        long newId = dbConn.insert(DBContract.DB_TABLE,null,sqlValues);
        return newId;

    }

    public List<MemoVO> getAllList() {

        List<MemoVO> memos = new ArrayList<MemoVO>();

        // 읽기 용으로 DB 열기
        SQLiteDatabase dbConn = this.getReadableDatabase();

        // 읽을 칼럼들을 문자열 배열로 생성
        String[] projection = {
                DBContract.DBColumn._ID,
                DBContract.DBColumn.MEMO_DATE,
                DBContract.DBColumn.MEMO_TIME,
                DBContract.DBColumn.MEMO_TEXT
        };

        // DB에서 읽은 table 구조를 갖는 클래스
        Cursor cursor = dbConn.query(
                DBContract.DB_TABLE,
                projection,
                null,
                null,
                null,
                null,
                null,
                null
        );

        // cursor에 담긴 데이터 개수만큼 반복
        while(cursor.moveToNext()) {

            // 1. 구현하고자 하는 코드
            int indexDate = cursor.getColumnIndex(DBContract.DBColumn.MEMO_DATE);
            String strDate = cursor.getString(indexDate);

            int indexTime = cursor.getColumnIndex(DBContract.DBColumn.MEMO_TIME);
            String strTime = cursor.getString(indexTime);


            int indexMemo = cursor.getColumnIndex(DBContract.DBColumn.MEMO_TEXT);
            String strMemo = cursor.getString(indexMemo);

//            MemoVO vo = new MemoVO(
//                strDate,
//                strTime,
//                strMemo
//            );

            // memos.add(vo);

            // 2. 실제 상황에서 사용을 권장하는 코드
            memos.add(new MemoVO(
              cursor.getString(cursor.getColumnIndex(DBContract.DBColumn.MEMO_DATE)),
              cursor.getString(cursor.getColumnIndex(DBContract.DBColumn.MEMO_TIME)),
              cursor.getString(cursor.getColumnIndex(DBContract.DBColumn.MEMO_TEXT))
            ));

        }
        return memos;
    }

    // id를 받아서 해당되는 데이터 row를 삭제하도록 한다.
    public void delete(long id) {

        // 데이터를 삭제하기(write)하기 위해 open
        SQLiteDatabase dbConn = this.getWritableDatabase();

        String[] deleteArgs = new String[] {String.valueOf(id)};


//        dbConn.delete(DBContract.DB_TABLE,
//                DBContract.DBColumn._ID + " = " + String.valueOf(id),
//                deleteArgs);


        dbConn.delete(DBContract.DB_TABLE,
                DBContract.DBColumn._ID + " = ?",
                deleteArgs);

    }

}


















