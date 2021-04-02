import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IRfbLocation } from '../rfb-location.model';
import { RfbLocationService } from '../service/rfb-location.service';

@Component({
  templateUrl: './rfb-location-delete-dialog.component.html',
})
export class RfbLocationDeleteDialogComponent {
  rfbLocation?: IRfbLocation;

  constructor(protected rfbLocationService: RfbLocationService, public activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.rfbLocationService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
