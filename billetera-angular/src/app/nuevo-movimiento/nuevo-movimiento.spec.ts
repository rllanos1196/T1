import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NuevoMovimientoComponent } from './nuevo-movimiento';

describe('NuevoMovimientoComponent', () => {
  let component: NuevoMovimientoComponent;
  let fixture: ComponentFixture<NuevoMovimientoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NuevoMovimientoComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(NuevoMovimientoComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('saldo', 250);
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should disable the button when monto is empty', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    const button = compiled.querySelector('button[type="submit"]') as HTMLButtonElement;
    expect(component.formularioValido).toBe(false);
    expect(button.disabled).toBe(true);
  });

  it('should reject a gasto greater than the saldo', () => {
    component.tipo = 'Gasto';
    component.montoTexto = '400';
    expect(component.saldoInsuficiente).toBe(true);
    expect(component.formularioValido).toBe(false);
  });
});
