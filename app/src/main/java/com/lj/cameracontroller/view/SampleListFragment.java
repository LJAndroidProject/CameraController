package com.lj.cameracontroller.view;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lj.cameracontroller.R;
import com.lj.cameracontroller.activity.DeviceListActivity;
import com.lj.cameracontroller.entity.SampleItem;

import java.util.ArrayList;
import java.util.List;

/**
 * 划出的菜单栏 - 用来显示界面中的列表的。
 */
public class SampleListFragment extends Fragment {
    private View view;
    private ListView listView;
    private MyAdapter adapter;
    private List<SampleItem> listData = new ArrayList<SampleItem>();
    private Activity mActivity;


    public interface OnItemClickListener {
        public void Onclick(int position);
    }

    OnItemClickListener listener;

    /**
     * 点击回调
     * @param clickListener
     */
    public void setOnItemClickListener(OnItemClickListener clickListener) {
        listener = clickListener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mActivity = SampleListFragment.this.getActivity();
        listData.add(new SampleItem("主页", getResources().getDrawable(R.mipmap.up_icon)));
        listData.add(new SampleItem("设置", getResources().getDrawable(R.mipmap.up_icon)));
        listData.add(new SampleItem("注销", getResources().getDrawable(R.mipmap.up_icon)));
        listData.add(new SampleItem("退出", getResources().getDrawable(R.mipmap.up_icon)));
        listData.add(new SampleItem("版本信息", getResources().getDrawable(R.mipmap.up_icon)));
        view = inflater.inflate(R.layout.left_menu, container, false);
        listView = (ListView) view.findViewById(R.id.list);
        adapter = new MyAdapter();
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(null!=listener){
                    listener.Onclick(position);
                }
            }
        });
        return view;
    }


    public class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return listData.size();
        }

        @Override
        public Object getItem(int position) {
            return listData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(mActivity).inflate(R.layout.row, null);
            }
            ImageView icon = (ImageView) convertView.findViewById(R.id.row_icon);
            icon.setImageDrawable(listData.get(position).iconRes);
            TextView title = (TextView) convertView.findViewById(R.id.row_title);
            title.setText(listData.get(position).tag);
            return convertView;
        }
    }

    //	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//		return inflater.inflate(R.layout.list, null);
//	}
//
//	public void onActivityCreated(Bundle savedInstanceState) {
//		super.onActivityCreated(savedInstanceState);
//
//		SampleAdapter adapter = new SampleAdapter(getActivity());
//
//		for (int i = 0; i < 20; i++) {
//			adapter.add(new SampleItem("Sample List", android.R.drawable.ic_menu_search));
//		}
//		setListAdapter(adapter);
//	}
//
//	public class SampleAdapter extends ArrayAdapter<SampleItem> {
//		public SampleAdapter(Context context) {
//			super(context, 0);
//		}
//
//		public View getView(int position, View convertView, ViewGroup parent) {
//			if (convertView == null) {
//				convertView = LayoutInflater.from(getContext()).inflate(R.layout.row, null);
//			}
//			ImageView icon = (ImageView) convertView.findViewById(R.id.row_icon);
//			icon.setImageResource(getItem(position).iconRes);
//			TextView title = (TextView) convertView.findViewById(R.id.row_title);
//			title.setText(getItem(position).tag);
//
//			return convertView;
//		}
//	}
//
//	private class SampleItem {
//		public String tag;
//		public int iconRes;
//		public SampleItem(String tag, int iconRes) {
//			this.tag = tag;
//			this.iconRes = iconRes;
//		}
//	}
}
