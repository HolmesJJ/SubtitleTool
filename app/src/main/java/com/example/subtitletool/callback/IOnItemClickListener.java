package com.example.subtitletool.callback;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public interface IOnItemClickListener {

    void onItemClick(int position, View view, RecyclerView.ViewHolder vh);
}
