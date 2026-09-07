import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TableLinkComponent } from './table-link.component';

describe('TableLinkComponent', () => {
  let component: TableLinkComponent;
  let fixture: ComponentFixture<TableLinkComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TableLinkComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(TableLinkComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
