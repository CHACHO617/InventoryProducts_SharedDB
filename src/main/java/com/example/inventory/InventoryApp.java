package com.example.inventory;

import java.util.Scanner;

public class InventoryApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        InventoryService service = new InventoryService();

        while (true) {
            System.out.println("\n=== Menú Inventario ===");
            System.out.println("1. Ver productos");
            System.out.println("2. Agregar producto");
            System.out.println("3. Actualizar producto");
            System.out.println("4. Eliminar producto");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opción: ");

            String opcion = scanner.nextLine();

            switch (opcion) {
                case "1":
                    service.viewAllProducts();
                    break;
                case "2":
                    service.addProduct(scanner);
                    break;
                case "3":
                    service.updateProduct(scanner);
                    break;
                case "4":
                    service.deleteProduct(scanner);
                    break;
                case "0":
                    System.out.println("👋 Hasta luego.");
                    return;
                default:
                    System.out.println("❌ Opción inválida.");
            }
        }
    }
}
