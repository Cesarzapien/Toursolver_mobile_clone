package com.cesar.toursolvermobile2.model;

import android.os.Parcel;
import android.os.Parcelable;

public class AgendaModel implements Parcelable {
    String id;
    String stopId;
    String label;
    String stopStartTime;
    String stopEndTime;
    String status;

    protected AgendaModel(Parcel in) {
        id = in.readString();
        stopId = in.readString();
        label = in.readString();
        stopStartTime = in.readString();
        stopEndTime = in.readString();
        status = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(stopId);
        dest.writeString(label);
        dest.writeString(stopStartTime);
        dest.writeString(stopEndTime);
        dest.writeString(status);
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public AgendaModel(String id, String stopId, String label, String stopStartTime, String stopEndTime, String status) {
        this.id = id;
        this.stopId = stopId;
        this.label = label;
        this.stopStartTime = stopStartTime;
        this.stopEndTime = stopEndTime;
        this.status = status;
    }

    public AgendaModel() {
    }

    @Override
    public String toString() {
        return "AgendaModel{" +
                "id='" + id + '\'' +
                ", stopId='" + stopId + '\'' +
                ", label='" + label + '\'' +
                ", stopStartTime='" + stopStartTime + '\'' +
                ", stopEndTime='" + stopEndTime + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
