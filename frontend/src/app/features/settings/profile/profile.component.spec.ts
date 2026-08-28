import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SettingsProfileComponent } from './profile.component';
import { describe, beforeEach, it, expect } from 'vitest';

describe('SettingsProfileComponent', () => {
  let component: SettingsProfileComponent;
  let fixture: ComponentFixture<SettingsProfileComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SettingsProfileComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(SettingsProfileComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
