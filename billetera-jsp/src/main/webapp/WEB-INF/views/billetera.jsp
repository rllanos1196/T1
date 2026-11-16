<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Billetera Digital - JSP + Servlet</title>
    <!-- Bootstrap 5.3.8 (CDN, exclusivo framework de estilos) -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">

    <!-- Barra superior -->
    <nav class="navbar navbar-dark bg-dark">
        <div class="container">
            <span class="navbar-brand fw-semibold">Mi Billetera Digital</span>
            <span class="badge text-bg-secondary">JSP + Servlet</span>
        </div>
    </nav>

    <main class="container py-4">

        <!-- Mensajes de validación -->
        <c:if test="${not empty error}">
            <div class="alert alert-danger" role="alert">
                <strong>Error:</strong> <c:out value="${error}"/>
            </div>
        </c:if>
        <c:if test="${not empty exito}">
            <div class="alert alert-success" role="alert">
                <strong>OK:</strong> <c:out value="${exito}"/>
            </div>
        </c:if>

        <div class="row g-3">
            <!-- Tarjeta de saldo disponible -->
            <div class="col-lg-12">
                <div class="card border-0 shadow-sm">
                    <div class="card-body">
                        <div class="text-uppercase text-muted small fw-semibold">Saldo disponible</div>
                        <div class="display-5 fw-bold text-success">
                            S/ <fmt:formatNumber value="${sessionScope.saldo}" minFractionDigits="2" maxFractionDigits="2" />
                        </div>
                        <div class="text-muted small">
                            <c:choose>
                                <c:when test="${empty sessionScope.historial}">Sin movimientos registrados</c:when>
                                <c:otherwise><c:out value="${sessionScope.historial.size()}"/> movimiento(s) registrado(s)</c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <div class="row g-3 mt-1">
            <!-- Historial de movimientos -->
            <div class="col-lg-12">
                <div class="card border-0 shadow-sm">
                    <div class="card-header bg-white fw-semibold">Movimientos recientes</div>
                    <div class="card-body p-0">
                        <c:choose>
                            <c:when test="${not empty sessionScope.historial}">
                                <div class="table-responsive">
                                    <table class="table table-hover align-middle mb-0">
                                        <thead class="table-dark">
                                            <tr>
                                                <th>Tipo</th>
                                                <th class="text-end">Monto (S/)</th>
                                                <th class="text-center">Orden</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach items="${sessionScope.historial}" var="m" varStatus="status">
                                                <tr>
                                                    <td>
                                                        <c:choose>
                                                            <c:when test="${m.tipo == 'Recarga'}">
                                                                <span class="badge text-bg-success">Recarga</span>
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span class="badge text-bg-danger">Gasto</span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                    <td class="text-end fw-semibold ${m.tipo == 'Recarga' ? 'text-success' : 'text-danger'}">
                                                        <c:choose>
                                                            <c:when test="${m.tipo == 'Recarga'}">+ </c:when>
                                                            <c:otherwise>− </c:otherwise>
                                                        </c:choose>
                                                        <fmt:formatNumber value="${m.monto}" minFractionDigits="2" maxFractionDigits="2" />
                                                    </td>
                                                    <td class="text-center small text-muted">
                                                        <c:choose>
                                                            <c:when test="${status.index == 0}">Más reciente</c:when>
                                                            <c:otherwise>Anterior</c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <!-- Estado vacío -->
                                <div class="p-4 text-center text-muted">
                                    Aún no hay movimientos
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>

            <!-- Formulario de nuevo movimiento -->
            <div class="col-lg-12">
                <div class="card border-0 shadow-sm">
                    <div class="card-header bg-white fw-semibold">Nuevo movimiento</div>
                    <div class="card-body">
                        <form method="POST" action="${pageContext.request.contextPath}/billetera">
                            <div class="row g-3 align-items-end">
                                <div class="col-md-4">
                                    <label class="form-label small" for="tipo">Tipo</label>
                                    <select class="form-select" id="tipo" name="tipo" required>
                                        <option value="Recarga">Recarga</option>
                                        <%-- Tras un error se conserva el tipo elegido --%>
                                        <option value="Gasto" ${not empty error and param.tipo == 'Gasto' ? 'selected' : ''}>Gasto</option>
                                    </select>
                                </div>
                                <div class="col-md-4">
                                    <label class="form-label small" for="monto">Monto (S/)</label>
                                    <input type="text" class="form-control ${not empty error ? 'is-invalid' : ''}"
                                           id="monto" name="monto" placeholder="0.00" inputmode="decimal" required
                                           value="${not empty error ? fn:escapeXml(param.monto) : ''}">
                                </div>
                                <div class="col-md-4 d-grid">
                                    <button type="submit" class="btn btn-primary">Registrar movimiento</button>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>

    </main>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
