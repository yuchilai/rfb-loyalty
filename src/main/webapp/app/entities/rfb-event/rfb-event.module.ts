import { NgModule } from '@angular/core';

import { SharedModule } from 'app/shared/shared.module';
import { RfbEventComponent } from './list/rfb-event.component';
import { RfbEventDetailComponent } from './detail/rfb-event-detail.component';
import { RfbEventUpdateComponent } from './update/rfb-event-update.component';
import { RfbEventDeleteDialogComponent } from './delete/rfb-event-delete-dialog.component';
import { RfbEventRoutingModule } from './route/rfb-event-routing.module';

@NgModule({
  imports: [SharedModule, RfbEventRoutingModule],
  declarations: [RfbEventComponent, RfbEventDetailComponent, RfbEventUpdateComponent, RfbEventDeleteDialogComponent],
  entryComponents: [RfbEventDeleteDialogComponent],
})
export class RfbEventModule {}
