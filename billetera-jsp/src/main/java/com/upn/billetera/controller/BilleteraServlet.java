package com.upn.billetera.controller;

import com.upn.billetera.model.Movimiento;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Controlador MVC (Model 2) de la Billetera Digital.
 * GET  -> muestra saldo e historial (forward a la vista JSP)
 * POST -> valida y procesa un nuevo movimiento.
 *         Si es inválido: forward a la misma vista con el error (sin modificar datos).
 *         Si es válido: Post-Redirect-Get (sendRedirect) para evitar reenvíos al actualizar.
 */
@WebServlet("/billetera")
public class BilleteraServlet extends HttpServlet {

    private static final String VISTA = "/WEB-INF/views/billetera.jsp";
    private static final BigDecimal SALDO_INICIAL = new BigDecimal("250.00");

    /** Solo dígitos, signo opcional y hasta 2 decimales (rechaza NaN, Infinity, 1e3, 100d, 0x10...). */
    private static final Pattern FORMATO_MONTO = Pattern.compile("-?\\d+([.,]\\d{1,2})?");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        inicializarSesion(session);

        // Mensaje "flash" dejado por el POST antes del redirect: se muestra una sola vez
        Object exito = session.getAttribute("exito");
        if (exito != null) {
            request.setAttribute("exito", exito);
            session.removeAttribute("exito");
        }

        request.getRequestDispatcher(VISTA).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        inicializarSesion(session);

        String tipo = request.getParameter("tipo");
        String montoTexto = request.getParameter("monto");

        BigDecimal saldo = (BigDecimal) session.getAttribute("saldo");
        @SuppressWarnings("unchecked")
        List<Movimiento> historial = (List<Movimiento>) session.getAttribute("historial");

        // --- Validaciones (no alteran la sesión si fallan) ---
        if (tipo == null || (!"Recarga".equals(tipo) && !"Gasto".equals(tipo))) {
            mostrarError(request, response, "Debe seleccionar un tipo de movimiento válido (Recarga o Gasto).");
            return;
        }

        if (montoTexto == null || montoTexto.trim().isEmpty()) {
            mostrarError(request, response, "El monto no puede quedar vacío.");
            return;
        }

        String texto = montoTexto.trim();
        if (!FORMATO_MONTO.matcher(texto).matches()) {
            mostrarError(request, response,
                    "El monto debe ser un valor numérico válido (ejemplo: 100 o 100.50, máximo 2 decimales).");
            return;
        }
        BigDecimal monto = new BigDecimal(texto.replace(",", "."));

        if (monto.signum() <= 0) {
            mostrarError(request, response, "El monto debe ser un número mayor a cero.");
            return;
        }

        if ("Gasto".equals(tipo) && monto.compareTo(saldo) > 0) {
            mostrarError(request, response, "Saldo insuficiente para realizar el gasto. Saldo disponible: S/ "
                    + saldo.setScale(2) + ".");
            return;
        }

        // --- Operación válida: actualiza saldo e historial ---
        saldo = "Recarga".equals(tipo) ? saldo.add(monto) : saldo.subtract(monto);
        session.setAttribute("saldo", saldo);

        // Orden cronológico inverso: el movimiento más reciente siempre arriba
        historial.add(0, new Movimiento(tipo, monto));
        session.setAttribute("historial", historial);

        // Post-Redirect-Get: al actualizar la página no se reenvía el formulario
        session.setAttribute("exito", "Movimiento registrado correctamente.");
        response.sendRedirect(request.getContextPath() + "/billetera");
    }

    /** Presenta el error en la misma vista sin modificar saldo ni historial. */
    private void mostrarError(HttpServletRequest request, HttpServletResponse response, String mensaje)
            throws ServletException, IOException {
        request.setAttribute("error", mensaje);
        request.getRequestDispatcher(VISTA).forward(request, response);
    }

    /**
     * Estado inicial obligatorio: saldo exactamente S/ 250.00 e historial vacío.
     */
    private void inicializarSesion(HttpSession session) {
        if (!(session.getAttribute("saldo") instanceof BigDecimal)) {
            session.setAttribute("saldo", SALDO_INICIAL);
        }
        if (session.getAttribute("historial") == null) {
            session.setAttribute("historial", new ArrayList<Movimiento>());
        }
    }
}
