package edu.utn;

public class Main {
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════╗");
        System.out.println("║  SISTEMA DE GESTIÓN DE BIBLIOTECA - TPI Programación 2 ║");
        System.out.println("╚════════════════════════════════════════════════════════╝");
        System.out.println();
        
        try {
            AppMenu menu = new AppMenu();
            menu.mostrarMenuPrincipal();
        } catch (Exception e) {
            System.err.println("\n❌ Error fatal: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("\n╔════════════════════════════════════════════════════════╗");
        System.out.println("║              Sistema finalizado correctamente          ║");
        System.out.println("╚════════════════════════════════════════════════════════╝");
    }
}