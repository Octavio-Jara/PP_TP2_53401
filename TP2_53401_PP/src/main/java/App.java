import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import excepciones.CupoExcedidoException;
import hilos.EnvioTicketsThread;
import modelo.Estudiante;
import modelo.EventoUniversitario;
import modelo.Inscripcion;
import modelo.Sala;
import modelo.actividades.Actividad;
import modelo.actividades.Charla;
import modelo.actividades.Curso;
import modelo.actividades.Taller;
import modelo.certificacion.Certificable;

public class App {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        boolean continuar = true;
        int id = 1;

        /* Se crean estudiantes */
        List<Estudiante> estudiantes = new ArrayList<>();

        System.out.println("REGISTRO DE ESTUDIANTES: ");
        System.out.println("======================");

        while (continuar) {
            System.out.println("Ingese legajo del estudiante: ");
            String legajo = scanner.nextLine();
            System.out.println("Ingese nombre y apellido del estudiante: ");
            String apenomb = scanner.nextLine();
            estudiantes.add(new Estudiante(legajo, apenomb));
            System.out.println("desea crear otro estudiante S/N?");
            String respuesta = scanner.nextLine().trim().toLowerCase();
            continuar = (respuesta.equals("s") || respuesta.equals("si") || respuesta.equals("sí"));
        }

        /* Se itera construyendo eventos */
        System.out.println("\n\nREGISTRO DE EVENTOS: ");
        System.out.println("====================");
        continuar = true;
        while (continuar) {
            System.out.println("Ingese un titulo para el evento: ");
            String titulo = scanner.nextLine();
            System.out.println("Ingese el costo base: ");
            double costoBase = scanner.nextDouble();
            scanner.nextLine(); // limpia el Enter pendiente

            System.out.println("El evento tendra costo para los participantes S/N?");
            String respuesta = scanner.nextLine().trim().toLowerCase();
            boolean esGratuito = true;
            if (respuesta.equals("s") || respuesta.equals("si") || respuesta.equals("sí")) {
                esGratuito = false;
            }

            EventoUniversitario evento = new EventoUniversitario(
                    "EVT-" + id,
                    titulo,
                    costoBase,
                    esGratuito
            );

            System.out.println("Ingese el nombre de la sala donde se realizará el evento: ");
            String nombreSala = scanner.nextLine();
            System.out.println("Ingese la capacidad de la sala donde se realizará el evento: ");
            int capacidadSala = scanner.nextInt();
            scanner.nextLine(); // Limpia buffer tras pedir la capacidad

            Sala sala = new Sala(id, nombreSala, capacidadSala);
            evento.asignarSala(sala);

            /* Se crean las actividades del evento */
            System.out.println("\n\nREGISTRO DE ACTIVIDADES PARA EL EVENTO " + evento.getTitulo());
            System.out.println("================================================================");
            int idActividad = 1;
            boolean continuarActividades = true;

            while (continuarActividades) {
                System.out.println("Ingese el título de la actividad: ");
                String tituloActividad = scanner.nextLine();
                System.out.println("Ingese el cupo máximo de estudiantes admitidos para la actividad: ");
                int cupo = scanner.nextInt();
                scanner.nextLine(); // CORRECCIÓN 1: Se consume el salto de linea que deja nextInt()

                System.out.println("Ingese el tipo de actividad (taller/charla/curso): ");
                String tipoActividad = scanner.nextLine().trim().toLowerCase();

                evento.crearActividad(idActividad, tituloActividad, cupo, tipoActividad);
                System.out.println("Desea crear otra actividad para el evento " + evento.getTitulo() + " S/N?");
                respuesta = scanner.nextLine().trim().toLowerCase();
                continuarActividades = (respuesta.equals("s") || respuesta.equals("si") || respuesta.equals("sí"));
                idActividad++;
            }

            try{
            /* Se inscriben estudiantes en actividades */
            System.out.println("\n\nINSCRIPCION DE ESTUDIANTES EN ACTIVIDADES DEL EVENTO " + evento.getTitulo());
            System.out.println("===============================================================================");
            boolean continuarInscripcion = true;

            while (continuarInscripcion) {
                System.out.println("Ingese legajo del estudiante a inscribir: ");
                String legajo = scanner.nextLine();
                System.out.println("Ingese id de la Actividad: ");
                int idActSeleccionado = scanner.nextInt();
                scanner.nextLine(); // se consume linea

                for (Estudiante estudiante : estudiantes) {
                    if (estudiante.getLegajo().equals(legajo)) {
                        // CORRECCIÓN 2: Se usa idActSeleccionado - 1 sin decrementar la variable directamente
                        if (idActSeleccionado > 0 && idActSeleccionado <= evento.getActividades().size()) {
                            evento.getActividades().get(idActSeleccionado - 1).inscribir(estudiante);
                        } else {
                            System.out.println("ID de actividad inválido.");
                        }
                    }
                }
                System.out.println("Desea generar otra inscripción S/N?");
                respuesta = scanner.nextLine().trim().toLowerCase();
                continuarInscripcion = (respuesta.equals("s") || respuesta.equals("si") || respuesta.equals("sí"));
                
                // Certificados
                System.out.println("\n\nEMISION DE CERTIFICADOS DEL EVENTO: " + evento.getTitulo());
                System.out.println("================================================================");
                int certificadosEmitidos = 0;

                for (Actividad act : evento.getActividades()) {
                    if (act instanceof Certificable) {
                        Certificable actCertificable = (Certificable) act;
                        for (Inscripcion inscripcion : act.getInscripciones()) {
                            String certificado = actCertificable.generarCertificado(inscripcion.getEstudiante());
                            System.out.println(certificado);
                            certificadosEmitidos++;
                        }
                    }
                }

                if (certificadosEmitidos == 0) {
                    System.out.println("No se emitieron certificados para este evento (sin inscriptos en talleres/cursos).");
                }
            }
            }catch (CupoExcedidoException e) {
                System.out.println("Error al inscribir: " + e.getMessage());
            }
            catch(Exception j)
            {
                System.out.println("Otro error" + j.getMessage());
            }

            System.out.println("\n\nRESUMEN DE ACTIVIDADES Y COSTOS POR TIPO: " + evento.getTitulo());
            List<Charla> charlas = evento.filtrarActividadesPorTipo(Charla.class);
            List<Taller> talleres = evento.filtrarActividadesPorTipo(Taller.class);
            List<Curso> cursos = evento.filtrarActividadesPorTipo(Curso.class);
                    
            //Mostrar cantidad de actividades por cada tipo
            System.out.println("Cantidad de Charlas: " + charlas.size());
            System.out.println("Cantidad de Talleres: " + talleres.size());
            System.out.println("Cantidad de Cursos: " + cursos.size());
                    
            //Calcular y mostrar costo de materiales por cada tipo (uso del wildcard ? extends Actividad)
            System.out.println("\nCosto de materiales para Charlas: $" + evento.calcularCostoMateriales(charlas));
            System.out.println("Costo de materiales para Talleres: $" + evento.calcularCostoMateriales(talleres));
            System.out.println("Costo de materiales para Cursos: $" + evento.calcularCostoMateriales(cursos));
            System.out.println("Costo TOTAL de materiales del Evento: $" + evento.calcularCostoMateriales(evento.getActividades()));
            System.out.println("================================================================");


            EnvioTicketsThread hiloEnvio = new EnvioTicketsThread(evento);
            //Se inicia el nuevo hilo
            hiloEnvio.start();
            try {
            for (int i = 1; i <= 3; i++) {
                System.out.println("[HILO] Reporte en pantalla (" + i + "/3):");
                System.out.println("  -> Evento: " + evento.getTitulo());
                for (Actividad act : evento.getActividades()) {
                    System.out.println("     * " + act.getTitulo() + " - Inscriptos: " + act.getInscripciones().size());
                }
            }

            // Esperar a que el hilo secundario finalice antes de cerrar
            hiloEnvio.join();

        } catch (InterruptedException e) {
            System.err.println("Error en el hilo principal: " + e.getMessage());
            Thread.currentThread().interrupt();
        }

        try { //bloque de intento para persistir el evento.
            if (evento.persistirEvento())
                System.out.println("Se grabo correctamente");

            EventoUniversitario copiaDesdeArchivo =
                    evento.recuperarEvento(evento.getId());

            System.out.println("\n\nDATOS DEL EVENTO");
            evento.mostrarDatos();

            System.out.println("\nDatos del evento recuperado desde archivo:");
            copiaDesdeArchivo.mostrarDatos();

        } catch (FileNotFoundException e) {
            System.out.println(
                    "Error 01: No se encontró el archivo del evento: "
                            + e.getMessage()
            );

        } catch (ClassNotFoundException e) {
            System.out.println("No fue posible reconstruir el objeto almacenado: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Se produjo un error de entrada/salida: " + e.getMessage());
        }
        String rtaOtroEvento = scanner.nextLine().trim().toLowerCase();
        continuar = (rtaOtroEvento.equals("s") || rtaOtroEvento.equals("si") || rtaOtroEvento.equals("sí"));
        // Incrementar ID para el próximo evento si se decide continuar
        if (continuar) {
            id++;
        }
        /* Se muestra la cantidad total de eventos creados */
        System.out.println("\n\nTOTAL DE EVENTOS CREADOS: " + EventoUniversitario.getCantidadEventos());
        }
    }
}