package org.traccar.manager.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
/**
 * Created by cf4Itirawat on 24-09-2016.
 */
public class Route_Attribute {


    private String battery;
    private String ip;
    private Double distance;
    private Double totalDistance;

    /**
     *
     * @return
     * The battery
     */
    @JsonProperty("battery")
    public String getBattery() {
        return battery;
    }

    /**
     *
     * @param battery
     * The battery
     */
    @JsonProperty("battery")
    public void setBattery(String battery) {
        this.battery = battery;
    }

    /**
     *
     * @return
     * The ip
     */
    @JsonProperty("ip")
    public String getIp() {
        return ip;
    }

    /**
     *
     * @param ip
     * The ip
     */
    @JsonProperty("ip")
    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     *
     * @return
     * The distance
     */
    @JsonProperty("distance")
    public Double getDistance() {
        return distance;
    }

    /**
     *
     * @param distance
     * The distance
     */
    @JsonProperty("distance")
    public void setDistance(Double distance) {
        this.distance = distance;
    }

    /**
     *
     * @return
     * The totalDistance
     */
    @JsonProperty("totalDistance")
    public Double getTotalDistance() {
        return totalDistance;
    }

    /**
     *
     * @param totalDistance
     * The totalDistance
     */
    @JsonProperty("totalDistance")
    public void setTotalDistance(Double totalDistance) {
        this.totalDistance = totalDistance;
    }

}
