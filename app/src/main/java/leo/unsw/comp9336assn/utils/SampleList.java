//package leo.unsw.comp9336assn.utils;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.TextView;
//
//import leo.unsw.comp9336assn.R;
//
//
///**
// * 生成一个adapter的类
// * 这里实例化的时候需要context参数
// * list的样式有xml的item来决定，这里需要修改内部的ViewHolder与之对应
// * 对于一些复杂的变化，在getView之中修改
// * 调用方法为
// * SampleList listapi=new SampleList(context);
// * normalModeList.setAdapter(listapi.normalModeAdapter);
// * 透明列表注意：android:cacheColorHint="#00000000" 在list中加此句，但是会大大影响效率，
// * 会使滚动变卡，所以如果长列表背景最好不要用图片，用单一颜色来使用，将这个也设置成该颜色
// * 经测试发现变卡的原因可能是list过长，如果小一点就会变得顺畅
// *
// * @author cxy
// *         Feb. 2 2012
// */
//
//public class SampleList {
//
//    static Context context = null;
//    public NormalModeAdapter normalModeAdapter = null;
//
//    public SampleList(Context context) {
//        this.context = context;
//        this.normalModeAdapter = new NormalModeAdapter();
//    }
//
//    static class ViewHolder {
//        TextView labId;
//    }
//
//    static class NormalModeAdapter extends BaseAdapter {
//        LayoutInflater inflater = null;
//
//        @Override
//        public int getCount() {
//            // TODO Auto-generated method stub
//            return 10;
//        }
//
//        @Override
//        public Object getItem(int position) {
//            // TODO Auto-generated method stub
//            return null;
//        }
//
//        @Override
//        public long getItemId(int position) {
//            // TODO Auto-generated method stub
//            return 0;
//        }
//
//        /**
//         * 此方法根据各种不同list样式进行修改
//         */
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            // TODO Auto-generated method stub
//            ViewHolder viewHolder;
//            if (convertView == null) {
//                if (inflater == null) {
//                    inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
//                }
//                convertView = inflater.inflate(R.layout.item_wifi, null);
//                viewHolder = new ViewHolder();
//                viewHolder.labId = (TextView) convertView.findViewById(R.id.tv_labId);
//                convertView.setTag(viewHolder);
//            } else {
//                viewHolder = (ViewHolder) convertView.getTag();
//            }
//            viewHolder.labId.setText("Lab"+(position+1));
//            return convertView;
//        }
//
//    }
//}
