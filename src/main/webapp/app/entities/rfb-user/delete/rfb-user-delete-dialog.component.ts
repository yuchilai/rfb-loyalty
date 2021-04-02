import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IRfbUser } from '../rfb-user.model';
import { RfbUserService } from '../service/rfb-user.service';

@Component({
  templateUrl: './rfb-user-delete-dialog.component.html',
})
export class RfbUserDeleteDialogComponent {
  rfbUser?: IRfbUser;

  constructor(protected rfbUserService: RfbUserService, public activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.rfbUserService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
