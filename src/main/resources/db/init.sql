-- =============================================================================
-- Schema: Librería TPO-PDS
-- Estrategia de herencia: Table-Per-Subclass
--   → usuarios: tabla base con discriminador 'tipo'
--   → clientes / administradores: tablas hija con datos específicos
-- =============================================================================

-- -----------------------------------------------------------------------------
-- USUARIOS (tabla base)
-- Discriminador 'tipo' evita un JOIN extra cuando solo se necesita autenticar
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS usuarios (
    username      VARCHAR(50)  PRIMARY KEY,
    password_hash VARCHAR(64)  NOT NULL,
    tipo          VARCHAR(20)  NOT NULL CHECK (tipo IN ('CLIENTE', 'ADMINISTRADOR'))
);

-- -----------------------------------------------------------------------------
-- CLIENTES (tabla hija de usuarios)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS clientes (
    username          VARCHAR(50)  PRIMARY KEY
                                   REFERENCES usuarios(username) ON DELETE CASCADE,
    direccion         VARCHAR(200),
    email             VARCHAR(100),
    telefono          VARCHAR(20),
    token_dispositivo VARCHAR(200)
);

-- -----------------------------------------------------------------------------
-- ADMINISTRADORES (tabla hija de usuarios)
-- El legajo se deriva del username en el dominio; lo persistimos igual
-- para no recalcularlo cada vez que se lee de la DB
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS administradores (
    username  VARCHAR(50)  PRIMARY KEY
                           REFERENCES usuarios(username) ON DELETE CASCADE,
    legajo    VARCHAR(50)  NOT NULL
);

-- -----------------------------------------------------------------------------
-- CANALES DE NOTIFICACIÓN POR CLIENTE
-- Representa List<CanalNotificacion> de Cliente
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS canales_notificacion (
    username  VARCHAR(50)  REFERENCES clientes(username) ON DELETE CASCADE,
    canal     VARCHAR(20)  NOT NULL CHECK (canal IN ('EMAIL', 'SMS', 'PUSH')),
    PRIMARY KEY (username, canal)
);

-- -----------------------------------------------------------------------------
-- NOTIFICACIONES (historial persistido de Notificacion)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS notificaciones (
    id         SERIAL       PRIMARY KEY,
    username   VARCHAR(50)  NOT NULL REFERENCES clientes(username) ON DELETE CASCADE,
    mensaje    TEXT         NOT NULL,
    timestamp  TIMESTAMP    NOT NULL DEFAULT NOW(),
    visto      BOOLEAN      NOT NULL DEFAULT FALSE
);

-- -----------------------------------------------------------------------------
-- CATEGORÍAS (nodo interior del Composite)
-- padre_id = NULL indica la raíz del árbol
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS categorias (
    id        SERIAL        PRIMARY KEY,
    nombre    VARCHAR(100)  NOT NULL,
    padre_id  INTEGER       REFERENCES categorias(id) ON DELETE SET NULL
);

-- -----------------------------------------------------------------------------
-- PRODUCTOS (hoja del Composite)
-- stock tiene CHECK >= 0 para garantizar la invariante del dominio en la DB
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS productos (
    id           SERIAL          PRIMARY KEY,
    nombre       VARCHAR(200)    NOT NULL,
    precio       DECIMAL(10, 2)  NOT NULL CHECK (precio >= 0),
    stock        INTEGER         NOT NULL CHECK (stock >= 0),
    categoria_id INTEGER         REFERENCES categorias(id) ON DELETE SET NULL
);

-- -----------------------------------------------------------------------------
-- PEDIDOS
-- estado refleja el State Pattern; se mapea a/desde los objetos EstadoXxx
-- tipo_pago refleja el enum TipoPago
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS pedidos (
    id               SERIAL          PRIMARY KEY,
    username_cliente  VARCHAR(50)    NOT NULL REFERENCES clientes(username),
    estado           VARCHAR(20)    NOT NULL
                                    CHECK (estado IN ('PENDIENTE', 'PAGADO', 'ENVIADO', 'ENTREGADO'))
                                    DEFAULT 'PENDIENTE',
    tipo_pago        VARCHAR(30)    NOT NULL,
    subtotal         DECIMAL(10, 2) NOT NULL,
    total            DECIMAL(10, 2) NOT NULL,
    fecha_creacion   TIMESTAMP      NOT NULL DEFAULT NOW()
);

-- -----------------------------------------------------------------------------
-- ITEMS DE PEDIDO (snapshot inmutable al momento de la compra)
-- Sin FK a productos: el ItemPedido captura nombre/precio al momento del pedido
-- y debe sobrevivir aunque el producto sea eliminado del catálogo
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS items_pedido (
    id               SERIAL          PRIMARY KEY,
    pedido_id        INTEGER         NOT NULL REFERENCES pedidos(id) ON DELETE CASCADE,
    nombre_producto  VARCHAR(200)    NOT NULL,
    precio_unitario  DECIMAL(10, 2)  NOT NULL,
    cantidad         INTEGER         NOT NULL CHECK (cantidad > 0)
);

-- =============================================================================
-- Índices de acceso frecuente
-- =============================================================================
CREATE INDEX IF NOT EXISTS idx_pedidos_cliente   ON pedidos(username_cliente);
CREATE INDEX IF NOT EXISTS idx_items_pedido      ON items_pedido(pedido_id);
CREATE INDEX IF NOT EXISTS idx_notif_username    ON notificaciones(username);
CREATE INDEX IF NOT EXISTS idx_productos_cat     ON productos(categoria_id);
