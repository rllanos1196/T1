# Billetera Digital — Caso 2: Angular (Componentes Padre e Hijo)

Aplicación Angular que implementa la misma billetera digital del Caso 1 mediante
**comunicación entre componentes con Signals modernos** (`input()` y `output()`),
interpolación, property binding (`[disabled]`) y flujo de control nativo (`@if`, `@for`).
Todo el estado vive en memoria (propiedades del componente padre): no hay servicios
inyectables, enrutamiento, `ReactiveFormsModule` ni backend.

## Versiones utilizadas

| Componente | Versión |
|---|---|
| Node.js | 24.22.1 |
| npm | 11.19.1 |
| Angular CLI | 21.2.30 (proyecto Angular 21) |
| Bootstrap | 5.3.8 (instalado por npm y agregado en `styles` de `angular.json`) |

## Estructura

```
billetera-angular/
├── angular.json                 # bootstrap.min.css en styles
├── package.json
├── README.md
└── src/app/
    ├── movimiento.model.ts      # interface Movimiento { tipo; monto }
    ├── app.ts                   # AppComponent: solo contenedor de <app-billetera>
    ├── app.html
    ├── billetera/               # BilleteraComponent (PADRE, dueño del estado)
    │   ├── billetera.ts
    │   └── billetera.html       # saldo con interpolación, @for, @if, [saldo], (movimientoValido)
    └── nuevo-movimiento/        # NuevoMovimientoComponent (HIJO, formulario)
        ├── nuevo-movimiento.ts  # input.required<number>(), output<Movimiento>()
        └── nuevo-movimiento.html # validación con @if y [disabled]
```

## Comandos de instalación y ejecución

```bash
npm install
ng serve        # o: npx ng serve --port 4200
```

Abrir http://localhost:4200

En Windows PowerShell (si `npm`/`npx` están bloqueados por la política de scripts,
usar `npm.cmd`/`npx.cmd`). Si la página queda en blanco porque el servidor solo
escucha en IPv6 (`[::1]`), forzar la dirección IPv4 y otro puerto:

```powershell
cd billetera-angular; npm.cmd install; npx.cmd ng serve --port 4300 --host 127.0.0.1
```

Abrir http://127.0.0.1:4300

## Compilar para producción

```bash
npx ng build
```

## Arquitectura de comunicación

```
┌────────────────────────────┐        [saldo]="saldo"         ┌──────────────────────────────┐
│   BilleteraComponent       │ ─────────────────────────────► │  NuevoMovimientoComponent   │
│   (PADRE)                  │                                │  (HIJO)                     │
│   saldo = 250              │ ◄───────────────────────────── │  saldo = input.required()   │
│   movimientos: Movimiento[]│   (movimientoValido)=$event     │  movimientoValido = output()│
└────────────────────────────┘                                └──────────────────────────────┘
```

- **Padre**: `saldo = 250` y `movimientos: Movimiento[] = []`. Muestra el saldo con
  `S/ {{ saldo | number:'1.2-2' }}` (pipe `decimal`), lista el historial con `@for`
  (insertando cada registro con `unshift`, el más reciente arriba) y muestra
  `Aún no hay movimientos` con `@if`.
- **Hijo**: recibe el saldo con `saldo = input.required<number>()`, valida en tiempo
  real que el monto sea numérico y `> 0`, y si `tipo === 'Gasto' && monto > saldo()`
  muestra `Saldo insuficiente para realizar el gasto` con `@if`. El botón se deshabilita
  con `[disabled]` cuando el formulario no es válido y el campo monto se limpia tras
  emitir un registro válido. **El hijo nunca modifica saldo ni historial.**
- El monto acepta solo formato numérico simple (`100`, `100.50`, máx. 2 decimales):
  se rechazan `Infinity`, `NaN`, `1e3`, `0x10`, etc.
- Como defensa adicional, el padre vuelve a validar el movimiento recibido y
  redondea el saldo a céntimos para evitar errores de coma flotante.

## Pruebas funcionales ejecutadas

| # | Acción | Resultado esperado | Estado |
|---|---|---|---|
| 1 | Abrir la aplicación | Saldo S/ 250.00; historial vacío | ✅ |
| 2 | Recarga de S/ 100.00 | Saldo S/ 350.00; recarga en historial | ✅ |
| 3 | Gasto de S/ 50.00 | Saldo S/ 300.00; gasto aparece antes que recarga | ✅ |
| 4 | Gasto de S/ 400.00 | Error "Saldo insuficiente…", botón deshabilitado, saldo sigue en S/ 300.00 | ✅ |
| 5 | Monto 0, -10, texto o vacío | Botón deshabilitado / error; no cambia saldo ni historial | ✅ |
| 6 | Monto `Infinity` / `0x10` / `1e3` | Botón deshabilitado / error | ✅ |

Pruebas unitarias: `npx ng test --watch=false`
