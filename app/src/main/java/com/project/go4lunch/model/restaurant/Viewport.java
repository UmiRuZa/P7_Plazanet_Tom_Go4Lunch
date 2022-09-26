
package com.project.go4lunch.model.restaurant;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.project.go4lunch.model.restaurant.Northeast;
import com.project.go4lunch.model.restaurant.Southwest;

public class Viewport {

    @SerializedName("northeast")
    @Expose
    private Northeast northeast;
    @SerializedName("southwest")
    @Expose
    private Southwest southwest;

    public Northeast getNortheast() {
        return northeast;
    }

    public void setNortheast(Northeast northeast) {
        this.northeast = northeast;
    }

    public Southwest getSouthwest() {
        return southwest;
    }

    public void setSouthwest(Southwest southwest) {
        this.southwest = southwest;
    }

}
