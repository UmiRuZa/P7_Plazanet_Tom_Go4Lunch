
package com.project.go4lunch.model.restaurant;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.project.go4lunch.model.restaurant.Location;

public class Geometry {

    @SerializedName("location")
    @Expose
    private Location location;
    @SerializedName("viewport")
    @Expose
    private Viewport viewport;

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Viewport getViewport() {
        return viewport;
    }

    public void setViewport(Viewport viewport) {
        this.viewport = viewport;
    }

}
