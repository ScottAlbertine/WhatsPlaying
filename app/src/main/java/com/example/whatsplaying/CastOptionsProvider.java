package com.example.whatsplaying;

import android.content.Context;
import com.google.android.gms.cast.framework.CastOptions;
import com.google.android.gms.cast.framework.OptionsProvider;
import com.google.android.gms.cast.framework.SessionProvider;

import java.util.List;

/**
 * @author Scott Albertine
 */

public class CastOptionsProvider implements OptionsProvider {

	public CastOptions getCastOptions(Context appContext) {
		return new CastOptions.Builder().build();
	}

	public List<SessionProvider> getAdditionalSessionProviders(Context context) {
		return null;
	}
}
