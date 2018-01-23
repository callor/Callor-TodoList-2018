package com.callor.lession.todolist;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by callor on 2018-01-18.
 */

public class MemoAdapter extends RecyclerView.Adapter{

    private List<MemoVO> memos = new ArrayList<MemoVO>();


    public MemoAdapter(List<MemoVO> memos) {
        this.memos = memos;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.memo_item,parent,false);

        RecyclerView.ViewHolder memoHolder = new MemoHolder(view);

        return memoHolder;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        MemoHolder memoHolder = (MemoHolder)holder ;

        memoHolder.txt_date.setText(memos.get(position).getStrDate());
        memoHolder.txt_time.setText(memos.get(position).getStrTime());
        memoHolder.txt_memo.setText(memos.get(position).getStrMemo());

    }

    @Override
    public int getItemCount() {
        return memos.size();
    }

    class MemoHolder extends RecyclerView.ViewHolder {

        // memo_list.xml 에 정의된 TextView와 연결되는 변수
        public TextView txt_date ;
        public TextView txt_time ;
        public TextView txt_memo ;

        public MemoHolder(View itemView) {
            super(itemView);

            txt_date = itemView.findViewById(R.id.txt_item_date);
            txt_time = itemView.findViewById(R.id.txt_item_time);
            txt_memo = itemView.findViewById(R.id.txt_item_memo);

        }
    }


}




