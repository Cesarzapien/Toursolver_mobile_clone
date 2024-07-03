package com.cesar.toursolvermobile2.model;

import android.os.Parcel;
import android.os.Parcelable;

public class AgendaModel implements Parcelable {
    String stopId;
    String label;
    String stopStartTime;
    String stopEndTime;

    protected AgendaModel(Parcel in) {
        stopId = in.readString();
        label = in.readString();
        stopStartTime = in.readString();
        stopEndTime = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(stopId);
        dest.writeString(label);
        dest.writeString(stopStartTime);
        dest.writeString(stopEndTime);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AgendaModel> CREATOR = new Creator<AgendaModel>() {
        @Override
        public AgendaModel createFromParcel(Parcel in) {
            return new AgendaModel(in);
        }

        @Override
        public AgendaModel[] newArray(int size) {
            return new AgendaModel[size];
        }
    };

    public String getStopId() {
        return stopId;
    }

    public void setStopId(String stopId) {
        this.stopId = stopId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getStopStartTime() {
        return stopStartTime;
    }

    public void setStopStartTime(String stopStartTime) {
        this.stopStartTime = stopStartTime;
    }

    public String getStopEndTime() {
        return stopEndTime;
    }

    public void setStopEndTime(String stopEndTime) {
        this.stopEndTime = stopEndTime;
    }

    public AgendaModel(String stopId, String label, String stopStartTime, String stopEndTime) {
        this.stopId = stopId;
        this.label = label;
        this.stopStartTime = stopStartTime;
        this.stopEndTime = stopEndTime;
    }

    public AgendaModel() {
    }

    @Override
    public String toString() {
        return "AgendaModel{" +
                "stopId='" + stopId + '\'' +
                ", label='" + label + '\'' +
                ", stopStartTime='" + stopStartTime + '\'' +
                ", stopEndTime='" + stopEndTime + '\'' +
                '}';
    }
}
