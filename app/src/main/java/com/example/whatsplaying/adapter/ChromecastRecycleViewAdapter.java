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

/**
 * Allows us to populate the Recycle View that shows the list of chromecasts.
 *
 * @author Scott Albertine
 */
public class ChromecastRecycleViewAdapter extends RecyclerView.Adapter<ChromecastRecycleViewAdapter.ChromecastViewHolder> {

	public ChromecastViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
		ConstraintLayout layout = (ConstraintLayout) LayoutInflater.from(viewGroup.getContext())
																   .inflate(R.layout.chromecast_entry, viewGroup, false);
		return new ChromecastViewHolder(layout);
	}

	public void onBindViewHolder(ChromecastViewHolder vh, int i) {
		ChromeCast chromecast = ChromeCasts.get().get(i);
		vh.nameView.setText(chromecast.getTitle());
		vh.nameView.setOnClickListener((View v) -> {
			Intent intent = new Intent(vh.layout.getContext(), NowPlayingActivity.class);
			intent.putExtra(NowPlayingActivity.INDEX_KEY, i);
			vh.layout.getContext().startActivity(intent);
		});
	}

	public int getItemCount() {
		return ChromeCasts.get().size();
	}

	/**
	 * Holds a chromecast view, so we can easily access the parts of it.
	 */
	public static class ChromecastViewHolder extends RecyclerView.ViewHolder {
		/** The main layout of the chromecast view. */
		public ConstraintLayout layout;
		/** The name view of the chromecast. */
		public TextView nameView;

		/**
		 * Create a new chromecast view holder, for the given constraint layout that holds one chromecast entry.
		 *
		 * @param layout duh
		 */
		public ChromecastViewHolder(ConstraintLayout layout) {
			super(layout);
			this.layout = layout;
			nameView = layout.findViewById(R.id.chromecast_name);
		}
	}

}
