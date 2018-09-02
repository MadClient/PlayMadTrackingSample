package cn.playmad.ads.gtch.google.com.playmadsdk.Model.Tracking.UUID.V2;

import java.util.Map;

public class ChannelInfo {
    private final String channel;
    private final Map<String, String> extraInfo;

    public ChannelInfo(final String channel, final Map<String, String> extraInfo) {
        this.channel = channel;
        this.extraInfo = extraInfo;
    }

    public String getChannel() {
        return channel;
    }

    public Map<String, String> getExtraInfo() {
        return extraInfo;
    }
}
