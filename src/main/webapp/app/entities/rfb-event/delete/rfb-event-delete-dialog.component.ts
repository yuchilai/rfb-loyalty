import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IRfbEvent } from '../rfb-event.model';
import { RfbEventService } from '../service/rfb-event.service';

@Component({
  templateUrl: './rfb-event-delete-dialog.component.html',
})
export class RfbEventDeleteDialogComponent {
  rfbEvent?: IRfbEvent;

  constructor(protected rfbEventService: RfbEventService, public activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.rfbEventService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
