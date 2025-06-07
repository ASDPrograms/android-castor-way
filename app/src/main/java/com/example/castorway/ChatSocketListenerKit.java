package com.example.castorway;

public interface ChatSocketListenerKit {
    void onNuevoMensaje(String emisor, String mensaje, String hora, int idCastor);
    void onEstadoConexionActualizado(int userId, boolean conectado);
    void onErrorAlParsearMensaje();
}
