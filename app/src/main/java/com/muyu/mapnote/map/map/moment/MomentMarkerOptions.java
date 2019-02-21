package com.muyu.mapnote.map.map.moment;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.mapbox.mapboxsdk.annotations.BaseMarkerOptions;
import com.mapbox.mapboxsdk.annotations.Icon;
import com.mapbox.mapboxsdk.annotations.IconFactory;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.exceptions.InvalidMarkerPositionException;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.muyu.minimalism.framework.app.BaseApplication;

public class MomentMarkerOptions extends BaseMarkerOptions {
    private MomentPoi momentPoi;
    /**
     * Defines options for a Marker.
     */
    public MomentMarkerOptions() {
    }

    protected MomentMarkerOptions(Parcel in) {
        position(in.readParcelable(LatLng.class.getClassLoader()));
        snippet(in.readString());
        title(in.readString());
        momentPoi(in.readParcelable(MomentPoi.class.getClassLoader()));
        if (in.readByte() != 0) {
            // this means we have an icon
            String iconId = in.readString();
            Bitmap iconBitmap = in.readParcelable(Bitmap.class.getClassLoader());
            IconFactory iconFactory = IconFactory.getInstance(BaseApplication.getInstance());
            Icon icon = iconFactory.fromBitmap(iconBitmap);
            icon(icon);
        }
    }

    @Override
    public MomentMarkerOptions getThis() {
        return this;
    }

    /**
     * Describe the kinds of special objects contained in this Parcelable's
     * marshalled representation.
     *
     * @return integer 0.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param out   The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written. May be 0 or
     *              {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeParcelable(getPosition(), flags);
        out.writeString(getSnippet());
        out.writeString(getTitle());
        out.writeParcelable(momentPoi, flags);
        Icon icon = getIcon();
        out.writeByte((byte) (icon != null ? 1 : 0));
        if (icon != null) {
            out.writeString(getIcon().getId());
            out.writeParcelable(getIcon().getBitmap(), flags);
        }
    }

    public void momentPoi(MomentPoi poi) {
        this.momentPoi = poi;
    }

    public MomentPoi getMomentPoi() {
        return momentPoi;
    }
    /**
     * Do not use this method. Used internally by the SDK.
     *
     * @return Marker The build marker
     */
    public MomentMarker getMarker() {
        if (position == null) {
            throw new InvalidMarkerPositionException();
        }
        MomentMarkerOptions markerOptions = new MomentMarkerOptions();
        markerOptions.position(position);
        markerOptions.icon(icon);
        markerOptions.snippet(snippet);
        markerOptions.momentPoi(momentPoi);
        return new MomentMarker(markerOptions);
    }

    /**
     * Returns the position set for this {@link MarkerOptions} object.
     *
     * @return A {@link LatLng} object specifying the marker's current position.
     */
    public LatLng getPosition() {
        return position;
    }

    /**
     * Gets the snippet set for this {@link MarkerOptions} object.
     *
     * @return A string containing the marker's snippet.
     */
    public String getSnippet() {
        return snippet;
    }

    /**
     * Gets the title set for this {@link MarkerOptions} object.
     *
     * @return A string containing the marker's title.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets the custom icon set for this {@link MarkerOptions} object.
     *
     * @return A {@link Icon} object that the marker is using. If the icon wasn't set, default icon
     * will return.
     */
    public Icon getIcon() {
        return icon;
    }

    public static final Parcelable.Creator<MomentMarkerOptions> CREATOR =
            new Parcelable.Creator<MomentMarkerOptions>() {
                public MomentMarkerOptions createFromParcel(Parcel in) {
                    return new MomentMarkerOptions(in);
                }

                public MomentMarkerOptions[] newArray(int size) {
                    return new MomentMarkerOptions[size];
                }
            };

    /**
     * Compares this {@link MarkerOptions} object with another {@link MarkerOptions} and
     * determines if their properties match.
     *
     * @param o Another {@link MarkerOptions} to compare with this object.
     * @return True if marker properties match this {@link MarkerOptions} object.
     * Else, false.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        MarkerOptions marker = (MarkerOptions) o;

        if (getPosition() != null ? !getPosition().equals(marker.getPosition()) : marker.getPosition() != null) {
            return false;
        }
        if (getSnippet() != null ? !getSnippet().equals(marker.getSnippet()) : marker.getSnippet() != null) {
            return false;
        }
        if (getIcon() != null ? !getIcon().equals(marker.getIcon()) : marker.getIcon() != null) {
            return false;
        }
        return !(getTitle() != null ? !getTitle().equals(marker.getTitle()) : marker.getTitle() != null);
    }

    /**
     * Gives an integer which can be used as the bucket number for storing elements of the set/map.
     * This bucket number is the address of the element inside the set/map. There's no guarantee
     * that this hash value will be consistent between different Java implementations, or even
     * between different execution runs of the same program.
     *
     * @return integer value you can use for storing element.
     */
    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + (getPosition() != null ? getPosition().hashCode() : 0);
        result = 31 * result + (getSnippet() != null ? getSnippet().hashCode() : 0);
        result = 31 * result + (getIcon() != null ? getIcon().hashCode() : 0);
        result = 31 * result + (getTitle() != null ? getTitle().hashCode() : 0);
        result = 31 * result + (getMomentPoi() != null ? getMomentPoi().hashCode() : 0);
        return result;
    }
}
