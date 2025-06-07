package com.example.castorway.modelsDB;

public class Chat {
    private int idChat;
    private int idKit;
    private int idCastor;
    private String contenido;
    private String emisor;
    private String fechaEnvio;

    public int getIdChat() {
        return idChat;
    }

    public void setIdChat(int idChat) {
        this.idChat = idChat;
    }

    public int getIdKit() {
        return idKit;
    }

    public void setIdKit(int idKit) {
        this.idKit = idKit;
    }

    public int getIdCastor() {
        return idCastor;
    }

    public void setIdCastor(int idCastor) {
        this.idCastor = idCastor;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public String getEmisor() {
        return emisor;
    }

    public void setEmisor(String emisor) {
        this.emisor = emisor;
    }

    public String getFechaEnvio() {
        return fechaEnvio;
    }

    public void setFechaEnvio(String fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }


}

