import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { RfbUserComponent } from '../list/rfb-user.component';
import { RfbUserDetailComponent } from '../detail/rfb-user-detail.component';
import { RfbUserUpdateComponent } from '../update/rfb-user-update.component';
import { RfbUserRoutingResolveService } from './rfb-user-routing-resolve.service';

const rfbUserRoute: Routes = [
  {
    path: '',
    component: RfbUserComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: RfbUserDetailComponent,
    resolve: {
      rfbUser: RfbUserRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: RfbUserUpdateComponent,
    resolve: {
      rfbUser: RfbUserRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: RfbUserUpdateComponent,
    resolve: {
      rfbUser: RfbUserRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(rfbUserRoute)],
  exports: [RouterModule],
})
export class RfbUserRoutingModule {}
