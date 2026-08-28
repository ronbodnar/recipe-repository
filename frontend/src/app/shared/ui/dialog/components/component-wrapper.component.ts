import {
  Component,
  Input,
  ViewContainerRef,
  ViewChild,
  ComponentRef,
  AfterViewInit,
  EventEmitter,
  Type,
  ChangeDetectionStrategy,
} from '@angular/core';

@Component({
  selector: 'app-component-wrapper',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <ng-template #container />
  `,
})
export class ComponentWrapperComponent implements AfterViewInit {
  @Input() component!: Type<unknown>;
  @Input() inputs: Record<string, unknown> = {};
  @Input() outputs: Record<string, unknown> = {};

  @ViewChild('container', { read: ViewContainerRef })
  container!: ViewContainerRef;

  ngAfterViewInit(): void {
    const ref: ComponentRef<any> = this.container.createComponent(this.component);

    // Bind inputs
    Object.entries(this.inputs || {}).forEach(([key, value]) => {
      ref.instance[key] = value;
    });

    ref.changeDetectorRef.detectChanges();

    // Bind outputs
    Object.entries(this.outputs || {}).forEach(([key, handler]) => {
      if (ref.instance[key] instanceof EventEmitter && typeof handler === 'function') {
        ref.instance[key].subscribe(handler);
      }
    });
  }
}
