package com.twd.gamesetting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.twd.gamesetting.bean.SettingItem;

import java.util.List;

public class SettingItemAdapter extends ArrayAdapter<SettingItem> {

    private LayoutInflater inflater;
    private int focusedItem = 0;

    public SettingItemAdapter(@NonNull Context context, List<SettingItem> items) {
        super(context, 0, items);
        inflater = LayoutInflater.from(context);
    }

    public void setFocusedItem(int position) {
        focusedItem = position;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = convertView;
        if (itemView == null) {
            itemView = inflater.inflate(R.layout.item_setting, parent, false);
        }
        TextView tvTitle = itemView.findViewById(R.id.tv_title);
        TextView tvSub = itemView.findViewById(R.id.tv_sub_title);
        ImageView ivLeft = itemView.findViewById(R.id.iv_left_arrow);
        ImageView ivRight = itemView.findViewById(R.id.iv_right_arrow);

        SettingItem item = getItem(position);
        if (item == null) return itemView;

        // 主标题
        tvTitle.setText(item.getTitleResId());

        // 副标题：有内容才显示
        if (item.getSubTitle() != null && !item.getSubTitle().isEmpty()) {
            tvSub.setVisibility(View.VISIBLE);
            tvSub.setText(item.getSubTitle());
        } else {
            tvSub.setVisibility(View.GONE);
        }

        // 箭头：亮度项显示左右箭头，普通项只显示右箭头，声音项不显示箭头
        if (item.getType() == SettingItem.TYPE_BRIGHT) {
            ivLeft.setVisibility(View.VISIBLE);
            ivRight.setVisibility(View.VISIBLE);
        } else if (item.getType() == SettingItem.TYPE_SOUND) {
            ivLeft.setVisibility(View.GONE);
            ivRight.setVisibility(View.GONE);
        } else {
            ivLeft.setVisibility(View.GONE);
            ivRight.setVisibility(View.VISIBLE);
        }

        // 焦点效果（参照 LanguageItemAdapter）
        if (position == focusedItem) {
            itemView.setBackgroundResource(R.drawable.bg_gradient_focus_red);
            tvTitle.setTextColor(ContextCompat.getColor(getContext(), R.color.tv_normal));
            tvSub.setTextColor(ContextCompat.getColor(getContext(), R.color.tv_normal));
            ivLeft.setImageResource(R.drawable.arrow_left_normal);
            ivRight.setImageResource(R.drawable.arrow_normal);
        } else {
            itemView.setBackgroundResource(R.drawable.bg_gradient_normal);
            tvTitle.setTextColor(ContextCompat.getColor(getContext(), R.color.tv_normal));
            tvSub.setTextColor(ContextCompat.getColor(getContext(), R.color.tv_normal));
            ivLeft.setImageResource(R.drawable.arrow_left_normal);
            ivRight.setImageResource(R.drawable.arrow_normal);
        }
        return itemView;
    }
}