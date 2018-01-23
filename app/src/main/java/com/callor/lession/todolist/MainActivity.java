package com.callor.lession.todolist;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.Toast;

import com.callor.lession.todolist.database.DBHelper;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    TextInputEditText txt_memo ;
    List<MemoVO> memos ; // = getMemos(); //new ArrayList<MemoVO>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        txt_memo = findViewById(R.id.txt_memo);

        // event 핸들러에서 접근할 변수앞에 final 키워드를 주어라

        final RecyclerView memo_list = findViewById(R.id.memo_list) ;


        // 최초에 open할때 list 가져오기
        DBHelper dbHelper = new DBHelper(getApplicationContext());
        memos = dbHelper.getAllList();


        // 1 Adapter를 생성하면서 데이터를 담을 VO list 를 넘겨주고
        final RecyclerView.Adapter memoAdapter = new MemoAdapter(memos);

        // 2. 리사클러의 레이아웃 매니저를 생성
        RecyclerView.LayoutManager memoLayoutManager = new LinearLayoutManager(getApplicationContext());

        // 3. 리사이클러와 아답터를 연결
        memo_list.setAdapter(memoAdapter);

        // 4. 리사이클러와 레이아웃 매너저를 연결
        memo_list.setLayoutManager(memoLayoutManager);


        // 아이템별로 구분선을 넣기 위해 설정
        DividerItemDecoration dividerItemDecoration
                = new DividerItemDecoration(getApplicationContext(),
                        new LinearLayoutManager(this).getOrientation());

        memo_list.addItemDecoration(dividerItemDecoration);
        memo_list.addItemDecoration(new VerticalSpace(40));


        // 밀어서 할일
        ItemTouchHelper.SimpleCallback simpleCallback
                = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

                int position = viewHolder.getAdapterPosition(); // swipe 한 아이템의 위치값 추출

                // vo에서 id를 꺼내고 id 값을 기준으로 db를 삭제 한다.
                long id = memos.get(position).getId();
                DBHelper dbHelper = new DBHelper(getApplicationContext());
                dbHelper.delete(id);

                memos.remove(position); //  그 위치의 item을 삭제
                memoAdapter.notifyItemRemoved(position); // 현재 아이템이 삭제 되었음을 adapter에게 알림

            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(memo_list);


        ImageButton btn_save = findViewById(R.id.bt_save);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strMemo = txt_memo.getText().toString();
                if(strMemo.isEmpty()) {
                    Toast.makeText(getApplicationContext(),"메모를 입력하세요",Toast.LENGTH_SHORT)
                            .show();
                    return ;
                }

                long now = System.currentTimeMillis();
                Date date = new Date(now);

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy년 MM월 dd일");
                SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("HH:mm:ss");

                String getDate = simpleDateFormat.format(date);
                String getTime = simpleTimeFormat.format(date);

                DBHelper dbHelper = new DBHelper(getApplicationContext());
                MemoVO vo = new MemoVO(getDate,getTime,strMemo);

                dbHelper.saveMemo(vo);
//                memos = dbHelper.getAllList();
                memos.add(new MemoVO(getDate,getTime,strMemo));
                memoAdapter.notifyDataSetChanged();
                txt_memo.setText("");

            }
        });


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent naverClova = new Intent(MainActivity.this,NaverClova.class);
                startActivity(naverClova);

            }
        });
    }


    // 가상의 데이터를 생성해서 return
    public List<MemoVO> getMemos() {

        List<MemoVO> memos = new ArrayList<MemoVO>();

        memos.add(new MemoVO("2018-01-01","10:11:12","새해 첫날이다"));
        memos.add(new MemoVO("2018-01-02","11:19:12","새해 복 많이 받으세요"));
        memos.add(new MemoVO("2018-01-03","12:20:12","작심 삼일 이다. 다시 시작하자"));
        memos.add(new MemoVO("2018-01-04","13:22:12","올해는 무슨 좋은 일을 만들까"));
        memos.add(new MemoVO("2018-01-05","14:31:12","세상의 주인공은 바로 나다"));
        memos.add(new MemoVO("2018-01-06","15:45:12","남을 위함이 아니라 나를 위한 계획"));
        memos.add(new MemoVO("2018-01-07","16:21:12","가치 있는 삶"));

        return memos;

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
