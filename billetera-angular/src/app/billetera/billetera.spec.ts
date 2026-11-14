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
});
