/*
 * Created by cd4 on 23-08-2016.
 */

package org.traccar.manager.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)

public class DeviceEvent {
    @JsonProperty("serverTime")
    private String serverTime;
    @JsonProperty("positionId")
    private Long positionId;
    @JsonProperty("geofenceId")
    private Long geofenceId;
    @JsonProperty("deviceId")
    private Long deviceId;
    @JsonProperty("type")
    private String type;
    @JsonProperty("id")
    private Long id;

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
     * The positionId
     */
    @JsonProperty("positionId")
    public Long getPositionId() {
        return positionId;
    }

    /**
     *
     * @param positionId
     * The positionId
     */
    @JsonProperty("positionId")
    public void setPositionId(Long positionId) {
        this.positionId = positionId;
    }

    /**
     *
     * @return
     * The geofenceId
     */
    @JsonProperty("geofenceId")
    public Long getGeofenceId() {
        return geofenceId;
    }

    /**
     *
     * @param geofenceId
     * The geofenceId
     */
    @JsonProperty("geofenceId")
    public void setGeofenceId(Long geofenceId) {
        this.geofenceId = geofenceId;
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
     * The type
     */
    @JsonProperty("type")
    public String getType() {
        return type;
    }

    /**
     *
     * @param type
     * The type
     */
    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
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

    @Override
    public String toString() {
        return type;
    }


}
