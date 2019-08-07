package com.example.whatsplaying.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import su.litvak.chromecast.api.v2.Device;
import su.litvak.chromecast.api.v2.Response;

import java.util.Map;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "responseType")
@JsonSubTypes({@JsonSubTypes.Type(name = "PING", value = BetterResponse.Ping.class),
			   @JsonSubTypes.Type(name = "PONG", value = BetterResponse.Pong.class),
			   @JsonSubTypes.Type(name = "RECEIVER_STATUS", value = BetterResponse.Status.class),
			   @JsonSubTypes.Type(name = "GET_APP_AVAILABILITY", value = BetterResponse.AppAvailability.class),
			   @JsonSubTypes.Type(name = "INVALID_REQUEST", value = BetterResponse.Invalid.class),
			   @JsonSubTypes.Type(name = "MEDIA_STATUS", value = BetterResponse.MediaStatus.class),
			   @JsonSubTypes.Type(name = "CLOSE", value = BetterResponse.Close.class),
			   @JsonSubTypes.Type(name = "LOAD_FAILED", value = BetterResponse.LoadFailed.class),
			   @JsonSubTypes.Type(name = "LAUNCH_ERROR", value = BetterResponse.LaunchError.class),
			   @JsonSubTypes.Type(name = "DEVICE_ADDED", value = BetterResponse.DeviceAdded.class),
			   @JsonSubTypes.Type(name = "DEVICE_UPDATED", value = BetterResponse.DeviceUpdated.class),
			   @JsonSubTypes.Type(name = "DEVICE_REMOVED", value = BetterResponse.DeviceRemoved.class)})
public abstract class BetterResponse implements Response {
	public Long requestId;

	public final Long getRequestId() {
		return requestId;
	}

	public final void setRequestId(Long requestId) {
		this.requestId = requestId;
	}

	/**
	 * Request to send 'Pong' message in reply.
	 */
	public static class Ping extends BetterResponse {
	}

	/**
	 * Response in reply to 'Ping' message.
	 */
	public static class Pong extends BetterResponse {
	}

	/**
	 * Request to 'Close' connection.
	 */
	public static class Close extends BetterResponse {
	}

	/**
	 * Identifies that loading of media has failed.
	 */
	public static class LoadFailed extends BetterResponse {
	}

	/**
	 * Request was invalid for some <code>reason</code>.
	 */
	public static class Invalid extends BetterResponse {
		public final String reason;

		public Invalid(@JsonProperty("reason") String reason) {
			this.reason = reason;
		}
	}

	/**
	 * Application cannot be launched for some <code>reason</code>.
	 */
	public static class LaunchError extends BetterResponse {
		public final String reason;

		public LaunchError(@JsonProperty("reason") String reason) {
			this.reason = reason;
		}
	}

	/**
	 * Response to "Status" request.
	 */
	public static class Status extends BetterResponse {
		@JsonProperty
		public final su.litvak.chromecast.api.v2.Status status;

		public Status(@JsonProperty("status") su.litvak.chromecast.api.v2.Status status) {
			this.status = status;
		}
	}

	/**
	 * Response to "MediaStatus" request.
	 */
	public static class MediaStatus extends BetterResponse {
		final su.litvak.chromecast.api.v2.MediaStatus[] statuses;

		MediaStatus(@JsonProperty("status") su.litvak.chromecast.api.v2.MediaStatus... statuses) {
			this.statuses = statuses;
		}
	}

	/**
	 * Response to "AppAvailability" request.
	 */
	public static class AppAvailability extends BetterResponse {
		@JsonProperty
		public Map<String, String> availability;
	}

	/**
	 * Received when power is cycled on ChromeCast Audio device in a group.
	 */
	public static class DeviceAdded extends BetterResponse {
		public final Device device;

		public DeviceAdded(@JsonProperty("device") Device device) {
			this.device = device;
		}
	}

	/**
	 * Received when volume is changed in ChromeCast Audio group.
	 */
	public static class DeviceUpdated extends BetterResponse {
		public final Device device;

		public DeviceUpdated(@JsonProperty("device") Device device) {
			this.device = device;
		}
	}

	/**
	 * Received when power is cycled on ChromeCast Audio device in a group.
	 */
	public static class DeviceRemoved extends BetterResponse {
		public final String deviceId;

		public DeviceRemoved(@JsonProperty("deviceId") String deviceId) {
			this.deviceId = deviceId;
		}
	}
}
