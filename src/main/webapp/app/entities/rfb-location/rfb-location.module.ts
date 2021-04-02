import { NgModule } from '@angular/core';

import { SharedModule } from 'app/shared/shared.module';
import { RfbLocationComponent } from './list/rfb-location.component';
import { RfbLocationDetailComponent } from './detail/rfb-location-detail.component';
import { RfbLocationUpdateComponent } from './update/rfb-location-update.component';
import { RfbLocationDeleteDialogComponent } from './delete/rfb-location-delete-dialog.component';
import { RfbLocationRoutingModule } from './route/rfb-location-routing.module';

@NgModule({
  imports: [SharedModule, RfbLocationRoutingModule],
  declarations: [RfbLocationComponent, RfbLocationDetailComponent, RfbLocationUpdateComponent, RfbLocationDeleteDialogComponent],
  entryComponents: [RfbLocationDeleteDialogComponent],
})
export class RfbLocationModule {}
