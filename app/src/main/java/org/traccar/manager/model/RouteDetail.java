package org.traccar.manager.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by cf4 on 10-10-2016.
 */
public class RouteDetail implements Parcelable {

    int id;
    Double lat;
    Double lon;
    String servertime;
    private String rgeocodeaddres;
    private String translatedaddress;

    public RouteDetail(Double lat, Double lon, String servertime) {

        this.lat = lat;
        this.lon = lon;
        this.servertime = servertime;

    }

    public int getId() {
        return id;
    }

    public double getLat() {
        return lat;
    }

    public double getlon() {
        return lon;
    }

    public String getservertime() {
        return servertime;
    }

    public String getRGeocodeAddress() {  return rgeocodeaddres ; }

    public String getTranslatedaddress() {  return translatedaddress ; }

    public void setLatitude(Double latitude) {    this.lat = latitude;  }

    public void setLongitude(Double longitude) { this.lon = longitude;  }

    public void setFixTime(String fixTime) { this.servertime = fixTime;  }

    public void setRGeocodeAddress(String address) { this.rgeocodeaddres = address;  }

    public void setTranslatedaddress(String address) { this.translatedaddress = address;  }


    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int arg1) {
        // TODO Auto-generated method stub
        dest.writeInt(id);
        dest.writeDouble(lat);
        dest.writeDouble(lon);
        dest.writeString(servertime);
        dest.writeString(rgeocodeaddres);
        dest.writeString(translatedaddress);

    }

    public RouteDetail(Parcel in) {
        id = in.readInt();
        lat = in.readDouble();
        lon = in.readDouble();
        servertime = in.readString();
        rgeocodeaddres = in.readString();
        translatedaddress = in.readString();

    }

    public static final Parcelable.Creator<RouteDetail> CREATOR = new Parcelable.Creator<RouteDetail>() {
        public RouteDetail createFromParcel(Parcel in) {
            return new RouteDetail(in);
        }

        public RouteDetail[] newArray(int size) {
            return new RouteDetail[size];
        }
    };
}