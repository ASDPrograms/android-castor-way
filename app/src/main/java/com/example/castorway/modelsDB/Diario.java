package com.example.castorway.modelsDB;

public class Diario {
    private int idDiario;
    private int idKit;
    private String titulo;
    private String info;
    private String imgPrivacidad;
    private int privacidad;
    private String imgSentimiento;
    private String diaCreacion;

    public int getIdDiario() {
        return idDiario;
    }

    public void setIdDiario(int idDiario) {
        this.idDiario = idDiario;
    }

    public int getIdKit() {
        return idKit;
    }

    public void setIdKit(int idKit) {
        this.idKit = idKit;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getImgPrivacidad() {
        return imgPrivacidad;
    }

    public void setImgPrivacidad(String imgPrivacidad) {
        this.imgPrivacidad = imgPrivacidad;
    }

    public int getPrivacidad() {
        return privacidad;
    }

    public void setPrivacidad(int privacidad) {
        this.privacidad = privacidad;
    }

    public String getImgSentimiento() {
        return imgSentimiento;
    }

    public void setImgSentimiento(String imgSentimiento) {
        this.imgSentimiento = imgSentimiento;
    }

    public String getDiaCreacion() {
        return diaCreacion;
    }

    public void setDiaCreacion(String datetime) {
        this.diaCreacion = datetime;
    }


}
