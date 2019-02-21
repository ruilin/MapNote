package com.muyu.mapnote.map.map.moment;

import com.mapbox.mapboxsdk.annotations.Marker;

public class MomentMarker extends Marker {
    private MomentPoi momentPoi;

    public MomentMarker(MomentMarkerOptions markerOptions) {
        super(markerOptions);
        momentPoi = markerOptions.getMomentPoi();
    }

    public MomentPoi getMomentPoi() {
        return momentPoi;
    }
}
