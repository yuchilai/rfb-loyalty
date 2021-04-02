import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { RfbEventAttendanceComponent } from '../list/rfb-event-attendance.component';
import { RfbEventAttendanceDetailComponent } from '../detail/rfb-event-attendance-detail.component';
import { RfbEventAttendanceUpdateComponent } from '../update/rfb-event-attendance-update.component';
import { RfbEventAttendanceRoutingResolveService } from './rfb-event-attendance-routing-resolve.service';

const rfbEventAttendanceRoute: Routes = [
  {
    path: '',
    component: RfbEventAttendanceComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: RfbEventAttendanceDetailComponent,
    resolve: {
      rfbEventAttendance: RfbEventAttendanceRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: RfbEventAttendanceUpdateComponent,
    resolve: {
      rfbEventAttendance: RfbEventAttendanceRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: RfbEventAttendanceUpdateComponent,
    resolve: {
      rfbEventAttendance: RfbEventAttendanceRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(rfbEventAttendanceRoute)],
  exports: [RouterModule],
})
export class RfbEventAttendanceRoutingModule {}
