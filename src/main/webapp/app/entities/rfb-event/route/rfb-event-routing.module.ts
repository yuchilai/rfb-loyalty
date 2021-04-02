import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { RfbEventComponent } from '../list/rfb-event.component';
import { RfbEventDetailComponent } from '../detail/rfb-event-detail.component';
import { RfbEventUpdateComponent } from '../update/rfb-event-update.component';
import { RfbEventRoutingResolveService } from './rfb-event-routing-resolve.service';

const rfbEventRoute: Routes = [
  {
    path: '',
    component: RfbEventComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: RfbEventDetailComponent,
    resolve: {
      rfbEvent: RfbEventRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: RfbEventUpdateComponent,
    resolve: {
      rfbEvent: RfbEventRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: RfbEventUpdateComponent,
    resolve: {
      rfbEvent: RfbEventRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(rfbEventRoute)],
  exports: [RouterModule],
})
export class RfbEventRoutingModule {}
