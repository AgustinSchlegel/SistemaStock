# Sistema de Stock — Documentación técnica

Documento de referencia sobre cómo está armado el proyecto y por qué se tomó cada
decisión de diseño. Pensado para vos mismo dentro de unos meses, o para cualquiera
que se sume al proyecto.

---

## 1. Qué hace el sistema

Es una herramienta pensada para emprendedores que fabrican productos a partir de
materiales. La idea central:

- Cargás **materiales** (ej: hojas, tapas, espiral) con su costo y stock.
- Armás **productos** (ej: una libreta) diciendo qué materiales y en qué cantidad
  necesita cada uno.
- El sistema calcula solo el precio del producto en base al costo de sus materiales,
  te dice cuántas unidades podés fabricar con el stock actual, y lleva la cuenta de
  cuánto ya produjiste y cuánto vendiste.
- Todo se guarda en disco (JSON) para que no se pierda al cerrar la app.

---

## 2. Arquitectura: MVVM

El proyecto sigue el patrón **Model-View-ViewModel**. La idea de fondo es que cada
capa tenga una sola responsabilidad y no conozca los detalles de las demás:

```
┌─────────────┐      DTOs       ┌──────────────────┐     objetos de dominio    ┌─────────────┐
│    View     │ ◄─────────────► │    ViewModel      │ ◄────────────────────►   │    Model     │
│  (Swing)    │  eventos/datos  │ (StockViewModel)  │      llamadas directas   │ (Sistema,    │
│             │                 │                   │                          │  Material,   │
└─────────────┘                 └──────────────────┘                          │  Producto)   │
                                                                                └──────┬──────┘
                                                                                       │ interfaz
                                                                                       ▼
                                                                                ┌─────────────┐
                                                                                │  Repository  │
                                                                                │ (JSON, o     │
                                                                                │  memoria)    │
                                                                                └─────────────┘
```

**Por qué esta separación y no todo junto (como estaba antes):**

Antes de este refactor, `sistema.java` hacía tres cosas a la vez: reglas de negocio,
lectura/escritura de archivos, y (a medias) preparaba datos para una interfaz Swing
que ni siquiera estaba conectada. Eso generaba dos problemas concretos:

1. **No se podía testear sin tocar disco.** Cualquier test de `sistema` terminaba
   leyendo/escribiendo el mismo JSON que usaría la app real.
2. **La lógica de negocio y la interfaz gráfica iban a quedar pegoteadas.** El día
   de mañana que quisieras cambiar Swing por JavaFX, o agregar una API web, hubiera
   habido que reescribir la lógica de negocio también.

Separando en capas, cada una se puede cambiar o testear sin tocar las otras.

### Paquetes del proyecto

```
src/
  model/           → Entidades y reglas de negocio (no sabe que existe Swing ni JSON)
    Material.java
    Producto.java
    Tupla.java
    Sistema.java
  repository/      → Persistencia, detrás de una interfaz
    MaterialRepository.java      (interfaz)
    ProductoRepository.java      (interfaz)
    JsonMaterialRepository.java  (implementación real, con Gson)
    JsonProductoRepository.java  (implementación real, con Gson)
  viewmodel/       → Puente entre Model y View
    StockViewModel.java
    MaterialView.java / ProductoView.java / RequisitoView.java  (DTOs)
  InterfazGrafica/ → Vista Swing
    ventanaPrincipal.java
    MaterialesPanel.java
    ProductosPanel.java
  Main.java        → Arma la cadena de dependencias y arranca la app

test/
  model/           → Tests de negocio, usando repositorios falsos en memoria
  repository/      → Tests de la lectura/escritura JSON real
```

---

## 3. Capa Model

### `Material`

Representa un insumo con nombre, cantidad en stock y costo por unidad.

**Decisión de diseño clave — `precioUnidad` es la fuente de verdad, no `precio`:**

Originalmente `precio` (costo total del lote comprado) era el dato guardado, y
`precioUnidad` se calculaba como `precio / cantidad` cada vez que cambiaba
cualquiera de los dos. Esto rompía en un caso muy común: cuando `Producto.producir()`
consumía stock (bajando `cantidad`), el costo unitario **subía solo**, como si el
material remanente se hubiera encarecido. Ejemplo real que encontramos con un test
de punta a punta:

```
500 unidades a $4500 total → $9/unidad
Se consumen 120 para producir → quedan 380 unidades, pero seguían "costando" $4500 en
total → $11.84/unidad (¡mal!)
```

**La solución:** invertir la relación. Ahora:

- `precioUnidad` es el dato que se fija explícitamente, ya sea al crear el material o
  al llamar `actualizarPrecio()` (una compra nueva).
- `precio` (total) ya no se guarda: se **deriva** siempre como `precioUnidad * cantidad`.
- `actualizarCant()` (la que usa `producir()`/`venderProducto()` para consumir o
  reponer stock) **solo** toca `cantidad`, nunca `precioUnidad`.

Con esto, consumir stock para producir ya no altera el costo por unidad, y el precio
total del stock remanente siempre es matemáticamente coherente con lo que queda.

Efecto colateral positivo: como `precio` ya no es un campo propio, el JSON de
materiales quedó más simple (`nombre`, `cantidad`, `precioUnidad`) — un dato menos
que podía desincronizarse.

### `Producto`

Representa algo fabricable a partir de una lista de `(Material, cantidad necesaria)`.
Sabe calcular:
- `produccionPosible()`: cuántas unidades se pueden fabricar con el stock actual
  (el mínimo entre todos los materiales que necesita).
- `calcularPrecio()`: suma `cantidad requerida × precioUnidad` de cada material.
- `producir(n)` / `venderProducto(n)`: mueven stock, devuelven `false` sin modificar
  nada si no alcanza (en vez de reventar o dejar el estado a medias).

Tiene dos constructores: uno para productos nuevos (`disponible` arranca en 0) y otro
para reconstruir un producto que ya existía al cargar desde disco (respeta el
`disponible` guardado). Esto evita que reabrir la app te "resetee" el stock de
productos ya fabricados.

### `Sistema`

Es el orquestador: mantiene los `HashMap` de materiales y productos, expone las
operaciones de alto nivel (`nuevoMaterial`, `nuevoProducto`, `producirProducto`,
`venderProducto`, etc.) y decide cuándo persistir.

**Decisión de diseño clave — inyección de dependencias:**

`Sistema` recibe los repositorios por constructor:

```java
public Sistema(MaterialRepository materialRepository, ProductoRepository productoRepository)
```

En vez de instanciar `new JsonMaterialRepository()` él mismo. Esto significa que
`Sistema` **no sabe ni le importa** si los datos vienen de un JSON, una base de datos,
o una lista en memoria — solo conoce la interfaz. La ventaja concreta: en los tests,
le pasamos repositorios falsos que guardan todo en memoria (`FakeMaterialRepository`,
`FakeProductoRepository`), así los tests corren rápido y no dejan basura en disco ni
pueden pisar datos reales entre sí.

---

## 4. Capa Repository

### Por qué una interfaz y no llamar a Gson directamente desde `Sistema`

Separar `MaterialRepository`/`ProductoRepository` (interfaces) de
`JsonMaterialRepository`/`JsonProductoRepository` (implementación concreta) es lo que
hace posible la inyección de dependencias del punto anterior. Si mañana cambiás JSON
por SQLite, solo escribís una nueva clase que implemente la interfaz — `Sistema` y el
`ViewModel` no se enteran del cambio.

### Por qué los productos no guardan el `Material` completo en su JSON

Un producto tiene una lista de `(Material, cantidad)`. Si serializáramos el `Material`
completo dentro de cada producto, tendríamos dos copias de los mismos datos: una en
`materiales.json` y otra embebida en cada producto que lo usa. Apenas actualizaras el
precio o la cantidad de un material, esas copias quedarían desincronizadas.

En cambio, `JsonProductoRepository` guarda solo `nombreMaterial` + `cantidad
requerida` (un DTO interno, `RequisitoMaterialDTO`). Al cargar, busca el objeto
`Material` real en el mapa de materiales ya cargados y arma la referencia real —
nunca una copia. Si el material referenciado ya no existe (fue borrado), el
requisito se omite con un aviso por consola en vez de romper la carga de todo el
archivo.

### Ruta de archivo inyectable

Ambos repositorios JSON reciben la ruta del archivo por constructor, con un valor por
defecto (`src/Sistema/materiales.json` / `productos.json`) si no se especifica nada.
Esto es lo que permite testear la lectura/escritura real apuntando a un archivo
temporal, sin tocar el archivo que usa la aplicación.

---

## 5. Capa ViewModel

`StockViewModel` es el único punto de contacto entre `model` y `InterfazGrafica`.

**Responsabilidades:**
- Traducir objetos de dominio (`Material`, `Producto`) a DTOs de solo lectura
  (`MaterialView`, `ProductoView`, `RequisitoView`) para que la View nunca tenga que
  importar clases de `model`.
- Validar datos de entrada (nombre vacío, duplicado, cantidades negativas, stock
  insuficiente) **antes** de tocar el `Model`, devolviendo mensajes de error
  entendibles para mostrar en un diálogo.
- Notificar cambios a quien esté escuchando, usando `PropertyChangeSupport` (la forma
  estándar de hacer "binding" reactivo en Java/Swing sin agregar librerías externas).
  Hay tres eventos: `EVENTO_MATERIALES`, `EVENTO_PRODUCTOS`, `EVENTO_ERROR`.

**Por qué DTOs en vez de pasar `Material`/`Producto` directamente a la View:**
Si la View pudiera llamar `material.actualizarCant(...)` directamente, la lógica de
negocio (¿se puede actualizar? ¿hay que guardar en disco después?) terminaría
repartida entre el `ViewModel` y la `View`. Los DTOs son inmutables y de solo
lectura: la única forma de modificar algo es a través de un método del
`ViewModel`, que sí sabe validar y persistir correctamente.

---

## 6. Capa View (Swing)

`ventanaPrincipal` es un `JFrame` con dos pestañas (`JTabbedPane`):
`MaterialesPanel` y `ProductosPanel`.

Cada panel:
1. Se suscribe al `StockViewModel` (`addPropertyChangeListener`).
2. Cuando llega `EVENTO_MATERIALES`/`EVENTO_PRODUCTOS`, refresca su tabla.
3. Cuando llega `EVENTO_ERROR`, muestra un `JOptionPane` con el mensaje.
4. Los botones (agregar, actualizar, producir, vender) solo parsean lo que el
   usuario escribió y llaman a un método del `ViewModel` — no hay lógica de
   negocio en los paneles.

Este patrón (View "tonta" que delega todo al ViewModel) es lo que permite, a
futuro, reemplazar Swing por otra tecnología de UI sin tocar una sola línea de
`model` ni de `viewmodel`.

---

## 7. Estrategia de testing

| Qué se testea | Cómo | Por qué |
|---|---|---|
| `Material`, `Producto` | Tests unitarios directos | Son clases sin dependencias externas |
| `Sistema` | Con `FakeMaterialRepository`/`FakeProductoRepository` (en memoria) | Corre rápido, sin tocar disco, sin dejar basura entre tests |
| `JsonMaterialRepository`, `JsonProductoRepository` | Con una ruta de archivo temporal, limpiada en `@AfterEach` | Es la única parte que sí necesita probarse contra el sistema de archivos real |

Cada bug encontrado durante el desarrollo quedó como un test de regresión:
- `cantidadCeroNoRompePorDivisionPorCero` / `establecerPrecioRequiereCargarLaCantidadPrimero`
- `actualizarCantidadNoModificaElPrecioUnidad` (el bug de `precioUnidad` explicado arriba)
- `producirNoAlteraElCostoUnitarioNiElPrecioDelProducto` (el mismo bug, probado a
  nivel `Sistema` con el escenario exacto que lo destapó)
- `siElMaterialYaNoExisteElRequisitoSeOmiteSinRomper` (producto con un material
  borrado)

---

## 8. Cómo correr el proyecto

1. Abrir la carpeta `SistemaStock` en IntelliJ (ya tiene configuradas las
   dependencias de Gson y JUnit 5 vía Maven).
2. Correr `Main.java`.
3. Los archivos `src/Sistema/materiales.json` y `src/Sistema/productos.json` se
   crean solos la primera vez que agregás algo.

Para correr los tests: click derecho sobre la carpeta `test` → Run All Tests.

---

## 9. Deuda técnica conocida / próximos pasos

- **No hay forma de eliminar** materiales ni productos, solo agregar y actualizar.
- **Sin estilo visual**: la interfaz usa el look and feel por defecto de Swing.
  Se puede sumar FlatLaf (una sola dependencia) para modernizarla sin rehacer nada.
- **Sin historial de movimientos**: hoy `disponible` es un contador, pero no queda
  registro de "cuándo se produjo/vendió qué cantidad".
- **`Sistema.actualizarProducto`/`actualizarMaterial` usan genéricos con casts**
  (`(Integer)`, `(Double)`, `(String)`) y un `boolean dato` para elegir qué campo
  actualizar. Funciona, pero no es muy legible ni type-safe; a futuro conviene
  métodos separados y explícitos (`actualizarCantidadMaterial`,
  `actualizarPrecioMaterial`, ya existen en el `ViewModel` — se podría empujar esa
  claridad hacia abajo, a `Sistema`).
