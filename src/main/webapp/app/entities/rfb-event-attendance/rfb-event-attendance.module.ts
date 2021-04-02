import { NgModule } from '@angular/core';

import { SharedModule } from 'app/shared/shared.module';
import { RfbEventAttendanceComponent } from './list/rfb-event-attendance.component';
import { RfbEventAttendanceDetailComponent } from './detail/rfb-event-attendance-detail.component';
import { RfbEventAttendanceUpdateComponent } from './update/rfb-event-attendance-update.component';
import { RfbEventAttendanceDeleteDialogComponent } from './delete/rfb-event-attendance-delete-dialog.component';
import { RfbEventAttendanceRoutingModule } from './route/rfb-event-attendance-routing.module';

@NgModule({
  imports: [SharedModule, RfbEventAttendanceRoutingModule],
  declarations: [
    RfbEventAttendanceComponent,
    RfbEventAttendanceDetailComponent,
    RfbEventAttendanceUpdateComponent,
    RfbEventAttendanceDeleteDialogComponent,
  ],
  entryComponents: [RfbEventAttendanceDeleteDialogComponent],
})
export class RfbEventAttendanceModule {}
