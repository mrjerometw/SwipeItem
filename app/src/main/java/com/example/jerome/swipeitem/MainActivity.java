package com.example.jerome.swipeitem;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private Activity mMainActivity;
    private ArrayList<String> mData = null;
    private MyAdapter mListAdapter = null;
    private SwipeListView mListView = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        mMainActivity = this;
        initView();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
    @Override
    public void onPause() {
        super.onPause();
    }

    private void initView() {
        mData = new ArrayList<String>();
        for (int index = 0 ; index < 30 ; index++)
            mData.add(""+index);
        mListView = (SwipeListView) findViewById(R.id.listView_message);
        mListAdapter = new MyAdapter(mData);
        mListView.setAdapter(mListAdapter);
        mListView.setOnClickListener(new SwipeListView.OnClickListener() {
            @Override
            public void OnClick(View view, int position) {
                Toast.makeText(mMainActivity, "Click ~",Toast.LENGTH_SHORT).show();
            }
        });
    }
    private class MyAdapter extends BaseAdapter {
        private ArrayList<String> mData = null;
        public MyAdapter(ArrayList<String> data) {
            mData = data;
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Object getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView != null) {
                holder = (ViewHolder) convertView.getTag();
            } else {
                View mainView  = LayoutInflater.from(getApplicationContext()).inflate(R.layout.adapter_friendmanagement, null);
                holder = new ViewHolder();
                holder.mainViewHolder.topTextView = (TextView) mainView.findViewById(R.id.textview_FriendManagementAdapter_top);
                View deleteView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.adapter_item_delete, null);
                holder.deleteViewHolder.deleteButton = (Button)deleteView.findViewById(R.id.button_adapteritem_delete);
                holder.deleteViewHolder.deleteButton.setText("Delete");
                convertView = new SwipeItemLayout(mainView, deleteView, null, null);
                convertView.setTag(holder);
            }
            holder.deleteViewHolder.deleteButton.setTag(position);
            holder.deleteViewHolder.deleteButton.setOnClickListener(deleteClickListener);

            String notifyData = mData.get(position);
            holder.mainViewHolder.topTextView.setText(notifyData);
            return convertView;
        }

        class MainViewHolder {
            TextView topTextView;
        }
        class DeleteViewHolder{
            Button deleteButton = null;
        }
        class ViewHolder{
            MainViewHolder mainViewHolder = new MainViewHolder();
            DeleteViewHolder deleteViewHolder = new DeleteViewHolder();
        }
    }
    View.OnClickListener deleteClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final int position = (int)view.getTag();
            mData.remove(position);
            mListAdapter.notifyDataSetChanged();
        }
    };

}
