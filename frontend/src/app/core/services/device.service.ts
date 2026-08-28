import { Injectable, signal } from '@angular/core';
import { v4 as uuidv4 } from 'uuid';

@Injectable({
  providedIn: 'root',
})
export class DeviceService {
  private readonly _deviceId = signal<string>('');

  constructor() {
    this.initializeDevice();
  }

  private initializeDevice(): void {
    const storedDeviceId = localStorage.getItem('deviceId');
    const deviceId = storedDeviceId ?? uuidv4();
    this._deviceId.set(deviceId);

    if (storedDeviceId == null) {
      localStorage.setItem('deviceId', deviceId);
    }
  }

  public getDeviceId(): string {
    if (!this._deviceId()) {
      this.initializeDevice();
    }
    return this._deviceId();
  }
}
