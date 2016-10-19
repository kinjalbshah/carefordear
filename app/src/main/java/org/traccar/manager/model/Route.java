package org.traccar.manager.model;

/**
 * Created by CF4 on 22-09-2016.
 */

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.traccar.manager.model.Route_Attribute ;

//import java.util.jar.Attributes;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Route {

    private String fixTime;

    private Boolean outdated;

    private Boolean valid;

    private Double latitude;

    private Double longitude;

    private Double altitude;

    private Double speed;

    private Double course;

    private String serverTime;

    private String deviceTime;

    private String protocol;

    private Long deviceId;

    private Long id;

    private Route_Attribute attributes;
/*
    @Override
    public LatLng getPosition() {
        return new LatLng(latitude, longitude);
    }

*/
    /**
     *
     * @return
     * The fixTime
     */
    @JsonProperty("fixTime")
    public String getFixTime() {
        return fixTime;
    }

    /**
     *
     * @param fixTime
     * The fixTime
     */
    @JsonProperty("fixTime")
    public void setFixTime(String fixTime) {
        this.fixTime = fixTime;
    }

    /**
     *
     * @return
     * The outdated
     */
    @JsonProperty("outdated")
    public Boolean getOutdated() {
        return outdated;
    }

    /**
     *
     * @param outdated
     * The outdated
     */
    @JsonProperty("outdated")
    public void setOutdated(Boolean outdated) {
        this.outdated = outdated;
    }

    /**
     *
     * @return
     * The valid
     */
    @JsonProperty("valid")
    public Boolean getValid() {
        return valid;
    }

    /**
     *
     * @param valid
     * The valid
     */
    @JsonProperty("valid")
    public void setValid(Boolean valid) {
        this.valid = valid;
    }

    /**
     *
     * @return
     * The latitude
     */
    @JsonProperty("latitude")
    public Double getLatitude() {
        return latitude;
    }

    /**
     *
     * @param latitude
     * The latitude
     */
    @JsonProperty("latitude")
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    /**
     *
     * @return
     * The longitude
     */
    @JsonProperty("longitude")
    public Double getLongitude() {
        return longitude;
    }

    /**
     *
     * @param longitude
     * The longitude
     */
    @JsonProperty("longitude")
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    /**
     *
     * @return
     * The altitude
     */
    @JsonProperty("altitude")
    public Double getAltitude() {
        return altitude;
    }

    /**
     *
     * @param altitude
     * The altitude
     */
    @JsonProperty("altitude")
    public void setAltitude(Double altitude) {
        this.altitude = altitude;
    }

    /**
     *
     * @return
     * The speed
     */
    @JsonProperty("speed")
    public Double getSpeed() {
        return speed;
    }

    /**
     *
     * @param speed
     * The speed
     */
    @JsonProperty("speed")
    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    /**
     *
     * @return
     * The course
     */
    @JsonProperty("course")
    public Double getCourse() {
        return course;
    }

    /**
     *
     * @param course
     * The course
     */
    @JsonProperty("course")
    public void setCourse(Double course) {
        this.course = course;
    }

    /**
     *
     * @return
     * The serverTime
     */
    @JsonProperty("serverTime")
    public String getServerTime() {
        return serverTime;
    }

    /**
     *
     * @param serverTime
     * The serverTime
     */
    @JsonProperty("serverTime")
    public void setServerTime(String serverTime) {
        this.serverTime = serverTime;
    }

    /**
     *
     * @return
     * The deviceTime
     */
    @JsonProperty("deviceTime")
    public String getDeviceTime() {
        return deviceTime;
    }

    /**
     *
     * @param deviceTime
     * The deviceTime
     */
    @JsonProperty("deviceTime")
    public void setDeviceTime(String deviceTime) {
        this.deviceTime = deviceTime;
    }

    /**
     *
     * @return
     * The protocol
     */
    @JsonProperty("protocol")
    public String getProtocol() {
        return protocol;
    }

    /**
     *
     * @param protocol
     * The protocol
     */
    @JsonProperty("protocol")
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    /**
     *
     * @return
     * The deviceId
     */
    @JsonProperty("deviceId")
    public Long getDeviceId() {
        return deviceId;
    }

    /**
     *
     * @param deviceId
     * The deviceId
     */
    @JsonProperty("deviceId")
    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    /**
     *
     * @return
     * The id
     */
    @JsonProperty("id")
    public Long getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    @JsonProperty("id")
    public void setId(Long id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The attributes
     */
    // Commented for timebeing for attributes
    @JsonProperty("attributes")
    public Route_Attribute getAttributes() {
        return attributes;
    }

    /*
     *
     * @param attributes
     * The attributes
     */

    @JsonProperty("attributes")
    public void setAttributes(Route_Attribute attributes) {
        this.attributes = attributes;
    }

}
