package hilos;

import modelo.EventoUniversitario;
import modelo.Inscripcion;
import modelo.actividades.Actividad;

public class EnvioTicketsThread extends Thread {
    private EventoUniversitario evento;

    public EnvioTicketsThread(EventoUniversitario evento) {
        this.evento = evento;
    }

    @Override
    public void run() {
        System.out.println("\n[HILO]Iniciando envío de tickets para el evento: " + evento.getTitulo());

        for (Actividad actividad : evento.getActividades()) {
            for (Inscripcion inscripcion : actividad.getInscripciones()) {
                if (inscripcion.getEstado().equals("CONFIRMADA") && inscripcion.getTicket() != null) {
                    
                    inscripcion.getTicket().enviarTicket();

                    try {
                        // Pausa simulada para evidenciar el comportamiento concurrente
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        System.err.println("[HILO] Error durante el envío: " + e.getMessage());
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }

        System.out.println("[HILO] Terminó el envío de tickets para: " + evento.getTitulo() + "\n");
    }
}