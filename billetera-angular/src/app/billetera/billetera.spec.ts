import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BilleteraComponent } from './billetera';

describe('BilleteraComponent', () => {
  let component: BilleteraComponent;
  let fixture: ComponentFixture<BilleteraComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BilleteraComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(BilleteraComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should start with saldo 250.00 and empty historial', async () => {
    const compiled = fixture.nativeElement as HTMLElement;
    expect(component.saldo).toBe(250);
    expect(component.movimientos.length).toBe(0);
    expect(compiled.textContent).toContain('S/ 250.00');
    expect(compiled.textContent).toContain('Aún no hay movimientos');
  });

  it('should apply valid movements, newest first, and ignore invalid ones', () => {
    component.registrarMovimiento({ tipo: 'Recarga', monto: 100 });
    component.registrarMovimiento({ tipo: 'Gasto', monto: 50 });
    component.registrarMovimiento({ tipo: 'Gasto', monto: 400 });
    component.registrarMovimiento({ tipo: 'Recarga', monto: Infinity });
    component.registrarMovimiento({ tipo: 'Recarga', monto: -10 });
    expect(component.saldo).toBe(300);
    expect(component.movimientos.map((m) => m.tipo)).toEqual(['Gasto', 'Recarga']);
  });

  it('should keep saldo rounded to cents', () => {
    component.registrarMovimiento({ tipo: 'Recarga', monto: 0.1 });
    component.registrarMovimiento({ tipo: 'Recarga', monto: 0.2 });
    expect(component.saldo).toBe(250.3);
  });
});
