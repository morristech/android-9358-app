package com.xmd.technician.widget.chatview;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.xmd.technician.Adapter.CommonRecyclerViewAdapter;
import com.xmd.technician.R;
import com.xmd.technician.common.Logger;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Created by Lhj on 17-5-10.
 */

public class ChatOperationDialog extends Dialog {


    ListView operationList;

    private List<String> mOperationMenu;
    private OperationAdapter mOperationAdapter;
    private Context mContext;
    private OperationMenuItemClickedListener mClickedListener;

    public interface OperationMenuItemClickedListener {
        void itemClicked(int position);
    }


    public ChatOperationDialog(Context context, String[] strings) {
        this(context, R.style.default_dialog_style);
        this.mContext = context;

        if (mOperationMenu == null) {
            mOperationMenu = new ArrayList<>();
        } else {
            mOperationMenu.clear();
        }
        for (int i = 0; i < strings.length; i++) {
            mOperationMenu.add(strings[i]);
        }

    }

    public void setOperationMenuItemClickedListener(OperationMenuItemClickedListener clickedListener) {
        this.mClickedListener = clickedListener;
    }


    public ChatOperationDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected ChatOperationDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        this.mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_operation_dialog);
        operationList = (ListView) findViewById(R.id.operation_list);
        mOperationAdapter = new OperationAdapter();
        operationList.setAdapter(mOperationAdapter);
        mOperationAdapter.notifyDataSetChanged();
        operationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mClickedListener.itemClicked(position);
            }
        });
    }

    class OperationAdapter extends BaseAdapter {

        public OperationAdapter() {

        }

        @Override
        public int getCount() {
            return mOperationMenu.size();
        }

        @Override
        public Object getItem(int position) {
            return mOperationMenu.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.operation_menu_item, null);
                viewHolder.menuText = (TextView) convertView.findViewById(R.id.menu_text);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.menuText.setText(mOperationMenu.get(position));

            return convertView;
        }

        class ViewHolder {
            TextView menuText;
        }
    }


}
