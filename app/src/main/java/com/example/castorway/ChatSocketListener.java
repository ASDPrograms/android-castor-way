package com.example.castorway;

public interface ChatSocketListener {
    void onNuevoMensaje(String emisor, String mensaje, String hora, int idKit);
    void onEstadoConexionActualizado(int userId, boolean conectado);
    void onErrorAlParsearMensaje();
}
