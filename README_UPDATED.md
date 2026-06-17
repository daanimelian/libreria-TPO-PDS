# EMarket — Librería TPO PDS

Sistema de e-commerce de librería desarrollado en Java 17 como Trabajo Práctico Obligatorio para la materia Análisis y Diseño de Sistemas. Implementa un catálogo jerárquico de libros, carrito de compras, gestión completa del ciclo de vida de pedidos y notificaciones multicanal, aplicando patrones de diseño GoF y principios SOLID.

---

## Descripción del negocio

EMarket es una plataforma de venta de libros en línea que permite:

- **Clientes**: explorar el catálogo por categorías y subcategorías, agregar libros al carrito, confirmar compras con distintos medios de pago y consultar el historial de pedidos.
- **Administradores**: visualizar todos los pedidos del sistema y avanzar sus estados a lo largo del ciclo de entrega.

Los libros están organizados en categorías jerárquicas (ej: Ficción → Fantasía → título) con profundidad arbitraria.

---

## Arquitectura general del sistema

El sistema se organiza en capas con responsabilidades bien definidas:

```
┌─────────────────────────────────────────────────────┐
│                  PRESENTACIÓN                        │
│  Main.java (consola)   │   ui/ (Swing, uso interno)  │
└─────────────────┬───────────────────────────────────┘
                  │ (solo usa)
┌─────────────────▼───────────────────────────────────┐
│                   FACADE                             │
│              LibreriaFacade                          │
│  (Composition Root + control de acceso por rol)      │
└──┬──────────┬───────────┬──────────────┬────────────┘
   │          │           │              │
┌──▼───┐  ┌──▼────┐  ┌───▼────┐  ┌─────▼──────┐
│ Auth │  │Catál. │  │Carrito │  │  Pedidos   │
│Serv. │  │Serv.  │  │Serv.   │  │  Service   │
└──┬───┘  └──┬────┘  └───┬────┘  └─────┬──────┘
   │         │           │             │
   └─────────┴─────────────────────────┘
                  │ (usan)
┌─────────────────▼───────────────────────────────────┐
│              REPOSITORIOS (DIP)                      │
│  IRepositorio*  ◄──── RepositorioFactory             │
│       ▲                    ▲                         │
│   InMemory*          Jdbc*                           │
└─────────────────────────────────────────────────────┘
```

**Paquetes y responsabilidades:**

| Paquete | Responsabilidad |
|---|---|
| `emarket` | `Main.java` — interfaz de consola; único punto que usa `LibreriaFacade` |
| `emarket.facade` | `LibreriaFacade` — Facade + Composition Root |
| `emarket.auth` | Entidades `Usuario`, `Cliente`, `Administrador`; `AutenticacionService` |
| `emarket.catalogo` | Patrón Composite: `ComponenteCatalogo`, `Categoria`, `Producto`; `CatalogoService` |
| `emarket.carrito` | `Carrito`, `ItemCarrito`, `CarritoService` |
| `emarket.pedido` | `Pedido` (State + Observer), `ItemPedido`, `PedidoService` |
| `emarket.estado` | Patrón State: `EstadoPedido`, `EstadoPendiente`, `EstadoPagado`, `EstadoEnviado`, `EstadoEntregado`, `EstadoPedidoFactory` |
| `emarket.pago` | Patrón Strategy: `MetodoPago`, implementaciones, `MetodoPagoFactory`, `ProcesadorPagos`, `DatosPago` |
| `emarket.notificacion` | Patrón Observer + Strategy: interfaces, implementaciones, `ManagerNotificaciones`, `EstrategiaNotificacion*` |
| `emarket.config` | `ConfiguracionSistema` (Singleton), `DataSourceProvider` (Singleton) |
| `emarket.repositorio` | Interfaces `IRepositorio*` y patrón Abstract Factory |
| `emarket.repositorio.inmemory` | Implementaciones en memoria (sin dependencias externas) |
| `emarket.repositorio.jdbc` | Implementaciones JDBC para PostgreSQL |
| `emarket.util` | `Validaciones` — utilidades estáticas de formato |

---

## Patrones de diseño implementados

### 1. Strategy — Métodos de pago

**Problema resuelto**: el sistema admite múltiples formas de cobro; agregar una nueva no debe modificar la lógica de procesamiento.

**Implementación**:

| Rol | Clase |
|---|---|
| Strategy (interfaz) | `MetodoPago` — define `pagar(double monto): boolean` |
| Estrategias concretas | `TarjetaDeCredito`, `PayPal`, `MercadoPago`, `Transferencia` |
| Contexto | `ProcesadorPagos` — llama `metodo.pagar()` sin conocer el tipo concreto |
| Factory | `MetodoPagoFactory` — crea la instancia correcta a partir de `TipoPago` + `DatosPago` |

**Flujo**: el usuario elige un `TipoPago` en `Main` → `Main` recolecta los datos y construye un `DatosPago` → `LibreriaFacade.confirmarCompra()` → `ProcesadorPagos.procesarCobro()` → `MetodoPagoFactory.crearMetodoPago()` → `metodo.pagar()`.

---

### 2. Strategy — Canales de notificación

Mismo patrón aplicado al envío de mensajes, independizando el "qué notificar" del "cómo enviarlo".

| Rol | Clase |
|---|---|
| Strategy (interfaz) | `EstrategiaNotificacion` — define `enviarMensaje(mensaje, destinatario)` |
| Estrategias concretas | `EstrategiaNotificacionEmail`, `EstrategiaNotificacionSMS`, `EstrategiaNotificacionPush` |
| Contexto | `ManagerNotificaciones` — itera los canales preferidos del cliente y despacha |

---

### 3. Observer — Notificaciones de cambio de estado

**Problema resuelto**: al cambiar el estado de un pedido, el cliente debe recibir una notificación sin que `Pedido` conozca nada del sistema de mensajería.

| Rol | Clase |
|---|---|
| Subject (interfaz) | `SujetoObservable` |
| Subject concreto | `Pedido` — llama `notificarCambios()` en `setEstado()` |
| Observer (interfaz) | `ObservadorNotificacion` — define `actualizar(EventoNotificacion)` |
| Observer concreto | `ManagerNotificaciones` |
| DTO de evento | `EventoNotificacion` — evita dependencia directa Observer → Pedido |

---

### 4. State — Ciclo de vida del pedido

**Problema resuelto**: el comportamiento del pedido varía según su estado; sin el patrón habría cadenas de `if/switch` en el contexto.

| Rol | Clase |
|---|---|
| State (interfaz) | `EstadoPedido` — define `procesar(Pedido)` y `getNombre()` |
| Contexto | `Pedido` — delega `avanzarEstado()` al estado actual |
| Estados | `EstadoPendiente` → `EstadoPagado` → `EstadoEnviado` → `EstadoEntregado` (terminal) |

Cada estado conoce su sucesor y lo asigna directamente. `EstadoEntregado` lanza `IllegalStateException` al intentar avanzar.

---

### 5. Composite — Catálogo jerárquico

**Problema resuelto**: las categorías pueden contener subcategorías y productos con profundidad arbitraria; el código cliente los trata de forma uniforme.

| Rol | Clase |
|---|---|
| Component (interfaz) | `ComponenteCatalogo` — `getNombre()`, `getPrecio()`, `getStock()` |
| Composite | `Categoria` — agrega hijos; `getPrecio()` y `getStock()` retornan sumas recursivas |
| Leaf | `Producto` — retorna valores unitarios |

> **Nota de diseño**: `Categoria.getPrecio()` devuelve el precio *agregado* (suma del subárbol) mientras que `Producto.getPrecio()` devuelve el precio *unitario*. Esta diferencia semántica está documentada en los Javadoc de la interfaz. El código que consume nodos mixtos debe tener en cuenta esta distinción.

---

### 6. Singleton — Configuración del sistema

`ConfiguracionSistema` provee parámetros globales (tasa de IVA, moneda) accesibles desde cualquier parte sin pasar dependencias. `getInstance()` es `synchronized` para garantizar seguridad en entornos multihilo.

`DataSourceProvider` usa el mismo patrón para gestionar el pool de conexiones HikariCP.

---

### 7. Facade — Punto de entrada único

`LibreriaFacade` encapsula todos los subsistemas. La UI solo llama métodos de la Facade; nunca instancia servicios internos directamente. Además actúa como *Composition Root*: recibe una `RepositorioFactory` y crea todos los servicios con sus dependencias correctas.

---

### 8. Abstract Factory — Repositorios duales

`RepositorioFactory` (interfaz) tiene dos implementaciones:

- `InMemoryRepositorioFactory`: para demo y tests, sin dependencias externas.
- `JdbcRepositorioFactory`: para producción con PostgreSQL.

El modo se elige con el argumento `--jdbc` al ejecutar el JAR.

---

### 9. Builder — DatosPago

`DatosPago` usa factory methods estáticos (`paraTarjeta`, `paraPayPal`, etc.) sobre un Builder interno para construir el objeto con solo los campos relevantes al tipo de pago elegido.

---

### 10. Template Method — JdbcRepositorioBase

`JdbcRepositorioBase` centraliza la apertura de conexiones, manejo de transacciones (commit/rollback) y cierre. Los repositorios JDBC concretos solo aportan el SQL específico de cada operación.

---

## Principios SOLID aplicados

| Principio | Aplicación concreta |
|---|---|
| **S — SRP** | Cada clase tiene una única razón de cambio. `CarritoService` gestiona el carrito, `AutenticacionService` gestiona usuarios, `PedidoService` orquesta pedidos. La UI (`Main`) es la única clase que lee input del usuario, incluyendo los datos de pago. |
| **O — OCP** | Agregar un nuevo método de pago requiere solo crear una clase que implemente `MetodoPago` y registrarla en `MetodoPagoFactory`. Lo mismo aplica a nuevos canales (`EstrategiaNotificacion`) y nuevos estados (`EstadoPedido`). |
| **L — LSP** | Cualquier `MetodoPago`, `EstadoPedido` o `EstrategiaNotificacion` es sustituible por otro sin romper el contexto. La tensión LSP en `ComponenteCatalogo` (semántica agregada vs. unitaria) está documentada explícitamente. |
| **I — ISP** | Las interfaces son pequeñas y enfocadas: `MetodoPago` tiene solo `pagar()`, `EstadoPedido` solo `procesar()` y `getNombre()`, `ObservadorNotificacion` solo `actualizar()`. |
| **D — DIP** | Los módulos de alto nivel dependen de abstracciones. `ProcesadorPagos` depende de `MetodoPago`, `Pedido` de `EstadoPedido`, todos los servicios de `IRepositorio*`. Las implementaciones concretas se inyectan vía factory. |

---

## Instrucciones para compilar y ejecutar

### Requisitos previos

- Java 17+
- Maven 3.x
- Docker (solo para modo JDBC)

### Modo en memoria (sin base de datos)

```bash
mvn package -DskipTests
java -jar target/libreria-TPO-PDS-1.0-SNAPSHOT.jar
```

### Modo PostgreSQL (JDBC)

```bash
# 1. Levantar la base de datos
docker-compose up -d

# 2. Copiar y configurar credenciales
cp src/main/resources/db.properties.example src/main/resources/db.properties

# 3. Compilar y ejecutar con JDBC
mvn package -DskipTests
java -jar target/libreria-TPO-PDS-1.0-SNAPSHOT.jar --jdbc
```

### Ejecutar tests

```bash
mvn test
```

### Usuarios de demo (precargados al iniciar)

| Usuario | Contraseña | Rol | Canales de notificación |
|---|---|---|---|
| `juan` | `juan1234` | Cliente | EMAIL, PUSH |
| `admin` | `admin123` | Administrador | — |

---

## Decisiones de diseño relevantes

### Separación estricta UI / dominio
Los métodos de pago (`TarjetaDeCredito`, `PayPal`, etc.) son estrategias de **cobro** puro; no saben nada de cómo se recolectó el input del usuario. La lógica de lectura de consola (validar formato de tarjeta, pedir CBU, etc.) reside exclusivamente en `Main.java`, respetando SRP.

### ItemPedido como snapshot inmutable
El pedido no referencia `Producto` directamente. Al confirmar la compra, se generan `ItemPedido` con copia del nombre y precio del instante de compra. Cambios futuros en precio o eliminaciones no alteran el historial.

### Estado inicial PENDIENTE sin observer
En `PedidoService.confirmarCompra()`, el estado `PENDIENTE` se asigna **antes** de registrar el `ManagerNotificaciones` como observer. Esto evita que el cliente reciba una notificación al crearse el pedido; solo recibe la notificación del estado `PAGADO` (cuando el cobro fue exitoso).

### EventoNotificacion como DTO de desacoplamiento
`ObservadorNotificacion.actualizar()` recibe `EventoNotificacion` en lugar de `Pedido`. Esto elimina la dependencia del paquete `notificacion` hacia el paquete `pedido`, aplicando bajo acoplamiento (GRASP).

### Dual-mode de persistencia
El mismo código de dominio funciona sin cambios en memoria o con PostgreSQL. La selección se hace en el `main()` al instanciar la `RepositorioFactory` correcta. Esta es la aplicación más explícita del principio DIP en el proyecto.
