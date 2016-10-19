package org.traccar.manager.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by cf4 on 18-10-2016.
 */
public class Translation {

        @JsonProperty("translatedText")
        private String translatedText;

        @JsonProperty("translatedText")
        public String getTranslatedText() {
            return translatedText;
        }

        @JsonProperty("translatedText")
        public void setTranslatedText(String translatedText) {
            this.translatedText = translatedText;
        }

    }

