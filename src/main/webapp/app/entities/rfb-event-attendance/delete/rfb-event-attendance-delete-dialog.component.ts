import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IRfbEventAttendance } from '../rfb-event-attendance.model';
import { RfbEventAttendanceService } from '../service/rfb-event-attendance.service';

@Component({
  templateUrl: './rfb-event-attendance-delete-dialog.component.html',
})
export class RfbEventAttendanceDeleteDialogComponent {
  rfbEventAttendance?: IRfbEventAttendance;

  constructor(protected rfbEventAttendanceService: RfbEventAttendanceService, public activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.rfbEventAttendanceService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
