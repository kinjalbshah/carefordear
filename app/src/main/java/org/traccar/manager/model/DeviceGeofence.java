/*
 * Created by cd4 on 23-08-2016.
 */

   package org.traccar.manager.model;

    import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


    @JsonIgnoreProperties(ignoreUnknown = true)

    public class DeviceGeofence {

        private long deviceId;

        public void setDeviceId(long deviceId) {
            this.deviceId = deviceId;
        }

        public long getDeviceId() {
            return deviceId;
        }

        private long geofenceId;

        public void setGeofenceId(long geofenceId) {
            this.geofenceId = geofenceId;
        }

        public long getGeofenceId() {
            return geofenceId;
        }


    }
