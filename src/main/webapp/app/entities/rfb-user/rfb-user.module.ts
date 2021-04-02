import { NgModule } from '@angular/core';

import { SharedModule } from 'app/shared/shared.module';
import { RfbUserComponent } from './list/rfb-user.component';
import { RfbUserDetailComponent } from './detail/rfb-user-detail.component';
import { RfbUserUpdateComponent } from './update/rfb-user-update.component';
import { RfbUserDeleteDialogComponent } from './delete/rfb-user-delete-dialog.component';
import { RfbUserRoutingModule } from './route/rfb-user-routing.module';

@NgModule({
  imports: [SharedModule, RfbUserRoutingModule],
  declarations: [RfbUserComponent, RfbUserDetailComponent, RfbUserUpdateComponent, RfbUserDeleteDialogComponent],
  entryComponents: [RfbUserDeleteDialogComponent],
})
export class RfbUserModule {}
