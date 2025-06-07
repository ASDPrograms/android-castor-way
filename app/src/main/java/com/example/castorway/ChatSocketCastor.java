package com.example.castorway;

import android.os.Looper;
import android.util.Log;

import org.json.JSONObject;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import android.os.Handler;
import android.os.Looper;

import com.example.castorway.ChatSocketListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class ChatSocketCastor {
    private static ChatSocketCastor instanciaUnica;
    private OkHttpClient client;
    private WebSocket webSocket;
    private ChatSocketListener listener;
    private ChatSocketCastor() {}

    public static synchronized ChatSocketCastor getInstance() {
        if (instanciaUnica == null) {
            instanciaUnica = new ChatSocketCastor();
        }
        return instanciaUnica;
    }

    public boolean estaConectado() {
        return webSocket != null;
    }
    public void iniciarConexion(String idUsuario, String tipoUsuario, String idsKit) {
        if (webSocket != null) {
            Log.d("WebSocket", "Ya hay una conexión activa, no se abrirá otra.");
            return;
        }

        client = new OkHttpClient();

        HttpUrl url = HttpUrl.parse("https://castorway.com.mx/CastorWay/chat")
                .newBuilder()
                .addQueryParameter("idUsuario", idUsuario)
                .addQueryParameter("tipoUsuario", tipoUsuario)
                .addQueryParameter("idsKit", idsKit)
                .build();


        Request request = new Request.Builder()
                .url(url)
                .build();

        webSocket = client.newWebSocket(request, new WebSocketListener() {
            private final Handler mainHandler = new Handler(Looper.getMainLooper());

            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                Log.d("WebSocket", "Conectado correctamente");
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                Log.d("WebSocket", "Mensaje recibido: " + text);

                try{
                    JSONObject mensaje = new JSONObject(text);

                    String tipo = mensaje.optString("type");
                    Log.d("WebSocket", "Tipo: " + tipo);

                    if ("ping".equals(tipo)) {
                        Log.d("WebSocket", "Ping recibido");
                    }
                    else if (tipo.equals("estadoConexion")) {
                        int userIdConect = mensaje.getInt("conectadoPor");
                        boolean estadoUsr = mensaje.getBoolean("conectado");

                        Log.d("WebSocket", "Estado conexión. Usuario: " + userIdConect + " Estado: " + estadoUsr);

                        mainHandler.post(() -> {
                            // Aquí llama tu método para actualizar UI de estado conexión
                            if (listener != null) {
                                listener.onEstadoConexionActualizado(userIdConect, estadoUsr);
                            }
                        });

                    }
                    else {
                        Log.d("WebSocket", "Mensaje normal recibido");

                        String emisor = mensaje.getString("emisor");
                        String textoMensaje = mensaje.getString("txtMsj");
                        String horaEnvio = mensaje.getString("horaEnvio");
                        int idKit = mensaje.getInt("idKit");

                        mainHandler.post(() -> {
                            if (listener != null) {
                                listener.onNuevoMensaje(emisor, textoMensaje, horaEnvio, idKit);
                            }
                        });
                    }

                }catch (Exception e) {
                    Log.e("WebSocket", "Error al parsear el mensaje", e);
                    mainHandler.post(() -> {
                        if (listener != null) {
                            listener.onErrorAlParsearMensaje();
                        }
                    });
                }
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                Log.e("WebSocket", "Fallo en conexión", t);
                webSocket = null;
            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                webSocket.close(1000, null);
                Log.d("WebSocket", "Cerrando conexión: " + reason);
                ChatSocketCastor.this.webSocket = null;
            }
        });
    }

    public void enviarMensaje(String mensajeJson) {
        if (webSocket != null) {
            webSocket.send(mensajeJson);
        }
    }

    public void cerrarConexion() {
        if (webSocket != null) {
            webSocket.close(1000, "Cierre normal");
            webSocket = null;
        }
        if (client != null) {
            client.dispatcher().executorService().shutdown();
        }
    }

    public void setChatSocketListener(ChatSocketListener listener) {
        this.listener = listener;
    }

}