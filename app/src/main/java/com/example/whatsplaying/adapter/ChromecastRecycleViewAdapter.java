package com.example.whatsplaying.adapter;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.whatsplaying.R;
import com.example.whatsplaying.activity.NowPlayingActivity;
import com.example.whatsplaying.dto.Chromecast;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Scott Albertine
 */
public class ChromecastRecycleViewAdapter extends RecyclerView.Adapter<ChromecastRecycleViewAdapter.ChromecastViewHolder> {

	private List<Chromecast> chromecasts = new ArrayList<>();

	public void addChromecast(Chromecast chromeCast) {
		chromecasts.add(chromeCast);
		notifyItemInserted(chromecasts.size() - 1);
	}

	@Override
	public ChromecastViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		ConstraintLayout layout = (ConstraintLayout) LayoutInflater.from(parent.getContext())
																   .inflate(R.layout.chromecast_entry, parent, false);
		return new ChromecastViewHolder(layout);
	}

	@Override
	public void onBindViewHolder(final ChromecastViewHolder holder, int position) {
		final Chromecast chromecast = chromecasts.get(position);
		holder.nameView.setText(chromecast.name);
		holder.nameView.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(holder.layout.getContext(), NowPlayingActivity.class);
				intent.putExtra("ip", chromecast.ip.getHostAddress());
				holder.layout.getContext().startActivity(intent);
			}
		});
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
