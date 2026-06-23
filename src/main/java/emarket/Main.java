package emarket;

import emarket.auth.AutenticacionService;
import emarket.auth.Cliente;
import emarket.carrito.Carrito;
import emarket.carrito.ItemCarrito;
import emarket.catalogo.Categoria;
import emarket.catalogo.CatalogoService;
import emarket.catalogo.ComponenteCatalogo;
import emarket.catalogo.Producto;
import emarket.estado.*;
import emarket.facade.LibreriaFacade;
import emarket.notificacion.CanalNotificacion;
import emarket.pago.TipoPago;
import emarket.pedido.ItemPedido;
import emarket.pedido.Pedido;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static LibreriaFacade facade;
    private static Scanner scanner;

    public static void main(String[] args) {
        facade  = new LibreriaFacade();
        scanner = new Scanner(System.in);

        precargarDatos();

        boolean corriendo = true;
        while (corriendo) {
            limpiarPantalla();
            AutenticacionService auth = facade.getAutService();

            if (!auth.estaAutenticado()) {
                corriendo = menuSinSesion();
            } else if (auth.getClienteActual() != null) {
                corriendo = menuCliente(auth.getClienteActual());
            } else {
                corriendo = menuAdmin(auth.getUsuarioActual().getUsername());
            }
        }

        System.out.println("\n  Gracias por usar EMarket. ¡Hasta pronto!");
        scanner.close();
    }

    // ══════════════════════════════════════════════════════════════════════════
    // MENÚS
    // ══════════════════════════════════════════════════════════════════════════

    private static boolean menuSinSesion() {
        cabecera("EMARKET v1.0");
        System.out.println("  1. Iniciar sesión");
        System.out.println("  2. Registrarse como cliente");
        System.out.println("  3. Registrarse como administrador");
        System.out.println("  0. Salir");
        separador();

        switch (leerOpcion()) {
            case 1 -> accionIniciarSesion();
            case 2 -> accionRegistrarCliente();
            case 3 -> accionRegistrarAdmin();
            case 0 -> { return false; }
            default -> error("Opción inválida.");
        }
        pausar();
        return true;
    }

    private static boolean menuCliente(Cliente cliente) {
        cabecera("EMARKET — " + cliente.getUsername() + " [CLIENTE]");
        System.out.println("  1. Ver catálogo");
        System.out.println("  2. Buscar producto por ID");
        System.out.println("  3. Agregar producto al carrito");
        System.out.println("  4. Ver mi carrito");
        System.out.println("  5. Confirmar compra");
        System.out.println("  6. Mis pedidos");
        System.out.println("  7. Cerrar sesión");
        System.out.println("  0. Salir");
        separador();

        switch (leerOpcion()) {
            case 1 -> accionVerCatalogo();
            case 2 -> accionBuscarProducto();
            case 3 -> accionAgregarAlCarrito();
            case 4 -> accionVerCarrito(cliente);
            case 5 -> accionConfirmarCompra();
            case 6 -> accionMisPedidos();
            case 7 -> { facade.cerrarSesion(); ok("Sesión cerrada."); }
            case 0 -> { return false; }
            default -> error("Opción inválida.");
        }
        pausar();
        return true;
    }

    private static boolean menuAdmin(String username) {
        cabecera("EMARKET — " + username + " [ADMINISTRADOR]");
        System.out.println("  1. Ver catálogo");
        System.out.println("  2. Buscar producto por ID");
        System.out.println("  3. Agregar producto");
        System.out.println("  4. Agregar categoría");
        System.out.println("  5. Modificar stock de producto");
        System.out.println("  8. Ver todos los pedidos");
        System.out.println("  9. Actualizar estado de pedido");
        System.out.println("  7. Cerrar sesión");
        System.out.println("  0. Salir");
        separador();

        switch (leerOpcion()) {
            case 1 -> accionVerCatalogo();
            case 2 -> accionBuscarProducto();
            case 3 -> accionAgregarProducto();
            case 4 -> accionAgregarCategoria();
            case 5 -> accionModificarStock();
            case 8 -> accionVerTodosLosPedidos();
            case 9 -> accionActualizarEstadoPedido();
            case 7 -> { facade.cerrarSesion(); ok("Sesión cerrada."); }
            case 0 -> { return false; }
            default -> error("Opción inválida.");
        }
        pausar();
        return true;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // ACCIONES — AUTH
    // ══════════════════════════════════════════════════════════════════════════

    private static void accionIniciarSesion() {
        System.out.print("  Usuario    : ");
        String username = scanner.nextLine().trim();
        String pass = leerPassword("  Contraseña : ");

        if (facade.iniciarSesion(username, pass)) {
            ok("Sesión iniciada. ¡Bienvenido/a, " + username + "!");
        } else {
            error("Usuario o contraseña incorrectos.");
        }
    }

    private static void accionRegistrarCliente() {
        System.out.print("  Usuario            : ");
        String username = scanner.nextLine().trim();
        String pass = pedirPasswordConConfirmacion();
        System.out.print("  Dirección          : ");
        String direccion = scanner.nextLine().trim();
        String email = pedirEmail();
        String telefono = pedirTelefono();
        // El token push lo genera el sistema operativo del dispositivo en una app real;
        // acá lo derivamos del username para mantener el canal PUSH funcional en la demo.
        String token = "TOKEN_" + username.toUpperCase();
        System.out.println("  Canales disponibles: EMAIL, SMS, PUSH");
        System.out.print("  Canales preferidos  (separados por coma): ");
        List<CanalNotificacion> canales = parsearCanales(scanner.nextLine().trim());

        try {
            facade.registrarClienteCompleto(username, pass, direccion, email, telefono, token, canales);
            ok("Cliente '" + username + "' registrado correctamente.");
        } catch (Exception e) {
            error("Error al registrar: " + e.getMessage());
        }
    }

    private static void accionRegistrarAdmin() {
        System.out.print("  Usuario    : ");
        String username = scanner.nextLine().trim();
        String pass = pedirPasswordConConfirmacion();

        try {
            facade.registrarAdministrador(username, pass);
            ok("Administrador '" + username + "' registrado correctamente.");
        } catch (Exception e) {
            error("Error al registrar: " + e.getMessage());
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // ACCIONES — CATÁLOGO
    // ══════════════════════════════════════════════════════════════════════════

    private static void accionVerCatalogo() {
        limpiarPantalla();
        cabecera("CATÁLOGO DE LIBROS");
        try {
            imprimirCatalogo(facade.listarCatalogo(), "  ");
        } catch (Exception e) {
            error(e.getMessage());
        }
    }

    private static void accionBuscarProducto() {
        System.out.print("  ID del producto: ");
        int id = leerEntero();
        if (id < 0) { error("ID inválido."); return; }

        try {
            Producto p = facade.buscarProducto(id);
            if (p == null) {
                error("No se encontró ningún producto con ID " + id + ".");
            } else {
                System.out.printf("  id=%-3d %-22s $%-8.2f stock=%d%n",
                        p.getId(), p.getNombre(), p.getPrecio(), p.getStock());
            }
        } catch (Exception e) {
            error(e.getMessage());
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // ACCIONES — CARRITO
    // ══════════════════════════════════════════════════════════════════════════

    private static void accionAgregarAlCarrito() {
        System.out.print("  ID del producto : ");
        int id = leerEntero();
        if (id < 0) { error("ID inválido."); return; }

        System.out.print("  Cantidad        : ");
        int cantidad = leerEntero();
        if (cantidad <= 0) { error("La cantidad debe ser mayor a 0."); return; }

        try {
            facade.agregarProductoAlCarrito(id, cantidad);
            ok("Producto agregado al carrito.");
        } catch (IllegalStateException e) {
            error(e.getMessage());
        }
    }

    private static void accionVerCarrito(Cliente cliente) {
        limpiarPantalla();
        cabecera("MI CARRITO");

        Carrito carrito = cliente.getCarrito();
        List<ItemCarrito> items = carrito.getItems();

        if (items.isEmpty()) {
            System.out.println("  El carrito está vacío.");
            return;
        }

        System.out.printf("  %-22s %6s   %10s   %10s%n", "Producto", "Cant.", "Precio unit.", "Subtotal");
        separador();
        for (ItemCarrito item : items) {
            System.out.printf("  %-22s %6d   $%9.2f   $%9.2f%n",
                    item.getProducto().getNombre(),
                    item.getCantidad(),
                    item.getProducto().getPrecio(),
                    item.getSubtotal());
        }
        separador();
        System.out.printf("  %-22s %6s   %10s   $%9.2f%n",
                "SUBTOTAL", "", "", carrito.calcularTotal());
        double total = carrito.calcularTotal() * (1 + 0.21);
        System.out.printf("  %-22s %6s   %10s   $%9.2f%n",
                "TOTAL (c/ IVA 21%)", "", "", total);
    }

    private static void accionConfirmarCompra() {
        System.out.println("  Métodos de pago:");
        System.out.println("    1. Tarjeta de crédito");
        System.out.println("    2. PayPal");
        System.out.println("    3. Transferencia bancaria");
        System.out.print("  Elegí un método: ");
        int metodo = leerEntero();

        TipoPago tipoPago = switch (metodo) {
            case 1 -> TipoPago.TARJETA_CREDITO;
            case 2 -> TipoPago.PAYPAL;
            case 3 -> TipoPago.TRANSFERENCIA;
            default -> null;
        };

        if (tipoPago == null) {
            error("Método de pago inválido.");
            return;
        }

        System.out.println();
        try {
            facade.confirmarCompra(tipoPago);
            ok("¡Compra confirmada con éxito!");
        } catch (IllegalStateException e) {
            error(e.getMessage());
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // ACCIONES — PEDIDOS (CLIENTE)
    // ══════════════════════════════════════════════════════════════════════════

    private static void accionMisPedidos() {
        limpiarPantalla();
        cabecera("MIS PEDIDOS");

        try {
            List<Pedido> pedidos = facade.listarPedidosCliente();
            if (pedidos.isEmpty()) {
                System.out.println("  No tenés pedidos aún.");
                return;
            }
            for (Pedido p : pedidos) {
                imprimirPedido(p);
            }
        } catch (Exception e) {
            error(e.getMessage());
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // ACCIONES — PEDIDOS (ADMIN)
    // ══════════════════════════════════════════════════════════════════════════

    private static void accionVerTodosLosPedidos() {
        limpiarPantalla();
        cabecera("TODOS LOS PEDIDOS");

        try {
            List<Pedido> pedidos = facade.listarTodosLosPedidos();
            if (pedidos.isEmpty()) {
                System.out.println("  No hay pedidos registrados en el sistema.");
                return;
            }
            for (Pedido p : pedidos) {
                imprimirPedido(p);
            }
        } catch (Exception e) {
            error(e.getMessage());
        }
    }

    private static void accionActualizarEstadoPedido() {
        System.out.print("  ID del pedido a actualizar: ");
        int id = leerEntero();
        if (id < 0) { error("ID inválido."); return; }

        System.out.println("  Estados disponibles:");
        System.out.println("    1. PENDIENTE");
        System.out.println("    2. PAGADO");
        System.out.println("    3. ENVIADO");
        System.out.println("    4. ENTREGADO");
        System.out.print("  Nuevo estado: ");

        EstadoPedido nuevoEstado = switch (leerEntero()) {
            case 1 -> new EstadoPendiente();
            case 2 -> new EstadoPagado();
            case 3 -> new EstadoEnviado();
            case 4 -> new EstadoEntregado();
            default -> null;
        };

        if (nuevoEstado == null) {
            error("Estado inválido.");
            return;
        }

        System.out.println();
        try {
            facade.actualizarEstadoPedido(id, nuevoEstado);
            ok("Estado del pedido #" + id + " actualizado a: " + nuevoEstado.getNombre());
        } catch (IllegalStateException e) {
            error(e.getMessage());
        }
    }

    private static void accionAgregarProducto() {
        System.out.print("  Nombre de la categoría : ");
        String cat = scanner.nextLine().trim();
        System.out.print("  ID del producto        : ");
        int id = leerEntero();
        System.out.print("  Nombre del producto    : ");
        String nombre = scanner.nextLine().trim();
        System.out.print("  Precio                 : ");
        double precio;
        try { precio = Double.parseDouble(scanner.nextLine().trim()); }
        catch (NumberFormatException e) { error("Precio inválido."); return; }
        System.out.print("  Stock inicial          : ");
        int stock = leerEntero();
        if (stock < 0) { error("Stock inválido."); return; }
        try {
            facade.agregarProducto(cat, id, nombre, precio, stock);
            ok("Producto '" + nombre + "' agregado a '" + cat + "'.");
        } catch (Exception e) {
            error(e.getMessage());
        }
    }

    private static void accionAgregarCategoria() {
        System.out.print("  Nombre de la nueva categoría : ");
        String nombre = scanner.nextLine().trim();
        System.out.print("  Categoría padre              : ");
        String padre = scanner.nextLine().trim();
        try {
            facade.agregarCategoria(nombre, padre);
            ok("Categoría '" + nombre + "' agregada bajo '" + padre + "'.");
        } catch (Exception e) {
            error(e.getMessage());
        }
    }

    private static void accionModificarStock() {
        System.out.print("  ID del producto : ");
        int id = leerEntero();
        if (id < 0) { error("ID inválido."); return; }
        System.out.print("  Nuevo stock     : ");
        int stock = leerEntero();
        if (stock < 0) { error("Stock inválido."); return; }
        try {
            facade.modificarStock(id, stock);
            ok("Stock del producto #" + id + " actualizado a " + stock + ".");
        } catch (Exception e) {
            error(e.getMessage());
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // PRECARGA DE DATOS DE EJEMPLO
    // ══════════════════════════════════════════════════════════════════════════

    private static void precargarDatos() {
        CatalogoService cat = facade.getCatService();

        Categoria raiz = new Categoria("Catálogo de Libros");
        cat.setCatalogoRaiz(raiz);

        Categoria ficcion = cat.crearCategoria("Ficción", raiz);
        cat.agregarProducto(ficcion, new Producto(1, "Cien años de soledad",  2500.0,  8));
        cat.agregarProducto(ficcion, new Producto(2, "El principito",         1800.0, 12));
        cat.agregarProducto(ficcion, new Producto(3, "1984",                  2200.0,  6));

        Categoria tecnicos = cat.crearCategoria("Técnicos", raiz);
        cat.agregarProducto(tecnicos, new Producto(4, "Clean Code",           4500.0,  5));
        cat.agregarProducto(tecnicos, new Producto(5, "Design Patterns",      5000.0,  3));

        Categoria historia = cat.crearCategoria("Historia", raiz);
        cat.agregarProducto(historia, new Producto(6, "Sapiens",              3200.0, 10));
        cat.agregarProducto(historia, new Producto(7, "El arte de la guerra", 1500.0, 15));

        // Usuarios precargados para la demo
        facade.registrarClienteCompleto(
                "juan", "1234", "Av. Corrientes 1234, CABA",
                "juan@email.com", "1155551234", "TOKEN_JUAN",
                Arrays.asList(CanalNotificacion.EMAIL, CanalNotificacion.PUSH));

        facade.registrarAdministrador("admin", "admin123");
    }

    // ══════════════════════════════════════════════════════════════════════════
    // HELPERS DE DISPLAY
    // ══════════════════════════════════════════════════════════════════════════

    private static void imprimirCatalogo(ComponenteCatalogo componente, String prefijo) {
        if (componente instanceof Categoria cat) {
            System.out.printf("%s[%s]  precio total: $%.2f  |  stock total: %d%n",
                    prefijo, cat.getNombre(), cat.getPrecio(), cat.getStock());
            for (ComponenteCatalogo hijo : cat.getHijos()) {
                imprimirCatalogo(hijo, prefijo + "  ");
            }
        } else if (componente instanceof Producto p) {
            System.out.printf("%sid=%-3d  %-22s  $%-8.2f  stock=%d%n",
                    prefijo, p.getId(), p.getNombre(), p.getPrecio(), p.getStock());
        }
    }

    private static void imprimirPedido(Pedido p) {
        System.out.printf("  Pedido #%-3d | Estado: %-10s | Total: $%.2f%n",
                p.getId(), p.getEstado().getNombre(), p.getTotal());
        for (ItemPedido item : p.getItems()) {
            System.out.printf("    - %-22s x%-3d @ $%-8.2f = $%.2f%n",
                    item.getNombreProducto(), item.getCantidad(),
                    item.getPrecioUnitario(), item.getSubtotal());
        }
        System.out.println("  " + "─".repeat(55));
    }

    // ══════════════════════════════════════════════════════════════════════════
    // HELPERS DE INPUT
    // ══════════════════════════════════════════════════════════════════════════

    private static int leerOpcion() {
        System.out.print("  Opción > ");
        return leerEntero();
    }

    private static int leerEntero() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    // Lee una contraseña ocultando los caracteres con ANSI conceal (\033[8m).
    // Siempre usa el scanner (conectado a stdin), así funciona tanto en terminal
    // real como con stdin redirigido. El ocultamiento visual es "best-effort":
    // funciona en Terminal/iTerm2/zsh; en IDEs los caracteres pueden verse igual.
    private static String leerPassword(String prompt) {
        System.out.print(prompt + "\033[8m"); // activar modo oculto
        String pass;
        try {
            pass = scanner.nextLine().trim();
        } catch (java.util.NoSuchElementException e) {
            pass = "";
        } finally {
            System.out.print("\033[0m"); // restaurar visibilidad
            System.out.flush();
        }
        return pass;
    }

    // Pide la contraseña dos veces y repite hasta que coincidan.
    private static String pedirPasswordConConfirmacion() {
        while (true) {
            String p1 = leerPassword("  Contraseña         : ");
            if (p1.isEmpty()) {
                error("La contraseña no puede estar vacía.");
                continue;
            }
            String p2 = leerPassword("  Repetir contraseña : ");
            if (p1.equals(p2)) return p1;
            error("Las contraseñas no coinciden. Intentá de nuevo.");
        }
    }

    // Pide un email y repite hasta que tenga texto antes y después del '@'.
    private static String pedirEmail() {
        while (true) {
            System.out.print("  Email              : ");
            String email = scanner.nextLine().trim();
            int at = email.indexOf('@');
            if (at > 0 && at < email.length() - 1) return email;
            error("El email debe contener '@' con texto antes y después. Ej: nombre@dominio.com");
        }
    }

    // Pide un teléfono y repite hasta que tenga exactamente 10 dígitos numéricos.
    private static String pedirTelefono() {
        while (true) {
            System.out.print("  Teléfono (10 dígitos): ");
            String tel = scanner.nextLine().trim();
            if (tel.matches("\\d{10}")) return tel;
            error("El teléfono debe tener exactamente 10 dígitos numéricos. Ej: 1155551234");
        }
    }

    private static List<CanalNotificacion> parsearCanales(String input) {
        List<CanalNotificacion> canales = new ArrayList<>();
        for (String parte : input.toUpperCase().split(",")) {
            try {
                canales.add(CanalNotificacion.valueOf(parte.trim()));
            } catch (IllegalArgumentException ignored) { }
        }
        // Si no se eligió ninguno válido, usar EMAIL por defecto
        if (canales.isEmpty()) canales.add(CanalNotificacion.EMAIL);
        return canales;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // HELPERS DE UI
    // ══════════════════════════════════════════════════════════════════════════

    private static void limpiarPantalla() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private static void cabecera(String titulo) {
        System.out.println("╔══════════════════════════════════╗");
        // Centrar el título dentro de 34 caracteres
        int padding = Math.max(0, (34 - titulo.length()) / 2);
        System.out.println("║" + " ".repeat(padding) + titulo
                + " ".repeat(Math.max(0, 34 - padding - titulo.length())) + "║");
        System.out.println("╚══════════════════════════════════╝");
        System.out.println();
    }

    private static void separador() {
        System.out.println("  " + "─".repeat(40));
    }

    private static void ok(String msg) {
        System.out.println("  ✓ " + msg);
    }

    private static void error(String msg) {
        System.out.println("  ✗ " + msg);
    }

    private static void pausar() {
        System.out.println();
        System.out.print("  [Enter para continuar] ");
        scanner.nextLine();
    }
}
