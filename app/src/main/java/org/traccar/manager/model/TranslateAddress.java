package org.traccar.manager.model;

/**
 * Created by cf4 on 18-10-2016.
 */

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

public class TranslateAddress {


      public class Data {

          @JsonProperty("translations")
          private ArrayList<Translation> translations = new ArrayList<Translation>();

          @JsonProperty("translations")
          public ArrayList<Translation> getTranslations() {
              return translations;
          }

          @JsonProperty("translations")
          public void setTranslations(ArrayList<Translation> translations) {
              this.translations = translations;
          }


    }
    @JsonProperty("data")
    private Data data;

   @JsonProperty("data")
    public Data getData() {     return data;
    }

    @JsonProperty("data")
    public void setData(Data data) { this.data = data;
    }

}



