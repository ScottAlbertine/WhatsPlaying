package com.example.whatsplaying.adapter;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.whatsplaying.R;
import com.example.whatsplaying.dto.Chromecast;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Scott Albertine
 */
public class ChromecastRecycleViewAdapter extends RecyclerView.Adapter<ChromecastRecycleViewAdapter.ChromecastViewHolder> {

	private List<Chromecast> chromecasts = new ArrayList<>();

	public void addChromecast(Chromecast chromecast) {
		chromecasts.add(chromecast);
		notifyItemInserted(chromecasts.size() - 1);
	}

	@Override
	public ChromecastViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		ConstraintLayout layout = (ConstraintLayout) LayoutInflater.from(parent.getContext())
																   .inflate(R.layout.chromecast_entry, parent, false);
		return new ChromecastViewHolder(layout);
	}

	@Override
	public void onBindViewHolder(ChromecastViewHolder holder, int position) {
		holder.nameView.setText(chromecasts.get(position).name);
	}

	public int getItemCount() {
		return chromecasts.size();
	}

	public static class ChromecastViewHolder extends RecyclerView.ViewHolder {
		public ConstraintLayout layout;
		public TextView nameView;

		public ChromecastViewHolder(ConstraintLayout layout) {
			super(layout);
			this.layout = layout;
			nameView = layout.findViewById(R.id.chromecast_name);
		}
	}

}
