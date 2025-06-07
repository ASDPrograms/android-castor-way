package com.example.castorway.modelsDB;

public class Castor {
    private int idCastor;
    private String codPresa;
    private String nombre;
    private String apellidos;
    private int edad;
    private String email;
    private String contraseña;
    private int ramitas;
    private String imagenPerfil;
    private String fechaRegistro;
    private boolean estadoConect;

    public int getIdCastor() {
        return idCastor;
    }

    public void setIdCastor(int idCastor) {
        this.idCastor = idCastor;
    }

    public String getCodPresa() {
        return codPresa;
    }

    public void setCodPresa(String codPresa) {
        this.codPresa = codPresa;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    public int getRamitas() {
        return ramitas;
    }

    public void setRamitas(int ramitas) {
        this.ramitas = ramitas;
    }

    public String getImagenPerfil() {
        return imagenPerfil;
    }

    public void setImagenPerfil(String imagenPerfil) {
        this.imagenPerfil = imagenPerfil;
    }

    public String getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(String fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public boolean getEstadoConect() {
        return estadoConect;
    }

    public void setEstadoConect(boolean estadoConect) {
        this.estadoConect = estadoConect;
    }
}
