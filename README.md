# 🃏 Blackjack API

## 📄 Descripción - Ejercicio

Esta es una API REST reactiva para el juego de Blackjack (21) desarrollada con Spring Boot. El proyecto implementa la lógica completa del juego de Blackjack, incluyendo gestión de jugadores, partidas, cartas y estadísticas. 

La aplicación utiliza una arquitectura de doble base de datos:
- **MySQL** para la gestión persistente de jugadores y sus estadísticas
- **MongoDB** para el almacenamiento de estados de partidas y acciones del juego

El objetivo del ejercicio es crear una API escalable y mantenible que permita a los usuarios jugar al Blackjack de manera interactiva, con seguimiento completo de estadísticas y historial de partidas.

## 💻 Tecnologías utilizadas

### Framework Principal
- **Spring Boot 3.2.3** - Framework principal de la aplicación
- **Spring WebFlux** - Programación reactiva para APIs no bloqueantes
- **Spring Security** - Seguridad y autenticación
- **Spring Data R2DBC** - Acceso reactivo a base de datos MySQL
- **Spring Data MongoDB Reactive** - Acceso reactivo a MongoDB

### Base de Datos
- **MySQL 8.0** - Base de datos relacional para jugadores
- **MongoDB** - Base de datos NoSQL para partidas
- **R2DBC MySQL Driver** - Driver reactivo para MySQL

### Herramientas de Desarrollo
- **Java 21** - Lenguaje de programación
- **Gradle 8.x** - Herramienta de construcción
- **Lombok** - Reducción de código boilerplate
- **SpringDoc OpenAPI 3** - Documentación automática de API

### Testing
- **JUnit 5** - Framework de testing
- **TestContainers** - Testing de integración con contenedores
- **Reactor Test** - Testing para programación reactiva
- **Spring Boot Test** - Testing de aplicaciones Spring Boot

### DevOps
- **Docker & Docker Compose** - Contenedorización de bases de datos
- **Swagger UI** - Interfaz de documentación interactiva

## 📋 REQUISITOS

### Software Requerido
- **Java 21** o superior
- **Docker** y **Docker Compose**
- **Git** para clonación del repositorio

### Puertos Requeridos
- **Puerto 8080** - Aplicación Spring Boot
- **Puerto 3307** - MySQL (mapeado desde 3306 del contenedor)
- **Puerto 27017** - MongoDB

### Memoria Recomendada
- **Mínimo**: 4GB RAM
- **Recomendado**: 8GB RAM para desarrollo completo

## 🛠️ Instalación

### 1. Clonar el Repositorio
```bash
git clone [URL_DEL_REPOSITORIO]
cd blackjack-api
```

### 2. Configurar las Bases de Datos
```bash
# Iniciar las bases de datos con Docker Compose
cd blackjack-api
docker-compose up -d
```

### 3. Verificar que las Bases de Datos estén Funcionando
```bash
# Verificar MySQL
docker exec -it blackjack-mysql mysql -u root -p78446660579 -e "SHOW DATABASES;"

# Verificar MongoDB
docker exec -it blackjack-mongodb mongosh --eval "db.adminCommand('ping')"
```

### 4. Configurar Variables de Entorno (Opcional)
```bash
# Para desarrollo local, las configuraciones por defecto funcionan
# Si necesitas cambiar las credenciales de base de datos:
export MYSQL_PASSWORD=tu_password
export MONGO_HOST=localhost
```

### 5. Construir la Aplicación
```bash
# Usando Gradle Wrapper (recomendado)
./gradlew build

# O usando Gradle instalado globalmente
gradle build
```

## ▶️ Ejecución

### Modo Desarrollo
```bash
# Ejecutar con Gradle (modo desarrollo)
./gradlew bootRun

# O ejecutar con profile específico
./gradlew bootRun --args='--spring.profiles.active=dev'
```

### Modo Producción
```bash
# Construir JAR
./gradlew build

# Ejecutar JAR
java -jar build/libs/blackjack-api-0.0.1-SNAPSHOT.jar
```

### Verificar que la Aplicación esté Funcionando
```bash
# Verificar health endpoint
curl http://localhost:8080/actuator/health

# Acceder a Swagger UI
# Abrir navegador en: http://localhost:8080/swagger-ui.html
```

## 🌐 DEPLOYMENT

### Deployment con Docker

#### 1. Crear Dockerfile para la Aplicación
```dockerfile
FROM openjdk:21-jdk-slim
COPY build/libs/blackjack-api-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

#### 2. Construir Imagen Docker
```bash
./gradlew build
docker build -t blackjack-api:latest .
```

#### 3. Deployment Completo con Docker Compose
```yaml
# Agregar al docker-compose.yml existente:
services:
  blackjack-api:
    build: .
    ports:
      - "8080:8080"
    depends_on:
      - mysql
      - mongodb
    environment:
      - SPRING_PROFILES_ACTIVE=production
```

### Deployment en Servidor

#### 1. Preparar el Servidor
```bash
# Instalar Java 21
sudo apt update
sudo apt install openjdk-21-jdk

# Instalar Docker y Docker Compose
sudo apt install docker.io docker-compose

# Crear usuario de aplicación
sudo useradd -m -s /bin/bash blackjack
```

#### 2. Configurar como Servicio del Sistema
```bash
# Crear archivo de servicio
sudo nano /etc/systemd/system/blackjack-api.service

[Unit]
Description=Blackjack API Service
After=syslog.target network.target

[Service]
User=blackjack
ExecStart=/usr/bin/java -jar /opt/blackjack-api/blackjack-api.jar
SuccessExitStatus=143
Restart=on-failure
RestartSec=10

[Install]
WantedBy=multi-user.target
```

#### 3. Configuración de Producción
```yaml
# application-production.yml
server:
  port: 8080
  
spring:
  r2dbc:
    url: r2dbc:mysql://localhost:3307/blackjack_prod
    username: ${DB_USERNAME:blackjack_user}
    password: ${DB_PASSWORD}
    
  data:
    mongodb:
      host: ${MONGO_HOST:localhost}
      port: 27017
      database: blackjack_prod
      
logging:
  level:
    com.blackjack: INFO
    org.springframework: WARN
```

## 🤝 Contribución

### Estructura del Proyecto
```
src/
├── main/java/com/blackjack/
│   ├── config/          # Configuraciones de Spring
│   ├── controller/      # Controladores REST
│   ├── dto/            # Data Transfer Objects
│   ├── exception/      # Manejo de excepciones
│   ├── model/          # Entidades del dominio
│   ├── repository/     # Repositorios de datos
│   ├── service/        # Lógica de negocio
│   └── util/           # Utilidades
└── test/               # Tests unitarios e integración
```

### Guías de Contribución

#### 1. Estándares de Código
- Seguir las convenciones de **Java Code Conventions**
- Usar **Lombok** para reducir boilerplate
- Implementar **programación reactiva** con Mono/Flux
- Escribir **tests unitarios** para toda nueva funcionalidad

#### 2. Proceso de Contribución
```bash
# 1. Fork del repositorio
# 2. Crear rama de feature
git checkout -b feature/nueva-funcionalidad

# 3. Realizar cambios y commits
git commit -m "feat: agregar nueva funcionalidad de X"

# 4. Ejecutar tests
./gradlew test

# 5. Crear Pull Request
```

#### 3. Convenciones de Commits
- `feat:` Nueva funcionalidad
- `fix:` Corrección de bugs
- `docs:` Cambios en documentación
- `test:` Agregar o modificar tests
- `refactor:` Refactorización de código

#### 4. Testing
```bash
# Ejecutar todos los tests
./gradlew test

# Ejecutar tests específicos
./gradlew test --tests "*PlayerServiceTest*"

# Generar reporte de cobertura
./gradlew jacocoTestReport
```

### Contacto y Soporte
Para reportar bugs o solicitar nuevas funcionalidades, por favor crear un **Issue** en el repositorio con la siguiente información:
- Descripción clara del problema o solicitud
- Pasos para reproducir (en caso de bugs)
- Versión de Java y sistema operativo
- Logs relevantes (si aplica) 
