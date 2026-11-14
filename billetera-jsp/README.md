# Billetera Digital — Caso 1: Java puro (Servlet + JSP + JSTL)

Aplicación web desarrollada bajo el patrón **MVC (Model 2)** para Apache Tomcat 10.1
(Jakarta EE 10 / Servlet 6.0). El estado (saldo e historial) vive exclusivamente en
**HttpSession**; no hay bases de datos, APIs externas, servicios REST ni autenticación.

## Versión utilizadas

| Componente | Versión |
|---|---|
| JDK | OpenJDK 23.0.2 (compila con `maven.compiler.release=17`, requisito JDK 17+) |
| Maven | 3.10.0 |
| Apache Tomcat | 10.1.61 (Servlet 6.0 / Jakarta EE 10) |
| Jakarta Servlet API | 6.0.0 (scope `provided`) |
| JSTL | `jakarta.servlet.jsp.jstl-api` 3.0.0 + `org.glassfish.web:jakarta.servlet.jsp.jstl` 3.0.1 |
| Bootstrap | 5.3.8 (CDN, único framework de estilos) |

## Estructura

```
billetera-jsp/
├── pom.xml
├── README.md
└── src/main/
    ├── java/com/upn/billetera/
    │   ├── model/
    │   │   └── Movimiento.java          # POJO (tipo, monto)
    │   └── controller/
    │       └── BilleteraServlet.java    # @WebServlet("/billetera")
    └── webapp/
        ├── index.jsp                    # Redirección automática a /billetera
        └── WEB-INF/
            ├── web.xml                  # Locale de formato JSTL (en_US)
            └── views/
                └── billetera.jsp        # Vista con Bootstrap 5.3.8 + JSTL (sin scriptlets)
```

## Compilar

```bash
mvn clean package
```

Genera el artefacto obligatorio de entrega: `target/billetera-jsp.war`.

## Despliegue en Tomcat 10.1

1. Copiar el WAR al directorio de aplicaciones de Tomcat:

   ```bash
   cp target/billetera-jsp.war $CATALINA_HOME/webapps/
   ```

2. Iniciar Tomcat (si aún no está en marcha):

   ```bash
   $CATALINA_HOME/bin/catalina.sh run     # Unix
   %CATALINA_HOME%\bin\catalina.bat run   # Windows
   ```

3. Abrir la aplicación en el navegador:

   ```
   http://localhost:8080/billetera-jsp/billetera
   ```

   `index.jsp` redirige automáticamente a ese mismo recurso.

## Reglas de negocio implementadas

- Saldo inicial exacto **S/ 250.00** con historial vacío al iniciar la sesión.
- **Recarga**: suma un monto `> 0` al saldo.
- **Gasto**: resta un monto `> 0` solo si `monto <= saldo`.
- Cada movimiento válido se inserta en la **posición 0** del historial
  (el más reciente aparece arriba).
- Validaciones (vacío, no numérico, `<= 0`, gasto mayor al saldo): se rechazan
  **sin alterar** saldo ni historial, mostrando un mensaje comprensible
  (alerta `alert-danger` de Bootstrap en la misma vista).
- La vista usa **JSTL** (`jakarta.tags.core`, `jakarta.tags.fmt`) con
  Expression Language; **cero scriptlets** `<% ... %>` con código Java.

## Pruebas funcionales ejecutadas

| # | Acción | Resultado esperado | Estado |
|---|---|---|---|
| 1 | Abrir la aplicación | Saldo S/ 250.00; historial vacío | ✅ |
| 2 | Recarga de S/ 100.00 | Saldo S/ 350.00; recarga en historial | ✅ |
| 3 | Gasto de S/ 50.00 | Saldo S/ 300.00; gasto aparece antes que recarga | ✅ |
| 4 | Gasto de S/ 400.00 | Se rechaza con "Saldo insuficiente"; saldo sigue en S/ 300.00 | ✅ |
| 5 | Monto 0, -10, texto o vacío | Se rechaza; no cambia saldo ni historial | ✅ |
