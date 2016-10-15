package org.traccar.manager.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by CF4 on 11-10-2016.
 */


@JsonIgnoreProperties(ignoreUnknown = true)
    public class GeocodeResult {

        @JsonProperty("address_components")
        private List<GeocodeAddressComponent> GeocodeAddressComponents = new ArrayList<GeocodeAddressComponent>();
        @JsonProperty("formatted_address")
        private String formattedAddress;
     //   @JsonProperty("geometry")
     //   private Geometry geometry;
        @JsonProperty("place_id")
        private String placeId;
        @JsonProperty("types")
        private List<String> types = new ArrayList<String>();
        @JsonIgnore
        private Map<String, Object> additionalProperties = new HashMap<String, Object>();

        /**
         *
         * @return
         * The GeocodeAddressComponents
         */
        @JsonProperty("address_components")
        public List<GeocodeAddressComponent> getGeocodeAddressComponents() {
            return GeocodeAddressComponents;
        }

        /**
         *
         * @param GeocodeAddressComponents
         * The address_components
         */
        @JsonProperty("address_components")
        public void setGeocodeAddressComponents(List<GeocodeAddressComponent> GeocodeAddressComponents) {
            this.GeocodeAddressComponents = GeocodeAddressComponents;
        }

        /**
         *
         * @return
         * The formattedAddress
         */
        @JsonProperty("formatted_address")
        public String getFormattedAddress() {
            return formattedAddress;
        }

        /**
         *
         * @param formattedAddress
         * The formatted_address
         */
        @JsonProperty("formatted_address")
        public void setFormattedAddress(String formattedAddress) {
            this.formattedAddress = formattedAddress;
        }


        /**
         *
         * @return
         * The placeId
         */
        @JsonProperty("place_id")
        public String getPlaceId() {
            return placeId;
        }

        /**
         *
         * @param placeId
         * The place_id
         */
        @JsonProperty("place_id")
        public void setPlaceId(String placeId) {
            this.placeId = placeId;
        }

        /**
         *
         * @return
         * The types
         */
        @JsonProperty("types")
        public List<String> getTypes() {
            return types;
        }

        /**
         *
         * @param types
         * The types
         */
        @JsonProperty("types")
        public void setTypes(List<String> types) {
            this.types = types;
        }



    }

