# Talleres

Aplicación Spring Boot + JDBC + MySQL + Thymeleaf para gestionar talleres y vehículos.

Incluye una API REST para Postman y una interfaz web con Thymeleaf. Ambas utilizan los mismos DAO y consultas SQL.

**Cómo consultar:** utilizar Ctrl + F para buscar una anotación, método, ruta o error concreto.

## ÍNDICE

1. [Flujo completo](#1-flujo-completo)
2. [Estructura del proyecto](#2-estructura-del-proyecto)
3. [Base de datos](#3-base-de-datos)
4. [Conexión y DAO](#4-conexión-y-dao)
5. [Consultas SQL y JDBC](#5-consultas-sql-y-jdbc)
6. [API REST y Postman](#6-api-rest-y-postman)
7. [Controller web](#7-controller-web)
8. [Thymeleaf](#8-thymeleaf)
9. [Flujo de cada operación](#9-flujo-de-cada-operación)
10. [Errores frecuentes](#10-errores-frecuentes)
11. [Todas las rutas](#11-todas-las-rutas)
12. [Resumen rápido para el examen](#12-resumen-rápido-para-el-examen)

---

## 1. Flujo completo

### Flujo de Thymeleaf

Navegador
    ↓
Controller
    ↓
DAOFactory
    ↓
DAOTalleresMySQL
    ↓
ConexionBD
    ↓
MySQL
    ↓
Controller (Model)
    ↓
HTML (Thymeleaf)
    ↓
Navegador

### Flujo de API REST

Postman
    ↓
RestController
    ↓
DAOFactory
    ↓
DAOTalleresMySQL
    ↓
ConexionBD
    ↓
MySQL
    ↓
RestController
    ↓
JSON

### Diferencia principal

- @Controller: devuelve páginas HTML.
- @RestController: devuelve datos en JSON.
- DAO: ejecuta las consultas SQL.
- DAOFactory: proporciona el DAO.
- Entidades: representan los datos.
- ConexionBD: establece la conexión con MySQL.

Los dos controladores utilizan el mismo DAO.

No es necesario duplicar consultas SQL para hacer funcionar Postman y Thymeleaf.

---

## 2. Estructura del proyecto

```text
src/main/
├── java/com/simulacro/talleres/
│   ├── TalleresApplication.java
│   ├── Conexion/
│   │   └── ConexionBD.java
│   ├── Entidades/
│   │   ├── Taller.java
│   │   └── Vehiculo.java
│   ├── DAO/
│   │   ├── DAOFactory.java
│   │   └── talleres/
│   │       ├── DAOTalleres.java
│   │       └── DAOTalleresMySQL.java
│   ├── Controller/
│   │   └── TalleresVehiculosController.java
│   └── Rest/
│       └── TalleresRestController.java
└── resources/
    ├── application.properties
    ├── Enunciado/
    │   └── Enunciado.txt
    └── templates/
        ├── talleres.html
        ├── detalle.html
        ├── vehiculos.html
        ├── editar.html
        └── nuevo.html
```

### Para qué sirve cada archivo

| Archivo | Función |
|---|---|
| TalleresApplication | Arrancar Spring Boot |
| ConexionBD | Conectar con MySQL |
| Taller | Representar un taller |
| Vehiculo | Representar un vehículo |
| DAOFactory | Obtener el DAO |
| DAOTalleres | Declarar los métodos |
| DAOTalleresMySQL | Implementar las consultas SQL |
| TalleresRestController | Gestionar las rutas de Postman |
| TalleresVehiculosController | Gestionar las páginas web |
| templates | Contener los HTML de Thymeleaf |
| pom.xml | Dependencias del proyecto |

El proyecto utiliza Java 17, Spring Boot, Thymeleaf, Web MVC y MySQL Connector/J.

No utiliza una capa Service.

---

## 3. Base de datos

Base de datos: gestion_talleres.

El script SQL se encuentra en:

src/main/resources/Enunciado/Enunciado.txt

### Tabla talleres

```sql
CREATE TABLE talleres (
    nombre VARCHAR(100) PRIMARY KEY,
    ciudad VARCHAR(100)
);
```

### Tabla vehiculos

```sql
CREATE TABLE vehiculos (
    matricula VARCHAR(10) PRIMARY KEY,
    marca VARCHAR(100),
    modelo VARCHAR(100),
    precio DECIMAL(10,2),
    taller VARCHAR(100),
    FOREIGN KEY (taller) REFERENCES talleres(nombre)
);
```

### Relación entre tablas

```text
TALLERES                  VEHICULOS

nombre (PK) ──────────── taller (FK)
ciudad                   matricula (PK)
                         marca
                         modelo
                         precio
```

Un taller puede tener varios vehículos.

Cada vehículo está relacionado con un taller mediante su nombre.

IMPORTANTE:

- No se utilizan IDs.
- La clave primaria del taller es nombre.
- La clave primaria del vehículo es matricula.
- La clave foránea del vehículo es taller.
- El nombre del taller debe existir antes de insertar un vehículo asociado.

### Entidades Java

Taller:

```java
private String nombre;
private String ciudad;
```

Vehiculo:

```java
private String matricula;
private String marca;
private String modelo;
private Double precio;
private String taller;
```

La entidad Vehiculo tiene constructor vacío, constructor con parámetros, getters y setters.

La entidad Taller no tiene constructor vacío en el código original.

Los getters permiten obtener los datos.

Los setters permiten modificar los datos.

Spring utiliza los constructores y las propiedades de las entidades para recibir información desde formularios o JSON.

---

## 4. Conexión y DAO

### 4.1. ConexionBD

Archivo: Conexion/ConexionBD.java

Se encarga de establecer la conexión con MySQL mediante JDBC.

Utiliza getInstance() para obtener la conexión.

```java
ConexionBD.getInstance()
```

Si no existe una conexión o está cerrada, se crea una nueva.

La conexión utiliza DriverManager.getConnection().

En este proyecto la URL, usuario y contraseña están configurados directamente en ConexionBD.java.

No se configura la conexión en application.properties.

### 4.2. DAOFactory

Archivo: DAO/DAOFactory.java

Permite obtener el DAO desde cualquier controlador.

```java
DAOFactory.getInstance().getDaoTalleres()
```

Ejemplo de utilización:

```java
DAOFactory.getInstance().getDaoTalleres().getAllTaller();
```

Desglose:

- getInstance(): obtiene la factoría.
- getDaoTalleres(): obtiene el DAO.
- getAllTaller(): ejecuta el método correspondiente.

### 4.3. Interfaz DAOTalleres

Archivo: DAO/talleres/DAOTalleres.java

Contiene los métodos que debe implementar el DAO.

```java
public interface DAOTalleres {

    // TALLERES

    List<Taller> getAllTaller();

    void saveTaller(Taller taller);

    Taller updateTaller(String nombre, Taller taller);

    Taller deleteTaller(String nombre);

    // VEHICULOS

    List<Vehiculo> getAllVehiculo();

    void saveVehiculo(Vehiculo vehiculo);

    Vehiculo updateVehiculo(String matricula, Vehiculo vehiculo);

    Vehiculo deleteVehiculo(String matricula);

    List<Vehiculo> filtrarVehiculo(String nombre);
}
```

Aquí solamente se declaran los métodos.

No se escriben consultas SQL.

### 4.4. DAOTalleresMySQL

Archivo: DAO/talleres/DAOTalleresMySQL.java

Implementa la interfaz:

```java
public class DAOTalleresMySQL implements DAOTalleres
```

Aquí se escriben todas las consultas SQL.

Cada método declarado en la interfaz tiene su implementación mediante @Override.

### Métodos disponibles

| Método | Función |
|---|---|
| getAllTaller() | Listar talleres |
| saveTaller() | Insertar taller |
| updateTaller() | Actualizar taller |
| deleteTaller() | Eliminar taller |
| getAllVehiculo() | Listar vehículos |
| saveVehiculo() | Insertar vehículo |
| updateVehiculo() | Actualizar vehículo |
| deleteVehiculo() | Eliminar vehículo |
| filtrarVehiculo() | Listar vehículos de un taller |

updateTaller() y deleteTaller() están implementados en el DAO, pero no tienen rutas asociadas en los controladores actuales.

---

## 5. Consultas SQL y JDBC

Todas las consultas se encuentran en DAOTalleresMySQL.java.

### 5.1. SELECT - Listar todos los registros

Método: getAllTaller()

```java
@Override
public List<Taller> getAllTaller() {
    String sql = "SELECT * FROM talleres";
    List<Taller> talleres = new ArrayList<>();

    try {
        PreparedStatement ps = ConexionBD.getInstance().prepareStatement(sql);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Taller taller = new Taller(
                rs.getString("nombre"),
                rs.getString("ciudad")
            );

            talleres.add(taller);
        }

    } catch (SQLException e) {
        throw new RuntimeException(e);
    }

    return talleres;
}
```

### Explicación

1. Escribir la consulta SQL.
2. Crear una lista vacía.
3. Obtener la conexión.
4. Preparar la consulta.
5. Ejecutar la consulta con executeQuery().
6. Recorrer los resultados con while.
7. Crear un objeto por cada fila.
8. Añadir cada objeto a la lista.
9. Devolver la lista.

### ResultSet

```java
rs.getString("nombre");
rs.getDouble("precio");
```

Permite obtener los valores de las columnas de la base de datos.

### while (rs.next())

Recorre todas las filas devueltas por el SELECT.

Cada vuelta representa un registro de la base de datos.

### 5.2. SELECT WHERE - Filtrar registros

Método: filtrarVehiculo(String nombre)

Fragmento principal:

```java
String sql = "SELECT * FROM vehiculos WHERE taller = ?";

PreparedStatement ps = ConexionBD.getInstance().prepareStatement(sql);

ps.setString(1, nombre);

ResultSet rs = ps.executeQuery();
```

Después se recorren los resultados y se añaden los vehículos a una lista.

El interrogante se sustituye por el valor recibido como parámetro.

Por ejemplo:

```java
filtrarVehiculo("Taller Central");
```

Devuelve los vehículos asociados a Taller Central.

Se utiliza en la página de detalle del taller.

### 5.3. INSERT - Guardar vehículo

Método: saveVehiculo(Vehiculo vehiculo)

Fragmento principal:

```java
String sql = "INSERT INTO vehiculos (matricula, marca, modelo, precio, taller) VALUES (?, ?, ?, ?, ?)";

PreparedStatement ps = ConexionBD.getInstance().prepareStatement(sql);

ps.setString(1, vehiculo.getMatricula());
ps.setString(2, vehiculo.getMarca());
ps.setString(3, vehiculo.getModelo());
ps.setDouble(4, vehiculo.getPrecio());
ps.setString(5, vehiculo.getTaller());

ps.executeUpdate();
ps.close();
```

Los números corresponden al orden de los interrogantes.

| Posición | Valor |
|---|---|
| 1 | matricula |
| 2 | marca |
| 3 | modelo |
| 4 | precio |
| 5 | taller |

Para insertar no es necesario utilizar ResultSet ni while.

El método saveTaller() utiliza el mismo funcionamiento con nombre y ciudad.

### 5.4. UPDATE - Actualizar vehículo

Método: updateVehiculo(String matricula, Vehiculo vehiculo)

Fragmento principal:

```java
String sql = "UPDATE vehiculos SET matricula = ?, marca = ?, modelo = ?, precio = ? WHERE matricula = ?";

PreparedStatement ps = ConexionBD.getInstance().prepareStatement(sql);

ps.setString(1, vehiculo.getMatricula());
ps.setString(2, vehiculo.getMarca());
ps.setString(3, vehiculo.getModelo());
ps.setDouble(4, vehiculo.getPrecio());
ps.setString(5, matricula);

ps.execute();
```

IMPORTANTE:

El método recibe dos cosas:

- matricula: identifica el vehículo original.
- vehiculo: contiene los datos nuevos.

El WHERE indica qué registro queremos modificar.

El SET indica qué valores queremos cambiar.

En este proyecto el UPDATE no modifica el taller asociado.

### 5.5. DELETE - Eliminar vehículo

Método: deleteVehiculo(String matricula)

Fragmento principal:

```java
String sql = "DELETE FROM vehiculos WHERE matricula = ?";

PreparedStatement statement = ConexionBD.getInstance().prepareStatement(sql);

statement.setString(1, matricula);

statement.execute();
```

Solamente es necesario conocer la matrícula del vehículo que queremos eliminar.

Para eliminar un taller se utiliza el mismo funcionamiento, pero filtrando por nombre.

### 5.6. Resumen JDBC

| Elemento | Función |
|---|---|
| PreparedStatement | Preparar SQL |
| setString() | Introducir un String en un interrogante |
| setDouble() | Introducir un Double |
| executeQuery() | Ejecutar SELECT |
| executeUpdate() | Ejecutar modificaciones |
| execute() | Ejecutar una sentencia SQL |
| ResultSet | Almacenar resultados |
| rs.next() | Pasar a la siguiente fila |
| rs.getString() | Obtener una columna de texto |
| rs.getDouble() | Obtener una columna numérica |
| lista.add() | Añadir un objeto a la lista |

---

## 6. API REST y Postman

Archivo: Rest/TalleresRestController.java

Utiliza:

```java
@RestController
```

Spring convierte los objetos y listas devueltos en JSON.

Las rutas del proyecto incluyen /api directamente en cada Mapping.

### 6.1. Rutas REST

| Método | Ruta | Función |
|---|---|---|
| GET | /api/talleres | Listar talleres |
| GET | /api/vehiculos | Listar vehículos |
| POST | /api/vehiculos | Crear vehículo |
| PUT | /api/vehiculos/{matricula} | Actualizar vehículo |
| DELETE | /api/vehiculos/{matricula} | Eliminar vehículo |

### 6.2. Anotaciones

```java
@GetMapping
@PostMapping
@PutMapping
@DeleteMapping
@PathVariable
@RequestBody
```

| Anotación | Función |
|---|---|
| @GetMapping | Recibir una petición GET |
| @PostMapping | Recibir una petición POST |
| @PutMapping | Recibir una petición PUT |
| @DeleteMapping | Recibir una petición DELETE |
| @PathVariable | Recoger un dato de la URL |
| @RequestBody | Recoger un objeto JSON |

### 6.3. GET - Listar

En Postman seleccionar GET.

```text
http://localhost:8080/api/talleres
```

O:

```text
http://localhost:8080/api/vehiculos
```

No necesita Body.

El controlador llama al DAO y devuelve la lista.

Ejemplo de respuesta:

```json
[
    {
        "nombre": "Taller Central",
        "ciudad": "Huesca"
    }
]
```

### 6.4. POST - Insertar

En Postman:

- Método: POST.
- Body.
- raw.
- JSON.

URL:

```text
http://localhost:8080/api/vehiculos
```

Body de ejemplo:

```json
{
    "matricula": "2222XYZ",
    "marca": "Seat",
    "modelo": "Leon",
    "precio": 11000.0,
    "taller": "Taller Central"
}
```

El controlador recoge el objeto mediante:

```java
@RequestBody Vehiculo vehiculo
```

Y llama al DAO:

```java
DAOFactory.getInstance().getDaoTalleres().saveVehiculo(vehiculo);
```

El nombre del taller debe existir en la base de datos.

### 6.5. PUT - Actualizar

En Postman seleccionar PUT.

URL:

```text
http://localhost:8080/api/vehiculos/2222XYZ
```

Body:

```json
{
    "matricula": "2222XYZ",
    "marca": "Seat",
    "modelo": "Ibiza",
    "precio": 9500.0,
    "taller": "Taller Central"
}
```

El controlador recibe:

```java
@PathVariable String matricula
```

Y:

```java
@RequestBody Vehiculo vehiculo
```

La matrícula de la URL identifica el vehículo original.

El JSON contiene los nuevos datos.

Llamada al DAO:

```java
DAOFactory.getInstance().getDaoTalleres().updateVehiculo(matricula, vehiculo);
```

### 6.6. DELETE - Eliminar

En Postman seleccionar DELETE.

URL:

```text
http://localhost:8080/api/vehiculos/2222XYZ
```

Llamada al DAO:

```java
DAOFactory.getInstance().getDaoTalleres().deleteVehiculo(matricula);
```

IMPORTANTE:

En el código actual, el método DELETE también recibe un @RequestBody Vehiculo vehiculo.

Por tanto, hay que enviar un JSON válido en el Body de Postman, aunque el DAO solamente necesite la matrícula.

Si no se envía el Body puede aparecer un error 400.

El objeto devuelto por el controlador es el recibido en la petición, no necesariamente el registro original de la base de datos.

### 6.7. PathVariable frente a RequestBody

PathVariable:

```java
@PutMapping("/api/vehiculos/{matricula}")
```

```java
@PathVariable String matricula
```

Obtiene la matrícula de la URL.

RequestBody:

```java
@RequestBody Vehiculo vehiculo
```

Obtiene el objeto enviado como JSON.

No confundir ambos.

---

## 7. Controller web

Archivo: Controller/TalleresVehiculosController.java

Utiliza:

```java
@Controller
```

A diferencia de RestController, devuelve nombres de archivos HTML o redirecciones.

### 7.1. Mostrar una página HTML

```java
@GetMapping("/nuevo")
public String nuevo() {
    return "nuevo";
}
```

Cuando entramos en:

```text
http://localhost:8080/nuevo
```

Spring busca automáticamente:

```text
src/main/resources/templates/nuevo.html
```

No es necesario escribir la extensión .html en el return.

### 7.2. Model - Pasar información al HTML

```java
@GetMapping("/talleres")
public String listarTaller(Model model) {
    model.addAttribute("taller", DAOFactory.getInstance().getDaoTalleres().getAllTaller());
    return "talleres";
}
```

Funcionamiento:

1. El navegador solicita /talleres.
2. El Controller recibe la petición.
3. El DAO obtiene todos los talleres.
4. Model almacena la lista con el nombre "taller".
5. Se carga talleres.html.
6. Thymeleaf puede utilizar la lista mediante ${taller}.

IMPORTANTE:

El nombre del atributo del Model debe coincidir con el utilizado en Thymeleaf.

```java
model.addAttribute("taller", lista);
```

```html
<div th:each="t : ${taller}">
```

Aunque contiene una lista, en este proyecto el atributo se llama taller en singular.

### 7.3. Mostrar el detalle de un taller

Ruta: /talleres/{nombre}

```java
@GetMapping("/talleres/{nombre}")
public String detalle(@PathVariable String nombre, Model model) {
    for (Taller taller : DAOFactory.getInstance().getDaoTalleres().getAllTaller()) {
        if (taller.getNombre().equals(nombre)) {
            model.addAttribute("taller", taller);
        }
    }

    model.addAttribute("vehiculos", DAOFactory.getInstance().getDaoTalleres().filtrarVehiculo(nombre));

    return "detalle";
}
```

Este método realiza dos operaciones diferentes:

1. Busca el taller seleccionado recorriendo la lista de talleres.
2. Obtiene los vehículos asociados mediante filtrarVehiculo(nombre).

No existe un método específico getTallerByNombre().

Se utiliza un for para encontrar el taller.

### Recoger un dato de la URL

```java
@PathVariable String nombre
```

Si entramos en:

```text
/talleres/Taller%20Central
```

La variable nombre contiene:

```text
Taller Central
```

%20 representa un espacio en una URL.

### Pasar varios atributos al Model

```java
model.addAttribute("taller", taller);
model.addAttribute("vehiculos", listaVehiculos);
```

El HTML puede utilizar ambos atributos de manera independiente.

Uno contiene un objeto y el otro una lista.

### 7.4. Crear un taller

Para crear un taller se utilizan dos rutas.

GET: mostrar el formulario.

```java
@GetMapping("/nuevo")
public String nuevo() {
    return "nuevo";
}
```

POST: recibir los datos y guardarlos.

```java
@PostMapping("/talleres/nuevo")
public String guardarTaller(Taller taller) {
    DAOFactory.getInstance().getDaoTalleres().saveTaller(taller);
    return "redirect:/talleres";
}
```

Spring recoge los campos del formulario y construye el objeto Taller.

Después se guarda mediante el DAO.

Finalmente se vuelve al listado.

### 7.5. Listar vehículos

Ruta: GET /vehiculos.

Fragmento principal:

```java
model.addAttribute("vehiculos", DAOFactory.getInstance().getDaoTalleres().getAllVehiculo());

model.addAttribute("talleres", DAOFactory.getInstance().getDaoTalleres().getAllTaller());

return "vehiculos";
```

¿Por qué se necesitan dos listas?

- vehiculos: mostrar todos los vehículos.
- talleres: mostrar los talleres disponibles en el select del formulario.

### 7.6. Guardar un vehículo

Ruta:

```text
POST /vehiculos/guardar
```

El Controller recibe un objeto Vehiculo desde el formulario.

Llama al DAO:

```java
DAOFactory.getInstance().getDaoTalleres().saveVehiculo(vehiculo);
```

Después devuelve:

```java
return "redirect:/vehiculos";
```

No utiliza @RequestBody porque los datos proceden de un formulario HTML, no de un JSON.

### 7.7. Editar un vehículo

Ruta:

```text
GET /vehiculos/editar/{matricula}
```

El Controller realiza las siguientes operaciones:

1. Recoge la matrícula mediante @PathVariable.
2. Obtiene todos los vehículos mediante getAllVehiculo().
3. Recorre la lista mediante un for.
4. Compara las matrículas mediante equals().
5. Añade el vehículo encontrado al Model.
6. Devuelve editar.html.

La comparación se realiza mediante el siguiente patrón:

```java
if (vehiculo.getMatricula().equals(matricula))
```

El objeto encontrado se pasa al HTML:

```java
model.addAttribute("vehiculo", vehiculo);
return "editar";
```

Si no encuentra el vehículo, redirige al listado.

```java
return "redirect:/vehiculos";
```

### 7.8. Actualizar un vehículo

Ruta:

```text
POST /vehiculos/actualizar
```

El formulario de editar.html envía los datos modificados.

El Controller utiliza:

```java
DAOFactory.getInstance().getDaoTalleres().updateVehiculo(vehiculo.getMatricula(), vehiculo);
```

Después redirige:

```java
return "redirect:/vehiculos";
```

La matrícula no se modifica desde el formulario porque el campo es readonly.

### 7.9. Eliminar un vehículo

Ruta:

```text
POST /vehiculos/eliminar
```

El Controller recibe la matrícula enviada desde un campo oculto del formulario.

```java
DAOFactory.getInstance().getDaoTalleres().deleteVehiculo(matricula);
```

Después vuelve al listado:

```java
return "redirect:/vehiculos";
```

No necesita recibir el objeto Vehiculo completo.

### 7.10. Return normal frente a redirect

```java
return "vehiculos";
```

Carga vehiculos.html directamente.

```java
return "redirect:/vehiculos";
```

Realiza una nueva petición a la ruta /vehiculos.

Esto permite volver a ejecutar el GET y obtener los datos actualizados de la base de datos.

### 7.11. Diferencias entre REST y Controller

| Característica | REST | Controller web |
|---|---|---|
| Anotación | @RestController | @Controller |
| Devuelve | JSON | HTML |
| Recibir objetos | @RequestBody | Parámetro de tipo entidad |
| Pasar datos al HTML | No | Model |
| Recoger datos URL | @PathVariable | @PathVariable |
| Actualizar | PUT | POST |
| Eliminar | DELETE | POST |
| Consultas SQL | DAO | Mismo DAO |

Los formularios HTML del proyecto utilizan GET y POST.

Por eso actualizar y eliminar desde Thymeleaf se realizan mediante POST, mientras que en Postman se utilizan PUT y DELETE.

---

## 8. Thymeleaf

Los archivos HTML se encuentran en:

```text
src/main/resources/templates/
```

### 8.1. Estructura básica

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>Talleres</title>
</head>
<body>

</body>
</html>
```

El proyecto utiliza HTML básico, sin CSS.

### 8.2. Mostrar una lista - th:each

Controller:

```java
model.addAttribute("taller", DAOFactory.getInstance().getDaoTalleres().getAllTaller());
```

HTML:

```html
<div th:each="t : ${taller}">
    <p th:text="${t.nombre}"></p>
    <p th:text="${t.ciudad}"></p>
    <hr>
</div>
```

Funcionamiento:

- ${taller}: lista enviada desde el Controller.
- t: variable que representa cada objeto de la lista.
- th:each: recorre la lista.
- th:text: muestra el contenido.
- hr: dibuja una línea horizontal de separación.

Es parecido a realizar un for en Java.

### 8.3. Mostrar un único objeto

Cuando el Controller envía un objeto en lugar de una lista:

```java
model.addAttribute("taller", taller);
```

Se pueden mostrar directamente sus propiedades:

```html
<p th:text="${taller.nombre}"></p>
<p th:text="${taller.ciudad}"></p>
```

No hace falta th:each porque solamente hay un objeto.

### 8.4. Mostrar vehículos

```html
<div th:each="vehiculo : ${vehiculos}">
    <p th:text="${vehiculo.matricula}"></p>
    <p th:text="${vehiculo.marca}"></p>
    <p th:text="${vehiculo.modelo}"></p>
    <p th:text="${vehiculo.precio}"></p>
    <hr>
</div>
```

Thymeleaf utiliza los getters de la entidad para obtener los valores.

Por ejemplo:

```html
<p th:text="${vehiculo.marca}"></p>
```

Obtiene el valor de getMarca().

### 8.5. Enlaces normales

Para ir a una ruta fija:

```html
<a href="/talleres">Talleres</a>
<a href="/vehiculos">Vehículos</a>
<a href="/nuevo">Nuevo taller</a>
```

No hace falta utilizar th:href si la URL no contiene variables.

### 8.6. Enlaces dinámicos - th:href

Para mostrar el detalle de un taller:

```html
<a th:href="@{/talleres/{nombre}(nombre=${t.nombre})}">
    <button>Detalle</button>
</a>
```

Partes del enlace:

- /talleres/{nombre}: ruta del Controller.
- nombre=: parámetro que queremos rellenar.
- ${t.nombre}: valor del taller actual.

Ejemplo de URL generada:

```text
/talleres/Taller%20Central
```

Para editar un vehículo:

```html
<a th:href="@{/vehiculos/editar/{matricula}(matricula=${vehiculo.matricula})}">
    Editar
</a>
```

Ejemplo:

```text
/vehiculos/editar/2222XYZ
```

### 8.7. Formulario para crear un vehículo

```html
<form action="/vehiculos/guardar" method="post">

    <input type="text" name="matricula" required>

    <input type="text" name="marca" required>

    <input type="text" name="modelo" required>

    <input type="number" name="precio" step="0.01" required>

    <select name="taller" required>
        <option th:each="t : ${talleres}"
                th:value="${t.nombre}"
                th:text="${t.nombre}">
        </option>
    </select>

    <button type="submit">Guardar</button>

</form>
```

### Funcionamiento de un formulario

action indica la ruta a la que se envían los datos.

method indica el método HTTP utilizado.

name indica la propiedad o parámetro que recibe Java.

required obliga a rellenar el campo desde el navegador.

step="0.01" permite introducir precios con decimales.

type="submit" envía el formulario.

### Relación entre name y Java

```html
<input type="text" name="marca">
```

Se corresponde con la propiedad:

```java
private String marca;
```

Y su setter:

```java
setMarca(...)
```

El nombre del campo debe coincidir con el nombre de la propiedad.

### 8.8. Select dinámico

Controller:

```java
model.addAttribute("talleres", DAOFactory.getInstance().getDaoTalleres().getAllTaller());
```

HTML:

```html
<select name="taller">
    <option th:each="t : ${talleres}"
            th:value="${t.nombre}"
            th:text="${t.nombre}">
    </option>
</select>
```

Funcionamiento:

- th:each: recorre todos los talleres.
- th:value: valor que se envía al Controller.
- th:text: texto que aparece en pantalla.
- name="taller": propiedad de Vehiculo que recibe el valor.

En este proyecto se envía el nombre del taller porque es su clave primaria.

No se utiliza ningún ID.

### 8.9. Formulario de edición - th:value

Para mostrar los datos actuales de un vehículo dentro de los inputs:

```html
<input type="text" name="matricula"
       th:value="${vehiculo.matricula}" readonly>

<input type="text" name="marca"
       th:value="${vehiculo.marca}" required>

<input type="text" name="taller"
       th:value="${vehiculo.taller}" readonly>
```

th:value rellena automáticamente el valor del input.

El objeto vehiculo procede del Model del Controller.

readonly permite mostrar un valor sin que el usuario pueda modificarlo.

IMPORTANTE:

Los campos readonly sí se envían al enviar el formulario.

El formulario de edición utiliza:

```html
<form action="/vehiculos/actualizar" method="post">
```

### 8.10. Eliminar mediante un formulario

```html
<form action="/vehiculos/eliminar" method="post">

    <input type="hidden" name="matricula"
           th:value="${vehiculo.matricula}">

    <button type="submit">Eliminar</button>

</form>
```

type="hidden" crea un campo oculto.

El usuario solamente ve el botón Eliminar.

Al pulsarlo se envía la matrícula al Controller.

El Controller utiliza esa matrícula para ejecutar el DELETE del DAO.

### 8.11. Barra de navegación

```html
<nav>
    <a href="/talleres">Talleres</a>
    <a href="/vehiculos">Vehículos</a>
    <a href="/nuevo">Nuevo taller</a>
</nav>
```

Se incluye directamente en los HTML.

No se utilizan fragmentos ni plantillas compartidas.

### 8.12. Resumen Thymeleaf

| Elemento | Función |
|---|---|
| ${variable} | Acceder a una variable del Model |
| ${objeto.nombre} | Acceder a una propiedad |
| th:each | Recorrer una lista |
| th:text | Mostrar texto |
| th:value | Rellenar un valor |
| th:href | Generar enlaces dinámicos |
| href | Enlace normal |
| action | Ruta de destino de un formulario |
| method | Método HTTP |
| name | Nombre del dato enviado |
| readonly | Impedir modificar un campo |
| hidden | Ocultar un campo |
| required | Campo obligatorio |
| select | Selector de opciones |
| option | Opción del selector |
| hr | Línea horizontal |

---

## 9. Flujo de cada operación

### 9.1. Listar talleres

```text
GET /talleres
    ↓
listarTaller()
    ↓
getAllTaller()
    ↓
SELECT * FROM talleres
    ↓
List<Taller>
    ↓
model.addAttribute("taller", ...)
    ↓
talleres.html
    ↓
th:each
```

### 9.2. Mostrar detalle

```text
Botón Detalle
    ↓
th:href
    ↓
GET /talleres/{nombre}
    ↓
@PathVariable String nombre
    ↓
Buscar taller mediante for
    ↓
filtrarVehiculo(nombre)
    ↓
SELECT * FROM vehiculos WHERE taller = ?
    ↓
Model: taller y vehiculos
    ↓
detalle.html
```

### 9.3. Crear taller

```text
GET /nuevo
    ↓
nuevo.html
    ↓
Rellenar formulario
    ↓
POST /talleres/nuevo
    ↓
Objeto Taller
    ↓
saveTaller()
    ↓
INSERT INTO talleres
    ↓
redirect:/talleres
```

### 9.4. Crear vehículo

```text
GET /vehiculos
    ↓
Model: vehiculos y talleres
    ↓
vehiculos.html
    ↓
Rellenar formulario
    ↓
POST /vehiculos/guardar
    ↓
Objeto Vehiculo
    ↓
saveVehiculo()
    ↓
INSERT INTO vehiculos
    ↓
redirect:/vehiculos
```

### 9.5. Editar vehículo

```text
Enlace Editar
    ↓
GET /vehiculos/editar/{matricula}
    ↓
@PathVariable String matricula
    ↓
Buscar vehículo mediante for
    ↓
model.addAttribute("vehiculo", vehiculo)
    ↓
editar.html
    ↓
th:value rellena los inputs
    ↓
Modificar datos
    ↓
POST /vehiculos/actualizar
    ↓
updateVehiculo()
    ↓
UPDATE vehiculos
    ↓
redirect:/vehiculos
```

### 9.6. Eliminar vehículo

```text
Botón Eliminar
    ↓
Formulario con matrícula oculta
    ↓
POST /vehiculos/eliminar
    ↓
String matricula
    ↓
deleteVehiculo(matricula)
    ↓
DELETE FROM vehiculos WHERE matricula = ?
    ↓
redirect:/vehiculos
```

### 9.7. Insertar desde Postman

```text
POST /api/vehiculos
    ↓
Body JSON
    ↓
@RequestBody Vehiculo vehiculo
    ↓
RestController
    ↓
saveVehiculo()
    ↓
INSERT INTO vehiculos
    ↓
Respuesta REST
```

### 9.8. Actualizar desde Postman

```text
PUT /api/vehiculos/{matricula}
    ↓
@PathVariable: matrícula original
@RequestBody: datos nuevos
    ↓
updateVehiculo()
    ↓
UPDATE vehiculos
    ↓
Respuesta REST
```

---

## 10. Errores frecuentes

### 10.1. Error 500 - Internal Server Error

El servidor ha encontrado un error durante la ejecución.

Lo primero es mirar la consola de IntelliJ donde se está ejecutando Spring Boot.

El navegador solamente muestra el error general.

La consola muestra la causa concreta.

Revisar:

- Consultas SQL.
- Conexión a MySQL.
- Nombres de columnas.
- Nombres de atributos del Model.
- Constructores de las entidades.
- Getters y setters.
- Claves primarias.
- Claves foráneas.

### 10.2. Error 500 al crear un taller

En el proyecto original Taller no tiene constructor vacío.

Vehiculo sí dispone de:

```java
public Vehiculo() {
}
```

Spring puede tener problemas al construir un objeto Taller desde el formulario dependiendo de cómo se realice el enlace de datos.

Revisar la entidad Taller y la excepción concreta de la consola.

La ausencia de constructor vacío no implica necesariamente que falle, porque Spring también puede utilizar un constructor con parámetros.

### 10.3. Error resolving template

Spring no encuentra o no puede resolver el archivo HTML.

Comprobar:

```java
return "vehiculos";
```

Debe existir:

```text
src/main/resources/templates/vehiculos.html
```

Revisar que el nombre sea exactamente igual.

### 10.4. Error 500 al mostrar detalle

En detalle.html se utilizan propiedades del objeto taller.

```html
<p th:text="${taller.nombre}"></p>
```

Si el Controller no encuentra ningún taller con ese nombre, no añade el objeto al Model.

Por tanto, Thymeleaf puede fallar al intentar acceder a sus propiedades.

Revisar:

- Nombre recibido por @PathVariable.
- Comparación mediante equals().
- model.addAttribute("taller", taller).
- Nombre existente en la base de datos.

### 10.5. Error 404 - Not Found

La ruta solicitada no existe.

Por ejemplo:

```text
http://localhost:8080/
```

El proyecto no tiene index.html ni un Controller para la ruta /.

Hay que entrar directamente en:

```text
http://localhost:8080/talleres
```

O:

```text
http://localhost:8080/vehiculos
```

Para Postman:

```text
http://localhost:8080/api/talleres
```

No confundir las rutas de Thymeleaf con las de la API REST.

### 10.6. Error 405 - Method Not Allowed

La ruta existe, pero no admite el método HTTP utilizado.

Por ejemplo:

```text
GET /vehiculos/guardar
```

La ruta está preparada para POST, no para GET.

Al escribir una URL directamente en el navegador se realiza un GET.

Para ejecutar un POST se utiliza un formulario o Postman.

### 10.7. Error 400 - Bad Request

Puede ocurrir cuando los datos recibidos no son válidos o faltan datos obligatorios.

En Postman revisar:

- Body.
- raw.
- JSON.
- Nombres de propiedades.
- Tipos de datos.
- @RequestBody.

Caso concreto del proyecto:

El DELETE de la API REST exige un @RequestBody Vehiculo.

Si no se envía un JSON válido, puede aparecer un error 400.

Aunque el DAO solamente necesita la matrícula, el Controller actual exige también el objeto.

### 10.8. Error de clave foránea

La relación existente es:

```text
vehiculos.taller → talleres.nombre
```

Si intentamos insertar un vehículo con un taller inexistente, MySQL devuelve un error.

También puede ocurrir al eliminar o modificar el nombre de un taller que tiene vehículos asociados.

El script original no contiene:

```sql
ON UPDATE CASCADE
ON DELETE CASCADE
```

Por tanto, MySQL no actualiza ni elimina automáticamente los registros relacionados.

### 10.9. Error de clave primaria duplicada

No se pueden repetir:

- El nombre de un taller.
- La matrícula de un vehículo.

Si se intenta insertar un registro con una clave primaria existente, MySQL devuelve un error.

El DAO no comprueba previamente si existe.

### 10.10. Error de conexión a MySQL

Revisar ConexionBD.java.

Comprobar:

- URL de conexión JDBC.
- Usuario.
- Contraseña.
- Servidor MySQL disponible.
- Base de datos gestion_talleres.
- Acceso al servidor remoto.

Las credenciales están configuradas directamente en ConexionBD.java.

No están en application.properties.

No se incluyen las credenciales en este README.

### 10.11. Thymeleaf no muestra los datos

Comprobar que los nombres coinciden.

Controller:

```java
model.addAttribute("vehiculos", lista);
```

HTML:

```html
<div th:each="vehiculo : ${vehiculos}">
```

También revisar que la consulta SQL devuelve registros.

En el listado de talleres recordar que el atributo se llama taller, no talleres.

### 10.12. El formulario no guarda los datos

Comprobar:

```html
<form action="/vehiculos/guardar" method="post">
```

Que coincide con el Mapping del Controller.

Comprobar también los nombres:

```html
<input name="marca">
```

Debe coincidir con:

```java
private String marca;
```

Revisar los getters, setters y constructores.

Finalmente, comprobar que el Controller llama al método saveVehiculo() del DAO.

### 10.13. El UPDATE no cambia el taller

No es un error de Thymeleaf.

La consulta SQL actual es:

```sql
UPDATE vehiculos
SET matricula = ?, marca = ?, modelo = ?, precio = ?
WHERE matricula = ?
```

No incluye la columna taller.

Por tanto, el taller asociado no se modifica.

### 10.14. Métodos existentes sin rutas

El DAO contiene:

```java
updateTaller()
deleteTaller()
```

Sin embargo, estos métodos no se utilizan desde los controladores actuales.

Que exista un método en el DAO no significa que exista una URL para utilizarlo.

### 10.15. Funcionalidades que no existen en el proyecto

- Capa Service.
- Consultas JOIN.
- Consultas COUNT.
- Paginación.
- GET individual de talleres en la API REST.
- Actualización de talleres desde la web.
- Eliminación de talleres desde la web.
- Creación, actualización y eliminación de talleres desde la API REST.
- Gestión personalizada de errores HTTP.
- Consultas específicas para obtener un único taller o vehículo.

Para buscar un objeto concreto, el Controller recorre las listas mediante un for.

---

## 11. Todas las rutas

### Thymeleaf

| Método | Ruta | Función |
|---|---|---|
| GET | /talleres | Listar talleres |
| GET | /talleres/{nombre} | Detalle del taller |
| GET | /nuevo | Formulario nuevo taller |
| POST | /talleres/nuevo | Guardar taller |
| GET | /vehiculos | Listar vehículos |
| POST | /vehiculos/guardar | Guardar vehículo |
| GET | /vehiculos/editar/{matricula} | Formulario editar vehículo |
| POST | /vehiculos/actualizar | Actualizar vehículo |
| POST | /vehiculos/eliminar | Eliminar vehículo |

### API REST

| Método | Ruta | Función |
|---|---|---|
| GET | /api/talleres | Listar talleres |
| GET | /api/vehiculos | Listar vehículos |
| POST | /api/vehiculos | Crear vehículo |
| PUT | /api/vehiculos/{matricula} | Actualizar vehículo |
| DELETE | /api/vehiculos/{matricula} | Eliminar vehículo |

### HTML y atributos del Model

| HTML | Atributos principales |
|---|---|
| talleres.html | taller (lista) |
| detalle.html | taller (objeto), vehiculos (lista) |
| vehiculos.html | vehiculos (lista), talleres (lista) |
| editar.html | vehiculo (objeto) |
| nuevo.html | Formulario de creación |

---

## 12. Resumen rápido para el examen

### ¿Dónde tengo que escribir cada cosa?

| Quiero hacer... | Archivo |
|---|---|
| Crear una propiedad | Entidad |
| Declarar un método DAO | DAOTalleres.java |
| Escribir una consulta SQL | DAOTalleresMySQL.java |
| Crear una ruta para Postman | TalleresRestController.java |
| Crear una ruta para HTML | TalleresVehiculosController.java |
| Crear un formulario | templates |
| Configurar MySQL | ConexionBD.java |

### Orden de trabajo

1. Crear la base de datos.
2. Crear las entidades.
3. Configurar ConexionBD.
4. Crear la interfaz DAO.
5. Implementar el DAO.
6. Configurar DAOFactory.
7. Crear RestController.
8. Probar las rutas con Postman.
9. Crear Controller web.
10. Crear los HTML de Thymeleaf.
11. Probar las rutas desde el navegador.

### Recordatorio de SQL

```sql
SELECT * FROM tabla
```

```sql
SELECT * FROM tabla WHERE columna = ?
```

```sql
INSERT INTO tabla (columna1, columna2) VALUES (?, ?)
```

```sql
UPDATE tabla SET columna1 = ? WHERE columna2 = ?
```

```sql
DELETE FROM tabla WHERE columna = ?
```

### Recordatorio de JDBC

```java
PreparedStatement ps = ConexionBD.getInstance().prepareStatement(sql);
```

Introducir parámetros:

```java
ps.setString(1, valor);
```

Ejecutar SELECT:

```java
ResultSet rs = ps.executeQuery();
```

Recorrer resultados:

```java
while (rs.next()) {
    // Crear objetos y añadirlos a la lista.
}
```

Ejecutar modificaciones:

```java
ps.executeUpdate();
```

O:

```java
ps.execute();
```

### Recordatorio de REST

```java
@RestController
```

```java
@GetMapping
@PostMapping
@PutMapping
@DeleteMapping
```

Recoger un dato de la URL:

```java
@PathVariable String matricula
```

Recoger un JSON:

```java
@RequestBody Vehiculo vehiculo
```

### Recordatorio de Controller

```java
@Controller
```

Pasar información al HTML:

```java
model.addAttribute("nombre", objeto);
```

Mostrar HTML:

```java
return "vehiculos";
```

Redirigir:

```java
return "redirect:/vehiculos";
```

### Recordatorio de Thymeleaf

Recorrer:

```html
<div th:each="v : ${vehiculos}">
```

Mostrar:

```html
<p th:text="${v.marca}"></p>
```

Enlace dinámico:

```html
<a th:href="@{/vehiculos/editar/{matricula}(matricula=${v.matricula})}">
    Editar
</a>
```

Formulario:

```html
<form action="/vehiculos/guardar" method="post">
    <input name="marca">
    <button type="submit">Guardar</button>
</form>
```

Rellenar un input:

```html
<input name="marca" th:value="${vehiculo.marca}">
```

Campo oculto:

```html
<input type="hidden" name="matricula" th:value="${vehiculo.matricula}">
```

Campo no modificable:

```html
<input name="matricula" th:value="${vehiculo.matricula}" readonly>
```

---

## RESUMEN FINAL

El DAO realiza las consultas SQL.

DAOFactory permite acceder al DAO.

RestController permite utilizar el DAO desde Postman.

Controller permite utilizar el DAO desde páginas web.

Model permite enviar información desde Java al HTML.

Thymeleaf permite mostrar la información y crear formularios dinámicos.

PathVariable recoge información de la URL.

RequestBody recoge información de un JSON.

Los formularios HTML envían los datos mediante los atributos name.

PreparedStatement permite ejecutar consultas SQL con parámetros.

ResultSet permite recorrer los resultados de un SELECT.

Redirect permite volver a ejecutar una ruta después de guardar, actualizar o eliminar.

La API REST y Thymeleaf son dos formas diferentes de acceder a las mismas operaciones del DAO.

No hay que duplicar el código SQL.
