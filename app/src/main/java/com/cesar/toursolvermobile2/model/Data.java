package com.cesar.toursolvermobile2.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Data implements Parcelable {
    @SerializedName("Contrato")
    private String Contrato;
    @SerializedName("nombre del cliente")
    private String nombre_del_cliente;

    @SerializedName("Tienda")
    private String Tienda;

    @SerializedName("Observaciones")
    private String Observaciones;

    @SerializedName("Direccion")
    private String Direccion;

    @SerializedName("Tipo_de_Visita")
    private String Tipo_de_Visita;


    protected Data(Parcel in) {
        Contrato = in.readString();
        nombre_del_cliente = in.readString();
        Tienda = in.readString();
        Observaciones = in.readString();
        Direccion = in.readString();
        Tipo_de_Visita = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(Contrato);
        dest.writeString(nombre_del_cliente);
        dest.writeString(Tienda);
        dest.writeString(Observaciones);
        dest.writeString(Direccion);
        dest.writeString(Tipo_de_Visita);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Data> CREATOR = new Creator<Data>() {
        @Override
        public Data createFromParcel(Parcel in) {
            return new Data(in);
        }

        @Override
        public Data[] newArray(int size) {
            return new Data[size];
        }
    };

    public String getContrato() {
        return Contrato;
    }

    public void setContrato(String contrato) {
        Contrato = contrato;
    }

    public String getNombre_del_cliente() {
        return nombre_del_cliente;
    }

    public void setNombre_del_cliente(String nombre_del_cliente) {
        this.nombre_del_cliente = nombre_del_cliente;
    }

    public String getTienda() {
        return Tienda;
    }

    public void setTienda(String tienda) {
        Tienda = tienda;
    }

    public String getObservaciones() {
        return Observaciones;
    }

    public void setObservaciones(String observaciones) {
        Observaciones = observaciones;
    }

    public String getDireccion() {
        return Direccion;
    }

    public void setDireccion(String direccion) {
        Direccion = direccion;
    }

    public String getTipo_de_Visita() {
        return Tipo_de_Visita;
    }

    public void setTipo_de_Visita(String tipo_de_Visita) {
        Tipo_de_Visita = tipo_de_Visita;
    }

    @Override
    public String toString() {
        return "Data{" +
                "Contrato='" + Contrato + '\'' +
                ", nombre_del_cliente='" + nombre_del_cliente + '\'' +
                ", Tienda='" + Tienda + '\'' +
                ", Observaciones='" + Observaciones + '\'' +
                ", Direccion='" + Direccion + '\'' +
                ", Tipo_de_Visita='" + Tipo_de_Visita + '\'' +
                '}';
    }
}