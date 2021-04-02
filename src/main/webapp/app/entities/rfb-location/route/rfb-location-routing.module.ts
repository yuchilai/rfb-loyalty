import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { RfbLocationComponent } from '../list/rfb-location.component';
import { RfbLocationDetailComponent } from '../detail/rfb-location-detail.component';
import { RfbLocationUpdateComponent } from '../update/rfb-location-update.component';
import { RfbLocationRoutingResolveService } from './rfb-location-routing-resolve.service';

const rfbLocationRoute: Routes = [
  {
    path: '',
    component: RfbLocationComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: RfbLocationDetailComponent,
    resolve: {
      rfbLocation: RfbLocationRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: RfbLocationUpdateComponent,
    resolve: {
      rfbLocation: RfbLocationRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: RfbLocationUpdateComponent,
    resolve: {
      rfbLocation: RfbLocationRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(rfbLocationRoute)],
  exports: [RouterModule],
})
export class RfbLocationRoutingModule {}
