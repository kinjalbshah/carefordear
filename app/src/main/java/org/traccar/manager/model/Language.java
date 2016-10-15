package org.traccar.manager.model;

/**
 * Created by cf4 on 14-10-2016.
 */
public class Language {


        private String id;
        private String name;

        public Language(String id, String name) {
            this.id = id;
            this.name = name;
        }


        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }


        //to display object as a string in spinner
        public String toString() {
            return name;
        }

    }

