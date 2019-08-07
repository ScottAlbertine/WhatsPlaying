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
import su.litvak.chromecast.api.v2.ChromeCast;
import su.litvak.chromecast.api.v2.ChromeCasts;
import su.litvak.chromecast.api.v2.ChromeCastsListener;

/**
 * @author Scott Albertine
 */
public class ChromecastRecycleViewAdapter extends RecyclerView.Adapter<ChromecastRecycleViewAdapter.ChromecastViewHolder> implements ChromeCastsListener {

	public void newChromeCastDiscovered(ChromeCast chromeCast) {
		notifyItemInserted(ChromeCasts.get().indexOf(chromeCast));
	}

	public void chromeCastRemoved(ChromeCast chromeCast) {
		notifyItemRemoved(ChromeCasts.get().indexOf(chromeCast));
	}

	public ChromecastViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		ConstraintLayout layout = (ConstraintLayout) LayoutInflater.from(parent.getContext())
																   .inflate(R.layout.chromecast_entry, parent, false);
		return new ChromecastViewHolder(layout);
	}

	public void onBindViewHolder(final ChromecastViewHolder holder, final int position) {
		final ChromeCast chromecast = ChromeCasts.get().get(position);
		holder.nameView.setText(chromecast.getTitle());
		holder.nameView.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(holder.layout.getContext(), NowPlayingActivity.class);
				intent.putExtra("com.example.whatsplaying.ccIndex", position);
				holder.layout.getContext().startActivity(intent);
			}
		});
	}

	public int getItemCount() {
		return ChromeCasts.get().size();
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
