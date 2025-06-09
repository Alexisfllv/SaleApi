# 🛠️ Proyecto dbSaleApi

Este proyecto consiste en una API REST para la gestión de ventas, productos, categorías, usuarios y clientes. Implementado en Java usando buenas prácticas de desarrollo, arquitectura por capas y principios SOLID.

---

## 📐 Arquitectura General

El proyecto sigue una arquitectura en capas bien definida:

- **Controller**: Maneja las solicitudes HTTP.
- **Service**: Contiene la lógica de negocio.
- **Repository (JPA)**: Acceso a la base de datos.
- **DTOs y Mappers**: Separación clara entre entidades y datos expuestos.
- **Excepciones globales**: Control uniforme de errores.

---

## 🧼 Clean Code Aplicado

- ✅ Métodos pequeños y con una sola responsabilidad.
- ✅ Nombres de clases y métodos claros y descriptivos.
- ✅ Separación de responsabilidades por paquetes y capas.

---

## 🧱 Principios SOLID Aplicados

| Principio | Aplicación en el proyecto |
|----------|----------------------------|
| **SRP** - Single Responsibility | Cada clase tiene una única responsabilidad (ej: `UserServiceImpl` solo gestiona usuarios). |
| **OCP** - Open/Closed | El sistema es fácilmente extensible (ej: se pueden agregar nuevas entidades sin modificar controladores existentes). |
| **LSP** - Liskov Substitution | Todas las implementaciones de interfaces cumplen los contratos (`UserService`, `ProductService`, etc). |
| **ISP** - Interface Segregation | Las interfaces de servicios están bien divididas (`CategoryService`, `SaleService`, etc.). |
| **DIP** - Dependency Inversion | Las capas dependen de abstracciones, no implementaciones (uso de interfaces y `@Autowired`/`@RequiredArgsConstructor`). |

---

## 🧪 Testing (Pendiente)

El proyecto **aún no cuenta con pruebas unitarias**, pero está estructurado para facilitar su incorporación usando:

- `JUnit 5` + `Mockito`
- `@WebMvcTest` para controladores
- `@MockBean` para servicios

Recomendación: comenzar por pruebas a `CategoryServiceImpl`, `ProductServiceImpl` y luego `SaleServiceImpl` con mocks.

---

## 🧰 Tecnologías utilizadas

- Java 17
- Spring Framework (sin Spring Boot)
- Lombok
- JPA (Hibernate)
- DTOs con records
- Validación con `javax.validation`
- Control de errores global con `@RestControllerAdvice`

---

## ✅ Buenas prácticas adicionales

- Uso correcto de `@Valid`, `@NotNull`, `@Size`, etc.
- Estructura RESTful clara con `ResponseEntity`.
- Repositorios separados por entidad (`ProductRepo`, `UserRepo`, etc.).
- Mapeo DTO-Entidad con MapStruct.

---

## 📌 Recomendaciones para mejorar

- Añadir pruebas unitarias con JUnit + Mockito.
- Agregar cobertura de código con JaCoCo.
- Refactorizar `SaleServiceImpl` para separar lógica de validación en componentes reutilizables.

---

## 🚀 Estado del Proyecto

✅ Listo para producción (con pruebas pendientes).  
🎯 Ideal como demostración para puestos Backend Java Junior.

---

## 👨‍💻 Autor

Alexis (Backend Java Developer)
