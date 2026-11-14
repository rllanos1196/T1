package com.upn.billetera.controller;

import com.upn.billetera.model.Movimiento;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador MVC (Model 2) de la Billetera Digital.
 * GET  -> muestra saldo e historial (forward a la vista JSP)
 * POST -> valida y procesa un nuevo movimiento
 */
@WebServlet("/billetera")
public class BilleteraServlet extends HttpServlet {

    private static final String VISTA = "/WEB-INF/views/billetera.jsp";
    private static final double SALDO_INICIAL = 250.0;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        inicializarSesion(session);
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

        Double saldo = (Double) session.getAttribute("saldo");
        @SuppressWarnings("unchecked")
        List<Movimiento> historial = (List<Movimiento>) session.getAttribute("historial");

        // --- Validaciones (no alteran la sesión si fallan) ---
        if (tipo == null || (!"Recarga".equals(tipo) && !"Gasto".equals(tipo))) {
            request.setAttribute("error", "Debe seleccionar un tipo de movimiento válido (Recarga o Gasto).");
            request.getRequestDispatcher(VISTA).forward(request, response);
            return;
        }

        if (montoTexto == null || montoTexto.trim().isEmpty()) {
            request.setAttribute("error", "El monto no puede quedar vacío.");
            request.getRequestDispatcher(VISTA).forward(request, response);
            return;
        }

        double monto;
        try {
            monto = Double.parseDouble(montoTexto.trim().replace(",", "."));
        } catch (NumberFormatException e) {
            request.setAttribute("error", "El monto debe ser un valor numérico válido.");
            request.getRequestDispatcher(VISTA).forward(request, response);
            return;
        }

        if (monto <= 0) {
            request.setAttribute("error", "El monto debe ser un número mayor a cero.");
            request.getRequestDispatcher(VISTA).forward(request, response);
            return;
        }

        if ("Gasto".equals(tipo) && monto > saldo) {
            request.setAttribute("error", "Saldo insuficiente para realizar el gasto. Saldo disponible: S/ "
                    + String.format("%.2f", saldo) + ".");
            request.getRequestDispatcher(VISTA).forward(request, response);
            return;
        }

        // --- Operación válida: actualiza saldo e historial ---
        if ("Recarga".equals(tipo)) {
            saldo = saldo + monto;
        } else {
            saldo = saldo - monto;
        }
        session.setAttribute("saldo", saldo);

        // Orden cronológico inverso: el movimiento más reciente siempre arriba
        historial.add(0, new Movimiento(tipo, monto));
        session.setAttribute("historial", historial);

        request.setAttribute("exito", "Movimiento registrado correctamente.");
        request.getRequestDispatcher(VISTA).forward(request, response);
    }

    /**
     * Estado inicial obligatorio: saldo exactamente S/ 250.00 e historial vacío.
     */
    private void inicializarSesion(HttpSession session) {
        if (session.getAttribute("saldo") == null) {
            session.setAttribute("saldo", SALDO_INICIAL);
        }
        if (session.getAttribute("historial") == null) {
            session.setAttribute("historial", new ArrayList<Movimiento>());
        }
    }
}
